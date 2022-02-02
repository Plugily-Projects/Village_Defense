
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
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class HalfInvisibleZombieSpawner implements SimpleEnemySpawner {
  @Override
  public int getMinWave() {
    return 7;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    return 1D / 8;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    int total = 0;
    if(wave > 23) {
      total++;
    }
    if(wave > 15) {
      total += spawnAmount - 13;
    } else if(wave > 7) {
      total += spawnAmount - 5;
    }
    return total;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return true;
  }

  @Override
  public Creature spawn(Location location) {
    Creature fastZombie = CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
    fastZombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    fastZombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    return fastZombie;
  }

  @Override
  public String getName() {
    return "HalfInvisibleZombie";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
