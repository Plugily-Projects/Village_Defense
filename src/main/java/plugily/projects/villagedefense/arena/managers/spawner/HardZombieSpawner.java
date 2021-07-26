package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class HardZombieSpawner implements SimpleZombieSpawner {
  @Override
  public int getMinWave() {
    return 4;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if (phase == 5 && wave > 14 && wave <= 20) {
      return 1D / 3;
    }
    if (phase == 15 && wave > 8) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if (phase == 5 && wave > 14 && wave <= 20) {
      return spawnAmount;
    }
    if (phase == 15 && wave > 8) {
      return spawnAmount - 7;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || phase == 15;
  }

  @Override
  public Creature spawnZombie(Location location) {
    Creature hardZombie = CreatureUtils.getCreatureInitializer().spawnHardZombie(location);
    hardZombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    hardZombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    hardZombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    hardZombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    return hardZombie;
  }

  @Override
  public String getName() {
    return "HardZombie";
  }
}
