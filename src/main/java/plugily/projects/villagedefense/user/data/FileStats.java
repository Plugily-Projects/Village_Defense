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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import plugily.projects.commonsbox.database.MysqlDatabase;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.sorter.SortUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.LegacyDataFixer;
import plugily.projects.villagedefense.utils.constants.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats implements UserDatabase, Runnable {

  private final Main plugin;
  private final FileConfiguration config;
  private final BukkitTask updateTask;
  private final AtomicBoolean updateRequired = new AtomicBoolean(false);

  public FileStats(Main plugin) {
    this.plugin = plugin;
    new LegacyDataFixer(plugin);
    this.config = ConfigUtils.getConfig(plugin, Constants.Files.STATS.getName());
    this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 40, 40);
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    config.set(user.getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    updateRequired.set(true);
  }

  @Override
  public void saveAllStatistic(User user) {
    updateStats(user);
    updateRequired.set(true);
  }

  @Override
  public void loadStatistics(User user) {
    String uuid = user.getUniqueId().toString();
    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      user.setStat(stat, config.getInt(uuid + "." + stat.getName(), 0));
    }
  }

  @NotNull
  @Override
  public Map<UUID, Integer> getStats(StatsStorage.StatisticType stat) {
    Map<UUID, Integer> stats = new TreeMap<>();
    for(String string : config.getKeys(false)) {
      if(string.equals("data-version")) {
        continue;
      }
      try {
        stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
      } catch (IllegalArgumentException ex) {
        plugin.getLogger().log(Level.WARNING, "Cannot load the UUID for {0}", string);
      }
    }
    return SortUtils.sortByValue(stats);
  }

  @Override
  public void disable() {
    for(Player player : plugin.getServer().getOnlinePlayers()) {
      updateStats(plugin.getUserManager().getUser(player));
    }
    updateTask.cancel();
    // Save the last time before disabling
    run();
  }

  @Override
  public MysqlDatabase getMySQLDatabase() {
    return null;
  }

  @Override
  public String getPlayerName(UUID uuid) {
    return Bukkit.getOfflinePlayer(uuid).getName();
  }

  private void updateStats(User user) {
    String uuid = user.getUniqueId().toString();

    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if(!stat.isPersistent()) {
        continue;
      }
      String path = user.getUniqueId().toString() + "." + stat.getName();
      int value = user.getStat(stat);
      if (value > 0 || config.contains(path)) {
        config.set(path, value);
      }
    }
  }

  // Save the config to the file
  @Override
  public void run() {
    if (updateRequired.get()) {
      ConfigUtils.saveConfig(plugin, config, Constants.Files.STATS.getName());
      updateRequired.set(false);
    }
  }
}
