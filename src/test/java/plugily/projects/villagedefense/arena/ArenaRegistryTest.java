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

package plugily.projects.villagedefense.arena;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import plugily.projects.villagedefense.MockUtils;

/**
 * @author Plajer
 * <p>
 * Created at 12.06.2019
 */
public class ArenaRegistryTest {

  private PlayerMock player;
  private ArenaMock arena;

  @Before
  public void setUp() {
    this.player = MockUtils.getConstantPlayer();
    this.arena = MockUtils.getPluginMockSafe().getTestArena();
  }

  @Test
  public void isInArena() {
    Assert.assertFalse(ArenaRegistry.isInArena(player));
    arena.getPlayers().add(player);
    Assert.assertTrue(ArenaRegistry.isInArena(player));
    arena.getPlayers().remove(player);
  }

  @Test
  public void getArenaOfPlayer() {
    Assert.assertNull(ArenaRegistry.getArena(player));
    arena.getPlayers().add(player);
    Assert.assertEquals(arena, ArenaRegistry.getArena(player));
    arena.getPlayers().remove(player);
  }

  @Test
  public void getArenaById() {
    Assert.assertNull(ArenaRegistry.getArena("non-existing_Arena"));
    Assert.assertEquals(arena, ArenaRegistry.getArena("test-arena"));
  }

}