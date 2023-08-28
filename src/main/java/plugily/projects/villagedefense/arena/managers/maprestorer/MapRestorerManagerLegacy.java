/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.DoorBreakListener;
import plugily.projects.villagedefense.utils.Utils;

import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 13.01.2021
 */
@SuppressWarnings("deprecation")
public class MapRestorerManagerLegacy extends MapRestorerManager {

  public MapRestorerManagerLegacy(Arena arena) {
    super(arena);
  }

  @Override
  public void restoreDoors() {
    int i = 0;
    for(Location location : doorBlocks) {
      Block block = location.getBlock();
      if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_11_R1) && block.getType() != org.bukkit.Material.AIR) {
        Material mat = Utils.getCachedDoor(block);
        try {
          int id = (int) mat.getClass().getDeclaredMethod("getId").invoke(mat);
          block.getClass().getDeclaredMethod("setTypeIdAndData", int.class, byte.class, boolean.class)
            .invoke(block, id, (byte) 1, false);
        } catch(Exception e) {
          e.printStackTrace();
        }
        i++;
      } else {
        try {
          if(block.getType() != XMaterial.AIR.parseMaterial()) {
            i++;
            continue;
          }
          Block below = block.getLocation().subtract(0, 1, 0).getBlock();
          boolean isAirBelow = below.getType().equals(XMaterial.AIR.parseMaterial());
          if(isAirBelow) {
            restoreBottomHalfDoorPart(below);
            restoreTopHalfDoorPart(block);
          } else {
            Block above = block.getLocation().add(0, 1, 0).getBlock();
            restoreBottomHalfDoorPart(block);
            restoreTopHalfDoorPart(above);
          }
        } catch(Exception ex) {
          arena.getPlugin().getDebugger().debug(Level.WARNING, "Door has failed to load for arena {0} message {1} type {2} skipping!", arena.getId(), ex.getMessage(), ex.getCause());
        }
      }
    }
    if(i != doorBlocks.size()) {
      arena.getPlugin().getDebugger().debug(Level.WARNING, "Failed to load doors for {0}! Expected {1} got {2}", arena.getId(), doorBlocks.size(), i);
    }
  }

  @Override
  public void restoreTopHalfDoorPart(Block block) {
    block.setType(Utils.getCachedDoor(block));

    Door doorBlockData = null;
    try {
      doorBlockData = new Door(TreeSpecies.GENERIC, arena.getPlugin().getBukkitHelper().getFacingByByte((byte) 8));
    } catch(NoSuchMethodError e) {
      try {
        doorBlockData = Door.class.getDeclaredConstructor(Material.class, byte.class)
          .newInstance(XMaterial.OAK_DOOR, (byte) 8);
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }

    if(doorBlockData == null)
      return;

    BlockState doorBlockState = block.getState();

    doorBlockData.setTopHalf(true);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());
    doorBlockState.setType(doorBlockData.getItemType());
    doorBlockState.setData(doorBlockData);
    doorBlockState.update(true);

    block.removeMetadata(DoorBreakListener.CREATURE_DOOR_BULLDOZER_METADATA, arena.getPlugin());
  }

  @Override
  public void restoreBottomHalfDoorPart(Block block) {
    block.setType(Utils.getCachedDoor(block));

    Door doorBlockData = null;
    try {
      doorBlockData = new Door(TreeSpecies.GENERIC, arena.getPlugin().getBukkitHelper().getFacingByByte((byte) 1));
    } catch(NoSuchMethodError e) {
      try {
        doorBlockData = Door.class.getDeclaredConstructor(Material.class, byte.class)
          .newInstance(XMaterial.OAK_DOOR.parseMaterial(), (byte) 1);
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }

    if(doorBlockData == null)
      return;

    BlockState doorBlockState = block.getState();

    doorBlockData.setTopHalf(false);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());
    doorBlockState.setType(doorBlockData.getItemType());
    doorBlockState.setData(doorBlockData);
    doorBlockState.update(true);

    block.removeMetadata(DoorBreakListener.CREATURE_DOOR_BULLDOZER_METADATA, arena.getPlugin());
  }

}
