
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.api.event.game;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.events.PlugilyEvent;
import plugily.projects.villagedefense.arena.Arena;

/**
 * Called when the player drops the item to the secret well
 */
public class VillageGameSecretWellEvent extends PlugilyEvent implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private final ItemStack item;
  private final Location location;

  public VillageGameSecretWellEvent(Arena arena, ItemStack item, Location location) {
    super(arena);
    this.item = item;
    this.location = location;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  public ItemStack getItem() {
    return item;
  }

  public Location getLocation() {
    return location;
  }
}
