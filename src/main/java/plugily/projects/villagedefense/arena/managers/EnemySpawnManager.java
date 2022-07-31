/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Villager;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.arena.Arena;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class EnemySpawnManager {

  private final Random random = new Random();
  private final Arena arena;
  private int localIdleProcess = 0;
  private final List<Creature> glitchedEnemies = new ArrayList<>();
  private final Map<Creature, Location> enemyCheckerLocations = new HashMap<>();

  public EnemySpawnManager(Arena arena) {
    this.arena = arena;
  }

  public void applyIdle(int idle) {
    localIdleProcess = idle;
  }

  /**
   * Increments ZOMBIE_GLITCH_CHECKER value and attempts to check
   * whether any enemies are glitched on spawn point when
   * ZOMBIE_GLITCH_CHECKER value is higher or equal than 60
   * <p>
   * Glitch checker also clean ups dead enemies and villagers from the arena
   */
  public void spawnGlitchCheck() {
    arena.changeArenaOptionBy("ZOMBIE_GLITCH_CHECKER", 1);
    if(arena.getArenaOption("ZOMBIE_GLITCH_CHECKER") >= 60) {
      Iterator<Villager> villagerIterator = arena.getVillagers().iterator();
      while(villagerIterator.hasNext()) {
        Villager villager = villagerIterator.next();
        if(villager.isDead()) {
          villagerIterator.remove();
          arena.removeVillager(villager);
        }
      }
      arena.setArenaOption("ZOMBIE_GLITCH_CHECKER", 0);

      Iterator<Creature> creatureIterator = arena.getEnemies().iterator();
      while(creatureIterator.hasNext()) {
        Creature creature = creatureIterator.next();
        if(creature.isDead()) {
          creatureIterator.remove();
          arena.removeEnemy(creature);
          continue;
        }
        if(glitchedEnemies.contains(creature) && creature.getLocation().distance(enemyCheckerLocations.get(creature)) <= 1) {
          creatureIterator.remove();
          arena.removeEnemy(creature);
          enemyCheckerLocations.remove(creature);
          creature.remove();
        }

        Location checkerLoc = enemyCheckerLocations.get(creature);
        if(checkerLoc == null) {
          enemyCheckerLocations.put(creature, creature.getLocation());
        } else if(creature.getLocation().distance(checkerLoc) <= 1) {
          VersionUtils.teleport(creature, arena.getRandomZombieSpawnLocation(random));
          enemyCheckerLocations.put(creature, creature.getLocation());
          glitchedEnemies.add(creature);
        }
      }
    }
  }

  public Map<Creature, Location> getEnemyCheckerLocations() {
    return enemyCheckerLocations;
  }

  /**
   * Spawns some enemies in arena.
   * <p>
   * Variety and amount of enemies depends
   * on random value and current wave
   */
  public void spawnEnemies() {
    if(checkForIdle()) {
      arena.getPlugin().getEnemySpawnerRegistry().spawnEnemies(random, arena);
    }
  }

  private boolean checkForIdle() {
    //Idling to ~~save server stability~~ protect against hordes of enemies
    if(localIdleProcess > 0) {
      localIdleProcess--;
      return false;
    }

    applyIdle(arena.getArenaOption("ZOMBIE_IDLE_PROCESS"));
    //continue spawning
    return true;
  }

}
