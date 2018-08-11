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

package pl.plajer.villagedefense3.events;

import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajerlair.core.services.ReportedException;

/**
 * Created by TomVerschueren on 6/02/2018.
 */
public class CombustDayLightEvent implements Listener {
  //class used to stop zombies from burning in daylight

  private Main plugin;

  public CombustDayLightEvent(Main main) {
    this.plugin = main;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Triggers when something combusts in the world.
   * Thanks to @HomieDion for part of this class!
   */
  @EventHandler(ignoreCancelled = true)
  public void onCombust(final EntityCombustEvent e) {
    try {
      // Ignore if this is caused by an event lower down the chain.
      if (e instanceof EntityCombustByEntityEvent || e instanceof EntityCombustByBlockEvent
              || !(e.getEntity() instanceof Zombie)
              || e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL) {
        return;
      }

      for (Arena arena : ArenaRegistry.getArenas()) {
        if (arena.getZombies().contains(e.getEntity())) {
          e.setCancelled(true);
          return;
        }
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }
}
