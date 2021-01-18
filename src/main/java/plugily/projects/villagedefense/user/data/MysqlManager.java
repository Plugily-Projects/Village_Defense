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

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.MessageUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 28.09.2018
 */
public class MysqlManager implements UserDatabase {

  private final Main plugin;
  private final MysqlDatabase database;

  public MysqlManager(Main plugin) {
    this.plugin = plugin;
    database = plugin.getMysqlDatabase();
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try (Connection connection = database.getConnection();
           Statement statement = connection.createStatement()) {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (\n"
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
          statement.executeUpdate("ALTER TABLE " + getTableName() + " ADD `name` text NOT NULL");
        } catch (MySQLSyntaxErrorException e) {
          if (!e.getMessage().contains("Duplicate column name")) {
            plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
        MessageUtils.errorOccurred();
        Debugger.sendConsoleMsg("Cannot save contents to MySQL database!");
        Debugger.sendConsoleMsg("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    });
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        database.executeUpdate("UPDATE " + getTableName() + " SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void saveAllStatistic(User user) {
    StringBuilder update = new StringBuilder(" SET ");
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if (!stat.isPersistent()) {
        continue;
      }
      if (update.toString().equalsIgnoreCase(" SET ")){
        update.append(stat.getName()).append('=').append(user.getStat(stat));
      }
      update.append(", ").append(stat.getName()).append('=').append(user.getStat(stat));
    }
    String finalUpdate = update.toString();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            database.executeUpdate("UPDATE "+getTableName()+ finalUpdate + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistics(User user) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      String uuid = user.getPlayer().getUniqueId().toString();
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        database.executeUpdate("UPDATE " + getTableName() + " SET " + "name" + "=" + user.getPlayer().getName() + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
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
