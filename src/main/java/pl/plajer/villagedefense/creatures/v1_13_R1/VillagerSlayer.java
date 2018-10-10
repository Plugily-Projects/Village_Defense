/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.creatures.v1_13_R1;

import java.util.LinkedHashSet;

import net.minecraft.server.v1_13_R1.EntityVillager;
import net.minecraft.server.v1_13_R1.EntityZombie;
import net.minecraft.server.v1_13_R1.GenericAttributes;
import net.minecraft.server.v1_13_R1.Navigation;
import net.minecraft.server.v1_13_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_13_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_13_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_13_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_13_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_13_R1.World;

import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

import pl.plajer.villagedefense.creatures.CreatureUtils;

/**
 * @author Plajer
 * <p>
 * Created at 02.05.2018
 */
public class VillagerSlayer extends EntityZombie {

  public int damage;
  private float bw;

  public VillagerSlayer(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  @SuppressWarnings("rawtypes")
  public VillagerSlayer(World world) {
    super(world);
    this.bw = 1.0f; //Change this to your liking. this is were you set the speed
    this.damage = 15; // set the damage
    //There's also a ton of options of you do this. play around with it


    LinkedHashSet goalB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    LinkedHashSet goalC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    LinkedHashSet targetB = (LinkedHashSet) CreatureUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    LinkedHashSet targetC = (LinkedHashSet) CreatureUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();


    ((Navigation) getNavigation()).b(true);

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
    this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, this.bw, false));
    this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, this.bw));
    this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, true));
    this.setHealth(70);
    this.p(true);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    this.getAttributeInstance(GenericAttributes.c).setValue(0D);
  }

}
