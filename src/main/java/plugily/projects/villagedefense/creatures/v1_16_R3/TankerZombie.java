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

package plugily.projects.villagedefense.creatures.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class TankerZombie extends EntityZombie {

  public TankerZombie(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public TankerZombie(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    getNavigation().q().b(true);

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalBreakDoor(this, enumDifficulty -> true));
    this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
    this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this));
    this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)); // this one to target human
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));
    this.setHealth(35);
    p().a(GenericAttributes.FOLLOW_RANGE, 200.0D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0D);
  }


}
