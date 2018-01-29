package me.tomthedeveloper.creatures.v1_7_R4;

import me.tomthedeveloper.utils.CreatureUtils;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

import java.util.List;

/**
 * Created by Tom on 17/08/2014.
 */
public class RidableIronGolem extends EntityIronGolem {

    @SuppressWarnings("rawtypes")
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
        getNavigation().b(true);
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
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, true, IMonster.a));
        this.setHealth(500);
    }

    public void e(float f, float f1) {
        if((this.passenger != null) && ((this.passenger instanceof EntityLiving))) {
            this.lastYaw = (this.yaw = this.passenger.yaw);
            this.pitch = (this.passenger.pitch * 0.5F);
            b(this.yaw, this.pitch);
            this.aO = (this.aM = this.yaw);
            f = ((EntityLiving) this.passenger).bd * 0.5F;
            f1 = ((EntityLiving) this.passenger).be;


            this.W = 1.0F;
            this.aQ = (bl() * 0.1F);
            if(!this.world.isStatic) {
                i((float) getAttributeInstance(GenericAttributes.d).getValue());
                super.e(f, f1);
            }

            this.aE = this.aF;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if(f4 > 1.0F) {
                f4 = 1.0F;
            }
            this.aF += (f4 - this.aF) * 0.4F;
            this.aG += this.aF;
        } else {
            this.W = 0.5F;
            this.aQ = 0.02F;
            super.e(f, f1);
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
