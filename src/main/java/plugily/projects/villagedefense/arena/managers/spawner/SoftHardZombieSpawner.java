package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class SoftHardZombieSpawner implements SimpleZombieSpawner {
    @Override
    public int getMinWave() {
        return 4;
    }

    @Override
    public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        if (phase == 5 && wave > 7 && wave <= 14) {
            return 1D / 3;
        }
        if (phase == 15 && wave > 4 && wave <= 8) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        if (phase == 5 && wave > 7 && wave <= 14) {
            return spawnAmount;
        }
        if (phase == 15 && wave > 4 && wave <= 8) {
            return spawnAmount - 3;
        }
        return 0;
    }

    @Override
    public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return phase == 5 || phase == 15;
    }

    @Override
    public Zombie spawnZombie(Location location) {
        Zombie hardBuster = CreatureUtils.getCreatureInitializer().spawnHardZombie(location);
        hardBuster.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        hardBuster.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        hardBuster.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        hardBuster.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        return hardBuster;
    }

    @Override
    public String getName() {
        return "SoftHardZombie";
    }
}
