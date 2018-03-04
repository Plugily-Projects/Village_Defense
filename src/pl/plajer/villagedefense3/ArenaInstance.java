package pl.plajer.villagedefense3;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;
import pl.plajer.villagedefense3.handlers.*;
import pl.plajer.villagedefense3.items.SpecialItemManager;
import pl.plajer.villagedefense3.kits.GolemFriendKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.villagedefenseapi.*;

import java.util.*;

//import me.confuser.barapi.BarAPI;
//import me.mgone.bossbarapi.BossbarAPI;

/**
 * Created by Tom on 12/08/2014.
 */
public abstract class ArenaInstance extends GameInstance implements Listener {

    public static Main plugin;
    private LinkedHashMap<Location, Byte> doorBlocks = new LinkedHashMap<>();
    protected List<Location> zombieSpawns = new ArrayList<>();
    protected int zombiesToSpawn;
    private List<Location> villagerSpawnPoints = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Wolf> wolfs = new ArrayList<>();
    private List<Villager> villagers = new ArrayList<>();
    private List<IronGolem> ironGolems = new ArrayList<>();
    private boolean isFighting;
    private int wave;
    private int rottenFleshAmount;
    private int rottenFleshLevel;
    private int zombieChecker = 0;
    private Random random;
    private List<Zombie> glitchedZombies = new ArrayList<>();
    private int spawnCounter = 0;
    private HashMap<Zombie, Location> zombieCheckerLocations = new HashMap<>();

    public ArenaInstance(String ID) {
        super(ID);
        random = new Random();
    }

    public HashMap<Location, Byte> getDoorLocations() {
        return doorBlocks;
    }

    @Override
    public boolean needsPlayers() {
        //Tom do you think is it a good code? :thinking:
        if(getGameState() == GameState.STARTING || getGameState() == GameState.WAITING_FOR_PLAYERS)
            return true;
        else
            return true;
    }

