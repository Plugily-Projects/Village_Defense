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

package plugily.projects.villagedefense.handlers.reward;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 26.05.2019
 */
public class RewardTest {

  private Reward reward;

  @Before
  public void setUpClass() {
    reward = new Reward(Reward.RewardType.END_GAME, "chance(10):p:test");
  }

  @Test
  public void getExecutor() {
    Assert.assertEquals(Reward.RewardExecutor.PLAYER, reward.getExecutor());
    System.out.println("> Reward#getExecutor | PASSED");
  }

  @Test
  public void getExecutableCode() {
    Assert.assertEquals("test", reward.getExecutableCode());
    System.out.println("> Reward#getExecutableCode| PASSED");
  }

  @Test
  public void getChance() {
    Assert.assertEquals(10, reward.getChance(), 0);
    System.out.println("> Reward#getChance | PASSED");
  }

  @Test
  public void getWaveExecute() {
    Assert.assertEquals(-1, reward.getWaveExecute());
    System.out.println("> Reward#getWaveExecute | PASSED");
  }

  @Test
  public void getType() {
    Assert.assertEquals(Reward.RewardType.END_GAME, reward.getType());
    System.out.println("> Reward#getType | PASSED");
  }

}