/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense4.kits.premium;

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

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.arena.Arena;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.handlers.PermissionsManager;
import pl.plajer.villagedefense4.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense4.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense4.user.UserManager;
import pl.plajer.villagedefense4.utils.ArmorHelper;
import pl.plajer.villagedefense4.utils.Utils;
import pl.plajer.villagedefense4.utils.WeaponHelper;
import pl.plajer.villagedefense4.utils.XMaterial;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * Created by Tom on 18/08/2014.
 */
public class TeleporterKit extends PremiumKit implements Listener {

  private Main plugin;

  public TeleporterKit(Main plugin) {
    this.plugin = plugin;
    setName(ChatManager.colorMessage("Kits.Teleporter.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Teleporter.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
    ItemStack enderpealteleporter = new ItemStack(Material.GHAST_TEAR);
    List<String> teleporationlore = Utils.splitString(ChatManager.colorMessage("Kits.Teleporter.Game-Item-Lore"), 40);
    this.setItemNameAndLore(enderpealteleporter, ChatManager.colorMessage("Kits.Teleporter.Game-Item-Name"), teleporationlore.toArray(new String[0]));
    player.getInventory().addItem(enderpealteleporter);
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
    try {
      if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
        if (arena == null || stack == null || !stack.hasItemMeta()
                || !stack.getItemMeta().hasDisplayName()) {
          return;
        }
        if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Teleporter.Game-Item-Name"))) {
          Inventory inventory = plugin.getServer().createInventory(null, 18, ChatManager.colorMessage("Kits.Teleporter.Game-Item-Menu-Name"));
          for (Player player : e.getPlayer().getWorld().getPlayers()) {
            if (ArenaRegistry.getArena(player) != null && !UserManager.getUser(player.getUniqueId()).isSpectator()) {
              ItemStack skull;
              if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
                skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
              } else {
                //todo check
                skull = XMaterial.PLAYER_HEAD.parseItem();
              }
              SkullMeta meta = (SkullMeta) skull.getItemMeta();
              meta.setOwningPlayer(player);
              meta.setDisplayName(player.getName());
              meta.setLore(Collections.singletonList(""));
              skull.setItemMeta(meta);
              inventory.addItem(skull);
            }
          }
          for (Villager villager : arena.getVillagers()) {
            ItemStack villagerItem = new ItemStack(Material.EMERALD);
            this.setItemNameAndLore(villagerItem, villager.getCustomName(), new String[]{villager.getUniqueId().toString()});
            inventory.addItem(villagerItem);
          }
          e.getPlayer().openInventory(inventory);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }


  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    try {
      Player p = (Player) e.getWhoClicked();
      Arena arena = ArenaRegistry.getArena(p);
      if (arena == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()
              || !e.getCurrentItem().getItemMeta().hasDisplayName() || !e.getCurrentItem().getItemMeta().hasLore()) {
        return;
      }
      if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Teleporter.Game-Item-Menu-Name"))) {
        e.setCancelled(true);
        if ((e.isLeftClick() || e.isRightClick())) {
          if (e.getCurrentItem().getType() == Material.EMERALD) {
            for (Villager villager : arena.getVillagers()) {
              if (villager.getCustomName() == null) {
                villager.remove();
              }
              if (villager.getCustomName().equalsIgnoreCase(e.getCurrentItem().getItemMeta().getDisplayName()) && villager.getUniqueId().toString().equalsIgnoreCase(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0)))) {
                e.getWhoClicked().teleport(villager.getLocation());
                Utils.playSound(p.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
                p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30);
                p.sendMessage(ChatManager.colorMessage("Kits.Teleporter.Teleported-To-Villager"));
                return;
              }
            }
            p.sendMessage(ChatManager.colorMessage("Kits.Teleporter.Villager-Warning"));
          } else { /*if(e.getCurrentItem().getType() == Material.SKULL_ITEM || e.getCurrentItem().getType() == Material.SKULL)*/
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            for (Player player : arena.getPlayers()) {
              if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
                p.sendMessage(ChatManager.formatMessage(arena, ChatManager.colorMessage("Kits.Teleporter.Teleported-To-Player"), player));
                p.teleport(player);
                Utils.playSound(p.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
                p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30);
                p.closeInventory();
                e.setCancelled(true);
                return;
              }
            }
            p.sendMessage(ChatManager.colorMessage("Kits.Teleporter.Player-Not-Found"));
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
