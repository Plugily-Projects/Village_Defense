/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 12/08/2014.
 */
public class WeaponHelper {

  private WeaponHelper() {
  }

  public static ItemStack getEnchantedBow(Enchantment enchantment, int level) {
    ItemStack itemStack = new ItemStack(Material.BOW);
    itemStack.addUnsafeEnchantment(enchantment, level);
    return itemStack;
  }

  public static ItemStack getEnchantedBow(Enchantment[] enchantments, int[] levels) {
    ItemStack itemStack = new ItemStack(Material.BOW);
    return getEnchanted(itemStack, enchantments, levels);
  }

  public static ItemStack getEnchanted(ItemStack itemStack, Enchantment[] enchantments, int[] levels) {
    Map<Enchantment, Integer> enchants = new HashMap<>();
    for (int i = 0; i < enchantments.length; i++) {
      enchants.put(enchantments[i], levels[i]);
    }
    itemStack.addUnsafeEnchantments(enchants);
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
        itemStack = XMaterial.IRON_SWORD.parseItem();
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case GOLD:
        itemStack = XMaterial.GOLDEN_SWORD.parseItem();
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case DIAMOND:
        itemStack = XMaterial.DIAMOND_SWORD.parseItem();
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
        return itemStack;
      case STONE:
        itemStack = XMaterial.STONE_SWORD.parseItem();
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
