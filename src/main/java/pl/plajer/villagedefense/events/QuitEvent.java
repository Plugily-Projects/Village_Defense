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

package pl.plajer.villagedefense.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.user.User;

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
    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArena(event.getPlayer()));
    }
  }

  @EventHandler
  public void onQuitSaveStats(PlayerQuitEvent event) {
    if (ArenaRegistry.getArena(event.getPlayer()) != null) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArena(event.getPlayer()));
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      plugin.getUserManager().saveStatistic(user, stat);
    }
    plugin.getUserManager().removeUser(user);

    plugin.getArgumentsRegistry().getSpyChat().disableSpyChat(event.getPlayer());
  }

}
