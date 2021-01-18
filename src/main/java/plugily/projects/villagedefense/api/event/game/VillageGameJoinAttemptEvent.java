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

package plugily.projects.villagedefense.api.event.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.villagedefense.api.event.VillageEvent;
import plugily.projects.villagedefense.arena.Arena;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when player is attempting to join arena.
 */
public class VillageGameJoinAttemptEvent extends VillageEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private boolean isCancelled;

  public VillageGameJoinAttemptEvent(Player player, Arena targetArena) {
    super(targetArena);
    this.player = player;
    this.isCancelled = false;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

}
