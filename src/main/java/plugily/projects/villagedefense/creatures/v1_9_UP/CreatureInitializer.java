package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import plugily.projects.villagedefense.creatures.BaseCreatureInitializer;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class CreatureInitializer implements BaseCreatureInitializer {

  @Override
  public Villager spawnVillager(Location location) {
    Creature creature = CreatureUtils.getPlugin().getEnemySpawnerRegistry().getRideableCreatureByName(CustomRideableCreature.RideableType.VILLAGER).get().spawn(location);
    if(creature instanceof Villager) {
      return (Villager) creature;
    }
    throw new ClassCastException("Villager creature isn't a villager");
  }

  @Override
  public Wolf spawnWolf(Location location) {
    Creature creature = CreatureUtils.getPlugin().getEnemySpawnerRegistry().getRideableCreatureByName(CustomRideableCreature.RideableType.WOLF).get().spawn(location);
    if(creature instanceof Wolf) {
      return (Wolf) creature;
    }
    throw new ClassCastException("Wolf creature isn't a wolf");
  }

  @Override
  public IronGolem spawnGolem(Location location) {
    Creature creature = CreatureUtils.getPlugin().getEnemySpawnerRegistry().getRideableCreatureByName(CustomRideableCreature.RideableType.IRON_GOLEM).get().spawn(location);
    if(creature instanceof IronGolem) {
      return (IronGolem) creature;
    }
    throw new ClassCastException("IronGolem creature isn't a iron golem");
  }

  @Override
  public Zombie spawnFastZombie(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnBabyZombie(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnHardZombie(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnPlayerBuster(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnGolemBuster(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnVillagerBuster(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnKnockbackResistantZombies(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }

  @Override
  public Zombie spawnVillagerSlayer(Location location) {
    throw new UnsupportedOperationException("Method isn't used on 1.9 and up");
  }
}
