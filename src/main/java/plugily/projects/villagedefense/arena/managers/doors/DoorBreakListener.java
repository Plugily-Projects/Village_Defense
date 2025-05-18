/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2025 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tom on 14/08/2014.
 */
public class DoorBreakListener extends BukkitRunnable {

  private final Main plugin;

  public DoorBreakListener(Main plugin) {
    this.plugin = plugin;
    runTaskTimer(plugin, 1, 20);
  }

  @Override
  public void run() {
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(arena.getArenaState() != IArenaState.IN_GAME) {
        continue;
      }
      if(!arena.isFighting()) {
        continue;
      }
      World world = arena.getStartLocation().getWorld();
      List<LivingEntity> entities = world.getLivingEntities().stream().filter(CreatureUtils::isEnemy).collect(Collectors.toList());
      for(LivingEntity entity : entities) {
        for(Block block : plugin.getBukkitHelper().getNearbyBlocks(entity, 1)) {
          if(!MaterialUtils.isDoor(block.getType())) {
            continue;
          }

          Location blockLoc = block.getLocation();

          VersionUtils.sendParticles("SMOKE_LARGE", null, blockLoc, 5, 0.1, 0.1, 0.1);
          VersionUtils.playSound(blockLoc, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR");

          if(ThreadLocalRandom.current().nextInt(20) == 5) {
            VersionUtils.sendParticles("SMOKE_LARGE", null, blockLoc, 15, 0.1, 0.1, 0.1);
            VersionUtils.sendParticles("EXPLOSION_HUGE", null, blockLoc, 1, 0.1, 0.1, 0.1);


            Material doorMaterial = XMaterial.matchXMaterial(block.getType()).or(XMaterial.OAK_DOOR).get();
            Block b = block.getRelative(BlockFace.UP);

            if(b.getType() == block.getType()) {
              b.setType(Material.AIR);
            } else if((b = block.getRelative(BlockFace.DOWN)).getType() == block.getType()) {
              b.setType(Material.AIR);
            }

            block.setType(Material.AIR);
            arena.getMapRestorerManager().getDoorManager().removeDoor(block, doorMaterial);


            VersionUtils.playSound(blockLoc, "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
          }
        }
      }
    }
  }

}
