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

package plugily.projects.villagedefense.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.utils.constants.Constants;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class SpawnComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Arena arena = setupInventory.getArena();
    if(arena == null) {
      return;
    }
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
        + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Ending Location"))
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported after the game)")
        .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".Endlocation"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".Endlocation", serializedLocation);
      arena.setEndLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.LAPIS_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Lobby Location"))
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".lobbylocation"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".lobbylocation", serializedLocation);
      arena.setLobbyLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.EMERALD_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Starting Location"))
        .lore(ChatColor.GRAY + "Click to SET the starting location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(locations where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported when game starts)")
        .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".Startlocation"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".Startlocation", serializedLocation);
      arena.setStartLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aStarting location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 2, 0);
  }

}
