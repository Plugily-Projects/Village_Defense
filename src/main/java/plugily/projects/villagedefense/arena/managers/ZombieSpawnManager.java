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
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ZombieSpawnManager {

  private final Random random;
  private final Arena arena;
  private int localIdleProcess = 0;
  private final List<Creature> glitchedZombies = new ArrayList<>();
  private final Map<Creature, Location> zombieCheckerLocations = new HashMap<>();

  public ZombieSpawnManager(Arena arena) {
    this.arena = arena;
    this.random = new Random();
  }

  public void applyIdle(int idle) {
    localIdleProcess = idle;
  }

  /**
   * Increments ZOMBIE_GLITCH_CHECKER value and attempts to check
   * whether any zombies are glitched on spawn point when
   * ZOMBIE_GLITCH_CHECKER value is higher or equal than 60
   * <p>
   * Glitch checker also clean ups dead zombies and villagers from the arena
   */
  public void spawnGlitchCheck() {
    arena.addOptionValue(ArenaOption.ZOMBIE_GLITCH_CHECKER, 1);
    if(arena.getOption(ArenaOption.ZOMBIE_GLITCH_CHECKER) >= 60) {
      Iterator<Villager> villagerIterator = arena.getVillagers().iterator();
      while(villagerIterator.hasNext()) {
        Villager villager = villagerIterator.next();
        if(villager.isDead()) {
          villagerIterator.remove();
          arena.removeVillager(villager);
        }
      }
      arena.setOptionValue(ArenaOption.ZOMBIE_GLITCH_CHECKER, 0);

      Iterator<Creature> zombieIterator = arena.getEnemies().iterator();
      while(zombieIterator.hasNext()) {
        Creature zombie = zombieIterator.next();
        if(zombie.isDead()) {
          zombieIterator.remove();
          arena.removeEnemy(zombie);
          continue;
        }
        if(glitchedZombies.contains(zombie) && zombie.getLocation().distance(zombieCheckerLocations.get(zombie)) <= 1) {
          zombieIterator.remove();
          arena.removeEnemy(zombie);
          zombieCheckerLocations.remove(zombie);
          zombie.remove();
        }

        Location checkerLoc = zombieCheckerLocations.get(zombie);
        if(checkerLoc == null) {
          zombieCheckerLocations.put(zombie, zombie.getLocation());
        } else if(zombie.getLocation().distance(checkerLoc) <= 1) {
          zombie.teleport(arena.getRandomZombieSpawn(random));
          zombieCheckerLocations.put(zombie, zombie.getLocation());
          glitchedZombies.add(zombie);
        }
      }
    }
  }

  public Map<Creature, Location> getZombieCheckerLocations() {
    return zombieCheckerLocations;
  }

  /**
   * Spawns some zombies in arena.
   * <p>
   * Variety and amount of zombies depends
   * on random value and current wave
   */
  public void spawnZombies() {
    if(checkForIdle()) {
      arena.getPlugin().getZombieSpawnerRegistry().spawnZombies(random, arena);
    }
  }

  private boolean checkForIdle() {
    //Idling to ~~save server stability~~ protect against hordes of zombies
    if(localIdleProcess > 0) {
      localIdleProcess--;
      return false;
    }

    applyIdle(arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS));
    //continue spawning
    return true;
  }

}
