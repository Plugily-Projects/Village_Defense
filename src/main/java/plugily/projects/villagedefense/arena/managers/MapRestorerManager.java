/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.entity.*;
import org.bukkit.material.Door;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.ServerVersion.Version;
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

  private final Map<Location, Byte> doorBlocks = new LinkedHashMap<>();
  private final Arena arena;

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
    arena.getZombies().forEach(Zombie::remove);
    arena.getZombies().clear();
  }

  public void clearDroppedEntities() {
    for (Entity entity : Utils.getNearbyEntities(arena.getStartLocation(), 200)) {
      if (entity.getType() == EntityType.EXPERIENCE_ORB || entity.getType() == EntityType.DROPPED_ITEM) {
        entity.remove();
      }
    }
  }

  public void clearGolemsFromArena() {
    arena.getIronGolems().forEach(IronGolem::remove);
    arena.getIronGolems().clear();
  }

  public void clearVillagersFromArena() {
    arena.getVillagers().forEach(Villager::remove);
    arena.getVillagers().clear();
  }

  public void clearWolvesFromArena() {
    arena.getWolves().forEach(Wolf::remove);
    arena.getWolves().clear();
  }

  private void restoreDoors() {
    int i = 0;
    for (Map.Entry<Location, Byte> entry : getGameDoorLocations().entrySet()) {
      Block block = entry.getKey().getBlock();
      Byte doorData = entry.getValue();
      if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
        Material mat = Material.getMaterial("WOODEN_DOOR");
        try {
          int id = (int) mat.getClass().getDeclaredMethod("getId").invoke(mat);
          Block.class.getDeclaredMethod("setTypeIdAndData", int.class, Byte.class, boolean.class)
            .invoke(block, id, doorData, false);
        } catch (Exception e) {
          e.printStackTrace();
        }

        i++;
      } else {
        //idk how does this work
        try {
          if (block.getType() != XMaterial.AIR.parseMaterial()) {
            i++;
            continue;
          }
          if (doorData == (byte) 8) {
            restoreTopHalfDoorPart(block);
            i++;
            continue;
          }
          restoreBottomHalfDoorPart(block, doorData);
          i++;
        } catch (Exception ex) {
          Debugger.debug(Level.WARNING, "Door has failed to load for arena {0} message {1} type {2} skipping!", arena.getId(), ex.getMessage(), ex.getCause());
        }
      }
    }
    if (i != getGameDoorLocations().size()) {
      Debugger.debug(Level.WARNING, "Failed to load doors for {0}! Expected {1} got {2}", arena.getId(), getGameDoorLocations().size(), i);
    }
  }

  private void restoreTopHalfDoorPart(Block block) {
    block.setType(XMaterial.OAK_DOOR.parseMaterial());
    BlockState doorBlockState = block.getState();
    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
      Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte((byte) 8));

      doorBlockData.setTopHalf(true);
      doorBlockData.setFacingDirection(doorBlockData.getFacing());

      doorBlockState.setType(doorBlockData.getItemType());
      doorBlockState.setData(doorBlockData);
    } else {
      org.bukkit.block.data.type.Door doorBlockData = (org.bukkit.block.data.type.Door) block.getBlockData();

      doorBlockData.setHalf(Half.TOP);
      doorBlockData.setFacing(doorBlockData.getFacing());

      doorBlockState.setType(doorBlockData.getMaterial());
      doorBlockState.setBlockData(doorBlockData);
    }
    doorBlockState.update(true);
  }

  private void restoreBottomHalfDoorPart(Block block, byte doorData) {
    block.setType(XMaterial.OAK_DOOR.parseMaterial());
    BlockState doorBlockState = block.getState();
    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
      Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte(doorData));

      doorBlockData.setTopHalf(false);
      doorBlockData.setFacingDirection(doorBlockData.getFacing());

      doorBlockState.setData(doorBlockData);
    } else {
      org.bukkit.block.data.type.Door doorBlockData = (org.bukkit.block.data.type.Door) block.getBlockData();

      doorBlockData.setHalf(Half.BOTTOM);
      doorBlockData.setFacing(doorBlockData.getFacing());

      doorBlockState.setType(doorBlockData.getMaterial());
      doorBlockState.setBlockData(doorBlockData);
    }
    doorBlockState.update(true);
  }

}
