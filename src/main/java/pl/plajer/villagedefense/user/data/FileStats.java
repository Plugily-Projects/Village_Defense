/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajerlair.core.utils.ConfigUtils;

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
    User user = plugin.getUserManager().getUser(player.getUniqueId());
    config.set(player.getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    try {
      config.save(ConfigUtils.getFile(plugin, "stats"));
    } catch (IOException e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Cannot save stats.yml file!");
      Bukkit.getConsoleSender().sendMessage("Restart the server, file COULD BE OVERRIDDEN!");
    }
  }

  public void loadStat(Player player, StatsStorage.StatisticType stat) {
    User user = plugin.getUserManager().getUser(player.getUniqueId());
    if (config.contains(player.getUniqueId().toString() + "." + stat.getName())) {
      user.setStat(stat, config.getInt(player.getUniqueId().toString() + "." + stat.getName()));
    } else {
      user.setStat(stat, 0);
    }
  }

  public void loadStatsForPlayersOnline() {
    for (final Player player : plugin.getServer().getOnlinePlayers()) {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        ArenaRegistry.getArenas().get(0).teleportToLobby(player);
      }
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          loadStat(player, s);
        }
        continue;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> MySQLConnectionUtils.loadPlayerStats(player));
    }
  }

}
