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

package pl.plajer.villagedefense.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 12/08/2014.
 */
public class WeaponHelper {

  public static ItemStack getEnchantedBow(Enchantment enchantment, int level) {
    ItemStack itemStack = new ItemStack(Material.BOW);
    itemStack.addUnsafeEnchantment(enchantment, level);
    return itemStack;
  }

  public static ItemStack getEnchantedBow(Enchantment[] enchantments, int[] levels) {
    ItemStack itemStack = new ItemStack(Material.BOW);
    Map<Enchantment, Integer> enchantmentsmap = new HashMap<>();
    int i = 0;
    for (Enchantment enchantment : enchantments) {
      enchantmentsmap.put(enchantment, levels[i]);
      i++;
    }
    itemStack.addUnsafeEnchantments(enchantmentsmap);
    return itemStack;
  }

  public static ItemStack getEnchanted(ItemStack itemStack, Enchantment[] enchantments, int[] levels) {
    Map<Enchantment, Integer> enchantmentsmap = new HashMap<>();
    int i = 0;
    for (Enchantment enchantment : enchantments) {
      enchantmentsmap.put(enchantment, levels[i]);
      i++;
    }
    itemStack.addUnsafeEnchantments(enchantmentsmap);
    return itemStack;
  }

  public static ItemStack getUnBreakingSword(ResourceType type, int level) {
    ItemStack itemStack;
    switch (type) {
      case WOOD:
        itemStack = XMaterial.WOODEN_SWORD.parseItem();
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case IRON:
        itemStack = new ItemStack(Material.IRON_SWORD);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case GOLD:
        itemStack = XMaterial.GOLDEN_SWORD.parseItem();
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case DIAMOND:
        itemStack = new ItemStack(Material.DIAMOND_SWORD);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case STONE:
        itemStack = new ItemStack(Material.STONE_SWORD);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      default:
        return getUnBreakingSword(ResourceType.WOOD, 10);
    }
  }

  public enum ResourceType {
    WOOD, GOLD, STONE, DIAMOND, IRON
  }

}
