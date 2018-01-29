package me.tomthedeveloper.creatures.v1_8_R3;

import me.tomthedeveloper.utils.CreatureUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.List;

/**
 * Created by Tom on 17/08/2014.
 */
public class RidableIronGolem extends EntityIronGolem {

    public RidableIronGolem(org.bukkit.World world) {
        super(((CraftWorld) world).getHandle());


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
        return;
    }

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

    @Override
    protected void dropDeathLoot(boolean flag, int i) {
     /*   int j = this.random.nextInt(3);

        int k;

        for (k = 0; k < j; ++k) {
            this.a(Item.getItemOf(Blocks.RED_ROSE), 1, 0.0F);
        }

        k = 3 + this.random.nextInt(3);

        for (int l = 0; l < k; ++l) {
            this.a(Items.IRON_INGOT, 1);
        } */
    }

}
