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

package pl.plajer.villagedefense.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.plajer.villagedefense.api.event.VillageEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.handlers.powerup.Powerup;

/**
 * @author Plajer
 * @see pl.plajer.villagedefense.handlers.powerup.PowerupRegistry
 * @since 3.8.0
 * <p>
 * Called when player pick up a power-up.
 */
public class VillagePlayerPowerupPickupEvent extends VillageEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private final Powerup powerup;

  public VillagePlayerPowerupPickupEvent(Arena eventArena, Player player, Powerup powerup) {
    super(eventArena);
    this.player = player;
    this.powerup = powerup;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Player getPlayer() {
    return player;
  }

  public Powerup getPowerup() {
    return powerup;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
