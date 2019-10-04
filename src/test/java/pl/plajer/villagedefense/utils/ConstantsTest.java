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

import org.junit.Assert;
import org.junit.Test;

import pl.plajer.villagedefense.utils.constants.Constants;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class ConstantsTest {

  @Test
  public void testAllConstants() {
    Assert.assertEquals("arenas", Constants.Files.ARENAS.getName());
    Assert.assertEquals("config", Constants.Files.CONFIG.getName());
    Assert.assertEquals("language", Constants.Files.LANGUAGE.getName());
    Assert.assertEquals("kits", Constants.Files.KITS.getName());
    Assert.assertEquals("special_items", Constants.Files.SPECIAL_ITEMS.getName());
    Assert.assertEquals("mysql", Constants.Files.MYSQL.getName());
    Assert.assertEquals("rewards", Constants.Files.REWARDS.getName());
    Assert.assertEquals("stats", Constants.Files.STATS.getName());
  }

}