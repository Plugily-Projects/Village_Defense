package plugily.projects.villagedefense.arena.managers.spawner;

import java.util.Random;
import plugily.projects.villagedefense.arena.Arena;

/**
 * The interface for zombie spawner
 */
public interface ZombieSpawner extends Comparable<ZombieSpawner> {
  /**
   * Get the name of the spawner
   *
   * @return the name
   */
  String getName();

  /**
   * Get the priority of the spawner (the higher the sooner)
   */
  default int getPriority() {
    return 0;
  }

  /**
   * Handle the zombie spawn
   *
   * @param random the random number generator
   * @param arena  the arena the manager is trying to spawn zombies
   * @param spawn  the amount to spawn
   */
  void spawnZombie(Random random, Arena arena, int spawn);

  @Override
  default int compareTo(ZombieSpawner spawner) {
    return Integer.compare(this.getPriority(), spawner.getPriority());
  }
}