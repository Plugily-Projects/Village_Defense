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

import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 12.06.2019
 */
public class ArenaMock extends Arena {

  public ArenaMock() {
    super("test-arena", "test-map-name");
  }

  @Test
  public void testValues() {
    Assert.assertEquals("test-arena", super.getId());
    Assert.assertEquals("test-map-name", super.getMapName());

    super.setMapName("testt");
    Assert.assertEquals("testt", super.getMapName());
  }

  @Override
  public void spawnVillager(Location location) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnWolf(Location location, Player player) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnGolem(Location location, Player player) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnFastZombie(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnBabyZombie(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnHardZombie(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnPlayerBuster(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnGolemBuster(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnVillagerBuster(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnSoftHardZombie(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnHalfInvisibleZombie(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnKnockbackResistantZombies(Random random) {
    throw new UnimplementedOperationException();
  }

  @Override
  public void spawnVillagerSlayer(Random random) {
    throw new UnimplementedOperationException();
  }
}
