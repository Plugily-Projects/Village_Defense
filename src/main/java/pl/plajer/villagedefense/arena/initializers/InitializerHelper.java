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

package pl.plajer.villagedefense.arena.initializers;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.creatures.CreatureUtils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

/**
 * Internal helper class
 */
class InitializerHelper {

  static void prepareHardZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    zombie.setRemoveWhenFarAway(false);
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareSoftHardZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
    zombie.setRemoveWhenFarAway(false);
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void preparePlayerBusterZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
    zombie.getEquipment().setHelmetDropChance(0.0F);
    zombie.getEquipment().setItemInMainHandDropChance(0F);
    zombie.getEquipment().setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
    zombie.getEquipment().setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
    zombie.getEquipment().setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareVillagerSlayerZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setItemInMainHand(new ItemStack(Material.EMERALD));
    zombie.getEquipment().setItemInMainHandDropChance(0F);
    zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareKnockbackResistantZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLD_AXE));
    zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    zombie.setRemoveWhenFarAway(false);
    CreatureUtils.applyAttributes(zombie, arena);
  }


}
