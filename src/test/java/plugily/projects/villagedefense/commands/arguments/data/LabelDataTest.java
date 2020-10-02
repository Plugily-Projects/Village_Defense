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

package plugily.projects.villagedefense.commands.arguments.data;

import org.bukkit.ChatColor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class LabelDataTest {

  private LabelData labelData;

  @Before
  public void setUpClass() {
    labelData = new LabelData("&6&lTest", "command", "&fDescription");
  }

  @Test
  public void getText() {
    Assert.assertEquals(ChatColor.GOLD + "" + ChatColor.BOLD + "Test", labelData.getText());
    System.out.println("> LabelData#getText | PASSED");
  }

  @Test
  public void getCommand() {
    Assert.assertEquals("command", labelData.getCommand());
    System.out.println("> LabelData#getCommand | PASSED");
  }

  @Test
  public void getDescription() {
    Assert.assertEquals(ChatColor.WHITE + "Description", labelData.getDescription());
    System.out.println("> LabelData#getDescription | PASSED");
  }

}