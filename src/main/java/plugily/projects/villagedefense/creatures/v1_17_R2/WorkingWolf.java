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
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowOwner;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class WorkingWolf extends EntityWolf {

  public WorkingWolf(org.bukkit.World world) {
    this(((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) world).getHandle());
  }

  public WorkingWolf(World world) {
    super(EntityTypes.bc, world);

    GoalSelectorCleaner.clearSelectors(this);

    a(1.4F, 2.9F);
    ((Navigation) getNavigation()).a(true);
    bP.a(0, new PathfinderGoalFloat(this));
    bP.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
    bP.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    bP.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F, true));
    bP.a(2, new PathfinderGoalMeleeAttack(this, 1.5F, false));
    bP.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    bP.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
    bP.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    bP.a(8, new PathfinderGoalRandomLookaround(this));
    bQ.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombie.class, true));
    bQ.a(1, new PathfinderGoalHurtByTarget(this, EntityVillager.class, EntityIronGolem.class, EntityHuman.class));
    w().a(GenericAttributes.b, 200.0D).a();
  }

}
