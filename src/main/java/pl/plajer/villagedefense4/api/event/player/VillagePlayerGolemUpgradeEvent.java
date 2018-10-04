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

package pl.plajer.villagedefense4.api.event.player;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.plajer.villagedefense4.api.event.VillageEvent;
import pl.plajer.villagedefense4.arena.Arena;

/**
 * @author Plajer
 * @since 3.8.0
 * <p>
 * Called when player upgrades a golem.
 */
public class VillagePlayerGolemUpgradeEvent extends VillageEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private IronGolem ironGolem;
  private Player player;
  private double originalHealth;

  public VillagePlayerGolemUpgradeEvent(Arena eventArena, IronGolem ironGolem, Player player, double originalHealth) {
    super(eventArena);
    this.ironGolem = ironGolem;
    this.player = player;
    this.originalHealth = originalHealth;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public IronGolem getIronGolem() {
    return ironGolem;
  }

  public Player getPlayer() {
    return player;
  }

  /**
   * Golem health before upgrade
   *
   * @return original golem health
   */
  public double getOriginalHealth() {
    return originalHealth;
  }
}
