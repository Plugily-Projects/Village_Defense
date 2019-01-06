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

package pl.plajer.villagedefense.events.spectator;

import java.util.Collections;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.spectator.SpectatorSettingsMenu;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

public class SpectatorItemEvents implements Listener {

  private Main plugin;
  private SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, plugin.getChatManager().colorMessage("In-Game.Spectator.Settings-Menu.Inventory-Name"),
        plugin.getChatManager().colorMessage("In-Game.Spectator.Settings-Menu.Speed-Name"));
  }

  @EventHandler
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    try {
      if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
      Arena arena = ArenaRegistry.getArena(e.getPlayer());
      ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
      if (arena == null || !Utils.isNamed(stack)) {
        return;
      }
      if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Item-Name"))) {
        e.setCancelled(true);
        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer());
      } else if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getChatManager().colorMessage("In-Game.Spectator.Settings-Menu.Item-Name"))) {
        e.setCancelled(true);
        spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  private void openSpectatorMenu(World world, Player p) {
    Inventory inventory = plugin.getServer().createInventory(null, MinigameUtils.serializeInt(ArenaRegistry.getArena(p).getPlayers().size()),
        plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    HashSet<Player> players = ArenaRegistry.getArena(p).getPlayers();
    for (Player player : world.getPlayers()) {
      if (players.contains(player) && !plugin.getUserManager().getUser(player.getUniqueId()).isSpectator()) {
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
        meta.setLore(Collections.singletonList(plugin.getChatManager().colorMessage("In-Game.Spectator.Target-Player-Health").replace("%health%",
            String.valueOf(MinigameUtils.round(player.getHealth(), 2)))));
        skull.setItemMeta(meta);
        inventory.addItem(skull);
      }
    }
    p.openInventory(inventory);
  }

  @EventHandler
  public void onSpectatorInventoryClick(InventoryClickEvent e) {
    try {
      Player p = (Player) e.getWhoClicked();
      if (ArenaRegistry.getArena(p) == null || !(e.isLeftClick() || e.isRightClick())) {
        return;
      }
      Arena arena = ArenaRegistry.getArena(p);
      if (!Utils.isNamed(e.getCurrentItem()) || !e.getCurrentItem().getItemMeta().hasLore()) {
        return;
      }
      if (e.getInventory().getName().equalsIgnoreCase(plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Menu-Name"))) {
        e.setCancelled(true);
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        for (Player player : arena.getPlayers()) {
          if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
            p.sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("Kits.Teleporter.Teleported-To-Player"), player));
            p.teleport(player);
            p.closeInventory();
            e.setCancelled(true);
            return;

          }
        }
        p.sendMessage(plugin.getChatManager().colorMessage("Kits.Teleporter.Player-Not-Found"));
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}