package plugily.projects.villagedefense.arena.managers;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.BabyZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.FastZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.GolemBusterSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.HalfInvisibleZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.HardZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.KnockbackResistantZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.PlayerBusterSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.SoftHardZombieSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.VillagerBusterSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.VillagerSlayerSpawner;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.arena.options.ArenaOption;

/**
 * The registry for all {@link EnemySpawner}
 */
public class EnemySpawnerRegistry {
  private final Set<EnemySpawner> enemySpawnerSet = new TreeSet<>(Collections.reverseOrder());
  private final Main plugin;

  public EnemySpawnerRegistry(Main plugin) {
    this.plugin = plugin;

    enemySpawnerSet.add(new FastZombieSpawner());
    enemySpawnerSet.add(new VillagerSlayerSpawner());
    enemySpawnerSet.add(new KnockbackResistantZombieSpawner());
    enemySpawnerSet.add(new HardZombieSpawner());
    enemySpawnerSet.add(new SoftHardZombieSpawner());
    enemySpawnerSet.add(new PlayerBusterSpawner());
    enemySpawnerSet.add(new GolemBusterSpawner());
    enemySpawnerSet.add(new VillagerBusterSpawner());
    enemySpawnerSet.add(new BabyZombieSpawner());
    enemySpawnerSet.add(new HalfInvisibleZombieSpawner());
  }

  /**
   * Spawn the enemies at the arena
   *
   * @param random the random instance
   * @param arena  the arena
   */
  public void spawnEnemies(Random random, Arena arena) {
    int wave = arena.getWave();
    int spawn = arena.getWave();
    int zombiesLimit = plugin.getConfig().getInt("Zombies-Limit", 75);
    if (zombiesLimit < wave) {
      spawn = (int) Math.ceil(zombiesLimit / 2.0);
    }

    arena.addOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 1);
    if (arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER) == 20) {
      arena.setOptionValue(ArenaOption.ZOMBIE_SPAWN_COUNTER, 0);
    }

    for (EnemySpawner enemySpawner : enemySpawnerSet) {
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
