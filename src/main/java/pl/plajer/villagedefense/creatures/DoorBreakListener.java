/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.creatures;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

/**
 * Created by Tom on 14/08/2014.
 */
public class DoorBreakListener extends BukkitRunnable {

  private Random random = new Random();
  private Main plugin;

  public DoorBreakListener(Main plugin) {
    this.plugin = plugin;
    this.runTaskTimer(plugin, 1, 20);
  }

  @Override
  public void run() {
    for (World world : ArenaRegistry.getArenaIngameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        if (entity.getType() != EntityType.ZOMBIE) {
          continue;
        }
        for (Block block : Utils.getNearbyBlocks(entity, 1)) {
          if (!isDoor(block)) {
            continue;
          }
          block.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 5, 0.1, 0.1, 0.1);
          Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD", "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR");
          if (random.nextInt(20) == 5) {
            block.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 15, 0.1, 0.1, 0.1);
            block.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, block.getLocation(), 1, 0.1, 0.1, 0.1);
            if (block.getRelative(BlockFace.UP).getType() == CompatMaterialConstants.getOakDoorBlock()) {
              block.getRelative(BlockFace.UP).setType(Material.AIR);
            } else if (block.getRelative(BlockFace.DOWN).getType() == CompatMaterialConstants.getOakDoorBlock()) {
              block.getRelative(BlockFace.DOWN).setType(Material.AIR);
            }
            block.setType(Material.AIR);
            Utils.playSound(block.getLocation(), "ENTITY_ZOMBIE_BREAK_DOOR_WOOD", "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
          }
        }
      }
    }
  }

  private boolean isDoor(Block block) {
    if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
      return block.getType() == Material.WOODEN_DOOR || block.getType() == Material.WOOD_DOOR;
    } else {
      return block.getType() == XMaterial.OAK_DOOR.parseMaterial();
    }
  }

}
