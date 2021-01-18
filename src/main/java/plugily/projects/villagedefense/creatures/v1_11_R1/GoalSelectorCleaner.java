/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_11_R1;

import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.LinkedHashSet;

/**
 * Internal helper class
 */
class GoalSelectorCleaner {

  private GoalSelectorCleaner() {
  }

  static void clearSelectors(EntityCreature creature) {
    LinkedHashSet goalB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, creature.goalSelector);
    goalB.clear();
    LinkedHashSet goalC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, creature.goalSelector);
    goalC.clear();
    LinkedHashSet targetB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, creature.targetSelector);
    targetB.clear();
    LinkedHashSet targetC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, creature.targetSelector);
    targetC.clear();
  }

}
