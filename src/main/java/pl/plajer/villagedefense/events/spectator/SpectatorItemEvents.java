/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.number.NumberUtils;

public class SpectatorItemEvents implements Listener {

  private Main plugin;
  private SpectatorSettingsMenu spectatorSettingsMenu;
  private boolean usesPaperSpigot = Bukkit.getServer().getVersion().contains("Paper");

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, plugin.getChatManager().colorMessage(Messages.SPECTATOR_SETTINGS_MENU_INVENTORY_NAME),
        plugin.getChatManager().colorMessage(Messages.SPECTATOR_SETTINGS_MENU_SPEED_NAME));
  }

  @EventHandler
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if (plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.PLAYERS_LIST.getName())) {
      e.setCancelled(true);
      openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer(), arena);
    } else if (plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    } else if (plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_LEAVE_ITEM.getName())) {
      e.setCancelled(true);
      ArenaManager.leaveAttempt(e.getPlayer(), arena);
    }
  }

  private void openSpectatorMenu(World world, Player player, Arena arena) {
    int rows = Utils.serializeInt(arena.getPlayers().size()) / 9;
    Gui gui = new Gui(plugin, rows, plugin.getChatManager().colorMessage(Messages.SPECTATOR_MENU_NAME));
    OutlinePane pane = new OutlinePane(9, rows);
    gui.addPane(pane);

    Set<Player> players = arena.getPlayers();
    for (Player arenaPlayer : world.getPlayers()) {
      if (players.contains(arenaPlayer) && !plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        ItemStack skull = CompatMaterialConstants.getPlayerHeadItem();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (usesPaperSpigot && player.getPlayerProfile().hasTextures()) {
          meta.setPlayerProfile(player.getPlayerProfile());
        } else {
          meta.setOwningPlayer(player);
        }
        meta.setDisplayName(arenaPlayer.getName());
        meta.setLore(Collections.singletonList(plugin.getChatManager().colorMessage(Messages.SPECTATOR_TARGET_PLAYER_HEALTH)
            .replace("%health%", String.valueOf(NumberUtils.round(arenaPlayer.getHealth(), 2)))));
        skull.setItemMeta(meta);
        //todo check why panes are not working! Also bb issue!
        pane.addItem(new GuiItem(skull, e -> {
          e.setCancelled(true);
          e.getWhoClicked().sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_PLAYER), arenaPlayer));
          e.getWhoClicked().closeInventory();
          e.getWhoClicked().teleport(arenaPlayer);
        }));
      }
    }
    gui.show(player);
  }
}