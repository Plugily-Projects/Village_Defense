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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;

/**
 * @author Plajer
 * <p>
 * Created at 26.05.2019
 */
public class WeaponHelperTest {

  @Test
  public void getEnchantedBow() {
    MockBukkit.mock();
    ItemStack stack = WeaponHelper.getEnchantedBow(Enchantment.ARROW_DAMAGE, 2);
    Assert.assertSame(stack.getType(), Material.BOW);
    Assert.assertTrue(stack.getEnchantments().containsKey(Enchantment.ARROW_DAMAGE));
    Assert.assertEquals(2, (int) stack.getEnchantments().get(Enchantment.ARROW_DAMAGE));
    System.out.println("> WeaponHelper#getEnchantedBow true material and enchantment with level | PASSED");
  }

}