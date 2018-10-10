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

package pl.plajer.villagedefense.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * Created by Tom on 11/08/2014.
 */
public class QuitEvent implements Listener {

  private Main plugin;

  public QuitEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (ArenaRegistry.getArena(event.getPlayer()) == null) {
      return;
    }
    if (!plugin.isBungeeActivated()) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArena(event.getPlayer()));
    }
  }

  @EventHandler
  public void onQuitSaveStats(PlayerQuitEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) != null) {
        ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArena(event.getPlayer()));
      }
      final User user = UserManager.getUser(event.getPlayer().getUniqueId());
      final Player player = event.getPlayer();
      if (plugin.isDatabaseActivated()) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          for (final StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
            int i;
            try {
              i = plugin.getMySQLManager().getStat(player, s);
            } catch (NullPointerException npe) {
              i = 0;
              Main.debug(Main.LogLevel.ERROR, "Couldn't get stats from player " + player.getName());
              npe.printStackTrace();
              MessageUtils.errorOccurred();
              Bukkit.getConsoleSender().sendMessage("Cannot get stats from MySQL database!");
              Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
            }

            if (i > user.getStat(s)) {
              plugin.getMySQLManager().setStat(player, s, user.getStat(s) + i);
            } else {
              plugin.getMySQLManager().setStat(player, s, user.getStat(s));
            }
            plugin.getMySQLDatabase().executeUpdate("UPDATE playerstats SET name='" + player.getName() + "' WHERE UUID='" + player.getUniqueId().toString() + "';");
          }
        });
      } else {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          plugin.getFileStats().saveStat(player, s);
        }
      }
      UserManager.removeUser(player.getUniqueId());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
