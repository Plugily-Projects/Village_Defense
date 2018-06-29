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

package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.handlers.PowerupManager;

/**
 * @author Plajer
 * @since 3.8.0
 * <p>
 * Called when player pick up a power-up.
 */
public class VillagePowerupPickEvent extends VillageEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PowerupManager.PowerupType powerupType;

    public VillagePowerupPickEvent(Arena eventArena, Player player, PowerupManager.PowerupType powerupType) {
        super(eventArena);
        this.player = player;
        this.powerupType = powerupType;
    }

    public Player getPlayer() {
        return player;
    }

    public PowerupManager.PowerupType getPowerupType() {
        return powerupType;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
