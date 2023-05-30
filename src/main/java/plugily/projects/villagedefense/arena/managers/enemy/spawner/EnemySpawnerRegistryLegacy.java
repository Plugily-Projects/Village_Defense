
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

package plugily.projects.villagedefense.arena.managers.enemy.spawner;

import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.BabyZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.FastZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.GolemBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.HalfInvisibleZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.HardZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.KnockbackResistantZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.PlayerBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.SoftHardZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.VillagerBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.VillagerSlayerSpawner;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomRideableCreature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * The registry for all {@link EnemySpawner}
 */
public class EnemySpawnerRegistryLegacy {
  final Set<EnemySpawner> enemySpawnerSet = new TreeSet<>(Collections.reverseOrder());
  final Set<CustomRideableCreature> rideableCreatures = new HashSet<>();
  final Main plugin;

  public EnemySpawnerRegistryLegacy(Main plugin) {
    this.plugin = plugin;
    registerCreatures();
    registerRideableCreatures();
  }

  public void registerRideableCreatures() {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return;
    }
  }

  public void registerCreatures() {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      enemySpawnerSet.add(new BabyZombieSpawner());
      enemySpawnerSet.add(new FastZombieSpawner());
      enemySpawnerSet.add(new GolemBusterSpawner());
      enemySpawnerSet.add(new HalfInvisibleZombieSpawner());
      enemySpawnerSet.add(new HardZombieSpawner());
      enemySpawnerSet.add(new KnockbackResistantZombieSpawner());
      enemySpawnerSet.add(new PlayerBusterSpawner());
      enemySpawnerSet.add(new SoftHardZombieSpawner());
      enemySpawnerSet.add(new VillagerBusterSpawner());
      enemySpawnerSet.add(new VillagerSlayerSpawner());
      return;
    }
  }

  /**
   * Spawn the enemies at the arena
   *
   * @param random the random instance
   * @param arena  the arena
   */
  public void spawnEnemies(Random random, Arena arena) {
    int spawn = arena.getWave();
    int zombiesLimit = plugin.getConfig().getInt("Limit.Spawn.Zombies", 75);
    if(zombiesLimit < spawn) {
      spawn = (int) Math.ceil(zombiesLimit / 2.0);
    }
    String zombieSpawnCounterOption = "ZOMBIE_SPAWN_COUNTER";
    arena.changeArenaOptionBy(zombieSpawnCounterOption, 1);
    if(arena.getArenaOption(zombieSpawnCounterOption) == 20) {
      arena.setArenaOption(zombieSpawnCounterOption, 0);
    }

    List<EnemySpawner> enemySpawners = new ArrayList<>(enemySpawnerSet);
    Collections.shuffle(enemySpawners);
    for(EnemySpawner enemySpawner : enemySpawners) {
      plugin.getDebugger().debug("Trying enemy spawn for " + enemySpawner.getName());
      enemySpawner.spawn(random, arena, spawn);
    }
  }

  /**
   * Get the set of enemy spawners
   *
   * @return the set of enemy spawners
   */
  public Set<EnemySpawner> getEnemySpawnerSet() {
    return enemySpawnerSet;
  }

  public Set<CustomRideableCreature> getRideableCreatures() {
    return rideableCreatures;
  }

  /**
   * Get the rideable creature by its type
   *
   * @param type the tyoe
   * @return the rideable creature
   */
  public Optional<CustomRideableCreature> getRideableCreatureByName(CustomRideableCreature.RideableType type) {
    return rideableCreatures.stream()
        .filter(creature -> creature.getRideableType().equals(type))
        .findFirst();
  }

  /**
   * Get the enemy spawner by its name
   *
   * @param name the name
   * @return the enemy spawner
   */
  public Optional<EnemySpawner> getSpawnerByName(String name) {
    return enemySpawnerSet.stream()
        .filter(enemySpawner -> enemySpawner.getName().equals(name))
        .findFirst();
  }
}
