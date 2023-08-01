
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
