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

package pl.plajer.villagedefense;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

/**
 * @author Plajer
 * <p>
 * Created at 26.05.2019
 */
public class MockUtils {

  private static PlayerMock playerMock;
  private static MainMock mockPlugin;

  public static ServerMock getServerMockSafe() {
    if (MockBukkit.isMocked()) {
      return MockBukkit.getMock();
    }
    return MockBukkit.mock();
  }

  public static PlayerMock getDefaultPlayer() {
    return getServerMockSafe().addPlayer();
  }

  public static PlayerMock getConstantPlayer() {
    if (playerMock == null) {
      playerMock = getServerMockSafe().addPlayer();
    }
    return playerMock;
  }

  public static MainMock getPluginMockSafe() {
    if (mockPlugin == null) {
      getServerMockSafe();
      MockUtils.mockPlugin = MockBukkit.load(MainMock.class);
    }
    return mockPlugin;
  }

}
