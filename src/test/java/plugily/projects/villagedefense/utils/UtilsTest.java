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

package plugily.projects.villagedefense.utils;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.InventoryMock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;
import pl.plajerlair.commonsbox.number.NumberUtils;
import plugily.projects.villagedefense.MainMock;
import plugily.projects.villagedefense.MockUtils;
import plugily.projects.villagedefense.handlers.language.Messages;

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
    Assert.assertFalse(NumberUtils.isInteger("1.5"));
    System.out.println("> Utils#isInteger false for 1.5 | PASSED");
    Assert.assertFalse(NumberUtils.isInteger("text"));
    System.out.println("> Utils#isInteger false for 'text' | PASSED");
    Assert.assertTrue(NumberUtils.isInteger("123"));
    System.out.println("> Utils#isInteger true for 123 | PASSED");
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

  @Test
  public void hasPermission() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    PlayerMock player = MockUtils.getDefaultPlayer();
    Assert.assertFalse(Utils.hasPermission(player, "never.gained.permission"));
    Assert.assertTrue(player.nextMessage().contains(plugin.getChatManager().colorMessage(Messages.COMMANDS_NO_PERMISSION)));
  }

}