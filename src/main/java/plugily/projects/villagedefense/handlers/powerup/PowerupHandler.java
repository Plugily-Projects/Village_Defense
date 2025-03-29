
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

package plugily.projects.villagedefense.handlers.powerup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugily.projects.minigamesbox.api.events.player.PlugilyPlayerPowerupPickupEvent;
import plugily.projects.minigamesbox.api.handlers.powerup.BasePowerup;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 20.12.2021
 */
public class PowerupHandler implements Listener {

  private final Main plugin;

  public PowerupHandler(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPowerUpPickup(PlugilyPlayerPowerupPickupEvent event) {
    BasePowerup powerup = event.getPowerup();
    Arena arena = plugin.getArenaRegistry().getArena(event.getArena().getId());
    if(arena == null) {
      return;
    }
    switch(powerup.getKey().toLowerCase()) {
      case "map-clean":
        ArenaUtils.removeSpawnedEnemies(arena);
        break;
      case "golem-raid":
        for(int i = 0; i < (plugin.getPowerupRegistry().getLongestEffect(powerup) == 0 ? 3 : plugin.getPowerupRegistry().getLongestEffect(powerup)); i++) {
          arena.spawnGolem(arena.getStartLocation(), event.getPlayer());
        }
        break;
      default:
        break;
    }
  }

}
