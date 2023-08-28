
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

package plugily.projects.villagedefense.arena.managers;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomCreature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 22.01.2022
 */
public class CreatureTargetManager {
  private final Arena arena;
  private final Main plugin;

  public CreatureTargetManager(Arena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
  }

  public void targetCreatures() {
    for(Creature creature : arena.getEnemies()) {
      LivingEntity creatureTarget = creature.getTarget();
      if(creatureTarget == null) {
        setTarget(creature);
        continue;
      }
      if(creatureTarget.getLocation().distance(creature.getLocation()) > 10 && creatureTarget instanceof Player) {
        setTarget(creature);
      }
    }
  }

  public void targetRideableCreatures() {
    List<Creature> creatures = new ArrayList<>();
    creatures.addAll(arena.getWolves());
    creatures.addAll(arena.getIronGolems());
    for(Creature creature : creatures) {
      if(arena.getEnemies().isEmpty() || !arena.isFighting()) {
        return;
      }
      LivingEntity creatureTarget = creature.getTarget();
      if(creatureTarget == null) {
        creature.setTarget(arena.getEnemies().get(arena.getEnemies().size() > 1 ? plugin.getRandom().nextInt(arena.getEnemies().size() - 1) : 0));
        continue;
      }
      if(creatureTarget instanceof Player) {
        creature.setTarget(arena.getEnemies().get(arena.getEnemies().size() > 1 ? plugin.getRandom().nextInt(arena.getEnemies().size() - 1) : 0));
      }
    }
  }

  private void setTarget(Creature creature) {
    LivingEntity nearestEntity = getNearestEntity(creature);
    if(nearestEntity == null) {
      return;
    }
    creature.setTarget(nearestEntity);
    plugin.getDebugger().debug("Arena {0} set Target {1} for Entity at Location {2}", arena.getId(), nearestEntity.getType(), creature.getLocation().toString());
  }

  public void unTargetCreature(Creature creature) {
    creature.setTarget(null);
  }

  public CustomCreature getCustomCreatureFromCreature(Creature creature) {
    List<MetadataValue> metadataValueList = creature.getMetadata("PlugilyProjects-VillageDefense-Name");
    if(metadataValueList.isEmpty()) {
      plugin.getDebugger().debug("Arena {0} Couldn't find creature meta data", arena.getId());
      return null;
    }
    for(MetadataValue metadataValue : metadataValueList) {
      Optional<EnemySpawner> spawnerByName = plugin.getEnemySpawnerRegistry().getSpawnerByName(metadataValue.asString());
      if(!spawnerByName.isPresent()) {
        continue;
      }
      EnemySpawner enemySpawner = spawnerByName.get();
      if(enemySpawner instanceof CustomCreature) {
        return (CustomCreature) enemySpawner;
      }
    }
    plugin.getDebugger().debug("Arena {0} Couldn't find creature spawner", arena.getId());
    return null;
  }

  public LivingEntity getNearestEntity(Creature creature) {

    CustomCreature customCreature = getCustomCreatureFromCreature(creature);
    if(customCreature == null) {
      plugin.getDebugger().debug("Arena {0} found no custom creature to set target", arena.getId());
      return null;
    }

    Location location = creature.getLocation();

    List<Entity> entities = new ArrayList<>();

    switch(customCreature.getPriorityTarget()) {
      case ANY:
      case VILLAGER:
        entities.addAll(arena.getVillagers());
        break;
      case IRON_GOLEM:
        entities.addAll(arena.getIronGolems());
        break;
      case WOLF:
        entities.addAll(arena.getWolves());
        break;
      case PLAYER:
        entities.addAll(arena.getPlayersLeft());
        break;
    }
    if(entities.isEmpty()) {
      entities.addAll(arena.getVillagers());
    }

    if(entities.isEmpty()) {
      plugin.getDebugger().debug("Arena {0} found no entity to target", arena.getId());
      return null;
    }

    Entity nearestEntity = entities.get(0);

    for(Entity entity : entities) {
      double distance = location.distance(entity.getLocation());
      if(distance < location.distance(nearestEntity.getLocation())) {
        nearestEntity = entity;
      }
    }
    plugin.getDebugger().debug("Arena {0} found at {1} the nearest villager for creature at {2} with distance of {3}", arena.getId(), nearestEntity.getLocation(), creature.getLocation(), location.distance(nearestEntity.getLocation()));
    return (LivingEntity) nearestEntity;
  }

  public void unTargetPlayerFromZombies(Player player, Arena arena) {
    for(Creature zombie : arena.getEnemies()) {
      LivingEntity target = zombie.getTarget();

      if(!player.equals(target)) {
        continue;
      }
      //set new target as villager so zombies won't stay still waiting for nothing
      zombie.setTarget(arena.getVillagers().get(plugin.getRandom().nextInt(arena.getVillagers().size() - 1)));
    }
  }

}
