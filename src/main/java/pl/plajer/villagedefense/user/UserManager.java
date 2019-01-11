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

package pl.plajer.villagedefense.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.user.data.FileStats;
import pl.plajer.villagedefense.user.data.MySQLManager;
import pl.plajer.villagedefense.user.data.UserDatabase;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager implements UserDatabase {

  private static Map<UUID, User> users = new HashMap<>();
  private MySQLManager mySQLManager;
  private FileStats fileStats;
  private Main plugin;

  public UserManager(Main plugin) {
    this.plugin = plugin;
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      mySQLManager = new MySQLManager(plugin);
    } else {
      fileStats = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      User user = getUser(player.getUniqueId());
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        loadStatistic(user, stat);
      }
    }
  }

  public User getUser(UUID uuid) {
    if (users.containsKey(uuid)) {
      return users.get(uuid);
    } else {
      Debugger.debug(LogLevel.INFO, "Registering new user with UUID: " + uuid);
      users.put(uuid, new User(uuid));
      return users.get(uuid);
    }
  }

  public List<User> getUsers(Arena arena) {
    List<User> users = new ArrayList<>();
    for (Player player : arena.getPlayers()) {
      users.add(getUser(player.getUniqueId()));
    }
    return users;
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Player player = user.toPlayer();
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> mySQLManager.saveStatistic(user, player, stat));
      return;
    }
    fileStats.saveStatistic(user, stat);
  }

  @Override
  public void loadStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> mySQLManager.loadStatistic(user, stat));
      return;
    }
    fileStats.loadStatistic(user, stat);
  }

  public void removeUser(UUID uuid) {
    users.remove(uuid);
  }

}
