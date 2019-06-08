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

package pl.plajer.villagedefense.commands.arguments.data;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Plajer
 * <p>
 * Created at 27.05.2019
 */
public class CommandArgumentTest {

  private CommandArgument commandArgument;

  @Before
  public void setUpClass() {
    commandArgument = new CommandArgument("argument", "no.permission", CommandArgument.ExecutorType.BOTH);
  }

  @Test
  public void getArgumentName() {
    Assert.assertEquals("argument", commandArgument.getArgumentName());
    System.out.println("> CommandArgument#getArgumentName | PASSED");
  }

  @Test
  public void getPermissions() {
    Assert.assertEquals(Collections.singletonList("no.permission"), commandArgument.getPermissions());
    System.out.println("> CommandArgument#getPermissions | PASSED");
  }

  @Test
  public void getValidExecutors() {
    Assert.assertEquals(CommandArgument.ExecutorType.BOTH, commandArgument.getValidExecutors());
    System.out.println("> CommandArgument#getValidExecutors | PASSED");
  }

}