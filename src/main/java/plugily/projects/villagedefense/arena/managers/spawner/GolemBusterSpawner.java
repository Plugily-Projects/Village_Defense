package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class GolemBusterSpawner implements SimpleZombieSpawner {
    @Override
    public int getMinWave() {
        return 1;
    }

    @Override
    public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        if (phase == 5) {
            return 1D / 3;
        }
        if (wave >= 6) {
            return 1D / 8;
        }
        return 0;
    }

    @Override
    public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        if (phase == 5) {
            return spawnAmount / 4;
        }
        if (wave >= 6) {
            return spawnAmount - 4;
        }
        return 0;
    }

    @Override
    public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return phase == 5 || (wave >= 6 && !arena.getIronGolems().isEmpty());
    }

    @Override
    public Zombie spawnZombie(Location location) {
        Zombie golemBuster = CreatureUtils.getCreatureInitializer().spawnGolemBuster(location);
        golemBuster.getEquipment().setHelmet(new ItemStack(Material.TNT));
        golemBuster.getEquipment().setHelmetDropChance(0.0F);
        VersionUtils.setItemInHandDropChance(golemBuster, 0F);
        golemBuster.getEquipment().setBoots(XMaterial.IRON_BOOTS.parseItem());
        golemBuster.getEquipment().setLeggings(XMaterial.IRON_LEGGINGS.parseItem());
        golemBuster.getEquipment().setChestplate(XMaterial.IRON_CHESTPLATE.parseItem());
        return golemBuster;
    }
}
