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

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.managers.EnemySpawnManager;
import plugily.projects.villagedefense.arena.managers.ScoreboardManager;
import plugily.projects.villagedefense.arena.managers.ShopManager;
import plugily.projects.villagedefense.arena.managers.maprestorer.MapRestorerManager;
import plugily.projects.villagedefense.arena.managers.maprestorer.MapRestorerManagerLegacy;
import plugily.projects.villagedefense.arena.states.EndingState;
import plugily.projects.villagedefense.arena.states.InGameState;
import plugily.projects.villagedefense.arena.states.RestartingState;
import plugily.projects.villagedefense.arena.states.StartingState;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 17.12.2021
 */
public class Arena extends PluginArena {

  private static Main plugin;
  private final List<Creature> enemies = new ArrayList<>();
  private final List<Wolf> wolves = new ArrayList<>();
  private final List<Villager> villagers = new ArrayList<>();
  private final List<IronGolem> ironGolems = new ArrayList<>();
  private final List<Item> droppedFleshes = new ArrayList<>();
  private MapRestorerManager mapRestorerManager;

  private final Map<SpawnPoint, List<Location>> spawnPoints = new EnumMap<>(SpawnPoint.class);

  private ShopManager shopManager;
  private EnemySpawnManager enemySpawnManager;

  private boolean fighting = false;

