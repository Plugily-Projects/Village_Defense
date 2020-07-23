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

package pl.plajer.villagedefense.user.data;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.constants.Constants;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats implements UserDatabase {

  private Main plugin;
  private FileConfiguration config;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, Constants.Files.STATS.getName());
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
  }

  @Override
  public void saveAllStatistic(User user) {
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if (!stat.isPersistent()) {
        continue;
      }
      config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    }
    ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
  }

  @Override
  public void loadStatistics(User user) {
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      user.setStat(stat, config.getInt(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), 0));
    }
  }
}
