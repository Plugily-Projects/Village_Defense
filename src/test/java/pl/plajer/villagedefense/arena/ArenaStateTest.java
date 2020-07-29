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

package pl.plajer.villagedefense.arena;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class ArenaStateTest {

  @Test
  public void getFormattedName() {
    Assert.assertEquals("Waiting", ArenaState.WAITING_FOR_PLAYERS.getFormattedName());
    Assert.assertEquals("Starting", ArenaState.STARTING.getFormattedName());
    Assert.assertEquals("Playing", ArenaState.IN_GAME.getFormattedName());
    Assert.assertEquals("Finishing", ArenaState.ENDING.getFormattedName());
    Assert.assertEquals("Restarting", ArenaState.RESTARTING.getFormattedName());
    System.out.println("> ArenaState#getFormattedName | PASSED");
  }
}