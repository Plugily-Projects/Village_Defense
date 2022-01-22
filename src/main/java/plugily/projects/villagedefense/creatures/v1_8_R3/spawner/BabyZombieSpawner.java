package plugily.projects.villagedefense.creatures.v1_8_R3.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class BabyZombieSpawner implements SimpleEnemySpawner {
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
  public Creature spawn(Location location) {
    return CreatureUtils.getCreatureInitializer().spawnBabyZombie(location);
  }

  @Override
  public String getName() {
    return "BabyZombie";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
