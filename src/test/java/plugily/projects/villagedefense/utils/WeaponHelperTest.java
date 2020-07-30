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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import plugily.projects.villagedefense.MockUtils;

/**
 * @author Plajer
 * <p>
 * Created at 26.05.2019
 */
public class WeaponHelperTest {

  @Before
  public void setUpClass() {
    MockUtils.getServerMockSafe();
  }

  @Test
  public void getEnchantedBow() {
    ItemStack stack = WeaponHelper.getEnchantedBow(Enchantment.ARROW_DAMAGE, 2);
    Assert.assertSame(Material.BOW, stack.getType());
    Assert.assertTrue(stack.getEnchantments().containsKey(Enchantment.ARROW_DAMAGE));
    Assert.assertEquals(2, (int) stack.getEnchantments().get(Enchantment.ARROW_DAMAGE));
    System.out.println("> WeaponHelper#getEnchantedBow | PASSED");
  }

  @Test
  public void getUnBreakingSword() {
    ItemStack stone = WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 1);
    Assert.assertEquals(Material.STONE_SWORD, stone.getType());
    Assert.assertTrue(stone.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertEquals(1, stone.getEnchantmentLevel(Enchantment.DURABILITY));

    ItemStack iron = WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.IRON, 50);
    Assert.assertEquals(Material.IRON_SWORD, iron.getType());
    Assert.assertTrue(iron.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertEquals(50, iron.getEnchantmentLevel(Enchantment.DURABILITY));

    ItemStack gold = WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.GOLD, 25);
    Assert.assertEquals(XMaterial.GOLDEN_SWORD.parseMaterial(), gold.getType());
    Assert.assertTrue(gold.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertEquals(25, gold.getEnchantmentLevel(Enchantment.DURABILITY));

    ItemStack diamond = WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.DIAMOND, -1);
    Assert.assertEquals(Material.DIAMOND_SWORD, diamond.getType());
    Assert.assertTrue(diamond.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertEquals(-1, diamond.getEnchantmentLevel(Enchantment.DURABILITY));

    ItemStack wood = WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 0);
    Assert.assertEquals(Material.WOOD_SWORD, wood.getType());
    Assert.assertTrue(wood.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertEquals(0, wood.getEnchantmentLevel(Enchantment.DURABILITY));
    System.out.println("> WeaponHelper#getUnBreakingSword | PASSED");
  }

  @Test
  public void getEnchantedBowArray() {
    ItemStack enchantedBowMany = WeaponHelper.getEnchantedBow(new Enchantment[] {Enchantment.DURABILITY, Enchantment.PROTECTION_FALL}, new int[] {1, 2});
    Assert.assertTrue(enchantedBowMany.containsEnchantment(Enchantment.DURABILITY));
    Assert.assertTrue(enchantedBowMany.containsEnchantment(Enchantment.PROTECTION_FALL));
    Assert.assertEquals(1, enchantedBowMany.getEnchantmentLevel(Enchantment.DURABILITY));
    Assert.assertEquals(2, enchantedBowMany.getEnchantmentLevel(Enchantment.PROTECTION_FALL));
    System.out.println("> WeaponHelper#getEnchantedBowArray | PASSED");
  }

}