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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.inventoryframework.gui.GuiItem;
import plugily.projects.inventoryframework.gui.type.ChestGui;
import plugily.projects.inventoryframework.pane.OutlinePane;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.utils.Utils;

import java.util.Collections;

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
  public void onSpecialItem(CBPlayerInteractEvent e) {
    if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    String key = plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName();
    if(key == null) {
      return;
    }
    if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.PLAYERS_LIST.getName())) {
      e.setCancelled(true);
      openSpectatorMenu(e.getPlayer(), arena);
    } else if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    }
  }

  private void openSpectatorMenu(Player player, Arena arena) {
    int rows = Utils.serializeInt(arena.getPlayers().size()) / 9;
    ChestGui gui = new ChestGui(rows, plugin.getChatManager().colorMessage(Messages.SPECTATOR_MENU_NAME));
    OutlinePane pane = new OutlinePane(9, rows);
    gui.addPane(pane);

    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack cloneSkull = skull.clone();
      SkullMeta meta = VersionUtils.setPlayerHead(arenaPlayer, (SkullMeta) cloneSkull.getItemMeta());
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(plugin.getChatManager().colorMessage(Messages.SPECTATOR_TARGET_PLAYER_HEALTH)
          .replace("%health%", Double.toString(NumberUtils.round(arenaPlayer.getHealth(), 2)))));
      cloneSkull.setItemMeta(meta);
      pane.addItem(new GuiItem(cloneSkull, e -> {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_PLAYER), arenaPlayer));
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().teleport(arenaPlayer);
      }));
    }
    gui.show(player);
  }
}
