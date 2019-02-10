/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.handlers.powerup;

import org.bukkit.entity.Player;

import pl.plajer.villagedefense.arena.Arena;

/**
 * @author Plajer
 * <p>
 * Created at 10.02.2019
 */
public class PowerupPickupHandler {

  private Powerup powerup;
  private Arena arena;
  private Player player;

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
