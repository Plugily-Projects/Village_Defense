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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajerlair.commonsbox.database.MysqlDatabase;

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
    try (Statement statement = database.getConnection().createStatement()) {
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS `playerstats` (\n"
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
        statement.executeUpdate("ALTER TABLE playerstats ADD `name` text NOT NULL");
      } catch (MySQLSyntaxErrorException e) {
        if (!e.getMessage().contains("Duplicate column name")) {
          plugin.getLogger().log(Level.WARNING, "Failed altering table 'name'! Cause: " + e.getSQLState() + " (" + e.getErrorCode() + ")");
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: " + e.getSQLState() + " (" + e.getErrorCode() + ")");
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
  }

  public void insertPlayer(Player player) {
    database.executeUpdate("INSERT INTO playerstats (UUID,name,xp) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "',0)");
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      ResultSet resultSet = database.executeQuery("SELECT UUID from playerstats WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "'");
      //insert into the database
      try {
        if (!resultSet.next()) {
          insertPlayer(user.getPlayer());
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "MySQL connection failed for player " + user.getPlayer().getName() + "! Cause: " + e.getSQLState() + " (" + e.getErrorCode() + ")");
      }

      ResultSet set = database.executeQuery("SELECT " + stat.getName() + " FROM playerstats WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "'");
      try {
        if (!set.next()) {
          user.setStat(stat, 0);
          return;
        }
        user.setStat(stat, set.getInt(1));
      } catch (SQLException e) {
        e.printStackTrace();
        user.setStat(stat, 0);
      }
    });
  }

}
