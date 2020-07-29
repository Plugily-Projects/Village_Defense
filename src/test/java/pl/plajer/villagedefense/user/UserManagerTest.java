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

package pl.plajer.villagedefense.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.MockUtils;
import pl.plajer.villagedefense.user.data.FileStats;

/**
 * @author Plajer
 * <p>
 * Created at 06.06.2019
 */
public class UserManagerTest {

  private Main plugin;
  private User user;

  @Before
  public void setUpClass() {
    this.plugin = MockUtils.getPluginMockSafe();
    this.user = plugin.getUserManager().getUser(MockUtils.getDefaultPlayer());
  }

  @Test
  public void getUser() {
    Assert.assertNotNull(user);
  }

  @Test
  public void getDatabase() {
    Assert.assertTrue(plugin.getUserManager().getDatabase() instanceof FileStats);
  }

}