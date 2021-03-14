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

package plugily.projects.villagedefense.arena.initializers;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Internal helper class
 */
class InitializerHelper {

  private InitializerHelper() {
  }

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

  static void prepareVillagerBusterZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
    zombie.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(zombie, 0F);
    zombie.getEquipment().setBoots(XMaterial.LEATHER_BOOTS.parseItem());
    zombie.getEquipment().setLeggings(XMaterial.LEATHER_LEGGINGS.parseItem());
    zombie.getEquipment().setChestplate(XMaterial.LEATHER_CHESTPLATE.parseItem());
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareGolemBusterZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
    zombie.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(zombie, 0F);
    zombie.getEquipment().setBoots(XMaterial.IRON_BOOTS.parseItem());
    zombie.getEquipment().setLeggings(XMaterial.IRON_LEGGINGS.parseItem());
    zombie.getEquipment().setChestplate(XMaterial.IRON_CHESTPLATE.parseItem());
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void preparePlayerBusterZombie(Zombie zombie, Arena arena) {
    zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
    zombie.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(zombie, 0F);
    zombie.getEquipment().setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
    zombie.getEquipment().setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
    zombie.getEquipment().setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareVillagerSlayerZombie(Zombie zombie, Arena arena) {
    VersionUtils.setItemInHand(zombie, XMaterial.EMERALD.parseItem());
    VersionUtils.setItemInHandDropChance(zombie, 0F);
    zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
    CreatureUtils.applyAttributes(zombie, arena);
  }

  static void prepareKnockbackResistantZombie(Zombie zombie, Arena arena) {
    VersionUtils.setItemInHand(zombie, XMaterial.GOLDEN_AXE.parseItem());
    zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    zombie.setRemoveWhenFarAway(false);
    CreatureUtils.applyAttributes(zombie, arena);
  }


}
