
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

public class VillagerBusterSpawner implements SimpleEnemySpawner {
  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return 1D / 3;
    }
    if(wave >= 15) {
      return 1D / 8;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return spawnAmount / 4;
    }
    if(wave >= 15) {
      return spawnAmount - 13;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || (wave >= 15 && !arena.getVillagers().isEmpty());
  }

  @Override
  public Creature spawn(Location location) {
    Creature villagerBuster = CreatureUtils.getCreatureInitializer().spawnVillagerBuster(location);
    villagerBuster.getEquipment().setHelmet(new ItemStack(Material.TNT));
    villagerBuster.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(villagerBuster, 0F);
    villagerBuster.getEquipment().setBoots(XMaterial.LEATHER_BOOTS.parseItem());
    villagerBuster.getEquipment().setLeggings(XMaterial.LEATHER_LEGGINGS.parseItem());
    villagerBuster.getEquipment().setChestplate(XMaterial.LEATHER_CHESTPLATE.parseItem());
    return villagerBuster;
  }

  @Override
  public String getName() {
    return "VillagerBuster";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
