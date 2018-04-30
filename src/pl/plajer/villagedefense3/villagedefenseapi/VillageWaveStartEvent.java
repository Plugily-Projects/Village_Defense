/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.arena.Arena;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when wave in arena has started.
 */
public class VillageWaveStartEvent extends VillageEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Integer waveNumber;
    private final Arena arena;

    public VillageWaveStartEvent(Arena arena, Integer waveNumber) {
        super(arena);
        this.arena = arena;
        this.waveNumber = waveNumber;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Integer getWaveNumber() {
        return waveNumber;
    }

    public Arena getArena() {
        return arena;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
