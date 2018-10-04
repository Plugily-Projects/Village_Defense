/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

/**
 * @author Plajer
 * <p>
 * Created at 18 lis 2017
 */
public class MySQLConnectionUtils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static void loadPlayerStats(Player player) {
    boolean b = false;
    MySQLManager database = plugin.getMySQLManager();
    ResultSet resultSet = plugin.getMySQLDatabase().executeQuery("SELECT UUID from playerstats WHERE UUID='" + player.getUniqueId().toString() + "'");
    try {
      if (!resultSet.next()) {
        database.insertPlayer(player);
        b = true;
      }
      User user = UserManager.getUser(player.getUniqueId());
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        user.setStat(stat, database.getStat(player, stat));
      }
      b = true;
    } catch (SQLException e1) {
      Main.debug(Main.LogLevel.ERROR, "Connection failed for player " + player.getName());
      e1.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
    if (!b) {
      try {
        if (!resultSet.next()) {
          database.insertPlayer(player);
        }
        User user = UserManager.getUser(player.getUniqueId());
        for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
          user.setStat(stat, database.getStat(player, stat));
        }
      } catch (SQLException e1) {
        Main.debug(Main.LogLevel.ERROR, "Connection called twice for player " + player.getName());
        e1.printStackTrace();
        MessageUtils.errorOccured();
        Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
        Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    }
  }

}
