package me.tomthedeveloper;

import me.tomthedeveloper.chunks.ChunkManager;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.game.GameState;
import me.tomthedeveloper.game.InstanceType;
import me.tomthedeveloper.handlers.*;
import me.tomthedeveloper.items.SpecialItemManager;
import me.tomthedeveloper.kits.GolemFriendKit;
import me.tomthedeveloper.permissions.PermissionsManager;
import me.tomthedeveloper.utils.ArmorHelper;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.*;

//import me.confuser.barapi.BarAPI;
//import me.mgone.bossbarapi.BossbarAPI;

/**
 * Created by Tom on 12/08/2014.
 */
public abstract class InvasionInstance extends GameInstance implements Listener {

    public static Main youtuberInvasion;
    public LinkedHashMap<Location, Byte> doorblocks = new LinkedHashMap<>();
    protected List<Location> zombiespawns = new ArrayList<>();
    protected int zombiestospawn;
    private List<Location> villagerspawns = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Wolf> wolfs = new ArrayList<>();
    private List<Villager> villagers = new ArrayList<>();
    private List<IronGolem> irongolems = new ArrayList<>();
    private boolean FIGHTING;
    private int wave;
    private int rottenflesh;
    private int rottenfleshlevel;
    private int zombiechecker = 0;
    private Random random;
    private List<Zombie> glitchedzombies = new ArrayList<>();

    private int spawncounter = 0;
    private HashMap<Zombie, Location> zombiecheckerlocations = new HashMap<>();


    public InvasionInstance(String ID) {
        super(ID);
        setType(InstanceType.VILLAGE_DEFENSE);
        random = new Random();
    }

