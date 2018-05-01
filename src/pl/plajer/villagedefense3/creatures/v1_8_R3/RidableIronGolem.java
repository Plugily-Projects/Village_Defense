/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IMonster;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathfinderGoalDefendVillage;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalOfferFlower;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import pl.plajer.villagedefense3.creatures.CreatureUtils;

import java.util.List;

/**
 * Created by Tom on 17/08/2014.
 */
public class RidableIronGolem extends EntityIronGolem {

    public RidableIronGolem(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public RidableIronGolem(World world) {
        super(world);

        List goalB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        List goalC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        List targetB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        List targetC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();

        this.a(1.4F, 2.9F);
        ((Navigation) getNavigation()).b(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, true, IMonster.e));
        this.setHealth(500);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(150D);
    }

    @Override
    protected void dropDeathLoot(boolean flag, int i) {}

    @Override
    public void g(float f, float f1) {
        if(this.passenger != null && this.passenger instanceof EntityLiving) {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            setYawPitch(this.yaw, this.pitch);
            this.aI = this.aG = this.yaw;
            f = ((EntityLiving) this.passenger).aZ * 0.5F;
            f1 = ((EntityLiving) this.passenger).ba;
            if(f1 <= 0.0F) {
                f1 *= 0.25F;
            }


            if(!this.world.isClientSide) {
                this.k((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                this.S = 1.0F;
                if(f1 <= 0.0F) {
                    f1 *= 0.25F;    // Make backwards slower
                }
                f *= 0.75F;    // Also make sideways slower

                float speed = 0.12F;    // 0.2 is the default entity speed. I made it slightly faster so that riding is better than walking
                this.k(speed);    // Apply the speed
                super.g(f, f1);
            }

            if(this.onGround) {

                this.j(false);
            }

            this.ay = this.az;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if(f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.az += (f4 - this.az) * 0.4F;
            this.aA += this.az;
        } else {
            this.S = 0.5F;
            this.aK = 0.02F;
            // this.S = 1.0F;

            if(f1 <= 0.0F) {
                f1 *= 0.25F;    // Make backwards slower
            }
            f *= 0.75F;    // Also make sideways slower

            float speed = 0.12F;    // 0.2 is the default entity speed. I made it slightly faster so that riding is better than walking
            this.k(speed);    // Apply the speed
            super.g(f, f1);
            this.S = 1.0F;
        }
    }

}
