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

package plugily.projects.villagedefense.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

public class Events1_9 implements Listener {

  @EventHandler
  public void onPickup(EntityPickupItemEvent e) {
    if(e.getEntityType() != EntityType.PLAYER) {
      return;
    }

    Arena arena = ArenaRegistry.getArena((Player) e.getEntity());
    if(arena == null) {
      return;
    }

    if(JavaPlugin.getPlugin(Main.class).getUserManager().getUser((Player) e.getEntity()).isSpectator()) {
      e.setCancelled(true);
    }

    arena.removeDroppedFlesh(e.getItem());
  }
}
