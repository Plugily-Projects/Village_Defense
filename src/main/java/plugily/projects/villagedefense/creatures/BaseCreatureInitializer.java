package plugily.projects.villagedefense.creatures;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;

public interface BaseCreatureInitializer {
  Villager spawnVillager(Location location);

  Wolf spawnWolf(Location location);

  IronGolem spawnGolem(Location location);

  Creature spawnFastZombie(Location location);

  Creature spawnBabyZombie(Location location);

  Creature spawnHardZombie(Location location);

  Creature spawnPlayerBuster(Location location);

  Creature spawnGolemBuster(Location location);

  Creature spawnVillagerBuster(Location location);

  Creature spawnKnockbackResistantZombies(Location location);

  Creature spawnVillagerSlayer(Location location);

  default void applyFollowRange(Creature zombie) {
    MiscUtils.getEntityAttribute(zombie, Attribute.GENERIC_FOLLOW_RANGE).ifPresent(ai -> ai.setBaseValue(200.0D));
  }

  default void applyDamageModifier(LivingEntity entity, double value) {
    MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_ATTACK_DAMAGE).ifPresent(ai -> ai.setBaseValue(value));
  }

  default void applySpeedModifier(LivingEntity entity, double value) {
    MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED).ifPresent(ai -> ai.setBaseValue(value));
  }
}
