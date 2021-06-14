package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The interface for simple zombie spawner
 */
public interface SimpleZombieSpawner extends ZombieSpawner {
    /**
     * The list of all phases in zombie spawners.
     * When using this with {@link #getSpawnPhases()}, the zombies will be spawned on every phases.
     */
    List<Integer> ALL_PHASES = IntStream.range(0, 20).boxed().collect(Collectors.toList());

    /**
     * Get the name of the spawner
     *
     * @return the name
     */
    String getName();

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
     * How often the zombies will be spawned? Amount between 0.0 and 1.0
     *
     * @return the spawn rate in double
     */
    double getSpawnRate();

    /**
     * Get the final amount of zombies to spawn, after some workaround
     *
     * @param arena       the arena
     * @param wave        the current wave
     * @param phase       the current phase
     * @param spawnAmount the raw amount that the arena suggests
     * @return the final amount
     */
    default int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        return spawnAmount;
    }

    /**
     * Get the phases where the zombies can be spawned
     *
     * @return the spawn phases
     */
    List<Integer> getSpawnPhases();

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
        arena.getZombies().add(zombie);
    }

    @Override
    default void spawnZombie(Random random, Arena arena, int spawn) {
        int wave = arena.getWave();
        int phase = arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER);
        if (!getSpawnPhases().contains(phase)) {
            return;
        }
        int minWave = getMinWave();
        int maxWave = getMaxWave();
        if (wave < minWave || (maxWave > 0 || wave > maxWave)) {
            return;
        }
        int spawnAmount = getFinalAmount(arena, wave, phase, spawn);
        for (int i = 0; i < spawnAmount; i++) {
            int weight = getSpawnWeight();
            int zombiesToSpawn = arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN);
            if (zombiesToSpawn >= weight && random.nextDouble() <= getSpawnRate()) {
                Location location = arena.getRandomZombieSpawn(random);
                spawnZombie(location, arena);
                arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesToSpawn - weight);
            }
        }
    }
}
