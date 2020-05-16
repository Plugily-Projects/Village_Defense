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

package pl.plajer.villagedefense.arena.options;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class ArenaOptionTest {

  @Test
  public void getDefaultValue() {
    Assert.assertEquals(0, ArenaOption.TIMER.getDefaultValue());
    Assert.assertEquals(2, ArenaOption.MINIMUM_PLAYERS.getDefaultValue());
    Assert.assertEquals(10, ArenaOption.MAXIMUM_PLAYERS.getDefaultValue());
    Assert.assertEquals(1, ArenaOption.WAVE.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.BAR_TOGGLE_VALUE.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ROTTEN_FLESH_LEVEL.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ROTTEN_FLESH_AMOUNT.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.TOTAL_ORBS_SPENT.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.TOTAL_KILLED_ZOMBIES.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ZOMBIES_TO_SPAWN.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ZOMBIE_GLITCH_CHECKER.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ZOMBIE_SPAWN_COUNTER.getDefaultValue());
    Assert.assertEquals(0, ArenaOption.ZOMBIE_IDLE_PROCESS.getDefaultValue());
    Assert.assertEquals(1, ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER.getDefaultValue());
    System.out.println("> ArenaOption#getDefaultValue | PASSED");
  }

}