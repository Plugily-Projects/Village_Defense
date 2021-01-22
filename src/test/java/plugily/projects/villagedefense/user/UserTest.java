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

package plugily.projects.villagedefense.user;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.Listener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import plugily.projects.villagedefense.MockUtils;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.handlers.language.Messages;

import static org.junit.Assert.assertEquals;

/**
 * @author Plajer
 * <p>
 * Created at 06.06.2019
 */
public class UserTest implements Listener {

  private User user;
  private PlayerMock player;

  @Before
  public void setUpClass() {
    MockUtils.getPluginMockSafe();
    this.player = MockUtils.getDefaultPlayer();
    this.user = new User(player);
    MockUtils.getServerMockSafe().getPluginManager().registerEvents(this, MockUtils.getPluginMockSafe());
  }

  @Test
  public void getPlayer() {
      assertEquals(player, user.getPlayer());
  }

  @Test
  public void spectatorTest() {
    Assert.assertFalse(user.isSpectator());

    user.setSpectator(true);
    Assert.assertTrue(user.isSpectator());
  }

  @Test
  public void statisticTest() {
      assertEquals(0, user.getStat(StatsStorage.StatisticType.ORBS));

      user.setStat(StatsStorage.StatisticType.KILLS, 1090);
      assertEquals(1090, user.getStat(StatsStorage.StatisticType.KILLS));

      user.addStat(StatsStorage.StatisticType.LEVEL, 3);
      assertEquals(3, user.getStat(StatsStorage.StatisticType.LEVEL));
  }

  @Test
  public void cooldownTest() {
    Assert.assertTrue(user.checkCanCastCooldownAndMessage("random_ability"));
    user.setCooldown("random_ability", 5);
    Assert.assertFalse(user.checkCanCastCooldownAndMessage("random_ability"));

    String message = MockUtils.getPluginMockSafe().getChatManager().colorMessage(Messages.KITS_ABILITY_STILL_ON_COOLDOWN);
    message = message.replaceFirst("%COOLDOWN%", Long.toString(user.getCooldown("random_ability")));

    assertEquals(message, player.nextMessage());
  }

}