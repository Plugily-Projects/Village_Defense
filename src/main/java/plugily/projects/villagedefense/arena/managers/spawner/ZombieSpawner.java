package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ZombieSpawner {
    List<Integer> ALL_PHASES = IntStream.range(0, 20).boxed().collect(Collectors.toList());

    String getName();

    int getMinWave();

    default int getMaxWave() {
        return -1;
    }

    double getSpawnRate();

    default int getFinalAmount(int spawnAmount) {
        return spawnAmount;
    }

    List<Integer> getSpawnPhases();

    Zombie spawnZombie(Location location);

    default int getSpawnWeight() {
        return 1;
    }

    default void spawnZombie(Location location, Arena arena) {
        Zombie zombie = spawnZombie(location);
        CreatureUtils.applyAttributes(zombie, arena);
        arena.getZombies().add(zombie);
    }
}
