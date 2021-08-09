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

import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreakDoor;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class TankerZombie extends EntityZombie {

  public TankerZombie(org.bukkit.World world) {
    this(((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) world).getHandle());
  }

  public TankerZombie(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    getNavigation().q().b(true);

    bQ.a(0, new PathfinderGoalFloat(this));
    bQ.a(1, new PathfinderGoalBreakDoor(this, enumDifficulty -> true));
    bQ.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    bQ.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    bQ.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
    bQ.a(7, new PathfinderGoalRandomLookaround(this));
    bP.a(1, new PathfinderGoalHurtByTarget(this));
    bP.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)); // this one to target human
    bP.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
    bP.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));
    setHealth(35);
    w().a(GenericAttributes.b, 200.0D).a(GenericAttributes.c, 0D).a();
  }


}
