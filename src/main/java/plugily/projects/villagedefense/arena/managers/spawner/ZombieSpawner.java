package plugily.projects.villagedefense.arena.managers.spawner;

import java.util.Random;
import plugily.projects.villagedefense.arena.Arena;

/**
 * The interface for zombie spawner
 */
public interface ZombieSpawner {
  /**
   * Get the name of the spawner
   *
   * @return the name
   */
  String getName();

  /**
   * Handle the zombie spawn
   *
   * @param random the random number generator
   * @param arena  the arena the manager is trying to spawn zombies
   * @param spawn  the amount to spawn
   */
  void spawnZombie(Random random, Arena arena, int spawn);
}