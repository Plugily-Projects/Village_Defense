package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.Random;

/**
 * The interface for simple zombie spawner
 */
public interface SimpleZombieSpawner extends ZombieSpawner {
    /**
     * Get the minimum wave to spawn the zombies
     *
     * @return the wave
     */
    int getMinWave();

    /**
     * Get the maximum wave to spawn the zombies (stop spawning when exceeding this value)
     *
     * @return the wave
     */
    default int getMaxWave() {
        return -1;
    }

    /**
     * Can the zombies be applied some holiday effects?
     *
     * @return true if they can
     */
    default boolean canApplyHolidayEffect() {
        return false;
    }

    /**
     * How often the zombies will be spawned? Amount between 0.0 and 1.0
     *
     * @param arena       the arena
     * @param wave        the current wave
     * @param phase       the current phase
     * @param spawnAmount the raw amount that the arena suggests
     * @return the spawn rate in double
     */
    double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount);

    /**
     * Get the final amount of zombies to spawn, after some workaround
     *
     * @param arena       the arena
     * @param wave        the current wave
     * @param phase       the current phase
     * @param spawnAmount the raw amount that the arena suggests
     * @return the final amount
     */
    int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount);

    /**
     * Check if the zombies can be spawned on this phase
     *
     * @param arena       the arena
     * @param wave        the current wave
     * @param phase       the current phase
     * @param spawnAmount the raw amount that the arena suggests
     * @return true if they can
     */
    boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount);

    /**
     * Spawn the zombie at the location
     *
     * @param location the location
     * @return the spawned zombie
     */
    Zombie spawnZombie(Location location);

    /**
     * Get the weight of the zombie in the arena.
     * Basically mean this zombie is worth how many normal zombies in the arena.
     *
     * @return the weight of the zombie
     */
    default int getSpawnWeight() {
        return 1;
    }

    /**
     * Spawn the zombie at the location of the arena.
     *
     * @param location the location
     * @param arena    the arena
     */
    default void spawnZombie(Location location, Arena arena) {
        Zombie zombie = spawnZombie(location);
        CreatureUtils.applyAttributes(zombie, arena);
        if (canApplyHolidayEffect()) {
            arena.getPlugin().getHolidayManager().applyHolidayZombieEffects(zombie);
        }
        arena.getZombies().add(zombie);
    }

    @Override
    default void spawnZombie(Random random, Arena arena, int spawn) {
        int wave = arena.getWave();
        int phase = arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER);
        if (!checkPhase(arena, wave, phase, spawn)) {
            return;
        }
        int minWave = getMinWave();
        int maxWave = getMaxWave();
        if (wave < minWave || (maxWave > 0 && wave > maxWave)) {
            return;
        }
        int spawnAmount = getFinalAmount(arena, wave, phase, spawn);
        double spawnRate = getSpawnRate(arena, wave, phase, spawn);
        for (int i = 0; i < spawnAmount; i++) {
            int weight = getSpawnWeight();
            int zombiesToSpawn = arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN);
            if (zombiesToSpawn >= weight && spawnRate != 0 && (spawnRate == 1 || random.nextDouble() < spawnRate)) {
                Location location = arena.getRandomZombieSpawn(random);
                spawnZombie(location, arena);
                arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesToSpawn - weight);
            }
        }
    }
}
