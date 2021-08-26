package plugily.projects.villagedefense.arena.managers.spawner;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.jetbrains.annotations.Nullable;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * The interface for simple enemy spawner
 */
public interface SimpleEnemySpawner extends EnemySpawner {
  /**
   * Get the minimum wave to spawn the enemies
   *
   * @return the wave
   */
  default int getMinWave() {
    return 1;
  }

  /**
   * Get the maximum wave to spawn the enemies (stop spawning when exceeding this value)
   *
   * @return the wave
   */
  default int getMaxWave() {
    return -1;
  }

  /**
   * Can the enemies be applied some holiday effects?
   *
   * @return true if they can
   */
  default boolean canApplyHolidayEffect() {
    return false;
  }

  /**
   * Can the enemies be applied arena attributes?
   *
   * @return true if they can
   */
  default boolean canApplyAttributes() {
    return true;
  }

  /**
   * How often the enemies will be spawned? Amount between 0.0 and 1.0
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return the spawn rate in double
   */
  double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount);

  /**
   * Get the final amount of enemies to spawn, after some workaround
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return the final amount
   */
  int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount);

  /**
   * Check if the enemies can be spawned on this phase
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return true if they can
   */
  boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount);

  /**
   * Spawn the enemy at the location
   *
   * @param location the location
   * @return the spawned enemy
   */
  @Nullable
  Creature spawn(Location location);

  /**
   * Get the weight of the enemy in the arena.
   * Basically mean this enemy is worth how many normal enemies in the arena.
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return the weight of the enemy
   */
  default int getSpawnWeight(Arena arena, int wave, int phase, int spawnAmount) {
    return 1;
  }

  /**
   * Spawn the enemy at the location of the arena.
   *
   * @param location the location
   * @param arena    the arena
   */
  default void spawn(Location location, Arena arena) {
    Creature creature = spawn(location);
    if (creature == null) {
      return;
    }
    if (canApplyAttributes()) {
      CreatureUtils.applyAttributes(creature, arena);
    }
    if (canApplyHolidayEffect()) {
      arena.getPlugin().getHolidayManager().applyHolidayCreatureEffects(creature);
    }
    arena.getEnemies().add(creature);
  }

  @Override
  default void spawn(Random random, Arena arena, int spawn) {
    int wave = arena.getWave();
    int phase = arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER);
    if (!checkPhase(arena, wave, phase, spawn)) {
      return;
    }

    int maxWave = getMaxWave();
    if (wave < getMinWave() || (maxWave > 0 && wave > maxWave)) {
      return;
    }
    int spawnAmount = getFinalAmount(arena, wave, phase, spawn);
    double spawnRate = getSpawnRate(arena, wave, phase, spawn);
    int weight = getSpawnWeight(arena, wave, phase, spawn);
    for (int i = 0; i < spawnAmount; i++) {
      int zombiesToSpawn = arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN);
      if (zombiesToSpawn >= weight && spawnRate != 0 && (spawnRate == 1 || random.nextDouble() < spawnRate)) {
        Location location = arena.getRandomZombieSpawn(random);
        spawn(location, arena);
        arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesToSpawn - weight);
      }
    }
  }
}