    public HashMap<Location, Byte> getDoorLocations() {
        return doorblocks;
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
                    plugin.getPlugin().getServer().setWhitelist(false);
                if(getPlayers().size() < getMIN_PLAYERS()) {
                    if(getTimer() <= 0) {
                        setTimer(15);
                        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Lobby-Messages.Waiting-For-Players"), getMIN_PLAYERS());
                        for(Player player1 : getPlayers()) {
                            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                        }
                        return;
                    }
                } else {
                    for(Player p : getPlayers()) {
                        p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Lobby-Messages.Enough-Players-To-Start"));
                    }
                    setGameState(GameState.STARTING);
                    setTimer(Main.STARTING_TIMER_TIME);
                    this.showPlayers();

                }
                setTimer(getTimer() - 1);
                break;

            case STARTING:
                if(getTimer() == 0) {
                    setGameState(GameState.INGAME);
                    setTimer(5);
                    teleportAllToStartLocation();
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SURVIVAL);
                        UserManager.getUser(player.getUniqueId()).setInt("orbs", 20);
                        hidePlayersOutsideTheGame(player);
                        if(UserManager.getUser(player.getUniqueId()).getKit() != null) {
                            UserManager.getUser(player.getUniqueId()).getKit().giveKitItems(player);
                        } else {
                            plugin.getKitHandler().getDefaultKit().giveKitItems(player);
                        }
                        player.updateInventory();
                        addStat(player, "gamesplayed");
                        addStat(player, "xp", 10);
                        setTimer(25);
                        player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Lobby-Messages.Game-Started"));
                    }
                    FIGHTING = false;


                }
                setTimer(getTimer() - 1);
                break;
            case INGAME:
                if(plugin.isBungeeActivated()) {
                    if(getMAX_PLAYERS() <= getPlayers().size()) {
                        youtuberInvasion.getServer().setWhitelist(true);
                    } else {
                        youtuberInvasion.getServer().setWhitelist(false);
                    }
                }
                zombiechecker++;
                if(zombiechecker >= 60) {
                    List<Villager> remove = new ArrayList<>();
                    for(Villager villager : getVillagers()) {
                        if(villager.isDead())
                            remove.add(villager);
                    }
                    for(Villager villager : remove) {
                        removeVillager(villager);
                    }
                    remove.clear();
                    zombiechecker = 0;
                    List<Zombie> removeaferloop = new ArrayList<>();
                    for(Zombie zombie : getZombies()) {
                        if(zombie.isDead()) {
                            removeaferloop.add(zombie);
                            continue;
                        }
                        if(glitchedzombies.contains(zombie) && zombie.getLocation().distance(zombiecheckerlocations.get(zombie)) <= 1) {
                            removeaferloop.add(zombie);
                            zombiecheckerlocations.remove(zombie);
                            zombie.remove();
                        }
                        if(zombiecheckerlocations.get(zombie) == null) {
                            zombiecheckerlocations.put(zombie, zombie.getLocation());
                        } else {
                            Location location = zombiecheckerlocations.get(zombie);

                            if(zombie.getLocation().distance(location) <= 1) {
                                zombie.teleport(zombiespawns.get(random.nextInt(zombiespawns.size() - 1)));
                                zombiecheckerlocations.put(zombie, zombie.getLocation());
                                glitchedzombies.add(zombie);
                            }
                        }
                    }

                    for(Zombie zombie : removeaferloop) {
                        removeZombie(zombie);
                    }
                    removeaferloop.clear();

                }
                if(getVillagers().size() <= 0) {
                    clearZombies();
                    this.stopGame();
                    this.setGameState(GameState.ENDING);
                    showPlayers();
                    this.setTimer(10);

                    return;
                }
                if(getPlayersLeft().size() <= 0) {
                    clearZombies();
                    this.stopGame();
                    this.setGameState(GameState.ENDING);
                    setTimer(5);
                    return;

                }
                if(FIGHTING) {

                    if(getZombiesLeft() <= 0) {
                        FIGHTING = false;
                        endWave();
                    }
                    if(zombiestospawn > 0) {
                        spawnZombies();
                        setTimer(500);
                    } else {
                        if(getTimer() == 0) {
                            if(getZombiesLeft() <= 5) {
                                //zombiestospawn = getZombiesLeft();
                                clearZombies();
                                zombiestospawn = 0;
                                for(Player p : getPlayers()) {
                                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Zombie-Got-Stuck-In-The-Map"));
                                }
                            } else {
                                int i = getZombiesLeft();
                                getZombies().clear();
                                for(i = getZombiesLeft(); i > 0; i++) {
                                    spawnFastZombie(random);
                                }

                                // zombiestospawn = getZombiesLeft();
                                //zombiestospawn = 0;
                                //zombies.clear();
                                // getChatManager().broadcastMessage("There went something wrong internal. Hopefully it's fixed now but that problem spawned some zombies!");
                            }

                        }

                    }
                    if(zombiestospawn < 0)
                        zombiestospawn = 0;
                    setTimer(getTimer() - 1);

                } else {
                    if(getTimer() <= 0) {
                        FIGHTING = true;

                        startWave();

                    }
                }
                setTimer(getTimer() - 1);
                break;
            case ENDING:
                if(plugin.isBungeeActivated())
                    youtuberInvasion.getServer().setWhitelist(false);
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
                        for(Player players : youtuberInvasion.getServer().getOnlinePlayers()) {
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
                        p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("commands.Teleported-To-The-Lobby"));
                    }

                    setGameState(GameState.RESTARTING);


                    for(User user : UserManager.getUsers(this)) {
                        user.setSpectator(false);
                        user.setInt("orbs", 0);
                        user.setFakeDead(false);
                    }
                    clearPlayers();
                    youtuberInvasion.getRewardsHandler().performEndGameRewards(this);
                    if(plugin.isBungeeActivated()) {
                        if(ConfigurationManager.getConfig("bungee").getBoolean("ShutdownWhenGameEnds"))
                            plugin.getPlugin().getServer().shutdown();
                    }
                }
                setTimer(getTimer() - 1);
                break;
            case RESTARTING:
                clearVillagers();
                this.restoreMap();


                getPlayers().clear();
                plugin.getSignManager().addToQueue(this);

                setGameState(GameState.WAITING_FOR_PLAYERS);

                wave = 1;
                if(plugin.isBungeeActivated()) {
                    for(Player player : youtuberInvasion.getServer().getOnlinePlayers()) {
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
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.formatMessage(ChatManager.colorMessage("In-game.You-Leveled-Up"), user.getInt("level")));
        }
    }

    public void setZombieAmount() {
        int playercount = getPlayers().size();
        zombiestospawn = (int) Math.ceil((playercount * 0.5) * (wave * wave) / 2);
    }

    public void resetRottenFlesh() {
        this.rottenflesh = 0;
        this.rottenfleshlevel = 0;
    }

    public void stopGame() {
        if(getPlayersLeft().size() > 0) {
            for(Player p : getPlayers()) {
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.All-Villagers-Died"));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.Reached-Wave-X").replaceAll("%NUMBER%", String.valueOf(getWave())));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds"));
            }
        } else {
            for(Player p : getPlayers()) {
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.All-Players-Died"));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.Reached-Wave-X").replaceAll("%NUMBER%", String.valueOf(getWave())));
                p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds"));
            }
        }
        for(Player player : getPlayers()) {
            //if (plugin.isBungeeActivated())
            //BossbarAPI.removeBar(player);
            setStat(player, "highestwave", wave);
            addStat(player, "xp", wave);

            UserManager.getUser(player.getUniqueId()).removeScoreboard();
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
        irongolems.clear();
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

    public int getOrbs(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("orbs");
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
        this.runTaskTimer(youtuberInvasion, 20L, 20L);
        this.setGameState(GameState.RESTARTING);
        for(Location location : villagerspawns) {
            ChunkManager.getInstance().keepLoaded(location.getChunk());
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
        return villagerspawns;
    }

    public void setVillagerSpawns(List<Location> villagerspawns) {
        this.villagerspawns = villagerspawns;
    }

    public void clearGolems() {
        for(IronGolem ironGolem : irongolems) {
            ironGolem.remove();
        }
        this.irongolems.clear();
    }

    public void clearWolfs() {
        for(Wolf wolf : wolfs) {
            wolf.remove();
        }
        this.wolfs.clear();
    }

    public void addVillagerSpawn(Location location) {
        this.villagerspawns.add(location);
    }

    public List<Location> getZombieSpawns() {
        return zombiespawns;
    }

    public void setZombieSpawns(List<Location> zombiespawns) {
        this.zombiespawns = zombiespawns;
    }

    public void addZombieSpawn(Location location) {
        zombiespawns.add(location);
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
        setZombieAmount();
        if(!youtuberInvasion.getConfig().contains("RespawnAfterWave"))
            youtuberInvasion.getConfig().set("RespawnAfterWave", true);
        if(youtuberInvasion.getConfig().getBoolean("RespawnAfterWave"))
            this.bringDeathPlayersBack();
        for(User user : UserManager.getUsers(this)) {
            user.getKit().reStock(user.toPlayer());
        }
        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Wave-Started"), wave);
        for(Player player1 : getPlayers()) {
            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
    }


    public void endWave() {
        youtuberInvasion.getRewardsHandler().performEndWaveRewards(this, wave);
        setTimer(25);
        zombiecheckerlocations.clear();
        wave = wave + 1;
        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Next-Wave-In"), getTimer());
        for(Player player1 : getPlayers()) {
            player1.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
        for(Player player : getPlayers()) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.You-Feel-Refreshed"));
            if(!(youtuberInvasion.is1_8_R3() || youtuberInvasion.is1_7_R4())) {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            } else {
                player.setHealth(player.getMaxHealth());
            }
            UserManager.getUser(player.getUniqueId()).addInt("orbs", wave * 10);
        }
        if(youtuberInvasion.getConfig().getBoolean("RespawnAfterWave"))
            this.bringDeathPlayersBack();
        for(Player player : getPlayersLeft()) {
            this.addStat(player, "xp", 5);
        }


    }

    public boolean isInventoryEmpty(Player p) {
        for(ItemStack item : p.getInventory().getContents()) {
            if(item != null)
                return false;

        }
        return true;
    }


    @Override
    public void joinAttempt(Player p) {
        if(Main.isDebugged()) {
            System.out.println("[Village Debugger] Player " + p.getName() + " attemping to join arena!");
        }
        if((getGameState() == GameState.INGAME || (getGameState() == GameState.STARTING && getTimer() <= 3) || getGameState() == GameState.ENDING)) {
            if(plugin.isInventoryManagerEnabled()) {
                p.setLevel(0);
                plugin.getInventoryManager().saveInventoryToFile(p);

            }
            this.teleportToStartLocation(p);
            p.sendMessage(ChatManager.colorMessage("In-game.You-Are-Spectator"));
            p.getInventory().clear();

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(LanguageManager.getLanguageMessage("In-game.Spectator.Spectator-Item-Name"));
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
            user.getKit().giveKitItems(p);


            for(Player spectator : plugin.getGameInstanceManager().getGameInstances().get(0).getPlayers()) {
                if(UserManager.getUser(spectator.getUniqueId()).isSpectator()) {
                    p.hidePlayer(spectator);
                    spectator.hidePlayer(p);
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
        user.setKit(plugin.getKitHandler().getDefaultKit());
        plugin.getKitMenuHandler().giveKitMenuItem(p);
        if(getGameState() == GameState.STARTING || getGameState() == GameState.WAITING_FOR_PLAYERS)
            p.getInventory().setItem(SpecialItemManager.getSpecialItem("Leave").getSlot(), SpecialItemManager.getSpecialItem("Leave").getItemStack());
        p.updateInventory();
        for(Player player : getPlayers()) {
            showPlayer(player);
        }
        showPlayers();
    }


    public int getZombiesLeft() {
        return zombiestospawn + getZombies().size();

    }

    private void addStat(Player player, String stat, int i) {
        User user = UserManager.getUser(player.getUniqueId());
        user.addInt(stat, i);
        if(stat.equalsIgnoreCase("xp")) {
            if(player.hasPermission(PermissionsManager.getVIP())) {
                user.addInt(stat, (int) Math.ceil(i / 2));
            }
            if(player.hasPermission(PermissionsManager.getMVP())) {
                user.addInt(stat, (int) Math.ceil(i / 2));
            }
            if(player.hasPermission(PermissionsManager.getELITE())) {
                user.addInt(stat, (int) Math.ceil(i / 2));
            }
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
                if(zombiestospawn > 0) {
                    spawnFastZombie(random);
                }

            }
        }
        spawncounter++;
        if(spawncounter == 20)
            spawncounter = 0;

        if(zombiestospawn < 5) {
            if(zombiestospawn > 0)
                spawnFastZombie(random);
            return;
        }
        if(spawncounter == 5) {
            if(random.nextInt(3) != 2) {
                for(int i = 0; i <= wave; i++) {
                    if(zombiestospawn > 0) {
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
                    if(zombiestospawn > 0)
                        spawnBabyZombie(random);
                }
            }
        }
        if(spawncounter == 15 && wave > 4) {
            if(wave > 8) {
                for(int i = 0; i < (wave - 7); i++) {
                    if(zombiestospawn > 0)
                        spawnHardZombie(random);
                }
            } else {
                for(int i = 0; i < (wave - 3); i++) {
                    if(zombiestospawn > 0)
                        spawnSoftHardZombie(random);
                }
            }

        }

        if(random.nextInt(8) == 0 && wave > 10) {
            for(int i = 0; i < (wave - 8); i++) {
                if(zombiestospawn > 0)
                    spawnPlayerBuster(random);
            }
        }
        if(random.nextInt(8) == 0 && wave > 7) {
            for(int i = 0; i < (wave - 5); i++) {
                if(zombiestospawn > 0)
                    spawnHalfInvisibleZombie(random);
            }
        }
        if(random.nextInt(8) == 0 && wave > 15) {
            for(int i = 0; i < (wave - 13); i++) {
                if(zombiestospawn > 0)
                    spawnHalfInvisibleZombie(random);
            }
        }
        if(random.nextInt(8) == 0 && getIronGolems().size() > 0 && wave >= 6) {
            for(int i = 0; i < (wave - 4); i++) {
                if(zombiestospawn > 0)
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
        return irongolems;
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
        irongolems.add(ironGolem);
    }

    public void removeIronGolem(IronGolem ironGolem) {
        if(irongolems.contains(ironGolem))
            irongolems.remove(ironGolem);
    }

    public void addDoor(Location location, byte data) {
        this.doorblocks.put(location, data);
    }

    public void restoreDoors() {
        for(Location location : doorblocks.keySet()) {

            Block block = location.getBlock();
            Byte doordata = doorblocks.get(location);
            int id = Material.WOODEN_DOOR.getId();
            block.setTypeIdAndData(id, doordata, false);

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
                case INGAME:
                    if(FIGHTING) {
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
                if(plugin.getGameInstanceManager().getGameInstance(event.getEntity().getKiller()) != null) {
                    addStat(event.getEntity().getKiller(), "kills");
                    addStat(event.getEntity().getKiller(), "xp", 2);
                }
            }
        }
        if(event.getEntity().getType() == EntityType.VILLAGER) {
            if(getVillagers().contains(event.getEntity())) {
                getStartLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                removeVillager((Villager) event.getEntity());
                for(Player p : getPlayers()) {
                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Villager-Died"));
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
            if(!youtuberInvasion.is1_7_R4()) {
                MessageHandler.sendTitle(player, ChatColor.stripColor(ChatManager.formatMessage("In-game.Death-Screen")), 0, 5 * 20, 0, ChatColor.RED);
                MessageHandler.sendActionbar(player, ChatManager.formatMessage(ChatManager.colorMessage("In-game.Died-Respawn-In-Next-Wave")));
            } else {
                player.sendMessage(ChatManager.colorMessage("In-game.You-Are-Spectator"));
            }
            getChatManager().broadcastDeathMessage(player);

            teleportToStartLocation(player);

            player.setAllowFlight(true);
            player.setFlying(true);

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(LanguageManager.getLanguageMessage("In-game.Spectator.Spectator-Item-Name"));
            spectatorItem.setItemMeta(spectatorMeta);
            player.getInventory().addItem(spectatorItem);
        }

    }

    public void hidePlayersOutsideTheGame(Player player) {
        for(Player players : youtuberInvasion.getServer().getOnlinePlayers()) {
            if(getPlayers().contains(players))
                continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    @Override
    public void leaveAttempt(Player p) {
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
        for(Player players : youtuberInvasion.getServer().getOnlinePlayers()) {
            if(plugin.getGameInstanceManager().getGameInstance(players) != null)
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
        rottenflesh = rottenflesh + i;
    }

    public int getRottenFlesh() {
        return rottenflesh;
    }

    public void removeRottenFlesh(int i) {
        rottenflesh = rottenflesh - i;
        if(rottenflesh < 0) {
            rottenflesh = 0;
        }
    }

    public boolean checkLevelUpRottenFlesh() {
        if(rottenfleshlevel == 0 && rottenflesh > 50) {
            rottenfleshlevel = 1;
            return true;
        }
        if(rottenfleshlevel * 10 * getPlayers().size() + 10 < rottenflesh) {
            rottenfleshlevel++;
            return true;
        }
        return false;
    }


    @EventHandler
    public void onrespawnEvent(PlayerRespawnEvent event) {
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
                player.sendMessage(ChatManager.colorMessage("In-game.Back-In-Game"));
            }

        }
    }


}
