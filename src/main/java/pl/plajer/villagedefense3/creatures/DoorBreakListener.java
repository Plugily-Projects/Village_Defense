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

package pl.plajer.villagedefense3.creatures;

import java.util.Queue;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.Utils;
import pl.plajerlair.core.services.ReportedException;

/**
 * Created by Tom on 14/08/2014.
 */
public class DoorBreakListener extends BukkitRunnable {

  private Random random = new Random();
  private Main plugin = JavaPlugin.getPlugin(Main.class);

  @Override
  public void run() {
    try {
      for (World world : Bukkit.getServer().getWorlds()) {
        for (Entity entity : world.getEntities()) {
          if (!(entity.getType() == EntityType.ZOMBIE)) {
            continue;
          }
          Queue<Block> blocks = Utils.getLineOfSight((LivingEntity) entity, null, 1, 1);
          for (Block block : blocks) {
            if (block.getType() == Material.WOOD_DOOR || block.getType() == Material.WOODEN_DOOR) {
              block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
              Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD", "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR");
              this.particleDoor(block);
              if (random.nextInt(20) == 5) {
                breakDoor(block);
                Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_BREAK_DOOR_WOOD", "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
              }
            }
          }
        }
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  private void particleDoor(Block block) {
    for (BlockFace blockFace : BlockFace.values()) {
      if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
      }
    }
  }

  private void breakDoor(Block block) {
    for (BlockFace blockFace : BlockFace.values()) {
      if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
        block.setType(Material.AIR);
      }
    }
  }

}
