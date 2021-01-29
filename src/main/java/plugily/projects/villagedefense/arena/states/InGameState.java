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

package plugily.projects.villagedefense.arena.states;

import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.language.Messages;

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public class InGameState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    bossBarUpdate(arena);
    arena.getZombieSpawnManager().spawnGlitchCheck();
    if(arena.getVillagers().isEmpty() || arena.getPlayersLeft().isEmpty() && arena.getArenaState() != ArenaState.ENDING) {
      ArenaManager.stopGame(false, arena);
      return;
    }
    if(arena.isFighting()) {
      if(arena.getZombiesLeft() <= 0) {
        arena.setFighting(false);
        ArenaManager.endWave(arena);
      }
      if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
        arena.getZombieSpawnManager().spawnZombies();
        arena.setTimer(500);
      } else if(arena.getTimer() == 0) {
        arena.getMapRestorerManager().clearZombiesFromArena();
        if(arena.getZombiesLeft() > 0) {
          plugin.getChatManager().broadcast(arena, Messages.ZOMBIE_GOT_STUCK_IN_THE_MAP);
        }
        arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
      }
      if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) < 0) {
        arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
      }
      arena.setTimer(arena.getTimer() - 1);
    } else {
      if(arena.getTimer() <= 0) {
        arena.setFighting(true);
        ArenaManager.startWave(arena);
      }
    }
    arena.setTimer(arena.getTimer() - 1);
  }

  private void bossBarUpdate(Arena arena) {
    if(arena.getOption(ArenaOption.BAR_TOGGLE_VALUE) > 5) {
      arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_IN_GAME_WAVE).replace("%wave%", String.valueOf(arena.getWave())));
      arena.addOptionValue(ArenaOption.BAR_TOGGLE_VALUE, 1);
      if(arena.getOption(ArenaOption.BAR_TOGGLE_VALUE) > 10) {
        arena.setOptionValue(ArenaOption.BAR_TOGGLE_VALUE, 0);
      }
    } else {
      arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_IN_GAME_INFO).replace("%wave%", String.valueOf(arena.getWave())));
      arena.addOptionValue(ArenaOption.BAR_TOGGLE_VALUE, 1);
    }
  }

}
