
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena.managers.spawner;

import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;

import java.util.Random;

/**
 * The interface for enemy spawner
 */
public interface EnemySpawner extends Comparable<EnemySpawner> {
  /**
   * Get the name of the spawner
   *
   * @return the name
   */
  String getName();

  /**
   * Get the priority of the spawner (the higher the sooner)
   */
  default int getPriority() {
    return 0;
  }

  /**
   * Handle the spawn
   *
   * @param random the random number generator
   * @param arena  the arena the manager is trying to spawn enemies
   * @param spawn  the amount to spawn
   */
  void spawn(Random random, Arena arena, int spawn);

  /**
   * Get the defined itemstack for drop reasons
   *
   * @return ItemStack that gets dropped on enemy death
   */
  ItemStack getDropItem();

  @Override
  default int compareTo(EnemySpawner spawner) {
    int compareValue = Integer.compare(getPriority(), spawner.getPriority());
    if (compareValue == 0) {
      return getName().compareTo(spawner.getName());
    }
    return compareValue;
  }
}