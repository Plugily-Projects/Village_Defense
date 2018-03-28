package pl.plajer.villagedefense3.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.creatures.CreatureUtils;

import java.util.List;

/**
 * Created by Tom on 14/08/2014.
 */
public class FastZombie extends net.minecraft.server.v1_8_R3.EntityZombie {
    public int damage;
    private float bw;


    @SuppressWarnings("rawtypes")
    public FastZombie(org.bukkit.World world) {
        super(((CraftWorld) world).getHandle());
        this.bw = Main.ZOMBIE_SPEED; //Change this to your liking. this is were you set the speed
        this.damage = 15; // set the damage
        //There's also a ton of options of you do this. play around with it


        List goalB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        List goalC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        List targetB = (List) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        List targetC = (List) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();


        ((Navigation) getNavigation()).b(true);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityHuman.class, this.bw, false)); // this one to attack human
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityIronGolem.class, this.bw, true));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityVillager.class, this.bw, true));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, this.bw));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true)); // this one to target human
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, false));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, false));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    }


}
