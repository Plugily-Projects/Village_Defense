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

package plugily.projects.villagedefense.handlers;

import org.bukkit.ChatColor;
import org.junit.Assert;
import org.junit.Test;
import plugily.projects.villagedefense.MainMock;
import plugily.projects.villagedefense.MockUtils;
import plugily.projects.villagedefense.handlers.language.Messages;

/**
 * @author Plajer
 * <p>
 * Created at 02.06.2019
 */
public class ChatManagerTest {

  @Test
  public void colorRawMessage() {
    String rawColored = "&6Test";
    String chatColored = ChatColor.GOLD + "Test";
    Assert.assertEquals(chatColored, MockUtils.getPluginMockSafe().getChatManager().colorRawMessage(rawColored));
  }

  @Test
  public void colorMessage() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    //in case if message has been changed replace with other
    Assert.assertEquals(plugin.getChatManager().colorRawMessage("&lInactive..."), plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_INACTIVE));
  }

}