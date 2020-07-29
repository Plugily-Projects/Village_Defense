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

package pl.plajer.villagedefense.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.plajer.villagedefense.MockUtils;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;

/**
 * @author Plajer
 * <p>
 * Created at 06.06.2019
 */
public class CompatMaterialConstantsTest {

  @Before
  public void setUpClass() {
    //to make sure test is done for 1.12
    Assert.assertTrue(MockUtils.getPluginMockSafe().is1_12_R1());
  }

  @Test
  public void getPlayerHead() {
    Assert.assertEquals(Material.SKULL_ITEM, CompatMaterialConstants.getPlayerHead());
  }

  @Test
  public void getPlayerHeadItem() {
    Assert.assertEquals(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), CompatMaterialConstants.getPlayerHeadItem());
  }

  @Test
  public void getOakDoorBlock() {
    Assert.assertEquals(Material.WOODEN_DOOR, CompatMaterialConstants.getOakDoorBlock());
  }

  @Test
  public void getOakDoorItem() {
    Assert.assertEquals(Material.WOOD_DOOR, CompatMaterialConstants.getOakDoorItem());
  }

}