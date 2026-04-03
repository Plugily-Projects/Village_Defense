/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena.managers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Wolf;
import plugily.projects.minigamesbox.classic.arena.managers.PluginMapRestorerManager;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XEntityType;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.doors.DoorManager;
import plugily.projects.villagedefense.arena.managers.doors.DoorManagerLegacy;
import plugily.projects.villagedefense.arena.managers.doors.IDoorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 14.02.2019
 */
public class MapRestorerManager extends PluginMapRestorerManager {

  public final Arena arena;

  IDoorManager doorManager;

  public MapRestorerManager(Arena arena) {
    super(arena);
    this.arena = arena;
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13)) {
      doorManager = new DoorManager(arena);
    } else {
      doorManager = new DoorManagerLegacy(arena);
    }
  }

  @Override
  public void fullyRestoreArena() {
    super.fullyRestoreArena();
    arena.setWave(1);
    arena.getSpawnedEntities().clear();
    arena.getDroppedFleshes().clear();

    doorManager.rebuildDoors();
    clearEnemiesFromArena();
    clearGolemsFromArena();
    clearVillagersFromArena();
    clearWolvesFromArena();
    clearDroppedEntities();
  }

  public final void clearEnemiesFromArena() {
    arena.getEnemySpawnManager().applyIdle(0);
    arena.getEnemies().forEach(Entity::remove);
    arena.getEnemies().clear();
  }

  public final void clearDroppedEntities() {
    for(Entity entity : arena.getPlugin().getBukkitHelper().getNearbyEntities(arena.getStartLocation(), 200)) {
      if(entity.getType() == XEntityType.EXPERIENCE_ORB.get() || entity.getType() == XEntityType.ITEM.get()) {
        entity.remove();
      }
    }
  }

  public final void clearGolemsFromArena() {
    List<IronGolem> ironGolems = new ArrayList<>(arena.getIronGolems());
    ironGolems.forEach(arena::removeIronGolem);
  }

  public final void clearVillagersFromArena() {
    arena.getVillagers().forEach(Entity::remove);
    arena.getVillagers().clear();
  }

  public final void clearWolvesFromArena() {
    List<Wolf> wolves = new ArrayList<>(arena.getWolves());
    wolves.forEach(arena::removeWolf);
  }

  public IDoorManager getDoorManager() {
    return doorManager;
  }
}
