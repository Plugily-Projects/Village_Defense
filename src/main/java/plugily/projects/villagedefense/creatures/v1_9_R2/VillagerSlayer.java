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

package plugily.projects.villagedefense.creatures.v1_9_R2;

import net.minecraft.server.v1_9_R2.EntityVillager;
import net.minecraft.server.v1_9_R2.EntityZombie;
import net.minecraft.server.v1_9_R2.GenericAttributes;
import net.minecraft.server.v1_9_R2.Navigation;
import net.minecraft.server.v1_9_R2.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_9_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R2.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R2.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R2.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;

/**
 * @author Plajer
 * <p>
 * Created at 02.05.2018
 */
public class VillagerSlayer extends EntityZombie {

  public VillagerSlayer(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public VillagerSlayer(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    ((Navigation) getNavigation()).b(true);

    goalSelector.a(0, new PathfinderGoalFloat(this));
    goalSelector.a(1, new PathfinderGoalBreakDoor(this));
    goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0f, false));
    goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0f));
    goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, true));
    setHealth(70);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    getAttributeInstance(GenericAttributes.c).setValue(0D);
  }

}
