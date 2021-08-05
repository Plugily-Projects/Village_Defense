package plugily.projects.villagedefense.arena.managers.spawner;

import java.util.Random;
import plugily.projects.villagedefense.arena.Arena;

/**
 * The interface for enemy spawner
 */
public interface EnemySpawner extends Comparable<EnemySpawner> {
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
   * Handle the spawn
   *
   * @param random the random number generator
   * @param arena  the arena the manager is trying to spawn enemies
   * @param spawn  the amount to spawn
   */
  void spawn(Random random, Arena arena, int spawn);

  @Override
  default int compareTo(EnemySpawner spawner) {
    int compareValue = Integer.compare(getPriority(), spawner.getPriority());
    if (compareValue == 0) {
      return getName().compareTo(spawner.getName());
    }
    return compareValue;
  }
}