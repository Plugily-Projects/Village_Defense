/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import pl.plajer.villagedefense4.api.event.VillageEvent;
import pl.plajer.villagedefense4.arena.Arena;
import pl.plajer.villagedefense4.kits.kitapi.basekits.Kit;

/**
 * @author TomTheDeveloper, Plajer
 * @since 2.0.0
 * <p>
 * Called when player chose kit in game.
 */
public class VillagePlayerChooseKitEvent extends VillageEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final Kit kit;
  private boolean isCancelled;

  public VillagePlayerChooseKitEvent(Player player, Kit kit, Arena arena) {
    super(arena);
    this.player = player;
    this.kit = kit;
    this.isCancelled = false;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public Kit getKit() {
    return kit;
  }

  public boolean isCancelled() {
    return this.isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    this.isCancelled = cancelled;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
