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

package plugily.projects.villagedefense.creatures.v1_14_R1;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tom on 15/08/2014.
 */
public class PlayerBuster extends EntityZombie {

  public PlayerBuster(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public PlayerBuster(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    getNavigation().q().b(true);

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalBreakDoor(this, enumDifficulty -> true));

    this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    this.goalSelector.a(5, new PathfinderGoalBreakDoorFaster(this));
    this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F)); // this one to look at human
    this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, true));// this one to target
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, false));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));
    this.setHealth(1);
    this.p(true);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(200.0D);
  }

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    if (damagesource != null && damagesource.getEntity() != null && damagesource.getEntity().getBukkitEntity().getType() == EntityType.PLAYER) {
      if (CreatureUtils.getPlugin().getUserManager().getUser((Player) damagesource.getEntity().getBukkitEntity()).isSpectator()) {
        return true;
      }
      ItemStack[] itemStack = new ItemStack[] {new ItemStack(Material.ROTTEN_FLESH)};
      Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) this.getBukkitEntity(), new ArrayList<>(Arrays.asList(itemStack)), expToDrop));
      getBukkitEntity().getWorld().spawnEntity(getBukkitEntity().getLocation(), EntityType.PRIMED_TNT);
      this.die();
      return true;
    }
    super.damageEntity(damagesource, f);
    return false;
  }
}
