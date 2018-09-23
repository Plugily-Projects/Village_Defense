/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.material.Door;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.RewardsHandler;
import pl.plajer.villagedefense3.handlers.language.LanguageManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Utils;
import pl.plajer.villagedefense3.utils.XMaterial;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameStartEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameStateChangeEvent;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.GameScoreboard;
import pl.plajerlair.core.utils.InventoryUtils;

/**
 * Created by Tom on 12/08/2014.
 */
public abstract class Arena extends BukkitRunnable {

  protected final List<Location> zombieSpawns = new ArrayList<>();
  private final List<Zombie> zombies = new ArrayList<>();
  private final List<Wolf> wolfs = new ArrayList<>();
  private final List<Villager> villagers = new ArrayList<>();
  private final List<IronGolem> ironGolems = new ArrayList<>();
  private final Main plugin;
  private final LinkedHashMap<Location, Byte> doorBlocks = new LinkedHashMap<>();
  private final List<Location> villagerSpawnPoints = new ArrayList<>();
  private final Random random;
  private final List<Zombie> glitchedZombies = new ArrayList<>();
  private final Map<Zombie, Location> zombieCheckerLocations = new HashMap<>();
  private final Set<UUID> players = new HashSet<>();
  private int zombiesToSpawn;
  private boolean fighting = false;
  private int wave;
  private int barToggle = 0;
  private int rottenFleshAmount;
  private int rottenFleshLevel;
  private int zombieChecker = 0;
  private int spawnCounter = 0;
  private int totalKilledZombies = 0;
  private int totalOrbsSpent = 0;
  private ArenaState arenaState;
  private BossBar gameBar;
  private int minimumPlayers = 2;
  private int maximumPlayers = 10;
  private String mapName = "";
  private int timer;
  private String ID;
  //instead of 3 location fields we use map with GameLocation enum
  private Map<GameLocation, Location> gameLocations = new HashMap<>();
  private boolean ready = true;

