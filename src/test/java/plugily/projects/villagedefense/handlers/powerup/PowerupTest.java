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

package plugily.projects.villagedefense.handlers.powerup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 27.05.2019
 */
public class PowerupTest {

  private Powerup powerup;

  @Before
  public void setUpClass() {
    powerup = new Powerup("test", "MyName", "DESC", XMaterial.POPPY, null);
  }

  @Test
  public void getId() {
    Assert.assertEquals("test", powerup.getId());
    System.out.println("> Powerup#getId | PASSED");
  }

  @Test
  public void getName() {
    Assert.assertEquals("MyName", powerup.getName());
    System.out.println("> Powerup#getId | PASSED");
  }

  @Test
  public void getDescription() {
    Assert.assertEquals("DESC", powerup.getDescription());
    System.out.println("> Powerup#getDescription | PASSED");
  }

  @Test
  public void getMaterial() {
    Assert.assertEquals(XMaterial.POPPY, powerup.getMaterial());
    System.out.println("> Powerup#getMaterial | PASSED");
  }
}