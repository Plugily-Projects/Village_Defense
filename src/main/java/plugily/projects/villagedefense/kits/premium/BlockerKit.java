/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.kits.premium;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;

import java.util.List;

/**
 * Created by Tom on 17/12/2015.
 */
public class BlockerKit extends PremiumKit implements Listener {

  public BlockerKit() {
    setName(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_NAME"));
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_BLOCKER_DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.blocker");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.RED, player);
    player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STONE_SWORD), new org.bukkit.enchantments.Enchantment[]{org.bukkit.enchantments.Enchantment.DURABILITY}, new int[]{10}));
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.OAK_FENCE.parseMaterial(), 3))
        .name(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_GAME_ITEM_NAME"))
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_BLOCKER_GAME_ITEM_DESCRIPTION"))
        .build());
    player.getInventory().addItem(new ItemStack(Material.SADDLE));

  }

  @Override
  public Material getMaterial() {
    return Material.BARRIER;
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.OAK_FENCE.parseMaterial(), 3))
        .name(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_GAME_ITEM_NAME"))
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_BLOCKER_GAME_ITEM_DESCRIPTION"))
        .build());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBarrierPlace(PlugilyPlayerInteractEvent event) {
    if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = event.getPlayer();
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    if(arena == null)
      return;

    ItemStack stack = VersionUtils.getItemInHand(player);
    if(!ItemUtils.isItemStackNamed(stack) || !ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta())
        .equalsIgnoreCase(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_GAME_ITEM_NAME"))) {
      return;
    }
    if(!(getPlugin().getUserManager().getUser(player).getKit() instanceof BlockerKit)) {
      return;
    }
    Block block = null;
    for(Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
      if(blocks.getType() == Material.AIR) {
        block = blocks;
      }
    }
    if(block == null) {
      event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_PLACE_FAIL"));
      return;
    }
    getPlugin().getBukkitHelper().takeOneItem(player, stack);
    event.setCancelled(false);

    event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("KIT_CONTENT_BLOCKER_PLACE_SUCCESS"));
    ZombieBarrier zombieBarrier = new ZombieBarrier();
    zombieBarrier.setLocation(block.getLocation());

    VersionUtils.sendParticles("FIREWORKS_SPARK", arena.getPlayers(), zombieBarrier.location, 20);
    removeBarrierLater(zombieBarrier, arena);
    block.setType(XMaterial.OAK_FENCE.parseMaterial());
  }

  private void removeBarrierLater(ZombieBarrier zombieBarrier, PluginArena arena) {
    new BukkitRunnable() {
      @Override
      public void run() {
        zombieBarrier.decrementSeconds();

        if(zombieBarrier.seconds <= 0) {
          zombieBarrier.location.getBlock().setType(Material.AIR);
          VersionUtils.sendParticles("FIREWORKS_SPARK", arena.getPlayers(), zombieBarrier.location, 20);
          cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 20, 20);
  }

  private static class ZombieBarrier {
    private Location location;
    private int seconds = 10;

    void setLocation(Location location) {
      this.location = location;
    }

    void decrementSeconds() {
      seconds--;
    }
  }
}
