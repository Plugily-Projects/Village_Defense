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

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.CompatMaterialConstants;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * Created by Tom on 18/08/2014.
 */
//todo use GUI instead of Inventory here please
public class TeleporterKit extends PremiumKit implements Listener {

  public TeleporterKit() {
    setName(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Kit-Name"));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
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
    player.getInventory().addItem(new ItemBuilder(new ItemStack(Material.GHAST_TEAR))
        .name(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Game-Item-Name"))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Game-Item-Lore"), 40))
        .build());
  }

  @Override
  public Material getMaterial() {
    return Material.ENDER_PEARL;
  }

  @Override
  public void reStock(Player player) {
  }

  @EventHandler
  public void onRightClick(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (arena == null || !Utils.isNamed(stack)) {
      return;
    }
    if (!stack.getItemMeta().getDisplayName().equalsIgnoreCase(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Game-Item-Name"))) {
      return;
    }
    Inventory inventory = getPlugin().getServer().createInventory(null, 18, getPlugin().getChatManager().colorMessage("Kits.Teleporter.Game-Item-Menu-Name"));
    for (Player player : e.getPlayer().getWorld().getPlayers()) {
      if (ArenaRegistry.getArena(player) != null && !getPlugin().getUserManager().getUser(player).isSpectator()) {
        ItemStack skull = CompatMaterialConstants.PLAYER_HEAD_ITEM.clone();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        meta.setLore(Collections.singletonList(""));
        skull.setItemMeta(meta);
        inventory.addItem(skull);
      }
    }
    for (Villager villager : arena.getVillagers()) {
      inventory.addItem(new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(villager.getCustomName())
          .lore(villager.getUniqueId().toString())
          .build());
    }
    e.getPlayer().openInventory(inventory);
  }


  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    Arena arena = ArenaRegistry.getArena(p);
    if (arena == null || !Utils.isNamed(e.getCurrentItem()) || !e.getCurrentItem().getItemMeta().hasLore()) {
      return;
    }
    if (!e.getInventory().getName().equals(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Game-Item-Menu-Name")) || !(e.isLeftClick() || e.isRightClick())) {
      return;
    }
    e.setCancelled(true);
    if (e.getCurrentItem().getType() == Material.EMERALD) {
      for (Villager villager : arena.getVillagers()) {
        if (villager.getCustomName().equals(e.getCurrentItem().getItemMeta().getDisplayName()) && villager.getUniqueId().toString().equals((ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0))))) {
          e.getWhoClicked().teleport(villager.getLocation());
          Utils.playSound(p.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
          p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30);
          p.sendMessage(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Teleported-To-Villager"));
          return;
        }
      }
      p.sendMessage(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Villager-Warning"));
      return;
    }
    //teleports to player
    ItemMeta meta = e.getCurrentItem().getItemMeta();
    for (Player player : arena.getPlayersLeft()) {
      if (player.getName().equals(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
        p.sendMessage(getPlugin().getChatManager().formatMessage(arena, getPlugin().getChatManager().colorMessage("Kits.Teleporter.Teleported-To-Player"), player));
        p.teleport(player);
        Utils.playSound(p.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30);
        p.closeInventory();
        e.setCancelled(true);
        return;
      }
    }
    p.sendMessage(getPlugin().getChatManager().colorMessage("Kits.Teleporter.Player-Not-Found"));
  }

}
