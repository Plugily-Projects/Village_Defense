
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures;

import org.bukkit.Location;
import org.bukkit.entity.*;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XAttribute;

public interface BaseCreatureInitializer {

  default String getCreatureCustomIDMetadata() {
    return "VD_CREATURE_ID";
  }

  default String getCreatureCustomNameMetadata() {
    return "VD_CREATURE_CUSTOM_NAME";
  }

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
    MiscUtils.getEntityAttribute(zombie, XAttribute.FOLLOW_RANGE.get()).ifPresent(ai -> ai.setBaseValue(200.0D));
  }

  default void applyDamageModifier(LivingEntity entity, double value) {
    MiscUtils.getEntityAttribute(entity, XAttribute.ATTACK_DAMAGE.get()).ifPresent(ai -> ai.setBaseValue(value));
  }

  default void applySpeedModifier(LivingEntity entity, double value) {
    MiscUtils.getEntityAttribute(entity, XAttribute.MOVEMENT_SPEED.get()).ifPresent(ai -> ai.setBaseValue(value));
  }
}
