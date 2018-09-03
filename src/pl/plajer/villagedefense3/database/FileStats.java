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

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.plajerlair.core.utils.ConfigUtils;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

  private Main plugin;
  private FileConfiguration config;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "stats");
  }

  public void saveStat(Player player, StatsStorage.StatisticType stat) {
    User user = UserManager.getUser(player.getUniqueId());
    config.set(player.getUniqueId().toString() + "." + stat, user.getStat(stat));
    try {
      config.save(ConfigUtils.getFile(plugin, "stats"));
    } catch (IOException e) {
      e.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save stats.yml file!");
      Bukkit.getConsoleSender().sendMessage("Restart the server, file COULD BE OVERRIDDEN!");
    }
  }

  public void loadStat(Player player, StatsStorage.StatisticType stat) {
    User user = UserManager.getUser(player.getUniqueId());
    if (config.contains(player.getUniqueId().toString() + "." + stat)) {
      user.setStat(stat, config.getInt(player.getUniqueId().toString() + "." + stat));
    } else {
      user.setStat(stat, 0);
    }
  }

  public void loadStatsForPlayersOnline() {
    for (final Player player : plugin.getServer().getOnlinePlayers()) {
      if (plugin.isBungeeActivated()) {
        ArenaRegistry.getArenas().get(0).teleportToLobby(player);
      }
      if (!plugin.isDatabaseActivated()) {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          loadStat(player, s);
        }
        continue;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> MySQLConnectionUtils.loadPlayerStats(player));
    }
  }

}
