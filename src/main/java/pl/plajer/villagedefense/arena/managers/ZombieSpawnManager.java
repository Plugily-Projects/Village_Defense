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

package pl.plajer.villagedefense.arena.managers;

import java.util.Random;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.options.ArenaOption;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ZombieSpawnManager {

  private Random random;
  private Arena arena;
  private int localIdleProcess = 0;

  public ZombieSpawnManager(Arena arena) {
    this.arena = arena;
    this.random = new Random();
  }

  public void applyIdle(int idle) {
    localIdleProcess = idle;
  }

  /**
   * Spawns some zombies in arena.
   * <p>
   * Variety and amount of zombies depends
   * on random value and current wave
   */
  public void spawnZombies() {
    if (!checkForIdle()) {
      return;
    }
    int wave = arena.getOption(ArenaOption.WAVE);
    int zombiesToSpawn = arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN);
    if (arena.getZombies() == null || arena.getZombies().size() <= 0) {
      for (int i = 0; i <= wave; i++) {
        if (zombiesToSpawn > 0) {
          arena.spawnFastZombie(random);
        }
      }
    }
    arena.addOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 1);
    if (arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 20) {
      arena.setOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 0);
    }
    if (zombiesToSpawn < 5 && zombiesToSpawn > 0) {
      arena.spawnFastZombie(random);
      return;
    }
    if (arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 5) {
      if (random.nextInt(3) != 2) {
        for (int i = 0; i <= wave; i++) {
          if (zombiesToSpawn > 0) {
            if (wave > 23) {
              if (random.nextInt(4) == 1) {
                arena.spawnVillagerSlayer(random);
              }
            } else if (wave > 20) {
              if (random.nextInt(3) == 1) {
                arena.spawnKnockbackResistantZombies(random);
              }
            } else if (wave > 14) {
              if (random.nextInt(2) == 1) {
                arena.spawnHardZombie(random);
              }
            }
            if (wave > 7) {
              if (random.nextInt(2) == 1) {
                arena.spawnSoftHardZombie(random);
              }
            } else {
              arena.spawnFastZombie(random);
            }
          }
        }
      } else {
        for (int i = 0; i <= wave; i++) {
          if (zombiesToSpawn > 0) {
            arena.spawnBabyZombie(random);
          }
        }
      }
    }
    if (arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 15 && wave > 4) {
      if (wave > 8) {
        for (int i = 0; i < (wave - 7); i++) {
          if (zombiesToSpawn > 0) {
            arena.spawnHardZombie(random);
          }
        }
      } else {
        for (int i = 0; i < (wave - 3); i++) {
          if (zombiesToSpawn > 0) {
            arena.spawnSoftHardZombie(random);
          }
        }
      }

    }

    if (random.nextInt(8) == 0 && wave > 10) {
      for (int i = 0; i < (wave - 8); i++) {
        if (zombiesToSpawn > 0) {
          arena.spawnPlayerBuster(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 7) {
      for (int i = 0; i < (wave - 5); i++) {
        if (zombiesToSpawn > 0) {
          arena.spawnHalfInvisibleZombie(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 15) {
      for (int i = 0; i < (wave - 13); i++) {
        if (zombiesToSpawn > 0) {
          arena.spawnHalfInvisibleZombie(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 23) {
      if (zombiesToSpawn > 0) {
        arena.spawnHalfInvisibleZombie(random);
      }
    }
    if (random.nextInt(8) == 0 && arena.getIronGolems().size() > 0 && wave >= 6) {
      for (int i = 0; i < (wave - 4); i++) {
        if (zombiesToSpawn > 0) {
          arena.spawnGolemBuster(random);
        }
      }
    }
  }

  private boolean checkForIdle() {
    //Idling to ~~save server stability~~ protect against hordes of zombies
    if (localIdleProcess > 0) {
      localIdleProcess--;
      return false;
    } else {
      applyIdle(arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS));
      //continue spawning
      return true;
    }
  }

}
