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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.user.User;

/**
 * Created by Tom on 11/08/2014.
 */
public class QuitEvent implements Listener {

  private final Main plugin;

  public QuitEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuitSaveStats(PlayerQuitEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena != null) {
      ArenaManager.leaveAttempt(event.getPlayer(), arena);
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    plugin.getUserManager().saveAllStatistic(user);
    plugin.getUserManager().removeUser(user);

    plugin.getArgumentsRegistry().getSpyChat().disableSpyChat(event.getPlayer());
  }

}
