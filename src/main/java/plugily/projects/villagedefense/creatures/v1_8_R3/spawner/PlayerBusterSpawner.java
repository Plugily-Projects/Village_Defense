
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.creatures.v1_8_R3.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class PlayerBusterSpawner implements SimpleEnemySpawner {
  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return 1D / 3;
    }
    if(wave > 10) {
      return 1D / 8;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return spawnAmount / 4;
    }
    if(wave > 10) {
      return spawnAmount - 8;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || wave > 10;
  }

  @Override
  public Creature spawn(Location location) {
    Creature playerBuster = CreatureUtils.getCreatureInitializer().spawnPlayerBuster(location);
    playerBuster.getEquipment().setHelmet(new ItemStack(Material.TNT));
    playerBuster.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(playerBuster, 0F);
    playerBuster.getEquipment().setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
    playerBuster.getEquipment().setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
    playerBuster.getEquipment().setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
    return playerBuster;
  }

  @Override
  public String getName() {
    return "PlayerBuster";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
