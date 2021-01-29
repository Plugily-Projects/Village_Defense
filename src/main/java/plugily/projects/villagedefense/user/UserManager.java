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

package plugily.projects.villagedefense.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.data.FileStats;
import plugily.projects.villagedefense.user.data.MysqlManager;
import plugily.projects.villagedefense.user.data.UserDatabase;
import plugily.projects.villagedefense.utils.Debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

  private final UserDatabase database;
  private final List<User> users = new ArrayList<>();
  private static Main plugin;

  public UserManager(Main main) {
    plugin = main;
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      database = new MysqlManager(plugin);
    } else {
      database = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    Bukkit.getServer().getOnlinePlayers().stream().map(this::getUser).forEach(this::loadStatistics);
  }

  public User getUser(Player player) {
    for(User user : users) {
      if(user.getPlayer().equals(player)) {
        return user;
      }
    }
    Debugger.debug("Registering new user {0} ({1})", player.getUniqueId(), player.getName());
    User user = new User(player);
    users.add(user);
    return user;
  }

  public List<User> getUsers(Arena arena) {
    return arena.getPlayers().stream().map(this::getUser).collect(Collectors.toList());
  }

  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if(stat.isPersistent()) {
      database.saveStatistic(user, stat);
    }
  }

  public void addExperience(Player player, int i) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(StatsStorage.StatisticType.XP, i);
    if(player.hasPermission(PermissionsManager.getVip())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if(player.hasPermission(PermissionsManager.getMvp())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if(player.hasPermission(PermissionsManager.getElite())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  public void addStat(Player player, StatsStorage.StatisticType stat) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(stat, 1);
    updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  public void updateLevelStat(Player player, Arena arena) {
    User user = plugin.getUserManager().getUser(player);
    if(Math.pow(50.0 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5) < user.getStat(StatsStorage.StatisticType.XP)) {
      user.addStat(StatsStorage.StatisticType.LEVEL, 1);
      //Arena can be null when player has left the arena before this message the arean is retrieved.
      if(arena != null)
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.YOU_LEVELED_UP), user.getStat(StatsStorage.StatisticType.LEVEL)));
    }
  }

  public void saveAllStatistic(User user) {
    database.saveAllStatistic(user);
  }

  public void loadStatistics(User user) {
    database.loadStatistics(user);
  }

  public void removeUser(User user) {
    users.remove(user);
  }

  public UserDatabase getDatabase() {
    return database;
  }

}
