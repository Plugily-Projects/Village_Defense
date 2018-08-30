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

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

public class MySQLDatabase {

  private MySQLConnectionManager manager;
  private JavaPlugin plugin;

  public MySQLDatabase(JavaPlugin javaPlugin) {
    this.plugin = javaPlugin;
    this.manager = new MySQLConnectionManager(plugin);
    Main.debug(Main.LogLevel.INFO, "Configuring MySQL connection");
    manager.configureConnPool();

    try {
      Connection connection = manager.getConnection();
      if (connection == null) {
        Main.debug(Main.LogLevel.ERROR, "Failed to connect to database");
        return;
      }
      connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `playerstats` (\n"
              + "  `UUID` text NOT NULL,\n"
              + "  `name` text NOT NULL,\n"
              + "  `kills` int(11) NOT NULL DEFAULT '0',\n"
              + "  `deaths` int(11) NOT NULL DEFAULT '0',\n"
              + "  `highestwave` int(11) NOT NULL DEFAULT '0',\n"
              + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
              + "  `level` int(11) NOT NULL DEFAULT '0',\n"
              + "  `xp` int(11) NOT NULL DEFAULT '0',\n"
              + "  `orbs` int(11) NOT NULL DEFAULT '0'\n"
              + ");");

      //temporary workaround
      try {
        connection.createStatement().executeUpdate("ALTER TABLE playerstats ADD `name` text NOT NULL");
      } catch (MySQLSyntaxErrorException e) {
        if (!e.getMessage().contains("Duplicate column name")) {
          e.printStackTrace();
        }
      }
      manager.closeConnection(connection);
    } catch (SQLException e) {
      e.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
    // Table exists
  }


  public void executeUpdate(String query) {
    try {
      Connection connection = manager.getConnection();
      Statement statement = connection.createStatement();
      statement.executeUpdate(query);
      manager.closeConnection(connection);
    } catch (SQLException e) {
      plugin.getLogger().warning("Failed to execute update: " + query);
    }

  }

  public ResultSet executeQuery(String query) {
    try {
      Connection connection = manager.getConnection();
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      manager.closeConnection(connection);
      return rs;
    } catch (SQLException exception) {
      exception.printStackTrace();
      plugin.getLogger().warning("Failed to execute request: " + query);
      return null;
    }
  }

  public void insertPlayer(Player player) {
    executeUpdate("INSERT INTO playerstats (UUID,name,xp) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "',0)");
  }

  public void closeDatabase() {
    manager.shutdownConnPool();
  }

  public void addStat(Player player, StatsStorage.StatisticType stat, int amount) {
    executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + stat + "+" + amount + " WHERE UUID='" + player.getUniqueId().toString() + "'");
  }

  public void setStat(Player player, StatsStorage.StatisticType stat, int number) {
    executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + number + " WHERE UUID='" + player.getUniqueId().toString() + "';");
  }

  public int getStat(Player player, StatsStorage.StatisticType stat) {
    ResultSet set = executeQuery("SELECT " + stat.getName() + " FROM playerstats WHERE UUID='" + player.getUniqueId().toString() + "'");
    try {
      if (!set.next()) {
        return 0;
      }
      return (set.getInt(1));
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public Map<UUID, Integer> getColumn(String player) {
    ResultSet set = executeQuery("SELECT UUID, " + player + " FROM playerstats ORDER BY " + player + " ASC;");
    Map<UUID, Integer> column = new LinkedHashMap<>();
    try {
      while (set.next()) {
        column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(player));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return column;
  }

}