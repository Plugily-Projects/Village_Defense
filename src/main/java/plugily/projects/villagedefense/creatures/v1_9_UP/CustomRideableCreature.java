package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 16.01.2022
 */
public class CustomRideableCreature {

  private final RideableType rideableType;
  private final boolean holidayEffects;
  private final Map<Attribute, Double> attributes;
  private final ItemStack dropItem;

  public CustomRideableCreature(RideableType rideableType, boolean holidayEffects, Map<Attribute, Double> attributes, ItemStack dropItem) {
    this.rideableType = rideableType;
    this.holidayEffects = holidayEffects;
    this.attributes = attributes;
    this.dropItem = dropItem;
  }

  public Creature spawn(Location location) {
    World world = location.getWorld();
    EntityType entityType = EntityType.VILLAGER;
    switch(rideableType) {
      case VILLAGER:
        entityType = EntityType.VILLAGER;
        break;
      case WOLF:
        entityType = EntityType.WOLF;
        break;
      case IRON_GOLEM:
        entityType = EntityType.IRON_GOLEM;
        break;
    }
    Entity entity = world.spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
    if(entity instanceof Creature) {
      Creature creature = (Creature) entity;
      creature.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200D);
      for(Map.Entry<Attribute, Double> attribute : attributes.entrySet()) {
        creature.getAttribute(attribute.getKey()).setBaseValue(attribute.getValue());
        if(attribute.getKey() == Attribute.GENERIC_MAX_HEALTH) {
          VersionUtils.setMaxHealth(creature, attribute.getValue());
          creature.setHealth(attribute.getValue());
        }
      }
      creature.setRemoveWhenFarAway(false);
      creature.setInvisible(false);
      return creature;
    } else {
      entity.remove();
      throw new IllegalStateException("Couldn't spawn Creature " + entityType + " as its not instance of creature");
    }
  }

  public RideableType getRideableType() {
    return rideableType;
  }

  public boolean isHolidayEffects() {
    return holidayEffects;
  }

  public Map<Attribute, Double> getAttributes() {
    return attributes;
  }

  public ItemStack getDropItem() {
    return dropItem;
  }

  public enum RideableType {
    VILLAGER, IRON_GOLEM, WOLF
  }
}
