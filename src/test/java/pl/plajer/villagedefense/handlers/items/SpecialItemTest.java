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

package pl.plajer.villagedefense.handlers.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.plajer.villagedefense.MockUtils;

/**
 * @author Plajer
 * <p>
 * Created at 06.06.2019
 */
public class SpecialItemTest {

  private SpecialItem specialItem;

  @Before
  public void setUpClass() {
    MockUtils.getServerMockSafe();
    specialItem = new SpecialItem("Test", new ItemStack(Material.STONE), 2, SpecialItem.DisplayStage.LOBBY);
  }

  @Test
  public void getName() {
    Assert.assertEquals("Test", specialItem.getName());
  }

  @Test
  public void getItemStack() {
    Assert.assertEquals(new ItemStack(Material.STONE), specialItem.getItemStack());
  }

  @Test
  public void getSlot() {
    Assert.assertEquals(2, specialItem.getSlot());
  }

  @Test
  public void setSlot() {
    specialItem.setSlot(6);
    Assert.assertEquals(6, specialItem.getSlot());
  }

}