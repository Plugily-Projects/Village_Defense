/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.arena.managers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.material.Door;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 14.02.2019
 */
public class MapRestorerManager {

  private Map<Location, Byte> doorBlocks = new LinkedHashMap<>();
  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private Arena arena;

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
  }

  public void clearZombiesFromArena() {
    for (Zombie zombie : arena.getZombies()) {
      zombie.remove();
    }
    arena.getZombies().clear();
  }

  public void clearGolemsFromArena() {
    for (IronGolem ironGolem : arena.getIronGolems()) {
      ironGolem.remove();
    }
    arena.getIronGolems().clear();
  }

  public void clearVillagersFromArena() {
    for (Villager villager : arena.getVillagers()) {
      villager.remove();
    }
    arena.getVillagers().clear();
  }

  public void clearWolvesFromArena() {
    for (Wolf wolf : arena.getWolfs()) {
      wolf.remove();
    }
    arena.getWolfs().clear();
  }

  private void restoreDoors() {
    int i = 0;
    for (Map.Entry<Location, Byte> entry : getGameDoorLocations().entrySet()) {
      Block block = entry.getKey().getBlock();
      Byte doorData = entry.getValue();
      if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
        int id = Material.WOODEN_DOOR.getId();
        block.setTypeIdAndData(id, doorData, false);
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
          Debugger.debug(LogLevel.WARN, "Door has failed to load for arena " + arena.getID() + ", skipping!");
        }
      }
    }
    if (i != getGameDoorLocations().size()) {
      Debugger.debug(LogLevel.WARN, "Some doors has failed to load for arena " + arena.getID() + "! Expected "
          + getGameDoorLocations().size() + " but loaded only " + i + "!");
    }
  }

  private void restoreTopHalfDoorPart(Block block) {
    block.setType(XMaterial.OAK_DOOR.parseMaterial());
    BlockState doorBlockState = block.getState();
    Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte((byte) 8));

    doorBlockData.setTopHalf(true);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());

    doorBlockState.setType(doorBlockData.getItemType());
    doorBlockState.setData(doorBlockData);
    doorBlockState.update(true);
  }

  private void restoreBottomHalfDoorPart(Block block, byte doorData) {
    block.setType(XMaterial.OAK_DOOR.parseMaterial());
    BlockState doorBlockState = block.getState();
    Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte(doorData));

    doorBlockData.setTopHalf(false);
    doorBlockData.setFacingDirection(doorBlockData.getFacing());

    doorBlockState.setData(doorBlockData);
    doorBlockState.update(true);
  }

}
