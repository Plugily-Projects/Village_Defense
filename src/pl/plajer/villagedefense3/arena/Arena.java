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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.handlers.language.LanguageManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameStartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tom on 12/08/2014.
 */
public abstract class Arena extends BukkitRunnable {

    protected final List<Location> zombieSpawns = new ArrayList<>();
    private final Main plugin;
    private final LinkedHashMap<Location, Byte> doorBlocks = new LinkedHashMap<>();
    private final List<Location> villagerSpawnPoints = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Wolf> wolfs = new ArrayList<>();
    private final List<Villager> villagers = new ArrayList<>();
    private final List<IronGolem> ironGolems = new ArrayList<>();
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
    private ArenaBossBar arenaBar;
    private ArenaState arenaState;
    private int minimumPlayers = 2;
    private int maximumPlayers = 10;
    private String mapName = "";
    private int timer;
    private String ID;
    private Location lobbyLoc = null;
    private Location startLoc = null;
    private Location endLoc = null;
    private boolean ready = true;

    public Arena(String ID, Main plugin) {
        this.plugin = plugin;
        arenaState = ArenaState.WAITING_FOR_PLAYERS;
        this.ID = ID;
        random = new Random();
        if(plugin.isBossbarEnabled()) {
            arenaBar = new ArenaBossBar(plugin, ChatManager.colorMessage("Bossbar.Main-Title"));
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Get current rotten flesh level in arena
     *
     * @return rotten flesh level (additional hearts)
     */
    public int getRottenFleshLevel() {
        return rottenFleshLevel;
    }

    void setRottenFleshLevel(int rottenFleshLevel) {
        this.rottenFleshLevel = rottenFleshLevel;
    }

    public ArenaBossBar getArenaBar() {
        return arenaBar;
    }

    /**
     * Location of game doors
     *
     * @return all game doors
     */
    public HashMap<Location, Byte> getDoorLocations() {
        return doorBlocks;
    }

    public void run() {
        //idle task
        if(getPlayers().size() == 0 && getArenaState() == ArenaState.WAITING_FOR_PLAYERS) return;
        updateScoreboard();
        switch(getArenaState()) {
            case WAITING_FOR_PLAYERS:
                if(plugin.isBungeeActivated())
                    plugin.getServer().setWhitelist(false);
                if(getPlayers().size() < getMinimumPlayers()) {
                    if(getTimer() <= 0) {
                        setTimer(15);
                        String message = ChatManager.formatMessage(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMinimumPlayers());
                        for(Player p : getPlayers()) {
                            p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                        }
                        return;
                    }
                } else {
                    if(plugin.isBossbarEnabled()) {
                        arenaBar.setTitle(ChatManager.colorMessage("Bossbar.Waiting-For-Players"));
                    }
                    for(Player p : getPlayers()) {
                        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
                    }
                    setArenaState(ArenaState.STARTING);
                    setTimer(Main.STARTING_TIMER_TIME);
                    this.showPlayers();
                }
                setTimer(getTimer() - 1);
                break;
            case STARTING:
                if(plugin.isBossbarEnabled()) {
                    arenaBar.setTitle(ChatManager.colorMessage("Bossbar.Starting-In").replaceAll("%time%", String.valueOf(getTimer())));
                    arenaBar.setProgress(getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time"));
                }
                if(getTimer() == 0) {
                    VillageGameStartEvent villageGameStartEvent = new VillageGameStartEvent(this);
                    Bukkit.getPluginManager().callEvent(villageGameStartEvent);
                    setArenaState(ArenaState.IN_GAME);
                    if(plugin.isBossbarEnabled()) {
                        arenaBar.setProgress(1.0);
                    }
                    setTimer(5);
                    teleportAllToStartLocation();
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SURVIVAL);
                        User user = UserManager.getUser(player.getUniqueId());
                        user.setInt("orbs", plugin.getConfig().getInt("Orbs-Starting-Amount"));
                        ArenaUtils.hidePlayersOutsideTheGame(player, this);
                        if(UserManager.getUser(player.getUniqueId()).getKit() != null) {
                            UserManager.getUser(player.getUniqueId()).getKit().giveKitItems(player);
                        } else {
                            KitRegistry.getDefaultKit().giveKitItems(player);
                        }
                        player.updateInventory();
                        addStat(player, "gamesplayed");
                        addExperience(player, 10);
                        setTimer(25);
                        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started"));
                    }
                    fighting = false;
                }
                setTimer(getTimer() - 1);
                break;
            case IN_GAME:
                if(plugin.isBossbarEnabled()) {
                    if(barToggle > 5) {
                        arenaBar.setTitle(ChatManager.colorMessage("Bossbar.In-Game-Wave").replaceAll("%wave%", String.valueOf(getWave())));
                        barToggle++;
                        if(barToggle > 10) {
                            barToggle = 0;
                        }
                    } else {
                        arenaBar.setTitle(ChatManager.colorMessage("Bossbar.In-Game-Info").replaceAll("%wave%", String.valueOf(getWave())));
                        barToggle++;
                    }
                }
                if(plugin.isBungeeActivated()) {
                    if(getMaximumPlayers() <= getPlayers().size()) {
                        plugin.getServer().setWhitelist(true);
                    } else {
                        plugin.getServer().setWhitelist(false);
                    }
                }
                zombieChecker++;
                if(zombieChecker >= 60) {
                    List<Villager> remove = new ArrayList<>();
                    for(Villager villager : getVillagers()) {
                        if(villager.isDead())
                            remove.add(villager);
                    }
                    for(Villager villager : remove) {
                        removeVillager(villager);
                    }
                    remove.clear();
                    zombieChecker = 0;
                    List<Zombie> removeAfterLoop = new ArrayList<>();
                    for(Zombie zombie : getZombies()) {
                        if(zombie.isDead()) {
                            removeAfterLoop.add(zombie);
                            continue;
                        }
                        if(glitchedZombies.contains(zombie) && zombie.getLocation().distance(zombieCheckerLocations.get(zombie)) <= 1) {
                            removeAfterLoop.add(zombie);
                            zombieCheckerLocations.remove(zombie);
                            zombie.remove();
                        }
                        if(zombieCheckerLocations.get(zombie) == null) {
                            zombieCheckerLocations.put(zombie, zombie.getLocation());
                        } else {
                            Location location = zombieCheckerLocations.get(zombie);

                            if(zombie.getLocation().distance(location) <= 1) {
                                zombie.teleport(zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1)));
                                zombieCheckerLocations.put(zombie, zombie.getLocation());
                                glitchedZombies.add(zombie);
                            }
                        }
                    }

                    for(Zombie zombie : removeAfterLoop) {
                        removeZombie(zombie);
                    }
                    removeAfterLoop.clear();

                }
                if(getVillagers().size() <= 0 || getPlayersLeft().size() <= 0) {
                    clearZombies();
                    this.setArenaState(ArenaState.ENDING);
                    ArenaManager.stopGame(false, this);
                    if(getVillagers().size() <= 0) {
                        showPlayers();
                        this.setTimer(10);
                    } else this.setTimer(5);
                    return;
                }
                if(fighting) {
                    if(getZombiesLeft() <= 0) {
                        fighting = false;
                        ArenaManager.endWave(this);
                    }
                    if(zombiesToSpawn > 0) {
                        spawnZombies();
                        setTimer(500);
                    } else {
                        if(getTimer() == 0) {
                            if(getZombiesLeft() <= 5) {
                                clearZombies();
                                zombiesToSpawn = 0;
                                for(Player p : getPlayers()) {
                                    p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Zombie-Got-Stuck-In-The-Map"));
                                }
                            } else {
                                getZombies().clear();
                                for(int i = getZombiesLeft(); i > 0; i++) {
                                    spawnFastZombie(random);
                                }
                            }
                        }
                    }
                    if(zombiesToSpawn < 0) zombiesToSpawn = 0;
                    setTimer(getTimer() - 1);

                } else {
                    if(getTimer() <= 0) {
                        fighting = true;
                        ArenaManager.startWave(this);
                    }
                }
                setTimer(getTimer() - 1);
                break;
            case ENDING:
                if(plugin.isBungeeActivated())
                    plugin.getServer().setWhitelist(false);
                if(getTimer() <= 0) {
                    if(plugin.isBossbarEnabled()) {
                        arenaBar.setTitle(ChatManager.colorMessage("Bossbar.Game-Ended"));
                    }
                    clearVillagers();
                    clearZombies();
                    clearGolems();
                    clearWolfs();

                    for(Player player : getPlayers()) {
                        UserManager.getUser(player.getUniqueId()).removeScoreboard();
                        player.setGameMode(GameMode.SURVIVAL);
                        for(Player players : Bukkit.getOnlinePlayers()) {
                            player.showPlayer(players);
                            players.hidePlayer(player);
                        }
                        for(PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        player.getInventory().clear();

                        player.getInventory().setArmorContents(null);
                        if(plugin.isBossbarEnabled()) {
                            arenaBar.removePlayer(player);
                        }
                        player.setMaxHealth(20.0);
                        player.setHealth(player.getMaxHealth());
                        player.setFireTicks(0);
                        player.setFoodLevel(20);
                        for(Player players : plugin.getServer().getOnlinePlayers()) {
                            if(ArenaRegistry.getArena(players) != null)
                                players.showPlayer(player);
                            player.showPlayer(players);
                        }
                    }

                    teleportAllToEndLocation();

                    if(plugin.isInventoryManagerEnabled()) {
                        for(Player player : getPlayers()) {
                            plugin.getInventoryManager().loadInventory(player);
                        }
                    }

                    for(Player p : getPlayers()) {
                        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
                    }

                    for(User user : UserManager.getUsers(this)) {
                        user.setSpectator(false);
                        user.setInt("orbs", 0);
                        user.setFakeDead(false);
                    }
                    plugin.getRewardsHandler().performEndGameRewards(this);
                    players.clear();
                    if(plugin.isBungeeActivated()) {
                        if(ConfigurationManager.getConfig("bungee").getBoolean("Shutdown-When-Game-Ends"))
                            plugin.getServer().shutdown();
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
                if(plugin.isBungeeActivated()) {
                    for(Player player : plugin.getServer().getOnlinePlayers()) {
                        this.addPlayer(player);
                    }
                }
                break;
            default:
                break; //o.o?
        }
    }

    private void updateScoreboard() {
        if(getPlayers().size() == 0 || getArenaState() == ArenaState.RESTARTING) return;
        for(Player p : getPlayers()) {
            User user = UserManager.getUser(p.getUniqueId());
            if(getArenaState() == ArenaState.ENDING) {
                user.removeScoreboard();
                return;
            }
            ArenaBoard displayBoard = new ArenaBoard("VD3", "board", ChatManager.colorMessage("Scoreboard.Title"));
            List<String> lines;
            if(getArenaState() == ArenaState.IN_GAME) {
                lines = LanguageManager.getLanguageFile().getStringList("Scoreboard.Content.Playing" + (fighting ? "" : "-Waiting"));
            } else {
                lines = LanguageManager.getLanguageFile().getStringList("Scoreboard.Content." + getArenaState().getFormattedName());
            }
            for(String line : lines) {
                displayBoard.addRow(formatScoreboardLine(line, user));
            }
            displayBoard.finish();
            displayBoard.display(p);
        }
    }

    private String formatScoreboardLine(String line, User user) {
        String formattedLine = line;
        formattedLine = formattedLine.replace("%TIME%", String.valueOf(getTimer()));
        formattedLine = formattedLine.replace("%PLAYERS%", String.valueOf(getPlayers().size()));
        formattedLine = formattedLine.replace("%MIN_PLAYERS%", String.valueOf(getMinimumPlayers()));
        formattedLine = formattedLine.replace("%PLAYERS_LEFT%", String.valueOf(getPlayersLeft().size()));
        formattedLine = formattedLine.replace("%VILLAGERS%", String.valueOf(getVillagers().size()));
        formattedLine = formattedLine.replace("%ORBS%", String.valueOf(user.getInt("orbs")));
        formattedLine = formattedLine.replace("%ZOMBIES%", String.valueOf(getZombiesLeft()));
        formattedLine = formattedLine.replace("%ROTTEN_FLESH%", String.valueOf(getRottenFlesh()));
        formattedLine = ChatManager.colorRawMessage(formattedLine);
        return formattedLine;
    }

    private void restoreMap() {
        this.restoreDoors();
        for(Zombie zombie : getZombies()) {
            zombie.remove();
        }
        for(IronGolem ironGolem : getIronGolems()) {
            ironGolem.remove();
        }
        for(Villager villager : getVillagers()) {
            villager.remove();
        }
        for(Wolf wolf : getWolfs()) {
            wolf.remove();
        }
        clearZombies();
        clearGolems();
        clearVillagers();
        clearWolfs();
        spawnVillagers();

    }

    private void spawnVillagers() {
        if(getVillagers().size() > 10) {
            return;
        } else if(getVillagerSpawns() == null || getVillagerSpawns().size() <= 0) {
            Main.debug("No villager spawns for " + getID() + ", game won't start", System.currentTimeMillis());
        } else {
            for(Location location : getVillagerSpawns()) {
                spawnVillager(location);
            }
            if(getVillagers().size() != 0) {
                spawnVillagers();
            } else {
                Main.debug("Villager spawns can't be set up!", System.currentTimeMillis());
            }
        }
    }

    /**
     * Get arena identifier used to get arenas by string
     *
     * @return arena name
     * @see ArenaRegistry#getArena(String)
     */
    public String getID() {
        return ID;
    }

    /**
     * Set arena identifier
     *
     * @param ID name of arena
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Get minimum players needed
     *
     * @return minimum players needed to start arena
     */
    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    /**
     * Set minimum players needed
     *
     * @param minimumPlayers players needed to start arena
     */
    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    /**
     * Get arena map name
     *
     * @return arena map name, [b]it's not arena ID[/b]
     * @see #getID()
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Set arena map name
     *
     * @param mapname new map name, [b] it's not arena ID[/b]
     * @see #setID(String)
     */
    public void setMapName(String mapname) {
        this.mapName = mapname;
    }

    /**
     * Get timer of arena
     *
     * @return timer of lobby time / time to next wave
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Modify game timer
     *
     * @param timer timer of lobby / time to next wave
     */
    public void setTimer(int timer) {
        this.timer = timer;
    }

    /**
     * Return maximum players arena can handle
     *
     * @return maximum players arena can handle
     */
    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    /**
     * Set maximum players arena can handle
     *
     * @param maximumPlayers how many players arena can handle
     */
    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    /**
     * Return game state of arena
     *
     * @return game state of arena
     * @see ArenaState
     */
    public ArenaState getArenaState() {
        return arenaState;
    }

    /**
     * Set game state of arena
     *
     * @param arenaState new game state of arena
     * @see ArenaState
     */
    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    /**
     * Get all players in arena
     *
     * @return set of players in arena
     */
    public HashSet<Player> getPlayers() {
        HashSet<Player> list = new HashSet<>();
        for(UUID uuid : players) {
            list.add(Bukkit.getPlayer(uuid));
        }
        return list;
    }

    public void teleportToLobby(Player player) {
        Location location = getLobbyLocation();
        player.setMaxHealth(20.0);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if(location == null) {
            System.out.print("LobbyLocation isn't intialized for arena " + getID());
        }
        player.teleport(location);
    }

    /**
     * Get lobby location of arena
     *
     * @return lobby location of arena
     */
    public Location getLobbyLocation() {
        return lobbyLoc;
    }

    /**
     * Set lobby location of arena
     *
     * @param loc new lobby location of arena
     */
    public void setLobbyLocation(Location loc) {
        this.lobbyLoc = loc;
    }

    /**
     * Get start location of arena
     *
     * @return start location of arena
     */
    public Location getStartLocation() {
        return startLoc;
    }

    /**
     * Set start location of arena
     *
     * @param location new start location of arena
     */
    public void setStartLocation(Location location) {
        startLoc = location;
    }

    public void teleportToStartLocation(Player player) {
        if(startLoc != null)
            player.teleport(startLoc);
        else
            System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
    }

    private void teleportAllToStartLocation() {
        for(Player player : getPlayers()) {
            if(startLoc != null)
                player.teleport(startLoc);
            else
                System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
        }
    }

    public void teleportAllToEndLocation() {
        if(plugin.isBungeeActivated()) {
            for(Player player : getPlayers()) {
                plugin.getBungeeManager().connectToHub(player);
            }
            return;
        }
        Location location = getEndLocation();

        if(location == null) {
            location = getLobbyLocation();
            System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
        }
        for(Player player : getPlayers()) {
            player.teleport(location);
        }
    }

    public void teleportToEndLocation(Player player) {
        if(plugin.isBungeeActivated()) {
            plugin.getBungeeManager().connectToHub(player);
            return;
        }
        Location location = getEndLocation();
        if(location == null) {
            location = getLobbyLocation();
            System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
        }

        player.teleport(location);
    }

    /**
     * Get end location of arena
     *
     * @return end location of arena
     */
    public Location getEndLocation() {
        return endLoc;
    }

    /**
     * Set end location of arena
     *
     * @param endLoc new end location of arena
     */
    public void setEndLocation(Location endLoc) {
        this.endLoc = endLoc;
    }

    public void start() {
        Main.debug("Game instance started, arena " + this.getID(), System.currentTimeMillis());
        this.runTaskTimer(plugin, 20L, 20L);
        this.setArenaState(ArenaState.RESTARTING);
        for(Location location : villagerSpawnPoints) {
            plugin.getChunkManager().keepLoaded(location.getChunk());
        }
    }

    /**
     * Get list of already spawned zombies
     * [b]This will only return alive zombies not total zombies in current wave[/b]
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
     * Clear all golems in arena
     */
    public void clearGolems() {
        for(IronGolem ironGolem : ironGolems) {
            ironGolem.remove();
        }
        this.ironGolems.clear();
    }

    /**
     * Clear all wolves in arena
     */
    public void clearWolfs() {
        for(Wolf wolf : wolfs) {
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
     * Clear all zombies in arena
     */
    public void clearZombies() {
        for(Zombie zombie : zombies) {
            zombie.remove();
        }
        zombies.clear();
    }

    private int getZombiesLeft() {
        return zombiesToSpawn + getZombies().size();
    }

    private void spawnZombies() {
        Random random = new Random();
        if(getZombies() == null || getZombies().size() <= 0) {
            for(int i = 0; i <= wave; i++) {
                if(zombiesToSpawn > 0) {
                    spawnFastZombie(random);
                }
            }
        }
        spawnCounter++;
        if(spawnCounter == 20)
            spawnCounter = 0;
        if(zombiesToSpawn < 5 && zombiesToSpawn > 0) {
            spawnFastZombie(random);
            return;
        }
        if(spawnCounter == 5) {
            if(random.nextInt(3) != 2) {
                for(int i = 0; i <= wave; i++) {
                    if(zombiesToSpawn > 0) {
                        if(wave > 7) {
                            if(random.nextInt(2) == 1)
                                spawnSoftHardZombie(random);
                        } else if(wave > 14) {
                            if(random.nextInt(2) == 1)
                                spawnHardZombie(random);
                        } else if(wave > 20) {
                            if(random.nextInt(3) == 1)
                                spawnKnockbackResistantZombies(random);
                        } else if(wave > 23) {
                            if(random.nextInt(4) == 1)
                                spawnVillagerSlayer(random);
                        } else {
                            spawnFastZombie(random);
                        }
                    }
                }
            } else {
                for(int i = 0; i <= wave; i++) {
                    if(zombiesToSpawn > 0)
                        spawnBabyZombie(random);
                }
            }
        }
        if(spawnCounter == 15 && wave > 4) {
            if(wave > 8) {
                for(int i = 0; i < (wave - 7); i++) {
                    if(zombiesToSpawn > 0)
                        spawnHardZombie(random);
                }
            } else {
                for(int i = 0; i < (wave - 3); i++) {
                    if(zombiesToSpawn > 0)
                        spawnSoftHardZombie(random);
                }
            }

        }

        if(random.nextInt(8) == 0 && wave > 10) {
            for(int i = 0; i < (wave - 8); i++) {
                if(zombiesToSpawn > 0)
                    spawnPlayerBuster(random);
            }
        }
        if(random.nextInt(8) == 0 && wave > 7) {
            for(int i = 0; i < (wave - 5); i++) {
                if(zombiesToSpawn > 0)
                    spawnHalfInvisibleZombie(random);
            }
        }
        if(random.nextInt(8) == 0 && wave > 15) {
            for(int i = 0; i < (wave - 13); i++) {
                if(zombiesToSpawn > 0)
                    spawnHalfInvisibleZombie(random);
            }
        }
        if(random.nextInt(8) == 0 && wave > 23) {
            if(zombiesToSpawn > 0)
                spawnHalfInvisibleZombie(random);
        }
        if(random.nextInt(8) == 0 && getIronGolems().size() > 0 && wave >= 6) {
            for(int i = 0; i < (wave - 4); i++) {
                if(zombiesToSpawn > 0)
                    spawnGolemBuster(random);
            }
        }
    }

    /**
     * Get current game wave
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
     * Get alive wolves
     *
     * @return alive wolves in game
     */
    public List<Wolf> getWolfs() {
        return wolfs;
    }

    /**
     * Get alive iron golems
     *
     * @return alive iron golems in game
     */
    public List<IronGolem> getIronGolems() {
        return ironGolems;
    }

    /**
     * Get alive villagers
     *
     * @return alive villagers in game
     */
    public List<Villager> getVillagers() {
        return villagers;
    }

    /**
     * Clear all villagers in arena
     */
    public void clearVillagers() {
        for(Villager villager : villagers) {
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
        if(rottenFleshLevel == 0 && rottenFleshAmount > 50) {
            rottenFleshLevel = 1;
            return true;
        }
        if(rottenFleshLevel * 10 * getPlayers().size() + 10 < rottenFleshAmount) {
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
        if(player == null)
            return;
        if(player.getUniqueId() == null)
            return;
        players.remove(player.getUniqueId());
    }

    List<Player> getPlayersLeft() {
        List<Player> players = new ArrayList<>();
        for(User user : UserManager.getUsers(this)) {
            if(!user.isFakeDead())
                players.add(user.toPlayer());
        }
        return players;
    }

    void showPlayers() {
        for(Player player : getPlayers()) {
            for(Player p : getPlayers()) {
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
        user.addInt("xp", i);
        if(player.hasPermission(PermissionsManager.getVip())) {
            user.addInt("xp", (int) Math.ceil(i / 2));
        }
        if(player.hasPermission(PermissionsManager.getMvp())) {
            user.addInt("xp", (int) Math.ceil(i / 2));
        }
        if(player.hasPermission(PermissionsManager.getElite())) {
            user.addInt("xp", (int) Math.ceil(i / 2));
        }
        ArenaUtils.updateLevelStat(player, this);
    }

    void addStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        user.addInt(stat, 1);
        ArenaUtils.updateLevelStat(player, this);
    }

    protected void addVillager(Villager villager) {
        villagers.add(villager);

    }

    void removeVillager(Villager villager) {
        if(villagers.contains(villager)) {
            villager.remove();
            villager.setHealth(0);
            villagers.remove(villager);
        }
    }

    protected void addIronGolem(IronGolem ironGolem) {
        ironGolems.add(ironGolem);
    }

    void restoreDoors() {
        for(Location location : doorBlocks.keySet()) {
            Block block = location.getBlock();
            Byte doorData = doorBlocks.get(location);
            int id = Material.WOODEN_DOOR.getId();
            block.setTypeIdAndData(id, doorData, false);
        }
    }

}
