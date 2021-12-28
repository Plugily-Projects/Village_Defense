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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.setup.components.PluginArenaRegisterComponent;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.villagedefense.arena.Arena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 17.12.2021
 */
public class ArenaRegisterComponent extends PluginArenaRegisterComponent {

  @Override
  public boolean addAdditionalArenaValidateValues(InventoryClickEvent event, PluginArena arena, PluginMain plugin, FileConfiguration config) {
    for(String s : new String[]{"zombiespawns", "villagerspawns"}) {
      org.bukkit.configuration.ConfigurationSection spawnSection = config.getConfigurationSection("instances." + arena.getId() + "." + s);

      if(spawnSection == null || spawnSection.getKeys(false).size() < 2) {
        event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (must be minimum 2 spawns)"));
        return false;
      }
    }

    if(config.getConfigurationSection("instances." + arena.getId() + ".doors") == null) {
      event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure doors properly!"));
      return false;
    }

    return true;
  }

  @Override
  public void addAdditionalArenaSetValues(PluginArena arena, FileConfiguration config) {
    Arena pluginArena = (Arena) getSetupInventory().getPlugin().getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns").getKeys(false)) {
      pluginArena.addZombieSpawn(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".zombiespawns." + string)));
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns").getKeys(false)) {
      pluginArena.addVillagerSpawn(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".villagerspawns." + string)));
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".doors").getKeys(false)) {
      String path = "instances." + arena.getId() + ".doors." + string + ".";
      pluginArena.getMapRestorerManager().addDoor(LocationSerializer.getLocation(config.getString(path + "location")),
          (byte) config.getInt(path + "byte"));
    }
  }

}
