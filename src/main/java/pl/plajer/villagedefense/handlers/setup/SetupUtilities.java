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

package pl.plajer.villagedefense.handlers.setup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class SetupUtilities {

  private FileConfiguration config;
  private Arena arena;

  SetupUtilities(FileConfiguration config, Arena arena) {
    this.config = config;
    this.arena = arena;
  }

  public String isOptionDone(String path) {
    if (config.isSet(path)) {
      return color("&a&l✔ Completed &7(value: &8" + config.getString(path) + "&7)");
    }
    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneSection(String path, int minimum) {
    if (config.isSet(path)) {
      if (config.getStringList(path).size() < minimum) {
        return color("&c&l✘ Not Completed | &cPlease add more locations");
      }
      return color("&a&l✔ Completed &7(value: &8" + config.getConfigurationSection(path).getKeys(false).size() + "&7)");
    }
    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneBool(String path) {
    if (config.isSet(path)) {
      if (Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(config.getString(path)))) {
        return color("&c&l✘ Not Completed");
      }
      return color("&a&l✔ Completed");
    }
    return color("&c&l✘ Not Completed");
  }

  public int getMinimumValueHigherThanZero(String path) {
    int amount = config.getInt("instances." + arena.getId() + "." + path);
    if (amount == 0) {
      amount = 1;
    }
    return amount;
  }

  private String color(String msg) {
    return ChatColor.translateAlternateColorCodes('&', msg);
  }

}
