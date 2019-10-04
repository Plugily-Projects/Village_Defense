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

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.MockUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 06.06.2019
 */
public class SpecialItemManagerTest {

  private Main plugin;
  private SpecialItem dummyItem1;
  private ItemStack item1;
  private SpecialItem dummyItem2;
  private ItemStack item2;

  @Before
  public void setUpClass() {
    MockUtils.getServerMockSafe();
    this.plugin = MockUtils.getPluginMockSafe();
    //create some dummy items
    this.item1 = new ItemBuilder(Material.BARRIER).name("test_item1").build();
    this.dummyItem1 = new SpecialItem("test", item1, 2, SpecialItem.DisplayStage.LOBBY);
    plugin.getSpecialItemManager().addItem(dummyItem1);
    this.item2 = new ItemBuilder(Material.BED).name("item_test2").build();
    this.dummyItem2 = new SpecialItem("test2", item2, 4, SpecialItem.DisplayStage.LOBBY);
    plugin.getSpecialItemManager().addItem(dummyItem2);
  }

  @Test
  public void getSpecialItem() {
    Assert.assertNotEquals(SpecialItem.INVALID_ITEM, plugin.getSpecialItemManager().getSpecialItem("test"));
    Assert.assertEquals(SpecialItem.INVALID_ITEM, plugin.getSpecialItemManager().getSpecialItem("invalid_itemNAME"));
    Assert.assertEquals(dummyItem1, plugin.getSpecialItemManager().getSpecialItem("test"));
    Assert.assertEquals(dummyItem2, plugin.getSpecialItemManager().getSpecialItem("test2"));
  }

  @Test
  public void getRelatedSpecialItem() {
    Assert.assertTrue(dummyItem1.getItemStack().isSimilar(plugin.getSpecialItemManager().getRelatedSpecialItem(item1).getItemStack()));
    Assert.assertEquals(SpecialItem.INVALID_ITEM, plugin.getSpecialItemManager().getRelatedSpecialItem(new ItemStack(Material.DIRT)));
  }

}