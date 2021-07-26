package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class BabyZombieSpawner implements SimpleZombieSpawner {
  @Override
  public boolean canApplyHolidayEffect() {
    return true;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 ? 1D / 3 : 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 ? spawnAmount / 4 : 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5;
  }

  @Override
  public Creature spawnZombie(Location location) {
    return CreatureUtils.getCreatureInitializer().spawnBabyZombie(location);
  }

  @Override
  public String getName() {
    return "BabyZombie";
  }
}
