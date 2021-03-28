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

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityIronGolem;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.GenericAttributes;
import net.minecraft.server.v1_11_R1.IMonster;
import net.minecraft.server.v1_11_R1.Navigation;
import net.minecraft.server.v1_11_R1.PathfinderGoalDefendVillage;
import net.minecraft.server.v1_11_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_11_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

/**
 * Created by Tom on 17/08/2014.
 */
public class RidableIronGolem extends EntityIronGolem {

  public RidableIronGolem(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public RidableIronGolem(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);

    this.a(1.4F, 2.9F);
    ((Navigation) getNavigation()).b(true);
    goalSelector.a(0, new PathfinderGoalFloat(this));
    goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
    goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
    goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.6D));
    goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    targetSelector.a(1, new PathfinderGoalDefendVillage(this));
    targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
    targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 0, false, true, IMonster.e));
    setHealth(500);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(150D);
  }

  @Override
  protected void dropDeathLoot(boolean flag, int i) {
    //do not drop death loot
  }

  @Override
  public void g(float f, float f1) {
    EntityLiving entityliving = (EntityLiving) bw();
    if(entityliving == null) {
      // search first human passenger
      for(final Entity e : passengers) {
        if(e instanceof EntityHuman) {
          entityliving = (EntityLiving) e;
          break;
        }
      }
      if(entityliving == null) {
        this.l((float) 0.12);
        super.g(f, f1);
        return;
      }
    }
    lastYaw = yaw = entityliving.yaw;
    pitch = entityliving.pitch * 0.5F;
    setYawPitch(yaw, pitch);
    aQ = aO = yaw;
    f = entityliving.be * 0.75F;
    f1 = entityliving.bf;
    if(f1 <= 0.0f) {
      f1 *= 0.25F;
    }
    this.l((float) 0.12);
    super.g(f, f1);
    P = (float) 1.0;
  }

}
