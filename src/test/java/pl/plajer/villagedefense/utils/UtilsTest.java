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

import org.bukkit.block.BlockFace;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 08.04.2019
 */
public class UtilsTest {

  @Test
  public void getFacingByByte() {
    Assert.assertEquals(BlockFace.EAST, Utils.getFacingByByte((byte) 3));
    Assert.assertEquals(BlockFace.NORTH, Utils.getFacingByByte((byte) 4));
    System.out.println("> Utils#getFacingByByte -> 3, 4 | PASSED");
  }

  @Test
  public void isInteger() {
    Assert.assertFalse(Utils.isInteger("1.5"));
    System.out.println("> Utils#isInteger -> false | PASSED");
    Assert.assertFalse(Utils.isInteger("text"));
    System.out.println("> Utils#isInteger -> false #2 | PASSED");
    Assert.assertTrue(Utils.isInteger("123"));
    System.out.println("> Utils#isInteger -> true | PASSED");
  }

}