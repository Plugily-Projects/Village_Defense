/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

import java.util.Arrays;

import net.minecraft.server.v1_13_R1.DamageSource;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EntityIronGolem;
import net.minecraft.server.v1_13_R1.EntityZombie;
import net.minecraft.server.v1_13_R1.GenericAttributes;
import net.minecraft.server.v1_13_R1.Navigation;
import net.minecraft.server.v1_13_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_13_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_13_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_13_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_13_R1.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_13_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import pl.plajer.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tom on 14/08/2014.
 */
public class GolemBuster extends EntityZombie {

  public GolemBuster(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public GolemBuster(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    ((Navigation) getNavigation()).b(true);

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
    this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    this.goalSelector.a(5, new PathfinderGoalBreakDoorFaster(this));
    this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F)); // this one to look at human
    this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)); // this one to target human
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));
    this.setHealth(5);
    this.p(true);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(200.0D);
  }

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    if (damagesource != null && damagesource.getEntity() != null && damagesource.getEntity().getBukkitEntity().getType() == EntityType.IRON_GOLEM) {
      this.die();
      org.bukkit.inventory.ItemStack[] itemStack = new org.bukkit.inventory.ItemStack[] {new org.bukkit.inventory.ItemStack(org.bukkit.Material.ROTTEN_FLESH)};
      Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) this.getBukkitEntity(), Arrays.asList(itemStack), expToDrop));
      IronGolem golem = (IronGolem) damagesource.getEntity().getBukkitEntity();
      golem.getWorld().spawnEntity(golem.getLocation(), EntityType.PRIMED_TNT);
      return true;
    } else {
      super.damageEntity(damagesource, f);
      return false;
    }
  }
}
