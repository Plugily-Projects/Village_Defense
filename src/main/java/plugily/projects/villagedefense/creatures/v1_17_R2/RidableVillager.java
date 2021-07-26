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

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalInteract;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTradeWithPlayer;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class RidableVillager extends EntityVillager {

  public RidableVillager(org.bukkit.World world) {
    this(((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) world).getHandle());
  }

  public RidableVillager(World world) {
    super(EntityTypes.aV, world);

    GoalSelectorCleaner.clearSelectors(this);

    //todo this.setSize(0.6F, 1.8F);
    getNavigation().q().b(true);
    ((Navigation) getNavigation()).a(true);
    bQ.a(0, new PathfinderGoalFloat(this));
    bQ.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
    bQ.a(1, new PathfinderGoalTradeWithPlayer(this));
    bQ.a(1, new PathfinderGoalLookAtTradingPlayer(this));
    //todo
    //bQ.a(2, new PathfinderGoalMoveIndoors(this));
    //bQ.a(3, new PathfinderGoalRestrictOpenDoor(this));
    //bQ.a(4, new PathfinderGoalOpenDoor(this, true));
    bQ.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
    //this.bO.a(6, new PathfinderGoalMakeLove(this));
    //this.bO.a(8, new PathfinderGoalPlay(this, 0.32D));
    bQ.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
    bQ.a(9, new PathfinderGoalInteract(this, EntityVillager.class, 5.0F, 0.02F));
    bQ.a(9, new PathfinderGoalRandomStroll(this, 0.6D));
    bQ.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
  }

}
