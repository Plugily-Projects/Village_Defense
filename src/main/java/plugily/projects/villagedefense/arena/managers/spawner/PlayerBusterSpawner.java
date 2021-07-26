package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class PlayerBusterSpawner implements SimpleZombieSpawner {
  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if (phase == 5) {
      return 1D / 3;
    }
    if (wave > 10) {
      return 1D / 8;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if (phase == 5) {
      return spawnAmount / 4;
    }
    if (wave > 10) {
      return spawnAmount - 8;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || wave > 10;
  }

  @Override
  public Creature spawnZombie(Location location) {
    Creature playerBuster = CreatureUtils.getCreatureInitializer().spawnPlayerBuster(location);
    playerBuster.getEquipment().setHelmet(new ItemStack(Material.TNT));
    playerBuster.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(playerBuster, 0F);
    playerBuster.getEquipment().setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
    playerBuster.getEquipment().setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
    playerBuster.getEquipment().setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
    return playerBuster;
  }

  @Override
  public String getName() {
    return "PlayerBuster";
  }
}
