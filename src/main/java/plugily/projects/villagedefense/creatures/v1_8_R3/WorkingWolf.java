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

package plugily.projects.villagedefense.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * Created by Tom on 17/08/2014.
 */
public class WorkingWolf extends EntityWolf {

  public WorkingWolf(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public WorkingWolf(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);

    this.a(1.4F, 2.9F);
    ((Navigation) getNavigation()).a(true);
    goalSelector.a(0, new PathfinderGoalFloat(this));
    goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
    goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
    goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.5F, false));
    goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
    goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombie.class, true));
    targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));

  }


  @Override
  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(70.0D);
  }


}
