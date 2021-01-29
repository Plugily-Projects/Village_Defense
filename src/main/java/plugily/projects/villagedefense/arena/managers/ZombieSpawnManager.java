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

import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ZombieSpawnManager {

  private final Random random;
  private final Arena arena;
  private int localIdleProcess = 0;
  private final List<Zombie> glitchedZombies = new ArrayList<>();
  private final Map<Zombie, Location> zombieCheckerLocations = new HashMap<>();
  private final Main plugin;

  public ZombieSpawnManager(Arena arena) {
    this.arena = arena;
    this.random = new Random();
    plugin = arena.getPlugin();
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

      Iterator<Zombie> zombieIterator = arena.getZombies().iterator();
      while(zombieIterator.hasNext()) {
        Zombie zombie = zombieIterator.next();
        if(zombie.isDead()) {
          zombieIterator.remove();
          arena.removeZombie(zombie);
          continue;
        }
        if(glitchedZombies.contains(zombie) && zombie.getLocation().distance(zombieCheckerLocations.get(zombie)) <= 1) {
          zombieIterator.remove();
          arena.removeZombie(zombie);
          zombieCheckerLocations.remove(zombie);
          zombie.remove();
        }
        if(zombieCheckerLocations.get(zombie) == null) {
          zombieCheckerLocations.put(zombie, zombie.getLocation());
        } else {
          Location location = zombieCheckerLocations.get(zombie);

          if(zombie.getLocation().distance(location) <= 1) {
            zombie.teleport(arena.getZombieSpawns().get(random.nextInt(arena.getZombieSpawns().size() - 1)));
            zombieCheckerLocations.put(zombie, zombie.getLocation());
            glitchedZombies.add(zombie);
          }
        }
      }
    }
  }

  public Map<Zombie, Location> getZombieCheckerLocations() {
    return zombieCheckerLocations;
  }

  /**
   * Spawns some zombies in arena.
   * <p>
   * Variety and amount of zombies depends
   * on random value and current wave
   */
  public void spawnZombies() {
    if(!checkForIdle()) {
      return;
    }
    int wave = arena.getOption(ArenaOption.WAVE);
    int spawn = arena.getOption(ArenaOption.WAVE);
    if(plugin.getConfig().getInt("Zombies-Limit", 75) < wave) {
      spawn = (int) Math.ceil(plugin.getConfig().getInt("Zombies-Limit", 75) / 2.0);
    }

    if(arena.getZombies().isEmpty()) {
      for(int i = 0; i <= spawn; i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnFastZombie(random);
        }
      }
    }
    arena.addOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 1);
    if(arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 20) {
      arena.setOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 0);
    }
    if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) < 5 && arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
      arena.spawnFastZombie(random);
      return;
    }
    if(arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 5) {
      if(random.nextInt(3) != 2) {
        for(int i = 0; i <= spawn; i++) {
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            if(wave > 23) {
              if(random.nextInt(4) == 1) {
                arena.spawnVillagerSlayer(random);
              }
            } else if(wave > 20) {
              if(random.nextInt(3) == 1) {
                arena.spawnKnockbackResistantZombies(random);
              }
            } else if(wave > 14) {
              if(random.nextInt(2) == 1) {
                arena.spawnHardZombie(random);
              }
            } else if(wave > 7) {
              if(random.nextInt(2) == 1) {
                arena.spawnSoftHardZombie(random);
              }
            } else {
              arena.spawnFastZombie(random);
            }
          }
        }
      } else {
        for(int i = 0; i <= spawn; i++) {
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnPlayerBuster(random);
          }
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnGolemBuster(random);
          }
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnVillagerBuster(random);
          }
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnBabyZombie(random);
          }
        }
      }
    }
    if(arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 15 && wave > 4) {
      if(wave > 8) {
        for(int i = 0; i < (spawn - 7); i++) {
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnHardZombie(random);
          }
        }
      } else {
        for(int i = 0; i < (spawn - 3); i++) {
          if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
            arena.spawnSoftHardZombie(random);
          }
        }
      }
    }
    if(random.nextInt(8) == 0 && wave > 10) {
      for(int i = 0; i < (spawn - 8); i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnPlayerBuster(random);
        }
      }
    }
    if(random.nextInt(8) == 0 && wave > 7) {
      for(int i = 0; i < (spawn - 5); i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnHalfInvisibleZombie(random);
        }
      }
    }
    if(random.nextInt(8) == 0 && wave > 15) {
      for(int i = 0; i < (spawn - 13); i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnHalfInvisibleZombie(random);
        }
      }
    }
    if(random.nextInt(8) == 0 && wave > 23) {
      if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
        arena.spawnHalfInvisibleZombie(random);
      }
    }
    if(random.nextInt(8) == 0 && !arena.getIronGolems().isEmpty() && wave >= 6) {
      for(int i = 0; i < (spawn - 4); i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnGolemBuster(random);
        }
      }
    }
    if(random.nextInt(8) == 0 && !arena.getVillagers().isEmpty() && wave >= 15) {
      for(int i = 0; i < (spawn - 13); i++) {
        if(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN) > 0) {
          arena.spawnVillagerBuster(random);
        }
      }
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
