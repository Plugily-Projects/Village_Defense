package plugily.projects.villagedefense.arena.managers;

import plugily.projects.villagedefense.arena.Arena;

import java.util.Random;

public interface CustomZombieSpawnManager {
    /**
     * Handle the zombie spawn
     *
     * @param random the random number generator
     * @param arena the arena the manager is trying to spawn zombies
     * @param spawn the amount to spawn
     */
    void spawnZombie(Random random, Arena arena, int spawn);
}