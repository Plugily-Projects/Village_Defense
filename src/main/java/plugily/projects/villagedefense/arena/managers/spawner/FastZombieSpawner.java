package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class FastZombieSpawner implements SimpleZombieSpawner {
    @Override
    public String getName() {
        return "FAST_ZOMBIE";
    }

    @Override
    public int getMinWave() {
        return 1;
    }

    @Override
    public boolean canApplyHolidayEffect() {
        return true;
    }

    @Override
    public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        if (arena.getZombies().isEmpty() || spawnAmount < 5) {
            return 1;
        } else if (phase == 5 && wave <= 7) {
            return 2d / 3;
        } else {
            return 0;
        }
    }

    @Override
    public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        if (arena.getZombies().isEmpty()) {
            return spawnAmount;
        } else if (spawnAmount < 5) {
            return spawnAmount;
        } else if (phase == 5 && wave <= 7) {
            return spawnAmount;
        } else {
            return 0;
        }
    }

    @Override
    public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return arena.getZombies().isEmpty() || spawnAmount < 5 || (phase == 5 && wave <= 7);
    }

    @Override
    public Zombie spawnZombie(Location location) {
        return CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
    }
}
