
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_8_R3.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class FastZombieSpawner implements SimpleEnemySpawner {
  @Override
  public boolean canApplyHolidayEffect() {
    return true;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if(spawnAmount < 5 || arena.getEnemies().isEmpty()) {
      return 1;
    }

    if(phase == 5 && wave <= 7) {
      return 2d / 3;
    }

    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    return (spawnAmount < 5 || (phase == 5 && wave <= 7) || arena.getEnemies().isEmpty()) ? spawnAmount : 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return arena.getEnemies().isEmpty() || spawnAmount < 5 || (phase == 5 && wave <= 7);
  }

  @Override
  public Creature spawn(Location location) {
    return CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
  }

  @Override
  public String getName() {
    return "FastZombie";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
