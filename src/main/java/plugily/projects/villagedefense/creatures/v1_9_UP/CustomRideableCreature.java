
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2025 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XAttribute;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XEntityType;

import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 16.01.2022
 */
public class CustomRideableCreature {

  private final RideableType rideableType;
  private final boolean holidayEffects;
  private final Map<XAttribute, Double> attributes;
  private final ItemStack dropItem;

  public CustomRideableCreature(RideableType rideableType, boolean holidayEffects, Map<XAttribute, Double> attributes, ItemStack dropItem) {
    this.rideableType = rideableType;
    this.holidayEffects = holidayEffects;
    this.attributes = attributes;
    this.dropItem = dropItem;
  }

  public Creature spawn(Location location) {
    EntityType entityType = XEntityType.VILLAGER.get();
    switch(rideableType) {
      case VILLAGER:
        entityType = XEntityType.VILLAGER.get();
        break;
      case WOLF:
        entityType = XEntityType.WOLF.get();
        break;
      case IRON_GOLEM:
        entityType = XEntityType.IRON_GOLEM.get();
        break;
    }
    Entity entity = VersionUtils.spawnEntity(location, entityType);
    if(entity instanceof Creature) {
      Creature creature = (Creature) entity;
      creature.getAttribute(XAttribute.FOLLOW_RANGE.get()).setBaseValue(200D);
      for(Map.Entry<XAttribute, Double> attribute : attributes.entrySet()) {
        creature.getAttribute(attribute.getKey().get()).setBaseValue(attribute.getValue());
        if(attribute.getKey().get() == XAttribute.MAX_HEALTH.get()) {
          VersionUtils.setMaxHealth(creature, attribute.getValue());
          creature.setHealth(attribute.getValue());
        }
      }
      creature.setRemoveWhenFarAway(false);
      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16)) {
        creature.setInvisible(false);
      }
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

  public Map<XAttribute, Double> getAttributes() {
    return attributes;
  }

  public ItemStack getDropItem() {
    return dropItem;
  }

  public enum RideableType {
    VILLAGER, IRON_GOLEM, WOLF
  }
}
