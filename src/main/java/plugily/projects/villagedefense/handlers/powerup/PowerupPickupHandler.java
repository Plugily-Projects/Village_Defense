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

package plugily.projects.villagedefense.handlers.powerup;

import org.bukkit.entity.Player;
import plugily.projects.villagedefense.arena.Arena;

/**
 * @author Plajer
 * <p>
 * Created at 10.02.2019
 */
public class PowerupPickupHandler {

    private final Powerup powerup;
    private final Arena arena;
    private final Player player;

    public PowerupPickupHandler(Powerup powerup, Arena arena, Player player) {
        this.powerup = powerup;
        this.arena = arena;
        this.player = player;
    }

    public Powerup getPowerup() {
        return powerup;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

}
