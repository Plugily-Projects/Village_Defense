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

package plugily.projects.villagedefense.user.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.sorter.SortUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.constants.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats implements UserDatabase {

  private final Main plugin;
  private final FileConfiguration config;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, Constants.Files.STATS.getName());
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    config.set(user.getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
  }

  @Override
  public void saveAllStatistic(User user) {
    updateStats(user);
    ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
  }

  @Override
  public void loadStatistics(User user) {
    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      user.setStat(stat, config.getInt(user.getUniqueId().toString() + "." + stat.getName(), 0));
    }
  }

  @Override
  public Map<UUID, Integer> getStats(StatsStorage.StatisticType stat) {
    Map<UUID, Integer> stats = new TreeMap<>();
    for(String string : config.getKeys(false)) {
      if(string.equals("data-version")) {
        continue;
      }
      stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
    }
    return SortUtils.sortByValue(stats);
  }

  @Override
  public void saveAll() {
    for(Player player : plugin.getServer().getOnlinePlayers()) {
      User user = plugin.getUserManager().getUser(player);
      updateStats(user);
    }
    ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
  }

  private void updateStats(User user) {
    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if(!stat.isPersistent()) {
        continue;
      }
      config.set(user.getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    }
  }
}
