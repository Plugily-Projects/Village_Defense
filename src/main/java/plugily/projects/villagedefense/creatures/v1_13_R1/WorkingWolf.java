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

package plugily.projects.villagedefense.creatures.v1_13_R1;

import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EntityLiving;
import net.minecraft.server.v1_13_R1.EntityWolf;
import net.minecraft.server.v1_13_R1.EntityZombie;
import net.minecraft.server.v1_13_R1.GenericAttributes;
import net.minecraft.server.v1_13_R1.Navigation;
import net.minecraft.server.v1_13_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R1.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_13_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_13_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_13_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_13_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_13_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_13_R1.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

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
  public void a(float f, float f1, float f2) {
    EntityLiving entityliving = null;
    for(final Entity e : passengers) {
      if(e instanceof EntityHuman) {
        entityliving = (EntityLiving) e;
        break;
      }
    }
    if(entityliving == null) {
      P = 0.5F;
      aR = 0.02F;
      o(0.12f);
      this.k((float) 0.12);
      super.a(f, f1, f2);
      return;
    }
    lastYaw = yaw = entityliving.yaw;
    pitch = entityliving.pitch * 0.5F;
    setYawPitch(yaw, pitch);
    aO = aM = yaw;

    f = entityliving.bh * 0.5F * 0.75F;
    f2 = entityliving.bj;
    if(f2 <= 0.0f) {
      f2 *= 0.25F;
    }

    //for 1.13
    entityliving.bj = 0.12f;
    o(0.12f);

    super.a(f, f1, f2);
    P = (float) 1.0;
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(200.0D);
  }


}