    @Override
    public void run() {
        User.handleCooldowns();
        updateScoreboard();
        switch(getGameState()) {
            case WAITING_FOR_PLAYERS:
                if(plugin.isBungeeActivated())
                    plugin.getServer().setWhitelist(false);
                if(getPlayers().size() < getMIN_PLAYERS()) {
                    if(getTimer() <= 0) {
                        setTimer(15);
                        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMIN_PLAYERS());
                        for(Player player1 : getPlayers()) {
                            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                        }
                        return;
                    }
                } else {
                    for(Player p : getPlayers()) {
                        p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
                    }
                    setGameState(GameState.STARTING);
                    setTimer(Main.STARTING_TIMER_TIME);
                    this.showPlayers();

                }
                setTimer(getTimer() - 1);
                break;

            case STARTING:
                if(getTimer() == 0) {
                    VillageGameStartEvent villageGameStartEvent = new VillageGameStartEvent(this);
                    Bukkit.getPluginManager().callEvent(villageGameStartEvent);
                    setGameState(GameState.IN_GAME);
                    setTimer(5);
                    teleportAllToStartLocation();
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SURVIVAL);
                        UserManager.getUser(player.getUniqueId()).setInt("orbs", plugin.getConfig().getInt("Orbs-Starting-Amount"));
                        hidePlayersOutsideTheGame(player);
                        if(UserManager.getUser(player.getUniqueId()).getKit() != null) {
                            UserManager.getUser(player.getUniqueId()).getKit().giveKitItems(player);
                        } else {
                            plugin.getKitRegistry().getDefaultKit().giveKitItems(player);
                        }
                        player.updateInventory();
                        addStat(player, "gamesplayed");
                        addStat(player, 10);
                        setTimer(25);
                        player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started"));
                    }
                    isFighting = false;
                }
                setTimer(getTimer() - 1);
                break;
            case IN_GAME:
                if(plugin.isBungeeActivated()) {
                    if(getMAX_PLAYERS() <= getPlayers().size()) {
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
                    this.stopGame(false);
                    this.setGameState(GameState.ENDING);
                    if(getVillagers().size() <= 0) {
                        showPlayers();
                        this.setTimer(10);
                    } else this.setTimer(5);
                    return;
                }
                if(isFighting) {
                    if(getZombiesLeft() <= 0) {
                        isFighting = false;
                        endWave();
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
                                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Zombie-Got-Stuck-In-The-Map"));
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
                        isFighting = true;
                        startWave();
                    }
                }
                setTimer(getTimer() - 1);
                break;
            case ENDING:
                if(plugin.isBungeeActivated())
                    plugin.getServer().setWhitelist(false);
                if(getTimer() <= 0) {
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

                        ArmorHelper.clearArmor(player);
                        // if (plugin.isBungeeActivated())
                        //BossbarAPI.removeBar(player);
                        player.setMaxHealth(20.0);
                        for(Player players : plugin.getServer().getOnlinePlayers()) {
                            if(plugin.getGameInstanceManager().getGameInstance(players) != null)
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
                        p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
                    }

                    setGameState(GameState.RESTARTING);


                    for(User user : UserManager.getUsers(this)) {
                        user.setSpectator(false);
                        user.setInt("orbs", 0);
                        user.setFakeDead(false);
                    }
                    clearPlayers();
                    plugin.getRewardsHandler().performEndGameRewards(this);
                    if(plugin.isBungeeActivated()) {
                        if(ConfigurationManager.getConfig("bungee").getBoolean("Shutdown-When-Game-Ends"))
                            plugin.getServer().shutdown();
                    }
                }
                setTimer(getTimer() - 1);
                break;
            case RESTARTING:
                clearVillagers();
                this.restoreMap();

                getPlayers().clear();

                setGameState(GameState.WAITING_FOR_PLAYERS);

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

    private void updateLevelStat(Player player) {
        User user = UserManager.getUser(player.getUniqueId());

        if(Math.pow(50 * user.getInt("level"), 1.5) < user.getInt("xp")) {
            user.addInt("level", 1);
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.formatMessage(ChatManager.colorMessage("In-Game.You-Leveled-Up"), user.getInt("level")));
        }
    }

    public void setZombieAmount() {
        zombiesToSpawn = (int) Math.ceil((getPlayers().size() * 0.5) * (wave * wave) / 2);
    }

    public void resetRottenFlesh() {
        this.rottenFleshAmount = 0;
        this.rottenFleshLevel = 0;
    }

    public void stopGame(boolean quickStop) {
        VillageGameStopEvent villageGameStopEvent = new VillageGameStopEvent(this);
        Bukkit.getPluginManager().callEvent(villageGameStopEvent);
        if(getPlayersLeft().size() > 0) {
            for(Player p : getPlayers()) {
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.All-Villagers-Died"));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Reached-Wave-X").replaceAll("%NUMBER%", String.valueOf(getWave())));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds"));
            }
        } else {
            for(Player p : getPlayers()) {
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.All-Players-Died"));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Reached-Wave-X").replaceAll("%NUMBER%", String.valueOf(getWave())));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds"));
            }
        }
        for(final Player player : getPlayers()) {
            setStat(player, "highestwave", wave);
            addStat(player, wave);

            UserManager.getUser(player.getUniqueId()).removeScoreboard();
            if(!quickStop) {
                if(plugin.getConfig().getBoolean("Firework-When-Game-Ends")) {
                    new BukkitRunnable() {
                        int i = 0;

                        @Override
                        public void run() {
                            if(i == 4) this.cancel();
                            if(!getPlayers().contains(player)) this.cancel();
                            Util.spawnRandomFirework(player.getLocation());
                            i++;
                        }
                    }.runTaskTimer(plugin, 30, 30);
                }
            }
        }
        this.resetRottenFlesh();
        this.restoreDoors();
        for(Zombie zombie : getZombies()) {
            zombie.remove();
        }
        zombies.clear();
        for(IronGolem ironGolem : getIronGolems()) {
            ironGolem.remove();
        }
        ironGolems.clear();
        for(Villager villager : getVillagers()) {
            villager.remove();

        }
        villagers.clear();
        for(Wolf wolf : getWolfs()) {
            wolf.remove();
        }
        wolfs.clear();
        clearZombies();
        clearGolems();
        clearVillagers();
        clearWolfs();
        for(Entity entity : getStartLocation().getWorld().getEntities()) {
            if(entity.getWorld().getName().equalsIgnoreCase(getStartLocation().getWorld().getName())
                    && entity.getLocation().distance(getStartLocation()) < 300)
                if(entity.getType() != EntityType.PLAYER)
                    entity.remove();
        }
    }

    public void restoreMap() {
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

    public void spawnVillagers() {
        if(getVillagers().size() > 10) {
        } else if(getVillagerSpawns() == null || getVillagerSpawns().size() <= 0) {
            if(Main.isDebugged()) {
                System.out.print(ChatColor.RED + "[Village Debugger] NO VILLAGERSPAWNS DEFINED FOR ARENA " + this.getID() + "! ARENA CAN'T RUN WITHOUT VILLAGER SPAWNS! PLEASE ADD VILLAGER SPAWNS!");
            }
        } else {
            for(Location location : getVillagerSpawns()) {
                spawnVillager(location);
            }
            if(getVillagers().size() != 0) {
                spawnVillagers();
            } else {
                if(Main.isDebugged()) {
                    System.out.print("[Village Debugger] UNABLE TO SPAWN VILLAGERS! PLEASE CONTACT THE DEV TO SOLVE this PROBLEM!!");
                }
            }
        }
    }


    public void start() {
        this.runTaskTimer(plugin, 20L, 20L);
        this.setGameState(GameState.RESTARTING);
        for(Location location : villagerSpawnPoints) {
            plugin.getChunkManager().keepLoaded(location.getChunk());
        }
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public void removeZombie(Zombie zombie) {
        if(zombies.contains(zombie))
            zombies.remove(zombie);
    }

    public List<Location> getVillagerSpawns() {
        return villagerSpawnPoints;
    }

    public void clearGolems() {
        for(IronGolem ironGolem : ironGolems) {
            ironGolem.remove();
        }
        this.ironGolems.clear();
    }

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

    public void clearZombies() {
        for(Zombie zombie : zombies) {
            zombie.remove();
        }
        zombies.clear();
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void startWave() {
        VillageWaveStartEvent villageWaveStartEvent = new VillageWaveStartEvent(this, wave);
        Bukkit.getPluginManager().callEvent(villageWaveStartEvent);
        setZombieAmount();
        if(plugin.getConfig().getBoolean("Respawn-After-Wave"))
            this.bringDeathPlayersBack();
        for(User user : UserManager.getUsers(this)) {
            user.getKit().reStock(user.toPlayer());
        }
        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Wave-Started"), wave);
        for(Player player1 : getPlayers()) {
            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
    }


    public void endWave() {
        plugin.getRewardsHandler().performEndWaveRewards(this, wave);
        setTimer(25);
        zombieCheckerLocations.clear();
        wave = wave + 1;
        VillageWaveEndEvent villageWaveEndEvent = new VillageWaveEndEvent(this, wave);
        Bukkit.getPluginManager().callEvent(villageWaveEndEvent);
        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Next-Wave-In"), getTimer());
        for(Player player1 : getPlayers()) {
            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
        for(Player player : getPlayers()) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.You-Feel-Refreshed"));
            player.setHealth(player.getMaxHealth());
            UserManager.getUser(player.getUniqueId()).addInt("orbs", wave * 10);
        }
        if(plugin.getConfig().getBoolean("Respawn-After-Wave"))
            this.bringDeathPlayersBack();
        for(Player player : getPlayersLeft()) {
            this.addStat(player, 5);
        }
    }

    @Override
    public void joinAttempt(Player p) {
        VillageGameJoinAttemptEvent villageGameJoinAttemptEvent = new VillageGameJoinAttemptEvent(p, this);
        Bukkit.getPluginManager().callEvent(villageGameJoinAttemptEvent);
        if(villageGameJoinAttemptEvent.isCancelled()) {
            p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
            return;
        }
        if(!plugin.isBungeeActivated()) {
            if(!p.hasPermission(PermissionsManager.getJoinPerm().replaceAll("<arena>", "*")) || !p.hasPermission(PermissionsManager.getJoinPerm().replaceAll("<arena>", this.getID()))) {
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Join-No-Permission"));
                return;
            }
        }
        if(Main.isDebugged()) {
            System.out.println("[Village Debugger] Player " + p.getName() + " attemping to join arena!");
        }
        if((getGameState() == GameState.IN_GAME || (getGameState() == GameState.STARTING && getTimer() <= 3) || getGameState() == GameState.ENDING)) {
            if(plugin.isInventoryManagerEnabled()) {
                p.setLevel(0);
                plugin.getInventoryManager().saveInventoryToFile(p);

            }
            this.teleportToStartLocation(p);
            p.sendMessage(ChatManager.colorMessage("In-Game.You-Are-Spectator"));
            p.getInventory().clear();

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
            spectatorItem.setItemMeta(spectatorMeta);
            p.getInventory().addItem(spectatorItem);

            for(PotionEffect potionEffect : p.getActivePotionEffects()) {
                p.removePotionEffect(potionEffect.getType());

            }

            this.addPlayer(p);
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(true);
            p.setFlying(true);
            User user = UserManager.getUser(p.getUniqueId());
            user.setSpectator(true);
            user.setFakeDead(true);
            user.setInt("orbs", 0);
            this.hidePlayer(p);

            for(Player spectator : this.getPlayers()) {
                if(UserManager.getUser(spectator.getUniqueId()).isSpectator()) {
                    p.hidePlayer(spectator);
                } else {
                    p.showPlayer(spectator);
                }
            }
            hidePlayersOutsideTheGame(p);
            return;
        }
        if(plugin.isInventoryManagerEnabled()) {
            p.setLevel(0);
            plugin.getInventoryManager().saveInventoryToFile(p);

        }
        teleportToLobby(p);
        this.addPlayer(p);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.setFlying(false);
        p.setAllowFlight(false);
        p.getInventory().clear();
        showPlayers();
        if(!UserManager.getUser(p.getUniqueId()).isSpectator())
            getChatManager().broadcastJoinMessage(p);
        User user = UserManager.getUser(p.getUniqueId());
        user.setKit(plugin.getKitRegistry().getDefaultKit());
        plugin.getKitManager().giveKitMenuItem(p);
        if(getGameState() == GameState.STARTING || getGameState() == GameState.WAITING_FOR_PLAYERS)
            p.getInventory().setItem(SpecialItemManager.getSpecialItem("Leave").getSlot(), SpecialItemManager.getSpecialItem("Leave").getItemStack());
        p.updateInventory();
        for(Player player : getPlayers()) {
            showPlayer(player);
        }
        showPlayers();
    }


    public int getZombiesLeft() {
        return zombiesToSpawn + getZombies().size();

    }

    private void addStat(Player player, int i) {
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
        this.updateLevelStat(player);
    }

    private void addStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        user.addInt(stat, 1);
        this.updateLevelStat(player);
    }

    private void setStat(Player player, String stat, int i) {
        User user = UserManager.getUser(player.getUniqueId());
        if(user.getInt(stat) <= i) {
            user.setInt(stat, i);
        }
    }

    public void spawnZombies() {
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

        if(zombiesToSpawn < 5) {
            if(zombiesToSpawn > 0)
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
        if(random.nextInt(8) == 0 && getIronGolems().size() > 0 && wave >= 6) {
            for(int i = 0; i < (wave - 4); i++) {
                if(zombiesToSpawn > 0)
                    spawnGolemBuster(random);

            }

        }


    }

    public int getWave() {
        return wave;
    }

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

    public void addWolf(Wolf wolf) {
        wolfs.add(wolf);
    }

    public List<Wolf> getWolfs() {
        return wolfs;
    }

    public List<IronGolem> getIronGolems() {
        return ironGolems;
    }

    public List<Villager> getVillagers() {
        return villagers;
    }

    public void addVillager(Villager villager) {
        villagers.add(villager);

    }

    public void removeVillager(Villager villager) {
        if(villagers.contains(villager)) {

            villager.remove();

            villager.setHealth(0);

            villagers.remove(villager);

        }
    }


    public void clearVillagers() {
        for(Villager villager : villagers) {
            villager.remove();
        }
        villagers.clear();
    }

    public void addIronGolem(IronGolem ironGolem) {
        ironGolems.add(ironGolem);
    }

    public void addDoor(Location location, byte data) {
        this.doorBlocks.put(location, data);
    }

    public void restoreDoors() {
        for(Location location : doorBlocks.keySet()) {

            Block block = location.getBlock();
            Byte doorData = doorBlocks.get(location);
            int id = Material.WOODEN_DOOR.getId();
            block.setTypeIdAndData(id, doorData, false);

        }
    }

    public void updateScoreboard() {
        if(getPlayers().size() == 0)
            return;
        for(Player p : getPlayers()) {
            User user = UserManager.getUser(p.getUniqueId());
            if(user.getScoreboard().getObjective("waiting") == null) {
                user.getScoreboard().registerNewObjective("waiting", "dummy");
                user.getScoreboard().registerNewObjective("starting", "dummy");
                user.getScoreboard().registerNewObjective("ingame", "dummy");
                user.getScoreboard().registerNewObjective("ingame2", "dummy");

            }
            switch(getGameState()) {
                case WAITING_FOR_PLAYERS:
                case STARTING:
                    Objective startingobj = user.getScoreboard().getObjective("starting");
                    startingobj.setDisplayName(ChatManager.colorMessage("Scoreboard.Header"));
                    startingobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if(getGameState() == GameState.STARTING) {
                        Score timerscore = startingobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Starting-In")));
                        timerscore.setScore(getTimer());
                    }
                    Score playerscore = startingobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Players")));
                    playerscore.setScore(getPlayers().size());
                    Score minplayerscore = startingobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Minimum-Players")));
                    minplayerscore.setScore(getMIN_PLAYERS());
                    break;
                case IN_GAME:
                    if(isFighting) {
                        Objective ingameobj = user.getScoreboard().getObjective("ingame");
                        ingameobj.setDisplayName(ChatManager.colorMessage("Scoreboard.Header"));
                        ingameobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                        Score playerleftscore = ingameobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Players-Left")));
                        playerleftscore.setScore(this.getPlayersLeft().size());

                        Score villagersscore = ingameobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Villagers-Left")));
                        villagersscore.setScore(getVillagers().size());
                        Score orbsscore = ingameobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Orbs")));
                        orbsscore.setScore(user.getInt("orbs"));
                        Score zombiesscore = ingameobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Zombies-Left")));
                        zombiesscore.setScore(getZombiesLeft());
                        Score rottenfleshscore = ingameobj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Rotten-Flesh")));
                        rottenfleshscore.setScore(getRottenFlesh());
                    } else {
                        Objective ingame2obj = user.getScoreboard().getObjective("ingame2");
                        ingame2obj.setDisplayName(ChatManager.colorMessage("Scoreboard.Header"));
                        ingame2obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                        Score playerleftscore = ingame2obj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Players-Left")));
                        playerleftscore.setScore(this.getPlayersLeft().size());

                        Score villagersscore = ingame2obj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Villagers-Left")));
                        villagersscore.setScore(getVillagers().size());
                        Score orbsscore = ingame2obj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Orbs")));
                        orbsscore.setScore(user.getInt("orbs"));
                        Score nextwavescore = ingame2obj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Next-Wave-In")));
                        nextwavescore.setScore(getTimer());
                        Score rottenfleshscore = ingame2obj.getScore(ChatManager.formatMessage(ChatManager.colorMessage("Scoreboard.Rotten-Flesh")));
                        rottenfleshscore.setScore(getRottenFlesh());
                    }
                    break;
                case ENDING:
                    user.removeScoreboard();
                    break;
                case RESTARTING:
                    break;
                default:
                    setGameState(GameState.WAITING_FOR_PLAYERS);
            }
            user.setScoreboard(user.getScoreboard());
        }


    }

    @EventHandler
    public void onDieEntity(EntityDeathEvent event) {
        if(event.getEntity().getType() == EntityType.ZOMBIE) {
            if(getZombies().contains(event.getEntity()))
                removeZombie((Zombie) event.getEntity());
            if(event.getEntity().getKiller() != null) {
                if(GameInstance.plugin.getGameInstanceManager().getGameInstance(event.getEntity().getKiller()) != null) {
                    addStat(event.getEntity().getKiller(), "kills");
                    addStat(event.getEntity().getKiller(), 2);
                }
            }
        }
        if(event.getEntity().getType() == EntityType.VILLAGER) {
            if(getVillagers().contains(event.getEntity())) {
                getStartLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                removeVillager((Villager) event.getEntity());
                for(Player p : getPlayers()) {
                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Villager-Died"));
                }
            }
        }

    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if(!getPlayers().contains(event.getEntity()))
            return;
        if(getPlayers().contains(event.getEntity()))
            this.onDeath(event.getEntity());
        if(event.getEntity().isDead())
            event.getEntity().setHealth(event.getEntity().getMaxHealth());
        event.setDeathMessage("");
        this.onDeath(event.getEntity());
    }


    public void onDeath(final Player player) {
        if(getGameState() == GameState.STARTING) {
            player.teleport(this.getStartLocation());
            return;
        }
        if(getGameState() == GameState.ENDING || getGameState() == GameState.RESTARTING) {
            player.getInventory().clear();
            player.setFlying(false);
            player.setAllowFlight(false);
            User user = UserManager.getUser(player.getUniqueId());
            user.setInt("orbs", 0);
            player.teleport(this.getEndLocation());
            return;
        }
        User user = UserManager.getUser(player.getUniqueId());
        addStat(player, "deaths");

        if(user.isFakeDead()) {
            player.setAllowFlight(true);
            player.setGameMode(GameMode.SURVIVAL);
            teleportToStartLocation(player);
        } else {
            teleportToStartLocation(player);
            user.setSpectator(true);
            player.setGameMode(GameMode.SURVIVAL);
            user.setFakeDead(true);
            user.setInt("orbs", 0);
            hidePlayer(player);
            player.setAllowFlight(true);
            player.getInventory().clear();
            MessageHandler.sendTitle(player, ChatColor.stripColor(ChatManager.formatMessage("In-Game.Death-Screen")), 0, 5 * 20, 0, ChatColor.RED);
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(user.isSpectator()) {
                    MessageHandler.sendActionBar(player, ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Died-Respawn-In-Next-Wave")));
                }
            }, 20, 20);
            getChatManager().broadcastDeathMessage(player);

            teleportToStartLocation(player);

            player.setAllowFlight(true);
            player.setFlying(true);

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
            spectatorItem.setItemMeta(spectatorMeta);
            player.getInventory().addItem(spectatorItem);
        }

    }

    public void hidePlayersOutsideTheGame(Player player) {
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(getPlayers().contains(players))
                continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    @Override
    public void leaveAttempt(Player p) {
        VillageGameLeaveAttemptEvent villageGameLeaveAttemptEvent = new VillageGameLeaveAttemptEvent(p, this);
        Bukkit.getPluginManager().callEvent(villageGameLeaveAttemptEvent);
        if(Main.isDebugged()) {
            System.out.println("[Village Debugger] Player " + p.getName() + " is attemping to leave arena!");
        }
        User user = UserManager.getUser(p.getUniqueId());
        user.setInt("orbs", 0);
        p.getInventory().clear();
        ArmorHelper.clearArmor(p);


        this.removePlayer(p);
        if(!user.isSpectator()) {
            getChatManager().broadcastLeaveMessage(p);
        }
        user.setFakeDead(false);
        user.setSpectator(false);
        user.removeScoreboard();
        if(user.getKit() instanceof GolemFriendKit) {
            for(IronGolem ironGolem : getIronGolems()) {
                if(ironGolem.getCustomName().contains(user.toPlayer().getName()))
                    ironGolem.remove();
            }
        }
        //  if(plugin.isBarEnabled())
        //    BossbarAPI.removeBar(p);

        p.setMaxHealth(20.0);
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        for(PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setFireTicks(0);
        if(getPlayers().size() == 0) {
            this.setGameState(GameState.RESTARTING);
        }


        p.setGameMode(GameMode.SURVIVAL);
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(GameInstance.plugin.getGameInstanceManager().getGameInstance(players) != null)
                players.showPlayer(p);
            p.showPlayer(players);
        }

        this.teleportToEndLocation(p);
        if(!plugin.isBungeeActivated() && plugin.isInventoryManagerEnabled()) {
            plugin.getInventoryManager().loadInventory(p);

        }


    }

    public void onRespawn(Player player) {
        User user = UserManager.getUser(player.getUniqueId());
        if(user.isFakeDead()) {
            teleportToStartLocation(player);
            player.setAllowFlight(true);
            player.setFlying(true);

        } else {
            teleportToStartLocation(player);
            user.setSpectator(true);
            player.setGameMode(GameMode.SURVIVAL);
            user.setFakeDead(true);
            player.setAllowFlight(true);
            player.setFlying(true);
            user.setInt("orbs", 0);

        }

    }

    public void addRottenFlesh(int i) {
        rottenFleshAmount = rottenFleshAmount + i;
    }

    public int getRottenFlesh() {
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


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(getPlayers().contains(event.getPlayer())) {
            this.onRespawn(event.getPlayer());
            event.setRespawnLocation(this.getStartLocation());
        }
    }

    public void bringDeathPlayersBack() {
        for(Player player : getPlayers()) {
            if(!getPlayersLeft().contains(player)) {

                User user = UserManager.getUser(player.getUniqueId());
                user.setFakeDead(false);
                user.setSpectator(false);

                teleportToStartLocation(player);
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setGameMode(GameMode.SURVIVAL);
                this.showPlayers();
                player.getInventory().clear();
                user.getKit().giveKitItems(player);
                player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
            }

        }
    }


}
