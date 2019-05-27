/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense.creatures.v1_14_R1;

import java.util.LinkedHashSet;

import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;

import pl.plajer.villagedefense.creatures.CreatureUtils;

/**
 * Internal helper class
 */
class GoalSelectorCleaner {

  static void clearSelectors(EntityCreature creature) {
    LinkedHashSet goalD = (LinkedHashSet) CreatureUtils.getPrivateField("d", PathfinderGoalSelector.class, creature.goalSelector);
    goalD.clear();
    LinkedHashSet targetD = (LinkedHashSet) CreatureUtils.getPrivateField("d", PathfinderGoalSelector.class, creature.targetSelector);
    targetD.clear();
  }

}
