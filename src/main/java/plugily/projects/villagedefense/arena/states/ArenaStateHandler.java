/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public interface ArenaStateHandler {

  /**
   * Initiate class with injecting the main plugin class
   *
   * @param plugin class to inject
   */
  void init(Main plugin);

  /**
   * Handle call for the current arena state for arena.
   * Basically when arena state is IN_GAME, the in game
   * state will be handled and called via that method.
   *
   * @param arena arena to call state update for
   */
  void handleCall(Arena arena);

}
