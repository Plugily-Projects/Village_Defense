package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class HalfInvisibleZombieSpawner implements SimpleZombieSpawner {
  @Override
  public int getMinWave() {
    return 7;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    return 1D / 8;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    int total = 0;
    if (wave > 23) {
      total++;
    }
    if (wave > 15) {
      total += spawnAmount - 13;
    } else if (wave > 7) {
      total += spawnAmount - 5;
    }
    return total;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return true;
  }

  @Override
  public Zombie spawnZombie(Location location) {
    Zombie fastZombie = CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
    fastZombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    fastZombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    return fastZombie;
  }

  @Override
  public String getName() {
    return "HalfInvisibleZombie";
  }
}
