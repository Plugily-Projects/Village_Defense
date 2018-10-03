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

package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.database.MySQLConnectionUtils;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.UpdateChecker;

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
      if (!plugin.isBungeeActivated() && !plugin.getServer().hasWhitelist()
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
      if (plugin.isBungeeActivated()) {
        return;
      }
      for (Player player : plugin.getServer().getOnlinePlayers()) {
        if (ArenaRegistry.getArena(player) == null) {
          continue;
        }
        player.hidePlayer(event.getPlayer());
        event.getPlayer().hidePlayer(player);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(final PlayerJoinEvent event) {
    try {
      //we want to be the first :)
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (event.getPlayer().hasPermission("villagedefense.updatenotify")) {
          if (plugin.getConfig().getBoolean("Update-Notifier.Enabled", true)) {
            String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("VillageDefense").getDescription().getVersion();
            try {
              boolean check = UpdateChecker.checkUpdate(plugin, currentVersion, 41869);
              if (check) {
                String latestVersion = "v" + UpdateChecker.getLatestVersion();
                if (latestVersion.contains("b")) {
                  event.getPlayer().sendMessage("");
                  event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
                  event.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                } else {
                  event.getPlayer().sendMessage("");
                  event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
                  event.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                }
              }
            } catch (Exception ex) {
              event.getPlayer().sendMessage(ChatColor.RED + "[VillageDefense] An error occured while checking for update!");
              event.getPlayer().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
              event.getPlayer().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
            }
          }
        }
      }, 25);
      if (plugin.isBungeeActivated() && ArenaRegistry.getArenas().size() >= 1) {
        ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
      }
      UserManager.registerUser(event.getPlayer().getUniqueId());
      if (!plugin.isDatabaseActivated()) {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          plugin.getFileStats().loadStat(event.getPlayer(), s);
        }
        return;
      }
      final Player player = event.getPlayer();
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> MySQLConnectionUtils.loadPlayerStats(player));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}
