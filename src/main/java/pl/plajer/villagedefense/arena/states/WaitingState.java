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

package pl.plajer.villagedefense.arena.states;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.handlers.language.Messages;

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public class WaitingState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      plugin.getServer().setWhitelist(false);
    }
    if (arena.getPlayers().size() < arena.getMinimumPlayers()) {
      if (arena.getTimer() <= 0) {
        arena.setTimer(15);
        plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_WAITING_FOR_PLAYERS), arena.getMinimumPlayers()));
        return;
      }
    } else {
      arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_WAITING_FOR_PLAYERS));
      plugin.getChatManager().broadcast(arena, Messages.LOBBY_MESSAGES_ENOUGH_PLAYERS_TO_START);
      arena.setArenaState(ArenaState.STARTING);
      arena.setTimer(plugin.getConfig().getInt("Starting-Waiting-Time", 60));
    }
    arena.setTimer(arena.getTimer() - 1);
  }

}
