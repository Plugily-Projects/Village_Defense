/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import fr.mrmicky.fastinv.FastInv;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.helper.ArmorHelper;
import plugily.projects.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.PremiumKit;
import plugily.projects.villagedefense.utils.Utils;

/**
 * Created by Tom on 18/08/2014.
 */
public class TeleporterKit extends PremiumKit implements Listener {

  public TeleporterKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.teleporter");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
    player.getInventory().addItem(new ItemBuilder(Material.GHAST_TEAR)
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_LORE), 40))
        .build());
  }

  @Override
  public Material getMaterial() {
    return Material.ENDER_PEARL;
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }

  @EventHandler
  public void onRightClick(CBPlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    Player player = e.getPlayer();
    Arena arena = ArenaRegistry.getArena(player);
    ItemStack stack = VersionUtils.getItemInHand(player);
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if(!ChatColor.stripColor(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta())).equalsIgnoreCase(ChatColor.stripColor(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_NAME)))) {
      return;
    }
    if (!(getPlugin().getUserManager().getUser(player).getKit() instanceof TeleporterKit)) {
      return;
    }
    int slots = arena.getVillagers().size();
    for(Player arenaPlayer : arena.getPlayers()) {
      if(getPlugin().getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      slots++;
    }
    slots = Utils.serializeInt(slots);
    prepareTeleporterGui(player, arena, slots);
  }

  private void prepareTeleporterGui(Player player, Arena arena, int slots) {
    FastInv gui = new FastInv(slots, getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_MENU_NAME));
    for(Player arenaPlayer : arena.getPlayers()) {
      if(getPlugin().getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
      SkullMeta meta = (SkullMeta) skull.getItemMeta();
      meta = VersionUtils.setPlayerHead(player, meta);
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(""));
      skull.setItemMeta(meta);
      gui.addItem(skull, onClick -> {
        player.sendMessage(getPlugin().getChatManager().formatMessage(arena, getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_PLAYER), arenaPlayer));
        player.teleport(arenaPlayer);
        Utils.playSound(player.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
        VersionUtils.sendParticles("PORTAL", arena.getPlayers(), player.getLocation(), 30);
        player.closeInventory();
      });
    }
    for(Villager villager : arena.getVillagers()) {
      gui.addItem(new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(villager.getCustomName())
          .lore(villager.getUniqueId().toString())
          .build(), onClick -> {
        player.teleport(villager.getLocation());
        Utils.playSound(player.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
        VersionUtils.sendParticles("PORTAL", arena.getPlayers(), player.getLocation(), 30);
        player.sendMessage(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_VILLAGER));
      });
    }
    gui.open(player);
  }

}
