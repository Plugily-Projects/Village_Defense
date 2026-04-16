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

package plugily.projects.villagedefense.arena.managers.doors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 18.05.2025
 */
public class DoorManager implements IDoorManager {
  private Arena arena;

  public DoorManager(Arena arena) {
    this.arena = arena;
  }


  private Map<Location, Material> doorBlocks = new HashMap<>();

  @Override
  public void rebuildDoors() {
    for(Location location : doorBlocks.keySet()) {
      Block block = location.getBlock();
      try {
        if(block.getType() != XMaterial.AIR.parseMaterial()) {
          continue;
        }
        Block relative = block.getRelative(BlockFace.DOWN).getLocation().getBlock();
        boolean isAirBelow = relative.getType().equals(XMaterial.AIR.parseMaterial());
        boolean relativeTopHalf = false;
        if(!isAirBelow) {
          relative = block.getRelative(BlockFace.UP).getLocation().getBlock();
          relativeTopHalf = true;
        }
        if(relativeTopHalf) {
          restoreDoor(relative, block, doorBlocks.get(location));
        } else {
          restoreDoor(block, relative, doorBlocks.get(location));
        }
      } catch(Exception ignored) {
        ignored.printStackTrace();
      }
    }
    doorBlocks.clear();
  }

  @Override
  public void removeDoor(Block block, Material material) {
    doorBlocks.put(block.getLocation(), material);
  }

  @Override
  public List<Location> getDoorLocations() {
    return new ArrayList<>(doorBlocks.keySet());
  }

  private void restoreDoor(Block top, Block bottom, Material material) {
    top.setType(material, false);
    bottom.setType(material, false);

    org.bukkit.block.data.type.Door d2 = (org.bukkit.block.data.type.Door) top.getBlockData();
    org.bukkit.block.data.type.Door d1 = (org.bukkit.block.data.type.Door) bottom.getBlockData();

    d2.setHalf(org.bukkit.block.data.Bisected.Half.TOP);
    d1.setHalf(org.bukkit.block.data.Bisected.Half.BOTTOM);

    d2.setFacing(top.getRelative(BlockFace.WEST).getType() == Material.AIR ? BlockFace.EAST : BlockFace.NORTH);
    d1.setFacing(top.getRelative(BlockFace.WEST).getType() == Material.AIR ? BlockFace.EAST : BlockFace.NORTH);

    top.setBlockData(d2);
    bottom.setBlockData(d1);
  }


}