  public Arena(String ID, Main plugin) {
    this.plugin = plugin;
    arenaState = ArenaState.WAITING_FOR_PLAYERS;
    this.ID = ID;
    random = new Random();
    gameBar = Bukkit.createBossBar(ChatManager.colorMessage("Bossbar.Main-Title"), BarColor.BLUE, BarStyle.SOLID);
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  /**
   * Get current rotten flesh level in arena.
   *
   * @return rotten flesh level (additional hearts)
   */
  public int getRottenFleshLevel() {
    return rottenFleshLevel;
  }

  void setRottenFleshLevel(int rottenFleshLevel) {
    this.rottenFleshLevel = rottenFleshLevel;
  }

  public int getTotalKilledZombies() {
    return totalKilledZombies;
  }

  public void setTotalKilledZombies(int totalKilledZombies) {
    this.totalKilledZombies = totalKilledZombies;
  }

  public int getTotalOrbsSpent() {
    return totalOrbsSpent;
  }

  public void setTotalOrbsSpent(int totalOrbsSpent) {
    this.totalOrbsSpent = totalOrbsSpent;
  }

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param p      player
   */
  public void doBarAction(BarAction action, Player p) {
    if (!plugin.isBossbarEnabled()) {
      return;
    }
    switch (action) {
      case ADD:
        gameBar.addPlayer(p);
        break;
      case REMOVE:
        gameBar.removePlayer(p);
        break;
    }
  }

  /**
   * Location of game doors.
   *
   * @return all game doors
   */
  public HashMap<Location, Byte> getDoorLocations() {
    return doorBlocks;
  }

  public void run() {
    try {
      //idle task
      if (getPlayers().size() == 0 && getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
        return;
      }
      updateScoreboard();
      switch (getArenaState()) {
        case WAITING_FOR_PLAYERS:
          if (plugin.isBungeeActivated()) {
            plugin.getServer().setWhitelist(false);
          }
          if (getPlayers().size() < getMinimumPlayers()) {
            if (getTimer() <= 0) {
              setTimer(15);
              ChatManager.broadcast(this, ChatManager.formatMessage(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMinimumPlayers()));
              return;
            }
          } else {
            gameBar.setTitle(ChatManager.colorMessage("Bossbar.Waiting-For-Players"));
            ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
            setArenaState(ArenaState.STARTING);
            setTimer(Main.STARTING_TIMER_TIME);
            this.showPlayers();
          }
          setTimer(getTimer() - 1);
          break;
        case STARTING:
          gameBar.setTitle(ChatManager.colorMessage("Bossbar.Starting-In").replace("%time%", String.valueOf(getTimer())));
          gameBar.setProgress(getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60));
          for (Player player : getPlayers()) {
            player.setExp((float) (getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60)));
            player.setLevel(getTimer());
          }
          if (getPlayers().size() < getMinimumPlayers()) {
            gameBar.setTitle(ChatManager.colorMessage("Bossbar.Waiting-For-Players"));
            gameBar.setProgress(1.0);
            ChatManager.broadcast(this, ChatManager.formatMessage(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMinimumPlayers()));
            setArenaState(ArenaState.WAITING_FOR_PLAYERS);
            Bukkit.getPluginManager().callEvent(new VillageGameStartEvent(this));
            setTimer(15);
            for (Player player : getPlayers()) {
              player.setExp(1);
              player.setLevel(0);
            }
            break;
          }
          if (getTimer() == 0) {
            VillageGameStartEvent villageGameStartEvent = new VillageGameStartEvent(this);
            Bukkit.getPluginManager().callEvent(villageGameStartEvent);
            setArenaState(ArenaState.IN_GAME);
            gameBar.setProgress(1.0);
            setTimer(5);
            teleportAllToStartLocation();
            for (Player player : getPlayers()) {
              player.setExp(0);
              player.setLevel(0);
              player.getInventory().clear();
              player.setGameMode(GameMode.SURVIVAL);
              User user = UserManager.getUser(player.getUniqueId());
              user.setStat(StatsStorage.StatisticType.ORBS, plugin.getConfig().getInt("Orbs-Starting-Amount", 20));
              ArenaUtils.hidePlayersOutsideTheGame(player, this);
              if (UserManager.getUser(player.getUniqueId()).getKit() != null) {
                UserManager.getUser(player.getUniqueId()).getKit().giveKitItems(player);
              } else {
                KitRegistry.getDefaultKit().giveKitItems(player);
              }
              player.updateInventory();
              addStat(player, StatsStorage.StatisticType.GAMES_PLAYED);
              addExperience(player, 10);
              setTimer(plugin.getConfig().getInt("Cooldown-Before-Next-Wave", 25));
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started"));
            }
            fighting = false;
          }
          setTimer(getTimer() - 1);
          break;
        case IN_GAME:
          if (barToggle > 5) {
            gameBar.setTitle(ChatManager.colorMessage("Bossbar.In-Game-Wave").replace("%wave%", String.valueOf(getWave())));
            barToggle++;
            if (barToggle > 10) {
              barToggle = 0;
            }
          } else {
            gameBar.setTitle(ChatManager.colorMessage("Bossbar.In-Game-Info").replace("%wave%", String.valueOf(getWave())));
            barToggle++;
          }
          if (plugin.isBungeeActivated()) {
            if (getMaximumPlayers() <= getPlayers().size()) {
              plugin.getServer().setWhitelist(true);
            } else {
              plugin.getServer().setWhitelist(false);
            }
          }
          zombieChecker++;
          if (zombieChecker >= 60) {
            Iterator<Villager> villagerIterator = getVillagers().iterator();
            while (villagerIterator.hasNext()) {
              Villager villager = villagerIterator.next();
              if (villager.isDead()) {
                villagerIterator.remove();
                removeVillager(villager);
              }
            }
            zombieChecker = 0;

            Iterator<Zombie> zombieIterator = getZombies().iterator();
            while (zombieIterator.hasNext()) {
              Zombie zombie = zombieIterator.next();
              if (zombie.isDead()) {
                zombieIterator.remove();
                removeZombie(zombie);
                continue;
              }
              if (glitchedZombies.contains(zombie) && zombie.getLocation().distance(zombieCheckerLocations.get(zombie)) <= 1) {
                zombieIterator.remove();
                removeZombie(zombie);
                zombieCheckerLocations.remove(zombie);
                zombie.remove();
              }
              if (zombieCheckerLocations.get(zombie) == null) {
                zombieCheckerLocations.put(zombie, zombie.getLocation());
              } else {
                Location location = zombieCheckerLocations.get(zombie);

                if (zombie.getLocation().distance(location) <= 1) {
                  zombie.teleport(zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1)));
                  zombieCheckerLocations.put(zombie, zombie.getLocation());
                  glitchedZombies.add(zombie);
                }
              }
            }
          }
          if (getVillagers().size() <= 0 || getPlayersLeft().size() <= 0 && getArenaState() != ArenaState.ENDING) {
            clearZombies();
            ArenaManager.stopGame(false, this);
            return;
          }
          if (fighting) {
            if (getZombiesLeft() <= 0) {
              fighting = false;
              ArenaManager.endWave(this);
            }
            if (zombiesToSpawn > 0) {
              spawnZombies();
              setTimer(500);
            } else {
              if (getTimer() == 0) {
                if (getZombiesLeft() <= 5) {
                  clearZombies();
                  zombiesToSpawn = 0;
                  ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Zombie-Got-Stuck-In-The-Map"));
                } else {
                  getZombies().clear();
                  for (int i = getZombiesLeft(); i > 0; i++) {
                    spawnFastZombie(random);
                  }
                }
              }
            }
            if (zombiesToSpawn < 0) {
              zombiesToSpawn = 0;
            }
            setTimer(getTimer() - 1);

          } else {
            if (getTimer() <= 0) {
              fighting = true;
              ArenaManager.startWave(this);
            }
          }
          setTimer(getTimer() - 1);
          break;
        case ENDING:
          if (plugin.isBungeeActivated()) {
            plugin.getServer().setWhitelist(false);
          }
          if (getTimer() <= 0) {
            gameBar.setTitle(ChatManager.colorMessage("Bossbar.Game-Ended"));
            clearVillagers();
            clearZombies();
            clearGolems();
            clearWolfs();

            for (Player player : getPlayers()) {
              UserManager.getUser(player.getUniqueId()).removeScoreboard();
              player.setGameMode(GameMode.SURVIVAL);
              for (Player players : Bukkit.getOnlinePlayers()) {
                player.showPlayer(players);
                players.hidePlayer(player);
              }
              for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
              }
              player.setFlying(false);
              player.setAllowFlight(false);
              player.getInventory().clear();

              player.getInventory().setArmorContents(null);
              doBarAction(BarAction.REMOVE, player);
              player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
              player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
              player.setFireTicks(0);
              player.setFoodLevel(20);
              for (Player players : plugin.getServer().getOnlinePlayers()) {
                if (ArenaRegistry.getArena(players) != null) {
                  players.showPlayer(player);
                }
                player.showPlayer(players);
              }
            }
            teleportAllToEndLocation();
            if (plugin.isInventoryManagerEnabled()) {
              for (Player player : getPlayers()) {
                InventoryUtils.loadInventory(plugin, player);
              }
            }
            ChatManager.broadcast(this, ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));

            for (User user : UserManager.getUsers(this)) {
              user.setSpectator(false);
              user.setStat(StatsStorage.StatisticType.ORBS, 0);
            }
            plugin.getRewardsHandler().performReward(this, RewardsHandler.RewardType.END_GAME);
            players.clear();
            if (plugin.isBungeeActivated()) {
              if (ConfigUtils.getConfig(plugin, "bungee").getBoolean("Shutdown-When-Game-Ends")) {
                plugin.getServer().shutdown();
              }
            }
            setArenaState(ArenaState.RESTARTING);
          }
          setTimer(getTimer() - 1);
          break;
        case RESTARTING:
          clearVillagers();
          this.restoreMap();

          getPlayers().clear();

          setArenaState(ArenaState.WAITING_FOR_PLAYERS);

          wave = 1;
          totalKilledZombies = 0;
          totalOrbsSpent = 0;
          if (plugin.isBungeeActivated()) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
              this.addPlayer(player);
            }
          }
          break;
        default:
          break; //o.o?
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  private void updateScoreboard() {
    if (getPlayers().size() == 0 || getArenaState() == ArenaState.RESTARTING) {
      return;
    }
    GameScoreboard scoreboard;
    for (Player p : getPlayers()) {
      User user = UserManager.getUser(p.getUniqueId());
      if (getArenaState() == ArenaState.ENDING) {
        user.removeScoreboard();
        return;
      }
      scoreboard = new GameScoreboard("PL_VD3", "PL_CR", ChatManager.colorMessage("Scoreboard.Title"));
      List<String> lines;
      if (getArenaState() == ArenaState.IN_GAME) {
        lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing" + (fighting ? "" : "-Waiting"));
      } else {
        lines = LanguageManager.getLanguageList("Scoreboard.Content." + getArenaState().getFormattedName());
      }
      for (String line : lines) {
        scoreboard.addRow(formatScoreboardLine(line, user));
      }
      scoreboard.finish();
      scoreboard.display(p);
    }
  }

  private String formatScoreboardLine(String line, User user) {
    String formattedLine = line;
    formattedLine = StringUtils.replace(formattedLine, "%TIME%", String.valueOf(getTimer()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS%", String.valueOf(getPlayers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%MIN_PLAYERS%", String.valueOf(getMinimumPlayers()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS_LEFT%", String.valueOf(getPlayersLeft().size()));
    formattedLine = StringUtils.replace(formattedLine, "%VILLAGERS%", String.valueOf(getVillagers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%ORBS%", String.valueOf(user.getStat(StatsStorage.StatisticType.ORBS)));
    formattedLine = StringUtils.replace(formattedLine, "%ZOMBIES%", String.valueOf(getZombiesLeft()));
    formattedLine = StringUtils.replace(formattedLine, "%ROTTEN_FLESH%", String.valueOf(getRottenFlesh()));
    formattedLine = ChatManager.colorRawMessage(formattedLine);
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      PlaceholderAPI.setPlaceholders(user.toPlayer(), formattedLine);
    }
    return formattedLine;
  }

  private void restoreMap() {
    this.restoreDoors();
    for (Zombie zombie : getZombies()) {
      zombie.remove();
    }
    for (IronGolem ironGolem : getIronGolems()) {
      ironGolem.remove();
    }
    for (Villager villager : getVillagers()) {
      villager.remove();
    }
    for (Wolf wolf : getWolfs()) {
      wolf.remove();
    }
    clearZombies();
    clearGolems();
    clearVillagers();
    clearWolfs();
    spawnVillagers();

  }

  private void spawnVillagers() {
    if (getVillagers().size() > 10) {
      return;
    } else if (getVillagerSpawns() == null || getVillagerSpawns().size() <= 0) {
      Main.debug(Main.LogLevel.WARN, "No villager spawns for " + getID() + ", game won't start");
    } else {
      for (Location location : getVillagerSpawns()) {
        spawnVillager(location);
      }
      if (getVillagers().size() != 0) {
        spawnVillagers();
      } else {
        Main.debug(Main.LogLevel.WARN, "Villager spawns can't be set up!");
      }
    }
  }

  /**
   * Get arena identifier used to get arenas by string.
   *
   * @return arena name
   * @see ArenaRegistry#getArena(String)
   */
  public String getID() {
    return ID;
  }

  /**
   * Get minimum players needed.
   *
   * @return minimum players needed to start arena
   */
  public int getMinimumPlayers() {
    return minimumPlayers;
  }

  /**
   * Set minimum players needed.
   *
   * @param minimumPlayers players needed to start arena
   */
  public void setMinimumPlayers(int minimumPlayers) {
    this.minimumPlayers = minimumPlayers;
  }

  /**
   * Get arena map name.
   *
   * @return arena map name, [b]it's not arena ID[/b]
   * @see #getID()
   */
  public String getMapName() {
    return mapName;
  }

  /**
   * Set arena map name.
   *
   * @param mapname new map name, [b]it's not arena ID[/b]
   */
  public void setMapName(String mapname) {
    this.mapName = mapname;
  }

  /**
   * Get timer of arena.
   *
   * @return timer of lobby time / time to next wave
   */
  public int getTimer() {
    return timer;
  }

  /**
   * Modify game timer.
   *
   * @param timer timer of lobby / time to next wave
   */
  public void setTimer(int timer) {
    this.timer = timer;
  }

  /**
   * Return maximum players arena can handle.
   *
   * @return maximum players arena can handle
   */
  public int getMaximumPlayers() {
    return maximumPlayers;
  }

  /**
   * Set maximum players arena can handle.
   *
   * @param maximumPlayers how many players arena can handle
   */
  public void setMaximumPlayers(int maximumPlayers) {
    this.maximumPlayers = maximumPlayers;
  }

  /**
   * Return game state of arena.
   *
   * @return game state of arena
   * @see ArenaState
   */
  public ArenaState getArenaState() {
    return arenaState;
  }

  /**
   * Set game state of arena.
   *
   * @param arenaState new game state of arena
   * @see ArenaState
   */
  public void setArenaState(ArenaState arenaState) {
    this.arenaState = arenaState;
    VillageGameStateChangeEvent villageGameStateChangeEvent = new VillageGameStateChangeEvent(this, getArenaState());
    Bukkit.getPluginManager().callEvent(villageGameStateChangeEvent);
  }

  /**
   * Get all players in arena.
   *
   * @return set of players in arena
   */
  public HashSet<Player> getPlayers() {
    HashSet<Player> list = new HashSet<>();
    Iterator<UUID> iterator = players.iterator();
    while (iterator.hasNext()) {
      UUID uuid = iterator.next();
      if (Bukkit.getPlayer(uuid) == null) {
        iterator.remove();
        Main.debug(Main.LogLevel.WARN, "Removed invalid player from arena " + getID() + " (not online?)");
      }
      list.add(Bukkit.getPlayer(uuid));
    }
    return list;
  }

  public void teleportToLobby(Player player) {
    Location location = getLobbyLocation();
    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
    player.setFoodLevel(20);
    player.setFlying(false);
    player.setAllowFlight(false);
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }
    if (location == null) {
      Main.debug(Main.LogLevel.WARN, "Lobby location of arena " + getID() + " doesn't exist!");
    }
    player.teleport(location);
  }

  /**
   * Get lobby location of arena.
   *
   * @return lobby location of arena
   */
  public Location getLobbyLocation() {
    return gameLocations.get(GameLocation.LOBBY);
  }

  /**
   * Set lobby location of arena.
   *
   * @param loc new lobby location of arena
   */
  public void setLobbyLocation(Location loc) {
    gameLocations.put(GameLocation.LOBBY, loc);
  }

  /**
   * Get start location of arena.
   *
   * @return start location of arena
   */
  public Location getStartLocation() {
    return gameLocations.get(GameLocation.START);
  }

  /**
   * Set start location of arena.
   *
   * @param location new start location of arena
   */
  public void setStartLocation(Location location) {
    gameLocations.put(GameLocation.START, location);
  }

  public void teleportToStartLocation(Player player) {
    if (gameLocations.get(GameLocation.START) != null) {
      player.teleport(gameLocations.get(GameLocation.START));
    } else {
      Main.debug(Main.LogLevel.WARN, "Start location of arena " + getID() + " doesn't exist!");
    }
  }

  private void teleportAllToStartLocation() {
    for (Player player : getPlayers()) {
      if (gameLocations.get(GameLocation.START) != null) {
        player.teleport(gameLocations.get(GameLocation.START));
      } else {
        Main.debug(Main.LogLevel.WARN, "Start location of arena " + getID() + " doesn't exist!");
      }
    }
  }

  public void teleportAllToEndLocation() {
    if (plugin.isBungeeActivated()) {
      for (Player player : getPlayers()) {
        plugin.getBungeeManager().connectToHub(player);
      }
      return;
    }
    Location location = getEndLocation();

    if (location == null) {
      location = getLobbyLocation();
      Main.debug(Main.LogLevel.WARN, "Ending location of arena " + getID() + " doesn't exist!");
    }
    for (Player player : getPlayers()) {
      player.teleport(location);
    }
  }

  public void teleportToEndLocation(Player player) {
    if (plugin.isBungeeActivated()) {
      plugin.getBungeeManager().connectToHub(player);
      return;
    }
    Location location = getEndLocation();
    if (location == null) {
      location = getLobbyLocation();
      Main.debug(Main.LogLevel.WARN, "Ending location of arena " + getID() + " doesn't exist!");
    }

    player.teleport(location);
  }

  /**
   * Get end location of arena.
   *
   * @return end location of arena
   */
  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  /**
   * Set end location of arena.
   *
   * @param endLoc new end location of arena
   */
  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
  }

  public void start() {
    Main.debug(Main.LogLevel.INFO, "Game instance started, arena " + this.getID());
    this.runTaskTimer(plugin, 20L, 20L);
    this.setArenaState(ArenaState.RESTARTING);
    for (Location location : villagerSpawnPoints) {
      plugin.getChunkManager().keepLoaded(location.getChunk());
    }
  }

  /**
   * Get list of already spawned zombies.
   * This will only return alive zombies not total zombies in current wave.
   *
   * @return list of spawned zombies in arena
   */
  public List<Zombie> getZombies() {
    return zombies;
  }

  public void removeZombie(Zombie zombie) {
    zombies.remove(zombie);
  }

  private List<Location> getVillagerSpawns() {
    return villagerSpawnPoints;
  }

  /**
   * Clear all golems in arena.
   */
  public void clearGolems() {
    for (IronGolem ironGolem : ironGolems) {
      ironGolem.remove();
    }
    this.ironGolems.clear();
  }

  /**
   * Clear all wolves in arena.
   */
  public void clearWolfs() {
    for (Wolf wolf : wolfs) {
      wolf.remove();
    }
    this.wolfs.clear();
  }

  public void addVillagerSpawn(Location location) {
    this.villagerSpawnPoints.add(location);
  }

  public void addZombieSpawn(Location location) {
    zombieSpawns.add(location);
  }

  /**
   * Clear all zombies in arena.
   */
  public void clearZombies() {
    for (Zombie zombie : zombies) {
      zombie.remove();
    }
    zombies.clear();
  }

  private int getZombiesLeft() {
    return zombiesToSpawn + getZombies().size();
  }

  private void spawnZombies() {
    if (getZombies() == null || getZombies().size() <= 0) {
      for (int i = 0; i <= wave; i++) {
        if (zombiesToSpawn > 0) {
          spawnFastZombie(random);
        }
      }
    }
    spawnCounter++;
    if (spawnCounter == 20) {
      spawnCounter = 0;
    }
    if (zombiesToSpawn < 5 && zombiesToSpawn > 0) {
      spawnFastZombie(random);
      return;
    }
    if (spawnCounter == 5) {
      if (random.nextInt(3) != 2) {
        for (int i = 0; i <= wave; i++) {
          if (zombiesToSpawn > 0) {
            if (wave > 7) {
              if (random.nextInt(2) == 1) {
                spawnSoftHardZombie(random);
              }
            } else if (wave > 14) {
              if (random.nextInt(2) == 1) {
                spawnHardZombie(random);
              }
            } else if (wave > 20) {
              if (random.nextInt(3) == 1) {
                spawnKnockbackResistantZombies(random);
              }
            } else if (wave > 23) {
              if (random.nextInt(4) == 1) {
                spawnVillagerSlayer(random);
              }
            } else {
              spawnFastZombie(random);
            }
          }
        }
      } else {
        for (int i = 0; i <= wave; i++) {
          if (zombiesToSpawn > 0) {
            spawnBabyZombie(random);
          }
        }
      }
    }
    if (spawnCounter == 15 && wave > 4) {
      if (wave > 8) {
        for (int i = 0; i < (wave - 7); i++) {
          if (zombiesToSpawn > 0) {
            spawnHardZombie(random);
          }
        }
      } else {
        for (int i = 0; i < (wave - 3); i++) {
          if (zombiesToSpawn > 0) {
            spawnSoftHardZombie(random);
          }
        }
      }

    }

    if (random.nextInt(8) == 0 && wave > 10) {
      for (int i = 0; i < (wave - 8); i++) {
        if (zombiesToSpawn > 0) {
          spawnPlayerBuster(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 7) {
      for (int i = 0; i < (wave - 5); i++) {
        if (zombiesToSpawn > 0) {
          spawnHalfInvisibleZombie(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 15) {
      for (int i = 0; i < (wave - 13); i++) {
        if (zombiesToSpawn > 0) {
          spawnHalfInvisibleZombie(random);
        }
      }
    }
    if (random.nextInt(8) == 0 && wave > 23) {
      if (zombiesToSpawn > 0) {
        spawnHalfInvisibleZombie(random);
      }
    }
    if (random.nextInt(8) == 0 && getIronGolems().size() > 0 && wave >= 6) {
      for (int i = 0; i < (wave - 4); i++) {
        if (zombiesToSpawn > 0) {
          spawnGolemBuster(random);
        }
      }
    }
  }

  /**
   * Get current game wave.
   *
   * @return current game wave
   */
  public int getWave() {
    return wave;
  }

  /**
   * Should be used with endWave.
   *
   * @param i new game wave
   * @see ArenaManager#endWave(Arena)
   */
  public void setWave(int i) {
    wave = i;
  }

  public abstract void spawnVillager(Location location);

  public abstract void spawnWolf(Location location, Player player);

  public abstract void spawnGolem(Location location, Player player);

  public abstract void spawnFastZombie(Random random);

  public abstract void spawnBabyZombie(Random random);

  public abstract void spawnHardZombie(Random random);

  public abstract void spawnPlayerBuster(Random random);

  public abstract void spawnGolemBuster(Random random);

  public abstract void spawnSoftHardZombie(Random random);

  public abstract void spawnHalfInvisibleZombie(Random random);

  public abstract void spawnKnockbackResistantZombies(Random random);

  public abstract void spawnVillagerSlayer(Random random);

  protected void addWolf(Wolf wolf) {
    wolfs.add(wolf);
  }

  /**
   * Get alive wolves.
   *
   * @return alive wolves in game
   */
  public List<Wolf> getWolfs() {
    return wolfs;
  }

  /**
   * Get alive iron golems.
   *
   * @return alive iron golems in game
   */
  public List<IronGolem> getIronGolems() {
    return ironGolems;
  }

  /**
   * Get alive villagers.
   *
   * @return alive villagers in game
   */
  public List<Villager> getVillagers() {
    return villagers;
  }

  /**
   * Clear all villagers in arena.
   */
  public void clearVillagers() {
    for (Villager villager : villagers) {
      villager.remove();
    }
    villagers.clear();
  }

  public void addDoor(Location location, byte data) {
    this.doorBlocks.put(location, data);
  }

  public void addRottenFlesh(int i) {
    rottenFleshAmount = rottenFleshAmount + i;
  }

  private int getRottenFlesh() {
    return rottenFleshAmount;
  }

  public boolean checkLevelUpRottenFlesh() {
    if (rottenFleshLevel == 0 && rottenFleshAmount > 50) {
      rottenFleshLevel = 1;
      return true;
    }
    if (rottenFleshLevel * 10 * getPlayers().size() + 10 < rottenFleshAmount) {
      rottenFleshLevel++;
      return true;
    }
    return false;
  }

  Map<Zombie, Location> getZombieCheckerLocations() {
    return zombieCheckerLocations;
  }

  protected void subtractZombiesToSpawn() {
    this.zombiesToSpawn--;
  }

  void setRottenFleshAmount(int rottenFleshAmount) {
    this.rottenFleshAmount = rottenFleshAmount;
  }

  void setZombieAmount() {
    zombiesToSpawn = (int) Math.ceil((getPlayers().size() * 0.5) * (wave * wave) / 2);
  }

  void addPlayer(Player player) {
    players.add(player.getUniqueId());
  }

  void removePlayer(Player player) {
    if (player == null || player.getUniqueId() == null) {
      return;
    }
    players.remove(player.getUniqueId());
  }

  List<Player> getPlayersLeft() {
    List<Player> players = new ArrayList<>();
    for (User user : UserManager.getUsers(this)) {
      if (!user.isSpectator()) {
        players.add(user.toPlayer());
      }
    }
    return players;
  }

  void showPlayers() {
    for (Player player : getPlayers()) {
      for (Player p : getPlayers()) {
        player.showPlayer(p);
        p.showPlayer(player);
      }
    }
  }

  protected void addZombie(Zombie zombie) {
    zombies.add(zombie);
  }

  void addExperience(Player player, int i) {
    User user = UserManager.getUser(player.getUniqueId());
    user.addStat(StatsStorage.StatisticType.XP, i);
    if (player.hasPermission(PermissionsManager.getVip())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2));
    }
    if (player.hasPermission(PermissionsManager.getMvp())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2));
    }
    if (player.hasPermission(PermissionsManager.getElite())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2));
    }
    ArenaUtils.updateLevelStat(player, this);
  }

  void addStat(Player player, StatsStorage.StatisticType stat) {
    User user = UserManager.getUser(player.getUniqueId());
    user.addStat(stat, 1);
    ArenaUtils.updateLevelStat(player, this);
  }

  protected void addVillager(Villager villager) {
    villagers.add(villager);
  }

  void removeVillager(Villager villager) {
    if (villagers.contains(villager)) {
      villager.remove();
      villager.setHealth(0);
      villagers.remove(villager);
    }
  }

  protected void addIronGolem(IronGolem ironGolem) {
    ironGolems.add(ironGolem);
  }

  void restoreDoors() {
    int i = 1;
    for (Location location : doorBlocks.keySet()) {
      Block block = location.getBlock();
      Byte doorData = doorBlocks.get(location);
      //todo check
      if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
        int id = Material.WOODEN_DOOR.getId();
        block.setTypeIdAndData(id, doorData, false);
      } else {
        //idk how does this work
        try {
          if (block.getType() != XMaterial.AIR.parseMaterial()) {
            i++;
            continue;
          }
          if (doorData == (byte) 8) {
            block.setType(XMaterial.OAK_DOOR.parseMaterial());
            BlockState doorBlockState = block.getState();
            Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte(doorData));

            doorBlockData.setTopHalf(true);
            doorBlockData.setFacingDirection(doorBlockData.getFacing());

            doorBlockState.setType(doorBlockData.getItemType());
            doorBlockState.setData(doorBlockData);
            doorBlockState.update(true);
            continue;
          }

          block.setType(XMaterial.OAK_DOOR.parseMaterial());
          BlockState doorBlockState = block.getState();
          Door doorBlockData = new Door(TreeSpecies.GENERIC, Utils.getFacingByByte(doorData));

          doorBlockData.setTopHalf(false);
          doorBlockData.setFacingDirection(doorBlockData.getFacing());

          doorBlockState.setData(doorBlockData);
          doorBlockState.update(true);
          i++;
        } catch (Exception ex) {
          Main.debug(Main.LogLevel.WARN, "Door has failed to load for arena " + getID() + ", skipping!");
        }
      }
    }
    if (i != doorBlocks.size()) {
      Main.debug(Main.LogLevel.WARN, "Some doors has failed to load for arena " + getID() + "! Expected " + doorBlocks.size() + " but loaded only " + i + "!");
    }
  }

  public enum BarAction {
    ADD, REMOVE
  }

  public enum GameLocation {
    START, LOBBY, END
  }

}