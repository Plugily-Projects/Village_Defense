package me.tomthedeveloper.creatures.v1_8_R3;

import me.tomthedeveloper.handlers.LanguageManager;
import me.tomthedeveloper.utils.CreatureUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 15/08/2014.
 */
public class RidableVillager extends EntityVillager {

    private String[] villagernames = LanguageManager.getLanguageMessage("In-game.Villager-Names") != null ? LanguageManager.getLanguageMessage("In-game.Villager-Names").split(",") : "Jagger,Kelsey,Kelton,Haylie,Harlow,Howard,Wulffric,Winfred,Ashley,Bailey,Beckett,Alfredo,Alfred,Adair,Edgar,ED,Eadwig,Edgaras,Buckley,Stanley,Nuffley,Mary,Jeffry,Rosaly,Elliot,Harry,Sam,Rosaline,Tom,Ivan,Kevin,Adam".split(",");

    @SuppressWarnings("rawtypes")
    public RidableVillager(org.bukkit.World world) {
        super(((CraftWorld) world).getHandle());

        List goalB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        List goalC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        List targetB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        List targetC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();

        this.a(0.6F, 1.8F);
        ((Navigation) getNavigation()).b(true);
        ((Navigation) getNavigation()).a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        // this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, new EntityZom(this), 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new PathfinderGoalMoveIndoors(this));
        this.goalSelector.a(3, new PathfinderGoalRestrictOpenDoor(this));
        this.goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
        this.goalSelector.a(6, new PathfinderGoalMakeLove(this));
        this.goalSelector.a(8, new PathfinderGoalPlay(this, 0.32D));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityVillager.class, 5.0F, 0.02F));
        this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.setCustomName(villagernames[new Random().nextInt(villagernames.length)]);
        this.setCustomNameVisible(true);
    }


  /*  public void e(float f, float f1)
    {
        if (f > 1.0F) {
            makeSound("mob.horse.land", 0.4F, 1.0F);
        }
        int i = MathHelper.f((f * 0.5F - 3.0F) * f1);
        if (i > 0)
        {
            damageEntity(DamageSource.FALL, i);
            if (this.passenger != null) {
                this.passenger.damageEntity(DamageSource.FALL, i);
            }
            Block block = this.world.getType(new BlockPosition(this.locX, this.locY - 0.2D - this.lastYaw, this.locZ)).getBlock();
            if ((block.getMaterial() != Material.AIR) && (!R()))
            {
                StepSound stepsound = block.stepSound;

                this.world.makeSound(this, stepsound.getStepSound(), stepsound.getVolume1() * 0.5F, stepsound.getVolume2() * 0.75F);
            }
        }
    } */

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



   /* @Override
    public void g(float f, float f1)
    {
        if ((this.passenger != null) && ((this.passenger instanceof EntityLiving)) && (cE()))
        {
            this.lastYaw = (this.yaw = this.passenger.yaw);
            this.pitch = (this.passenger.pitch * 0.5F);
            setYawPitch(this.yaw, this.pitch);
            this.aI = (this.aG = this.yaw);
            f = ((EntityLiving)this.passenger).aX * 0.5F;
            f1 = ((EntityLiving)this.passenger).aY;
            if (f1 <= 0.0F)
            {
                f1 *= 0.25F;
                this.bL = 0;
            }
            if ((this.onGround) && (this.bp == 0.0F) && (cx()) && (!this.bE))
            {
                f = 0.0F;
                f1 = 0.0F;
            }
            if ((this.bp > 0.0F) && (!ct()) && (this.onGround))
            {
                this.motY = (getJumpStrength() * this.bp);
                if (hasEffect(MobEffectList.JUMP)) {
                    this.motY += (getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F;
                }
                m(true);
                this.ai = true;
                if (f1 > 0.0F)
                {
                    float f2 = MathHelper.sin(this.yaw * 3.141593F / 180.0F);
                    float f3 = MathHelper.cos(this.yaw * 3.141593F / 180.0F);

                    this.motX += -0.4F * f2 * this.bp;
                    this.motZ += 0.4F * f3 * this.bp;
                    makeSound("mob.horse.jump", 0.4F, 1.0F);
                }
                this.bp = 0.0F;
            }
            this.S = 1.0F;
            this.aK = (bH() * 0.1F);
            if (!this.world.isStatic)
            {
                j((float)getAttributeInstance(GenericAttributes.d).getValue());
                super.g(f, f1);
            }
            if (this.onGround)
            {
                this.bp = 0.0F;
                m(false);
            }
            this.ay = this.az;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if (f4 > 1.0F) {
                f4 = 1.0F;
            }
            this.az += (f4 - this.az) * 0.4F;
            this.aA += this.az;
        }
        else
        {
            this.S = 0.5F;
            this.aK = 0.02F;
            super.g(f, f1);
        }
    }
*/


    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return this.b(entityAgeable);
    }


}