  public Arena(String id) {
    super(id);
    shopManager = new ShopManager(this);
    enemySpawnManager = new EnemySpawnManager(this);
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_16_R1)) {
      mapRestorerManager = new MapRestorerManagerLegacy(this);
    } else {
      mapRestorerManager = new MapRestorerManager(this);
    }
    setMapRestorerManager(mapRestorerManager);
    setScoreboardManager(new ScoreboardManager(this));

    addGameStateHandler(ArenaState.ENDING, new EndingState());
    addGameStateHandler(ArenaState.IN_GAME, new InGameState());
    addGameStateHandler(ArenaState.RESTARTING, new RestartingState());
    addGameStateHandler(ArenaState.STARTING, new StartingState());
  }

  public static void init(Main plugin) {
    Arena.plugin = plugin;
  }

  @Override
  public Main getPlugin() {
    return plugin;
  }

  private void setDefaultValues() {
    for(SpawnPoint point : SpawnPoint.values()) {
      spawnPoints.put(point, new ArrayList<>());
    }
  }

  public ShopManager getShopManager() {
    return shopManager;
  }

  public EnemySpawnManager getEnemySpawnManager() {
    return enemySpawnManager;
  }


  public void spawnVillagers() {
    List<Location> villagerSpawns = getVillagerSpawns();
    if(villagerSpawns.isEmpty()) {
      getPlugin().getDebugger().debug(Level.WARNING, "No villager spawns set for {0} game won't start", getId());
      return;
    }

    int amount = getPlugin().getConfig().getInt("Villager-Amount", 10);
    int spawnSize = villagerSpawns.size();
    for(int i = 0; i < amount; i++) {
      spawnVillager(villagerSpawns.get(i % spawnSize));
    }

    if(villagers.isEmpty()) {
      getPlugin().getDebugger().debug(Level.WARNING, "Spawning villagers for {0} failed! Are villager spawns set in safe and valid locations?", getId());
    }
  }

  public boolean isFighting() {
    return fighting;
  }

  public void setFighting(boolean fighting) {
    this.fighting = fighting;
  }


  /**
   * Get list of already spawned enemies.
   * This will only return alive enemies not total enemies in current wave.
   *
   * @return list of spawned enemies in arena
   */
  @NotNull
  public List<Creature> getEnemies() {
    return enemies;
  }

  public void removeEnemy(Creature enemy) {
    enemies.remove(enemy);
  }

  @NotNull
  public List<Location> getVillagerSpawns() {
    return spawnPoints.getOrDefault(SpawnPoint.VILLAGER, new ArrayList<>());
  }

  public void addVillagerSpawn(Location location) {
    getVillagerSpawns().add(location);
  }

  public void addZombieSpawn(Location location) {
    getZombieSpawns().add(location);
  }

  @NotNull
  public List<Item> getDroppedFleshes() {
    return droppedFleshes;
  }

  public void addDroppedFlesh(Item item) {
    droppedFleshes.add(item);
  }

  public void removeDroppedFlesh(Item item) {
    droppedFleshes.remove(item);
  }

  public int getZombiesLeft() {
    return getArenaOption("ZOMBIES_TO_SPAWN") + enemies.size();
  }

  public int getWave() {
    return getArenaOption("WAVE");
  }

  /**
   * Should be used with endWave.
   *
   * @param wave new game wave
   * @see ArenaManager#endWave(Arena)
   */
  public void setWave(int wave) {
    setArenaOption("WAVE", wave);
  }

  public void spawnVillager(Location location) {
    Villager villager = CreatureUtils.getCreatureInitializer().spawnVillager(location);
    villager.setCustomNameVisible(getPlugin().getConfigPreferences().getOption("NAME_VISIBILITY_VILLAGER"));
    villager.setCustomName(CreatureUtils.getRandomVillagerName());
    addVillager(villager);
  }

  public void spawnWolf(Location location, Player player) {
    if(!canSpawnMobForPlayer(player, EntityType.WOLF)) {
      return;
    }

    Wolf wolf = CreatureUtils.getCreatureInitializer().spawnWolf(location);
    wolf.setOwner(player);
    wolf.setCustomNameVisible(getPlugin().getConfigPreferences().getOption("NAME_VISIBILITY_WOLF"));
    wolf.setCustomName(getPlugin().getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_WOLF_NAME").replace("%player%", player.getName()));
    addWolf(wolf);
  }

  public void spawnGolem(Location location, Player player) {
    if(!canSpawnMobForPlayer(player, EntityType.IRON_GOLEM)) {
      return;
    }

    IronGolem ironGolem = CreatureUtils.getCreatureInitializer().spawnGolem(location);
    ironGolem.setCustomNameVisible(getPlugin().getConfigPreferences().getOption("NAME_VISIBILITY_GOLEM"));
    ironGolem.setCustomName(getPlugin().getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_GOLEM_NAME").replace("%player%", player.getName()));
    addIronGolem(ironGolem);
  }

  protected void addWolf(Wolf wolf) {
    wolves.add(wolf);
  }

  protected boolean canSpawnMobForPlayer(Player player, EntityType type) {
    if(type != EntityType.IRON_GOLEM && type != EntityType.WOLF) {
      return true;
    }

    for(Map.Entry<String, Boolean> map : getAllEffectivePermissions(player).entrySet()) {
      if(!map.getValue()) {
        continue;
      }

      int limit = 0;
      try {
        limit = Integer.parseInt(map.getKey().split("\\.", 2)[1]);
      } catch(NumberFormatException ex) {
      }

      if(limit < 1) {
        continue;
      }

      if((type == EntityType.IRON_GOLEM && map.getKey().endsWith("limit.golem." + limit) && ironGolems.size() + 1 < limit)
          || (type == EntityType.WOLF && map.getKey().endsWith("limit.wolf." + limit) && wolves.size() + 1 < limit)) {
        return false;
      }
    }

    return true;
  }

  private Map<String, Boolean> getAllEffectivePermissions(Player player) {
    Map<String, Boolean> permLimits = new HashMap<>();
    for(PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
      if(permission.getPermission().startsWith("villagedefense.limit.")) {
        permLimits.put(permission.getPermission(), permission.getValue());
      }
    }

    return permLimits;
  }

  /**
   * Get alive wolves.
   *
   * @return alive wolves in game
   */
  @NotNull
  public List<Wolf> getWolves() {
    return wolves;
  }

  /**
   * Get alive iron golems.
   *
   * @return alive iron golems in game
   */
  @NotNull
  public List<IronGolem> getIronGolems() {
    return ironGolems;
  }

  /**
   * Get alive villagers.
   *
   * @return alive villagers in game
   */
  @NotNull
  public List<Villager> getVillagers() {
    return villagers;
  }

  public boolean checkLevelUpRottenFlesh() {
    int rottenFleshLevel = getArenaOption("ROTTEN_FLESH_LEVEL");
    int rottenFleshAmount = getArenaOption("ROTTEN_FLESH_AMOUNT");

    if(rottenFleshLevel == 0 && rottenFleshAmount > 50) {
      setArenaOption("ROTTEN_FLESH_LEVEL", 1);
      return true;
    }

    if(rottenFleshLevel * 10 * getPlayers().size() + 10 < rottenFleshAmount) {
      changeArenaOptionBy("ROTTEN_FLESH_LEVEL", 1);
      return true;
    }

    return false;
  }

  protected void addVillager(Villager villager) {
    villagers.add(villager);
  }

  public void removeVillager(Villager villager) {
    villager.remove();
    villager.setHealth(0);
    villagers.remove(villager);
  }

  @Override
  public MapRestorerManager getMapRestorerManager() {
    return mapRestorerManager;
  }

  @NotNull
  public List<Location> getZombieSpawns() {
    return spawnPoints.getOrDefault(SpawnPoint.ZOMBIE, new ArrayList<>());
  }

  public final Location getRandomZombieSpawn(Random random) {
    List<Location> spawns = getZombieSpawns();
    return spawns.get(spawns.size() == 1 ? 0 : random.nextInt(spawns.size()));
  }

  protected void addIronGolem(IronGolem ironGolem) {
    ironGolems.add(ironGolem);
  }

  public void removeIronGolem(IronGolem ironGolem) {
    ironGolem.remove();
    ironGolems.remove(ironGolem);
  }

  public void removeWolf(Wolf wolf) {
    wolf.remove();
    wolves.remove(wolf);
  }

  public enum SpawnPoint {
    ZOMBIE, VILLAGER
  }

}