package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class FastZombieSpawner implements SimpleZombieSpawner {
  @Override
  public boolean canApplyHolidayEffect() {
    return true;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if (spawnAmount < 5 || arena.getZombies().isEmpty()) {
      return 1;
    }

    if (phase == 5 && wave <= 7) {
      return 2d / 3;
    }

    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    return (spawnAmount < 5 || (phase == 5 && wave <= 7) || arena.getZombies().isEmpty()) ? spawnAmount : 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return arena.getZombies().isEmpty() || spawnAmount < 5 || (phase == 5 && wave <= 7);
  }

  @Override
  public Creature spawnZombie(Location location) {
    return CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
  }

  @Override
  public String getName() {
    return "FastZombie";
  }

  @Override
  public int getPriority() {
    return -1;
  }
}
