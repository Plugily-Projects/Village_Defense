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

package pl.plajer.villagedefense.commands.completion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 27.05.2019
 */
public class CompletableArgumentTest {

  private CompletableArgument completableArgument;

  @Before
  public void setUpClass() {
    completableArgument = new CompletableArgument("villagedefense", "testing", Arrays.asList("yes", "no"));
  }

  @Test
  public void getMainCommand() {
    Assert.assertEquals("villagedefense", completableArgument.getMainCommand());
    System.out.println("> CompletableArgument#getMainCommand | PASSED");
  }

  @Test
  public void getArgument() {
    Assert.assertEquals("testing", completableArgument.getArgument());
    System.out.println("> CompletableArgument#getArgument | PASSED");
  }

  @Test
  public void getCompletions() {
    Assert.assertEquals(Arrays.asList("yes", "no"), completableArgument.getCompletions());
    System.out.println("> CompletableArgument#getCompletions | PASSED");
  }
}