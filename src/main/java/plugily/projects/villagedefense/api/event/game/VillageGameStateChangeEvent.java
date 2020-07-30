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

package plugily.projects.villagedefense.api.event.game;

import org.bukkit.event.HandlerList;
import plugily.projects.villagedefense.api.event.VillageEvent;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaState;

/**
 * @author Plajer
 * @since 3.8.0
 * <p>
 * Called when arena game state has changed.
 */
public class VillageGameStateChangeEvent extends VillageEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final ArenaState arenaState;

  public VillageGameStateChangeEvent(Arena eventArena, ArenaState arenaState) {
    super(eventArena);
    this.arenaState = arenaState;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public ArenaState getArenaState() {
    return arenaState;
  }
}
