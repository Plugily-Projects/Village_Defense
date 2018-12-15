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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.services.update.UpdateChecker;

/**
 * Created by Tom on 10/07/2015.
 */
public class JoinEvent implements Listener {

  private Main plugin;

  public JoinEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent e) {
    try {
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && !plugin.getServer().hasWhitelist()
          || e.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) {
        return;
      }
      if (e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames())) {
        e.setResult(PlayerLoginEvent.Result.ALLOWED);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    try {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        if (ArenaRegistry.getArenas().size() >= 1) {
          ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
        }
        return;
      }
      for (Player player : plugin.getServer().getOnlinePlayers()) {
        if (ArenaRegistry.getArena(player) == null) {
          continue;
        }
        player.hidePlayer(event.getPlayer());
        event.getPlayer().hidePlayer(player);
      }

      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        plugin.getUserManager().loadStatistic(plugin.getUserManager().getUser(event.getPlayer().getUniqueId()), stat);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(final PlayerJoinEvent event) {
    try {
      //we want to be the first :)
      if (!(plugin.getConfig().getBoolean("Update-Notifier.Enabled", true) || event.getPlayer().hasPermission("villagedefense.updatenotify"))) {
        return;
      }
      Bukkit.getScheduler().runTaskLater(plugin, () -> UpdateChecker.init(plugin, 41869).requestUpdateCheck().whenComplete((result, exception) -> {
        if (!result.requiresUpdate()) {
          return;
        }
        if (result.getNewestVersion().contains("b")) {
          event.getPlayer().sendMessage("");
          event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
          event.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
          event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
        } else {
          event.getPlayer().sendMessage("");
          event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
          event.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
          event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
        }
      }), 25);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}
