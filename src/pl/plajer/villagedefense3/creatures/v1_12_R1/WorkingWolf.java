package pl.plajer.villagedefense3.creatures.v1_12_R1;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.Navigation;
import net.minecraft.server.v1_12_R1.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import pl.plajer.villagedefense3.creatures.CreatureUtils;

import java.util.LinkedHashSet;

/**
 * Created by Tom on 17/08/2014.
 */
public class WorkingWolf extends EntityWolf {

    public WorkingWolf(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public WorkingWolf(World world) {
        super(world);

        LinkedHashSet goalB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        LinkedHashSet goalC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        LinkedHashSet targetB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        LinkedHashSet targetC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();

        this.a(1.4F, 2.9F);
        ((Navigation) getNavigation()).a(true);
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.5F, false));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityZombie.class, true));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));

    }

  /*  @Override
    public void e(float f, float f1) {


        if (this.passenger != null && this.passenger instanceof EntityLiving) {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            this.b(this.yaw, this.pitch);
            this.aO = this.aM = this.yaw;
            f = ((EntityLiving) this.passenger).bd * 0.5F;
            f1 = ((EntityLiving) this.passenger).be;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }


            if (!this.world.isStatic) {
                this.i((float) this.getAttributeInstance(GenericAttributes.d).getValue());
                this.Y = 1.0F;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;    // Make backwards slower
                }
                f *= 0.75F;    // Also make sideways slower

                float speed = 0.12F;    // 0.2 is the default entity speed. I made it slightly faster so that riding is better than walking
                this.i(speed);    // Apply the speed
                super.e(f, f1);
            }

            if (this.onGround) {

                this.h(false);
            }

            this.aE = this.aF;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aF += (f4 - this.aF) * 0.4F;
            this.aG += this.aF;
        } else {
            this.W = 0.5F;
            this.aQ = 0.02F;
            this.Y = 1.0F;

            if (f1 <= 0.0F) {
                f1 *= 0.25F;    // Make backwards slower
            }
            f *= 0.75F;    // Also make sideways slower

            float speed = 0.12F;    // 0.2 is the default entity speed. I made it slightly faster so that riding is better than walking
            this.i(speed);    // Apply the speed
            super.e(f, f1);
        }
    } */

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(70.0D);
    }


}
