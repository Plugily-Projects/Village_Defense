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

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 28.09.2018
 */
public class MysqlManager implements UserDatabase {

  private Main plugin;
  private MysqlDatabase database;

  public MysqlManager(Main plugin) {
    this.plugin = plugin;
    database = plugin.getMysqlDatabase();
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try (Connection connection = database.getConnection();
           Statement statement = connection.createStatement()) {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `"+getTableName()+"` (\n"
            + "  `UUID` char(36) NOT NULL PRIMARY KEY,\n"
            + "  `name` varchar(32) NOT NULL,\n"
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
          statement.executeUpdate("ALTER TABLE "+getTableName()+" ADD `name` text NOT NULL");
        } catch (MySQLSyntaxErrorException e) {
          if (!e.getMessage().contains("Duplicate column name")) {
            plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
        MessageUtils.errorOccurred();
        Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
        Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    });
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        database.executeUpdate("UPDATE "+getTableName()+" " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistics(User user) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      String uuid = user.getPlayer().getUniqueId().toString();
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * from "+getTableName()+" WHERE UUID='" + uuid + "'");
        if (rs.next()) {
          //player already exists - get the stats
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) {
              continue;
            }
            int val = rs.getInt(stat.getName());
            user.setStat(stat, val);
          }
        } else {
          //player doesn't exist - make a new record
          statement.executeUpdate("INSERT INTO "+getTableName()+" (UUID,name) VALUES ('" + uuid + "','" + user.getPlayer().getName() + "')");
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) {
              continue;
            }
            user.setStat(stat, 0);
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
      }
    });
  }

  public String getTableName() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "mysql");
    return config.getString("table", "playerstats");
  }


  public MysqlDatabase getDatabase() {
    return database;
  }
}
