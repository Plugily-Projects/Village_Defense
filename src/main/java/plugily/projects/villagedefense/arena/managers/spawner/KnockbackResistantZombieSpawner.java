package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class KnockbackResistantZombieSpawner implements SimpleZombieSpawner {
    @Override
    public int getMinWave() {
        return 20;
    }

    @Override
    public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        return 2D / 9;
    }

    @Override
    public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        return spawnAmount;
    }

    @Override
    public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return phase == 5;
    }

    @Override
    public Zombie spawnZombie(Location location) {
        Zombie tankerZombie = CreatureUtils.getCreatureInitializer().spawnKnockbackResistantZombies(location);
        VersionUtils.setItemInHand(tankerZombie, XMaterial.GOLDEN_AXE.parseItem());
        tankerZombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        tankerZombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        tankerZombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        tankerZombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        return tankerZombie;
    }
}
