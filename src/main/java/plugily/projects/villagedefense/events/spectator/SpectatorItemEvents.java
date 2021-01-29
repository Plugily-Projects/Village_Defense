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

package plugily.projects.villagedefense.events.spectator;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.number.NumberUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.utils.Utils;

import java.util.Collections;
import java.util.Set;

public class SpectatorItemEvents implements Listener {

  private final Main plugin;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, plugin.getChatManager().colorMessage(Messages.SPECTATOR_SETTINGS_MENU_INVENTORY_NAME),
        plugin.getChatManager().colorMessage(Messages.SPECTATOR_SETTINGS_MENU_SPEED_NAME));
  }

  @EventHandler
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.PLAYERS_LIST.getName())) {
      e.setCancelled(true);
      openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer(), arena);
    } else if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    } else if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_LEAVE_ITEM.getName())) {
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
    for(Player arenaPlayer : world.getPlayers()) {
      if(players.contains(arenaPlayer) && !plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = MiscUtils.setPlayerHead(arenaPlayer, meta);
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