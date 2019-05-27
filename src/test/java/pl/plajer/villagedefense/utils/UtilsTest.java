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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;

import pl.plajer.villagedefense.MockUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import be.seeseemelk.mockbukkit.inventory.InventoryMock;

/**
 * @author Plajer
 * <p>
 * Created at 08.04.2019
 */
public class UtilsTest {

  @Test
  public void getBlockFaceFacingByteValue() {
    Assert.assertEquals(BlockFace.SOUTH, Utils.getFacingByByte((byte) 1));
    Assert.assertEquals(BlockFace.WEST, Utils.getFacingByByte((byte) 2));
    Assert.assertEquals(BlockFace.EAST, Utils.getFacingByByte((byte) 3));
    Assert.assertEquals(BlockFace.NORTH, Utils.getFacingByByte((byte) 4));
    Assert.assertEquals(BlockFace.SOUTH, Utils.getFacingByByte((byte) -1));
    System.out.println("> Utils#getFacingByByte | PASSED");
  }

  @Test
  public void getDoorFacingByteValue() {
    Assert.assertEquals((byte) 0, Utils.getDoorByte(BlockFace.EAST));
    Assert.assertEquals((byte) 1, Utils.getDoorByte(BlockFace.SOUTH));
    Assert.assertEquals((byte) 2, Utils.getDoorByte(BlockFace.WEST));
    Assert.assertEquals((byte) 3, Utils.getDoorByte(BlockFace.NORTH));
    Assert.assertEquals((byte) 0, Utils.getDoorByte(BlockFace.DOWN));
    System.out.println("> Utils#getDoorByte | PASSED");
  }

  @Test
  public void isInteger() {
    Assert.assertFalse(Utils.isInteger("1.5"));
    System.out.println("> Utils#isInteger false for 1.5 | PASSED");
    Assert.assertFalse(Utils.isInteger("text"));
    System.out.println("> Utils#isInteger false for 'text' | PASSED");
    Assert.assertTrue(Utils.isInteger("123"));
    System.out.println("> Utils#isInteger true for 123 | PASSED");
  }


  @Test
  public void isItemNamed() {
    MockUtils.getServerMockSafe();
    Assert.assertFalse(Utils.isNamed(null));
    Assert.assertFalse(Utils.isNamed(new ItemStack(Material.DIRT)));
    Assert.assertTrue(Utils.isNamed(new ItemBuilder(Material.DIRT).name("test").build()));
    System.out.println("> Utils#isNamed | PASSED");
  }

  @Test
  public void serializeInt() {
    Assert.assertEquals(27, Utils.serializeInt(25));
    Assert.assertEquals(9, Utils.serializeInt(1));
    Assert.assertEquals(27, Utils.serializeInt(27));
    System.out.println("> Utils#serializeInt | PASSED");
  }

  @Test
  public void takeOneItem() {
    Player player = MockUtils.getDefaultPlayer();
    player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 3));
    ((InventoryMock) player.getInventory()).assertContainsAtLeast(new ItemStack(Material.SANDSTONE), 3);
    Utils.takeOneItem(player, new ItemStack(Material.SANDSTONE));
    ((InventoryMock) player.getInventory()).assertContainsAtLeast(new ItemStack(Material.SANDSTONE), 2);
    System.out.println("> Utils#takeOneItem | PASSED");
  }

}