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

package plugily.projects.villagedefense;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class OptionEnumTest {

  @Test
  public void getPath() {
    Assert.assertEquals("Bossbar-Enabled", ConfigPreferences.Option.BOSSBAR_ENABLED.getPath());
    Assert.assertEquals("ChatFormat-Enabled", ConfigPreferences.Option.CHAT_FORMAT_ENABLED.getPath());
    Assert.assertEquals("DatabaseActivated", ConfigPreferences.Option.DATABASE_ENABLED.getPath());
    Assert.assertEquals("InventoryManager", ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED.getPath());
    System.out.println("> ConfigPreferences.Option#getPath() | PASSED");
  }

  @Test
  public void getDefault() {
    Assert.assertTrue(ConfigPreferences.Option.BOSSBAR_ENABLED.getDefault());
    Assert.assertTrue(ConfigPreferences.Option.CHAT_FORMAT_ENABLED.getDefault());
    Assert.assertFalse(ConfigPreferences.Option.DATABASE_ENABLED.getDefault());
    Assert.assertTrue(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED.getDefault());
    System.out.println("> ConfigPreferences.Option#getDefault() | PASSED");
  }

}
