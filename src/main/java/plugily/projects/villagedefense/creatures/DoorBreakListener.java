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

package plugily.projects.villagedefense.creatures;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tom on 14/08/2014.
 */
public class DoorBreakListener extends BukkitRunnable {

  public DoorBreakListener(Main plugin) {
    runTaskTimer(plugin, 1, 20);
  }

  @Override
  public void run() {
    for(World world : ArenaRegistry.getArenaIngameWorlds()) {
      for(LivingEntity entity : world.getLivingEntities()) {
        if(entity.getType() != EntityType.ZOMBIE) {
          continue;
        }
        for(Block block : Utils.getNearbyBlocks(entity, 1)) {
          if(block.getType() != Utils.getCachedDoor(block)) {
            continue;
          }
          block.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 5, 0.1, 0.1, 0.1);
          Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD", "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR");
          if(ThreadLocalRandom.current().nextInt(20) == 5) {
            block.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 15, 0.1, 0.1, 0.1);
            block.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, block.getLocation(), 1, 0.1, 0.1, 0.1);
            if(block.getRelative(BlockFace.UP).getType() == Utils.getCachedDoor(block)) {
              block.getRelative(BlockFace.UP).setType(Material.AIR);
            } else if(block.getRelative(BlockFace.DOWN).getType() == Utils.getCachedDoor(block)) {
              block.getRelative(BlockFace.DOWN).setType(Material.AIR);
            }
            block.setType(Material.AIR);
            Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_BREAK_DOOR_WOOD", "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
          }
        }
      }
    }
  }

}
