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

package plugily.projects.villagedefense.arena.options;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public enum ArenaOption {
  /**
   * Current arena timer, ex. 30 seconds before game starts.
   */
  TIMER(0),
  /**
   * Minimum players in arena needed to start.
   */
  MINIMUM_PLAYERS(2),
  /**
   * Maximum players arena can hold, users with full games permission can bypass this!
   */
  MAXIMUM_PLAYERS(10),
  /**
   * Current arena wave.
   */
  WAVE(1),
  /**
   * Value for toggling boss bar message status.
   */
  BAR_TOGGLE_VALUE(0),
  /**
   * Current bonus hearts level based on rotten fleshes
   * donated by players to secret well
   */
  ROTTEN_FLESH_LEVEL(0),
  /**
   * Amount of rotten fleshes donated to secret well
   */
  ROTTEN_FLESH_AMOUNT(0),
  /**
   * Total amount of orbs (in game currency) spent by all players
   * in that arena in one game
   */
  TOTAL_ORBS_SPENT(0),
  /**
   * Total amount of zombies killed by all players
   * in that arena in one game
   */
  TOTAL_KILLED_ZOMBIES(0),
  /**
   * Amount of zombies that game still need to spawn before
   * ending current wave and start another
   */
  ZOMBIES_TO_SPAWN(0),
  /**
   * Value used to check all alive zombies if they weren't glitched on map
   * i.e. still stay near spawn position but cannot move.
   * <p>
   * Arena itself checks this value each time it reaches 60 (so each 60 seconds).
   */
  ZOMBIE_GLITCH_CHECKER(0),
  /**
   * Value that describes progress of zombies spawning in wave in arena.
   * <p>
   * It's counting up to 20 and resets to 0.
   * If value is equal 5 or 15 and wave is enough high special
   * zombie units will be spawned in addition to standard ones.
   */
  ZOMBIE_SPAWN_COUNTER(0),
  /**
   * Value describes how many seconds zombie spawn system should halt and not spawn any entity.
   * This value reduces server load and lag preventing spawning hordes at once.
   * Example when wave is 30 counter will set value to 2 halting zombies spawn for 2 seconds
   * Algorithm: floor(wave / 15)
   */
  ZOMBIE_IDLE_PROCESS(0),
  /**
   * Value that describes the multiplier of extra health zombies will receive.
   * Current health + multiplier.
   * <p>
   * Since 4.0.0 there is maximum amount of 750 to spawn in wave.
   * The more value will be above 750 the stronger zombies will be.
   * <p>
   * Zombies amount is based on algorithm: ceil((players * 0.5) * (wave * wave) / 2)
   * Difficulty multiplier is based on: ceil((ceil((players * 0.5) * (wave * wave) / 2) - 750) / 15)
   * Example: 12 players in wave 20 will receive 30 difficulty multiplier.
   * So each zombie will get 30 HP more, harder!
   */
  ZOMBIE_DIFFICULTY_MULTIPLIER(1);

  private final int defaultValue;

  ArenaOption(int defaultValue) {
    this.defaultValue = defaultValue;
  }

  public int getDefaultValue() {
    return defaultValue;
  }
}
