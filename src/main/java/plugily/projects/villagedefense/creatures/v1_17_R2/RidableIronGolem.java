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

package plugily.projects.villagedefense.creatures.v1_17_R2;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveThroughVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalDefendVillage;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class RidableIronGolem extends EntityIronGolem {

  public RidableIronGolem(org.bukkit.World world) {
    this(((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) world).getHandle());
  }

  public RidableIronGolem(World world) {
    super(EntityTypes.P, world);

    GoalSelectorCleaner.clearSelectors(this);

    this.a(1.4F, 2.9F);
    getNavigation().q().b(true);
    bP.a(0, new PathfinderGoalFloat(this));
    bP.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    bP.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
    bP.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, false, 4, () -> false));
    bP.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    bP.a(5, new PathfinderGoalRandomStroll(this, 0.6D));
    bP.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    bP.a(7, new PathfinderGoalRandomLookaround(this));
    bQ.a(1, new PathfinderGoalDefendVillage(this));
    bQ.a(2, new PathfinderGoalHurtByTarget(this));
    bQ.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 0, false, true, (entity) -> entity instanceof IMonster && !entity.isInvisible()));
    setHealth(500);
    w().a(GenericAttributes.b, 200.0D).a();
  }

  /*@Override
  public void a(float f, float f1, float f2) {
    EntityLiving entityliving = null;
    for (final Entity e : passengers) {
      if (e instanceof EntityHuman) {
        entityliving = (EntityLiving) e;
        break;
      }
    }
    if (entityliving == null) {
      this.P = 0.5F;
      this.aR = 0.02F;
      o(0.12f);
      this.k((float) 0.12);
      super.a(f, f1, f2);
      return;
    }
    this.lastYaw = this.yaw = entityliving.yaw;
    this.pitch = entityliving.pitch * 0.5F;
    this.setYawPitch(this.yaw, this.pitch);
    this.aO = this.aM = this.yaw;

    f = entityliving.bh * 0.5F * 0.75F;
    f2 = entityliving.bj;
    if (f2 <= 0.0f) {
      f2 *= 0.25F;
    }

    //for 1.13
    entityliving.bj = 0.12f;
    o(0.12f);

    super.a(f, f1, f2);
    P = (float) 1.0;
  }*/ //todo


  /*@Override
  protected void dropDeathLoot(boolean flag, int i) {
    //do not drop death loot
  }*/

}
