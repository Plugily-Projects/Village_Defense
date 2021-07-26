package plugily.projects.villagedefense.creatures;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import plugily.projects.commonsbox.minecraft.misc.MiscUtils;

public interface BaseCreatureInitializer {
    Villager spawnVillager(Location location);

    Wolf spawnWolf(Location location);

    IronGolem spawnGolem(Location location);

    Zombie spawnFastZombie(Location location);

    Zombie spawnBabyZombie(Location location);

    Zombie spawnHardZombie(Location location);

    Zombie spawnPlayerBuster(Location location);

    Zombie spawnGolemBuster(Location location);

    Zombie spawnVillagerBuster(Location location);

    Zombie spawnKnockbackResistantZombies(Location location);

    Zombie spawnVillagerSlayer(Location location);

    default void applyFollowRange(Zombie zombie) {
        MiscUtils.getEntityAttribute(zombie, Attribute.GENERIC_FOLLOW_RANGE).ifPresent(ai -> ai.setBaseValue(200.0D));
    }

    default void applyDamageModifier(LivingEntity entity, double value) {
        MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_ATTACK_DAMAGE).ifPresent(ai -> ai.setBaseValue(value));
    }

    default void applySpeedModifier(LivingEntity entity, double value) {
        MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED).ifPresent(ai -> ai.setBaseValue(value));
    }
}
