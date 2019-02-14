/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.kits.premium;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 17/12/2015.
 */
public class BlockerKit extends PremiumKit implements Listener {

  public BlockerKit() {
    setName(getPlugin().getChatManager().colorMessage("Kits.Blocker.Kit-Name"));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Blocker.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.blocker");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.RED, player);
    player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STONE_SWORD), new org.bukkit.enchantments.Enchantment[] {org.bukkit.enchantments.Enchantment.DURABILITY}, new int[] {10}));
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.OAK_FENCE.parseMaterial(), 3))
        .name(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Name"))
        .lore(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Lore").split("\n"))
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
        .name(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Name"))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Lore"), 40))
        .build());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBarrierPlace(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Player player = event.getPlayer();
    ItemStack stack = player.getInventory().getItemInMainHand();
    if (!ArenaRegistry.isInArena(player) || !Utils.isNamed(stack) || !stack.getItemMeta().getDisplayName().equalsIgnoreCase(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Name"))) {
      return;
    }
    Block block = null;
    for (Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
      if (blocks.getType() == Material.AIR) {
        block = blocks;
      }
    }
    if (block == null) {
      event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Place-Fail"));
      return;
    }
    if (stack.getAmount() <= 1) {
      player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    } else {
      player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
    }
    event.setCancelled(false);

    event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Blocker.Game-Item-Place-Message"));
    ZombieBarrier zombieBarrier = new ZombieBarrier();
    zombieBarrier.setLocation(block.getLocation());
    zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
    removeBarrierLater(zombieBarrier);
    block.setType(XMaterial.OAK_FENCE.parseMaterial());
  }

  private void removeBarrierLater(ZombieBarrier zombieBarrier) {
    new BukkitRunnable() {
      @Override
      public void run() {
        zombieBarrier.decrementSeconds();
        if (zombieBarrier.getSeconds() <= 0) {
          zombieBarrier.getLocation().getBlock().setType(Material.AIR);
          zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
          this.cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 20, 20);
  }

  private static class ZombieBarrier {
    private Location location;
    private int seconds = 10;

    Location getLocation() {
      return location;
    }

    void setLocation(Location location) {
      this.location = location;
    }

    int getSeconds() {
      return seconds;
    }

    void decrementSeconds() {
      this.seconds = seconds - 1;
    }
  }
}
