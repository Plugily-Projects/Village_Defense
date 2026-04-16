
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

package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XEntityType;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.01.2022
 */
public class CustomCreatureEvents implements Listener {

  private final Main plugin;

  public CustomCreatureEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }


  @EventHandler
  public void onCreatureDeathEvent(EntityDeathEvent event) {
    LivingEntity entity = event.getEntity();
    if(!(entity instanceof Creature)) {
      return;
    }
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(entity instanceof IronGolem || entity instanceof Wolf || entity instanceof Villager) {
        if(arena.getIronGolems().contains(entity) || arena.getWolves().contains(entity) || arena.getVillagers().contains(entity)) {
          Optional<CustomRideableCreature> customRideableCreatureOptional = plugin.getEnemySpawnerRegistry().getRideableCreatureByName(CustomRideableCreature.RideableType.valueOf(entity.getType().name().toUpperCase()));
          if(!customRideableCreatureOptional.isPresent()) {
            continue;
          }
          ItemStack itemStack = customRideableCreatureOptional.get().getDropItem();
          event.getDrops().add(itemStack);
        }
      } else if(arena.getEnemies().contains(entity)) {
        CustomCreature customCreature = arena.getCreatureTargetManager().getCustomCreatureFromCreature((Creature) entity);
        if(customCreature == null) {
          continue;
        }
        ItemStack itemStack = customCreature.getDropItem();
        event.getDrops().add(itemStack);
        if(customCreature.isExplodeTarget()) {
          event.getDrops().add(new ItemStack(XMaterial.ROTTEN_FLESH.get(), 5));
        }
        event.setDroppedExp(customCreature.getExpDrop());

        arena.removeEnemy((Creature) entity);
        arena.changeArenaOptionBy("TOTAL_KILLED_ZOMBIES", 1);

        Player killer = entity.getKiller();
        if(killer != null) {
          plugin.getUserManager().addStat(killer, plugin.getStatsStorage().getStatisticType("KILLS"));
          plugin.getUserManager().addExperience(killer, 2 * arena.getArenaOption("CREATURE_DIFFICULTY_MULTIPLIER"));
          plugin.getRewardsHandler().performReward(killer, plugin.getRewardsHandler().getRewardType("ZOMBIE_KILL"));
          plugin.getPowerupRegistry().spawnPowerup(entity.getLocation(), arena);
        }
      }
    }
  }


  @EventHandler
  public void onExplosiveHit(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Creature)) {
      return;
    }
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(!arena.isFighting()) {
        continue;
      }
      CustomCreature customCreature = getCustomCreatureFromCreature(arena, (Creature) event.getDamager());
      if(customCreature == null) {
        return;
      }
      if(!customCreature.isExplodeTarget()) {
        return;
      }
      CustomCreature.PriorityTarget priorityTarget = customCreature.getPriorityTarget();
      List<EntityType> entityTypes = new ArrayList<>();
      if(priorityTarget == CustomCreature.PriorityTarget.ANY) {
        for(CustomCreature.PriorityTarget priorityTargets : CustomCreature.PriorityTarget.values()) {
          if(priorityTargets == CustomCreature.PriorityTarget.ANY) {
            continue;
          }
          entityTypes.add(XEntityType.of(priorityTargets.toString()).get().get());
        }
      } else {
        entityTypes.add(XEntityType.of(priorityTarget.toString()).get().get());
      }
      if(entityTypes.contains(event.getEntity().getType())) {
        event.getDamager().getLocation().getWorld().spawnEntity(event.getDamager().getLocation(), XEntityType.TNT.get());
        event.getDamager().remove();
        // not working anymore since 1.21 changed event with DAMAGESOURCE!
        // Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) event.getDamager(), new ArrayList<>(Collections.singletonList(new ItemStack(Material.ROTTEN_FLESH))), 6));
      }
    }
  }

  public CustomCreature getCustomCreatureFromCreature(Arena arena, Creature creature) {
    List<MetadataValue> metadataValueList = creature.getMetadata("PlugilyProjects-VillageDefense-Name");
    if(metadataValueList.isEmpty()) {
      plugin.getDebugger().debug("Arena {0} Couldn't find creature meta data", arena.getId());
      return null;
    }
    for(MetadataValue metadataValue : metadataValueList) {
      if(plugin.getEnemySpawnerRegistry().getSpawnerByName(metadataValue.asString()).isPresent()) {
        EnemySpawner enemySpawner = plugin.getEnemySpawnerRegistry().getSpawnerByName(metadataValue.asString()).get();
        if(enemySpawner instanceof CustomCreature) {
          return (CustomCreature) enemySpawner;
        }
      }
    }
    plugin.getDebugger().debug("Arena {0} Couldn't find creature spawner", arena.getId());
    return null;
  }

}
