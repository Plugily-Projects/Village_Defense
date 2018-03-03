package pl.plajer.villagedefense3.creatures.v1_12_R1;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import pl.plajer.villagedefense3.creatures.CreatureUtils;
import pl.plajer.villagedefense3.handlers.LanguageManager;

import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by Tom on 15/08/2014.
 */
public class RidableVillager extends EntityVillager {

    private String[] villagernames = LanguageManager.getLanguageMessage("In-Game.Villager-Names") != null ? LanguageManager.getLanguageMessage("In-Game.Villager-Names").split(",") : "Jagger,Kelsey,Kelton,Haylie,Harlow,Howard,Wulffric,Winfred,Ashley,Bailey,Beckett,Alfredo,Alfred,Adair,Edgar,ED,Eadwig,Edgaras,Buckley,Stanley,Nuffley,Mary,Jeffry,Rosaly,Elliot,Harry,Sam,Rosaline,Tom,Ivan,Kevin,Adam".split(",");

    public RidableVillager(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @SuppressWarnings("rawtypes")
    public RidableVillager(World world) {
        super(world);

        LinkedHashSet goalB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        LinkedHashSet goalC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        LinkedHashSet targetB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        LinkedHashSet targetC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();

        this.setSize(0.6F, 1.8F);
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

    /*@Override
    public void g(float f, float f1) {
        if(passengers != null && !passengers.isEmpty()) {
            EntityLiving entityliving = (EntityLiving) passengers.get(0);
            if(entityliving == null) {
                for(final Entity e : passengers) {
                    if(e instanceof EntityHuman) {
                        entityliving = (EntityLiving) e;
                        break;
                    }
                }
                if(entityliving == null) {
                    P = 0.5f;
                    this.l((float) 0.12);
                    super.g(f, f1);
                    return;
                }
            }
            final float yaw = entityliving.yaw;
            this.yaw = yaw;
            lastYaw = yaw;
            pitch = entityliving.pitch * 0.5f;
            setYawPitch(this.yaw, pitch);
            final float yaw2 = this.yaw;
            aM = yaw2;
            aO = yaw2;
            f = entityliving.be * 0.75F;
            f1 = entityliving.be;
            if(f1 <= 0.0f) {
                f1 *= 0.25F;
            }
            this.l((float) 0.12);
            super.g(f, f1);
            P = 1.0F;
            return;
        }
        P = 0.5f;
        this.l((float) 0.12);
        super.g(f, f1);
    }*/

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return this.b(entityAgeable);
    }


}
