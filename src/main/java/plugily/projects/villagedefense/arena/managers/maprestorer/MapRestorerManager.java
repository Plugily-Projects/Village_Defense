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

package plugily.projects.villagedefense.arena.managers.maprestorer;

import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Door;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 14.02.2019
 */
@SuppressWarnings("deprecation")
public class MapRestorerManager {

  protected final Map<Location, Byte> doorBlocks = new LinkedHashMap<>();
  public final Arena arena;

  public MapRestorerManager(Arena arena) {
    this.arena = arena;
  }

  public void addDoor(Location location, byte data) {
    doorBlocks.put(location, data);
  }

  public Map<Location, Byte> getGameDoorLocations() {
    return doorBlocks;
  }

  public void fullyRestoreArena() {
    restoreDoors();
    clearZombiesFromArena();
    clearGolemsFromArena();
    clearVillagersFromArena();
    clearWolvesFromArena();
    clearDroppedEntities();
  }

  public void clearZombiesFromArena() {
    arena.getZombies().forEach(org.bukkit.entity.Zombie::remove);
    arena.getZombies().clear();
  }

  public void clearDroppedEntities() {
    for(org.bukkit.entity.Entity entity : Utils.getNearbyEntities(arena.getStartLocation(), 200)) {
      if(entity.getType() == EntityType.EXPERIENCE_ORB || entity.getType() == EntityType.DROPPED_ITEM) {
        entity.remove();
      }
    }
  }

  public void clearGolemsFromArena() {
    arena.getIronGolems().forEach(org.bukkit.entity.IronGolem::remove);
    arena.getIronGolems().clear();
  }

  public void clearVillagersFromArena() {
    arena.getVillagers().forEach(org.bukkit.entity.Villager::remove);
    arena.getVillagers().clear();
  }

  public void clearWolvesFromArena() {
    arena.getWolves().forEach(org.bukkit.entity.Wolf::remove);
    arena.getWolves().clear();
  }

  public void restoreDoors() {
    int i = 0;
    for(Map.Entry<Location, Byte> entry : doorBlocks.entrySet()) {
      Block block = entry.getKey().getBlock();
      Byte doorData = entry.getValue();
      try {
        if(block.getType() != XMaterial.AIR.parseMaterial()) {
          i++;
          continue;
        }
        if(doorData == (byte) 8) {
          restoreTopHalfDoorPart(block);
          i++;
          continue;
        }
        restoreBottomHalfDoorPart(block, doorData);
        i++;
      } catch(Exception ex) {
        Debugger.debug(Level.WARNING, "Door has failed to load for arena {0} message {1} type {2} skipping!", arena.getId(), ex.getMessage(), ex.getCause());
      }
    }
    if(i != doorBlocks.size()) {
      Debugger.debug(Level.WARNING, "Failed to load doors for {0}! Expected {1} got {2}", arena.getId(), doorBlocks.size(), i);
    }
  }

  public void restoreTopHalfDoorPart(Block block) {
    block.setType(Utils.getCachedDoor(block));
    BlockState doorBlockState = block.getState();
    Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte((byte) 8));

    doorBlockData.setTopHalf(true);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());

    doorBlockState.setType(doorBlockData.getItemType());
    doorBlockState.setData(doorBlockData);

    doorBlockState.update(true);
  }

  public void restoreBottomHalfDoorPart(Block block, byte doorData) {
    block.setType(Utils.getCachedDoor(block));
    BlockState doorBlockState = block.getState();
    Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte(doorData));

    doorBlockData.setTopHalf(false);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());

    doorBlockState.setType(doorBlockData.getItemType());
    doorBlockState.setData(doorBlockData);

    doorBlockState.update(true);
  }

}
