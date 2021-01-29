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

package plugily.projects.villagedefense.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.UpdateChecker;

/**
 * Created by Tom on 10/07/2015.
 */
public class JoinEvent implements Listener {

  private final Main plugin;

  public JoinEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    plugin.getUserManager().loadStatistics(plugin.getUserManager().getUser(event.getPlayer()));
    //load player inventory in case of server crash, file is deleted once loaded so if file was already
    //deleted player won't receive his backup, in case of crash he will get it back
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, event.getPlayer());
    }
  }

  @EventHandler
  public void onJoinCheckVersion(PlayerJoinEvent event) {
    //we want to be the first :)
    if(!plugin.getConfig().getBoolean("Update-Notifier.Enabled", true) || !event.getPlayer().hasPermission("villagedefense.updatenotify")) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> UpdateChecker.init(plugin, 41869).requestUpdateCheck().whenComplete((result, exception) -> {
      if(!result.requiresUpdate()) {
        return;
      }
      if(result.getNewestVersion().contains("b")) {
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
  }
}
