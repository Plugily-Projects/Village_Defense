package me.tomthedeveloper;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.tomthedeveloper.chunks.ChunkManager;
import me.tomthedeveloper.commands.InstanceCommands;
import me.tomthedeveloper.creatures.v1_12_R1.*;
import me.tomthedeveloper.events.Events;
import me.tomthedeveloper.events.SpectatorItemEvents;
import me.tomthedeveloper.events.customevents.PlayerAddCommandEvent;
import me.tomthedeveloper.events.customevents.PlayerAddSpawnCommandEvent;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.game.GameState;
import me.tomthedeveloper.handlers.*;
import me.tomthedeveloper.items.SpecialItem;
import me.tomthedeveloper.kits.*;
import me.tomthedeveloper.shop.Shop;
import me.tomthedeveloper.stats.FileStats;
import me.tomthedeveloper.stats.MySQLDatabase;
import me.tomthedeveloper.stats.VillageDefenseStats;
import me.tomthedeveloper.utils.Items;
import me.tomthedeveloper.utils.MySQLConnectionUtils;
import me.tomthedeveloper.utils.ParticleEffect;
import me.tomthedeveloper.utils.Util;
import me.tomthedeveloper.versions.InvasionInstance1_12_R1;
import me.tomthedeveloper.versions.InvasionInstance1_8_R3;
import me.tomthedeveloper.versions.InvasionInstance1_9_R1;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;


/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin implements CommandsInterface, Listener, CommandExecutor {

    public static int STARTING_TIMER_TIME = 60;
    public static float MINI_ZOMBIE_SPEED;
    public static float ZOMBIE_SPEED;
    // private MyDatabase database;
    private boolean databaseActivated = false;
    private MySQLDatabase database;
    private FileConfiguration statsConfig = null;
    private FileStats fileStats;
    private boolean chatformat = true;
    private RewardsHandler rewardsHandler;
    private GameAPI gameAPI = new GameAPI();
    private String currentVersion;
	private String latestVersion;
	private static Boolean debug;
	private int LANGUAGE_FILE_VERSION = 2;
	public final int CONFIG_VERSION = 1;

	private Map<String, Integer> customPermissions = new HashMap<>();

    private HashMap<UUID, Boolean> spyChatEnabled = new HashMap<>();
    private String version;

    public void onPreStart() {
        gameAPI.setAbreviation("vd");
    }

    public boolean is1_7_R4() {
        return getVersion().equalsIgnoreCase("v1_7_R4");
    }
    
    public boolean is1_8_R3() {
        return getVersion().equalsIgnoreCase("v1_8_R3");
    }

    public boolean is1_9_R1() {
        return getVersion().equalsIgnoreCase("v1_9_R1");
    }
    
    public boolean is1_12_R1() {
        return getVersion().equalsIgnoreCase("v1_12_R1");
    }

	public Map<String, Integer> getCustomPermissions() {
		return customPermissions;
	}
    
	public void debugChecker() {
        if(!getConfig().isSet("Debug")) {
        	getConfig().set("Debug", false);
        	saveConfig();
        }
        debug = getConfig().getBoolean("Debug");
	}
	
	public static boolean isDebugged() {
		return debug;
	}

    public String getVersion() {
        return version;
    }

    public GameAPI getGameAPI() {
        return gameAPI;
    }

    @Override
    public void onEnable() {
        LanguageManager.init(this);
        LanguageManager.saveDefaultLanguageFile();
        //LanguageMigrator.init(this);
        saveDefaultConfig();
        debugChecker();
        if(LanguageManager.getLanguageMessage("File-Version") == null || LanguageManager.getLanguageMessage("File-Version").equals("0")) {
        	LanguageMigrator.initiateMigration();
        }
        LanguageMigrator.ineffectiveFileUpdate();
        gameAPI.setGameName("VillageDefense");
        gameAPI.setAbreviation("vd");
        gameAPI.enableKits();
        gameAPI.setAllowBuilding(true);
        InvasionInstance.youtuberInvasion = this;
		Items.villageDefense = this;
        gameAPI.onSetup(this, this);
        new MetricsLite(this);
        
        currentVersion = "v" + Bukkit.getPluginManager().getPlugin("VillageDefense").getDescription().getVersion();
		if (this.getConfig().getBoolean("update-notify")){
            try {
                UpdateChecker.checkUpdate(currentVersion);
                latestVersion = UpdateChecker.getLatestVersion();
                if (latestVersion != null) {
                    latestVersion = "v" + latestVersion;
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Plugin is up to date! Your version %old%, new version %new%".replaceAll("%old%", currentVersion).replaceAll("%new%", latestVersion));
                }
            } catch (Exception ex) {
            	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] An error occured while checking for update!");
            	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
            	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
            }
        }
        
        this.getCommand(gameAPI.getGameName()).setExecutor(new InstanceCommands(gameAPI, this));
        STARTING_TIMER_TIME = this.getConfig().getInt("Starting-Waiting-Time");
        MINI_ZOMBIE_SPEED = (float) this.getConfig().getDouble("Mini-Zombie-Speed");
        ZOMBIE_SPEED = (float) this.getConfig().getDouble("Zombie-Speed");
        databaseActivated = this.getConfig().getBoolean("DatabaseActivated");
        if (databaseActivated)
            this.database = new MySQLDatabase(this);
        else {
            fileStats = new FileStats(this);
        }
        
        setupNMSEntities();

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new Events(this), this);

        this.getCommand("setprice").setExecutor(this);
        Shop.plugin = this;
        new Shop();
        this.getServer().getPluginManager().registerEvents(ChunkManager.getInstance(), this);
        new MessageHandler(this);
        new SpectatorItemEvents(gameAPI, this);

        BreakFenceListener listener = new BreakFenceListener();
        listener.runTaskTimer(this, 1L, 20L);
        
        setupGameKits();
        rewardsHandler = new RewardsHandler(this);

        SpecialItem.loadAll();
        loadInstances();
        //database = new MyDatabase();
        FileConfiguration config = ConfigurationManager.getConfig("bungee");
        if (!config.contains("ShutdownWhenGameEnds")) {
            config.set("ShutdownWhenGameEnds", false);
            try {
                ConfigurationManager.getConfig("bungee").save(ConfigurationManager.getFile("bungee"));
            } catch (IOException e) {
            	ChatManager.sendErrorHeader("bungee.yml file save");
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- create blank file named bungee.yml if it doesn't exists");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- disable bungee option in config (Bungeecord support will not work)");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
            }
        }
        if (!getConfig().contains("ChatFormat-Enabled")) {
            getConfig().set("ChatFormat-Enabled", true);
            saveConfig();
        }

        chatformat = getConfig().getBoolean("ChatFormat-Enabled");

        if(!getConfig().isSet("Config-Version") || getConfig().getInt("Config-Version") != CONFIG_VERSION) {
            if(isDebugged()){
                System.out.println("[Village Debugger] Updating config file!");
            }
            if(getConfig().getInt("Config-Version") == 1) {
                getConfig().set("CustomPermissions.mypermissionone", 10);
                getConfig().set("Config-Version", 1);
            }
            saveConfig();
        }
	    ConfigurationSection cs = getConfig().getConfigurationSection("CustomPermissions");
        for(String key : cs.getKeys(false)){
        	customPermissions.put(key, getConfig().getInt("CustomPermissions." + key));
        	if(isDebugged()){
        		System.out.println("[Village Debugger] Loaded custom permission " + key + "!");
	        }
        }
        loadStatsForPlayersOnline();
        VillageDefenseStats.plugin = this;
    }


    public RewardsHandler getRewardsHandler() {
        return rewardsHandler;
    }

    public boolean isChatFormatEnabled() {
        return chatformat;
    }

    @Override
    public boolean checkPlayerCommands(Player player, Command command, String s, String[] strings) {
        if (strings.length == 2 && strings[0].equals("join")) {
            for (GameInstance gameInstance : gameAPI.getGameInstanceManager().getGameInstances()) {
                if (strings[1].equalsIgnoreCase(gameInstance.getID())) {
                    gameInstance.joinAttempt(player);
                    return true;
                }

            }
            player.sendMessage(ChatManager.colorMessage("commands.No-Arena-Like-That"));
            return true;
        }
        if (strings.length == 1 && strings[0].equals("leave")) {
            GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(player);
            if (gameInstance != null) {
                gameInstance.leaveAttempt(player);
            }
            return true;
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("spychat") && player.hasPermission("villagedefense.spychat")) {
            if (!this.spyChatEnabled.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "SpyChat Enabled!");
                this.spyChatEnabled.put(player.getUniqueId(), true);
                return true;
            } else {
                this.spyChatEnabled.put(player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "SpyChat Disabled!");
                return true;

            }
        }
        return false;
    }

    @Override
    public boolean checkSpecialCommands(Player player, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            player.sendMessage(ChatColor.GOLD + "----------------{VillageDefense commands}----------");
            player.sendMessage("   ");
            player.sendMessage(ChatColor.GREEN + "Setup the game:");
            player.sendMessage("   ");

            player.sendMessage(ChatColor.AQUA + "/villagedefense create <ARENAID>: " + ChatColor.GRAY + "Create an arena!");
            player.sendMessage(ChatColor.AQUA + "/villagedefense <ARENAID> edit: " + ChatColor.GRAY + "Opens the menu to edit the arena!");
            player.sendMessage(ChatColor.AQUA + "/villagedefense <ARENAID> addspawn zombie: " + ChatColor.GRAY + "Adds a zombiespawn. Zombiespawns do not represent the amount of zombies that will spawn!");
            player.sendMessage(ChatColor.AQUA + "/villagedefense <ARENAID> addspawn villager: " + ChatColor.GRAY + "Adds a villagerspawn. Villagerspawns do not represent the amount of villagers that will spawn!");
            player.sendMessage(ChatColor.AQUA + "/villagedefense <ARENAID> add doors: " + ChatColor.GRAY + "Select whole the map with world edit. Then perfrom this command. The plugin will filter out all the doors!");

            player.sendMessage(ChatColor.AQUA + "/addsigns: " + ChatColor.GRAY + "Select signs with World Edit. Then perform this command. The plugin will filter out the signs.");

            player.sendMessage("   ");
            player.sendMessage(ChatColor.GREEN + "Manage the game:");
            player.sendMessage("   ");

            player.sendMessage(ChatColor.AQUA + "/villagedefense admin: " + ChatColor.GRAY + "Shows all the admin commands");
            player.sendMessage(ChatColor.AQUA + "/villagedefense reload: " + ChatColor.GRAY + "Reloads the arenas");

            player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
            return true;
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            this.loadInstances();
            player.sendMessage("Instances reloaded!");
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("spychat")) {
            if (!this.spyChatEnabled.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "SpyChat Enabled!");
                this.spyChatEnabled.put(player.getUniqueId(), true);
                return true;
            } else {
                this.spyChatEnabled.put(player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "SpyChat Disabled!");
                return true;
            }
        }
        if (strings.length == 1 && strings[0].equals("admin")) {
            player.sendMessage(ChatManager.HIGHLIGHTED + "--------{Ingame Admin commands}-----------");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense clear zombies" + ChatColor.GRAY + ": Clears the zombies in the arena");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense clear villagers" + ChatColor.GRAY + ": Clears the villagers in the arena");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense clear golems" + ChatColor.GRAY + ": Clears the golems in the arena");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense forcestart" + ChatColor.GRAY + ": ForceStarts the arena");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense respawn" + ChatColor.GRAY + ": Respawns you if u are dead");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense respawn <PLAYER>" + ChatColor.GRAY + ": Respawns the named if he is dead");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense set wave <NUMBER>" + ChatColor.GRAY + ": Sets the number from a wave");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense stop" + ChatColor.GRAY + ": Stops the arena");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense add orbs <amount>" + ChatColor.GRAY + ": Gives u the given amount of orbs");
            player.sendMessage(ChatManager.PREFIX + "/villagedefense add orbs <amount> <player>" + ChatColor.GRAY + ": Gives the named player the given amount of orbs");

            return true;
        }

        if (strings.length == 3 && strings[0].equals("add") && strings[1].equals("orbs")) {
            if (NumberUtils.isNumber(strings[2])) {
                if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                    return true;
                InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
                User user = UserManager.getUser(player.getUniqueId());
                user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(strings[2]));
                player.sendMessage(ChatManager.PLUGINPREFIX + "Orbs added to player!");
                //player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("commands.Admin-Commands.Added-Orbs"));
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Wrong usage. Do /villagedefense add orbs <amount>");
                return true;
            }
        }
        if (strings.length == 4 && strings[0].equals("add") && strings[1].equals("orbs")) {
            if (NumberUtils.isNumber(strings[2])) {
                if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                    return true;
                InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
                for (Player getplayer : invasionInstance.getPlayers()) {
                    if (getplayer.getName().equals(strings[3])) {
                        User user = UserManager.getUser(getplayer.getUniqueId());
                        user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(strings[2]));
                        player.sendMessage(ChatManager.PLUGINPREFIX + "Orbs added to player!");
                        //player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("commands.Admin-Commands.Added-Orbs"));
                        return true;
                    }
                }
                player.sendMessage(ChatManager.PLUGINPREFIX + "Player not found!");
                //player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("commands.Admin-Commands.Player-Not-Found"));
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Wrong usage. Do /villagedefense add orbs <amount> <Player");
                return true;
            }

        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("stop")) {

            if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                return true;
            InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
            invasionInstance.stopGame();

        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("clear")) {
            if (strings[1].equalsIgnoreCase("zombies")) {
                if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                    return true;
                InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
                if (invasionInstance.getZombies() != null) {
                    for (Zombie zombie : invasionInstance.getZombies()) {
                        ParticleEffect.LAVA.display(1, 1, 1, 1, 20, zombie.getLocation(), 100);
                        zombie.remove();

                    }
                    invasionInstance.getZombies().clear();
                } else {
                    player.sendMessage(ChatManager.colorMessage("kits.Cleaner.Nothing-To-Clean"));
                    return true;
                }

                if (this.is1_9_R1() || this.is1_12_R1()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
                }
                for(Player player1 : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Admin-Messages.Removed-Zombies"), new Player[] {(player1)});
                    player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                }
                return true;
            }
            if (strings[1].equalsIgnoreCase("villagers")) {
                if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                    return false;
                InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
                if (invasionInstance.getVillagers() != null) {
                    for (Villager zombie : invasionInstance.getVillagers()) {
                        ParticleEffect.LAVA.display(1, 1, 1, 1, 20, zombie.getLocation(), 100);
                        zombie.remove();

                    }
                    invasionInstance.getVillagers().clear();
                } else {
                    player.sendMessage(ChatManager.colorMessage("kits.Cleaner.Nothing-To-Clean"));
                    return true;
                }
                if (this.is1_9_R1() || this.is1_12_R1()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
                }
                for(Player player1 : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Admin-Messages.Removed-Villagers"), new Player[] {(player1)});
                    player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                }
                return true;
            }
            if (strings[1].equalsIgnoreCase("golems")) {
                if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                    return false;
                InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
                if (invasionInstance.getIronGolems() != null) {
                    for (IronGolem zombie : invasionInstance.getIronGolems()) {
                        ParticleEffect.LAVA.display(1, 1, 1, 1, 20, zombie.getLocation(), 100);
                        zombie.remove();

                    }
                    invasionInstance.getIronGolems().clear();

                } else {
                    player.sendMessage(ChatManager.colorMessage("kits.Cleaner.Nothing-To-Clean"));
                    return true;
                }
                if(this.is1_9_R1()|| this.is1_12_R1()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
                }
                for(Player player1 : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Admin-Messages.Removed-Golems"), new Player[] {(player1)});
                    player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                }
            }
        }
        if (strings.length == 3 && strings[0].equalsIgnoreCase("set") && strings[1].equalsIgnoreCase("wave")) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                return false;
            InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
            if (NumberUtils.isNumber(strings[2])) {
                invasionInstance.setWave(Integer.parseInt(strings[2]) - 1);
                invasionInstance.endWave();
                String message = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Admin-Messages.Changed-Wave"), invasionInstance.getWave());
                for(Player player1 : invasionInstance.getPlayers()) {
                    player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                }
                if (invasionInstance.getZombies() != null) {
                    for (Zombie zombie : invasionInstance.getZombies()) {
                        ParticleEffect.LAVA.display(1, 1, 1, 1, 20, zombie.getLocation(), 100);
                        zombie.setHealth(0.0);

                    }
                    invasionInstance.getZombies().clear();
                } else {
                    player.sendMessage(ChatManager.colorMessage("kits.Cleaner.Nothing-To-Clean"));
                }
                if(this.is1_9_R1() || this.is1_12_R1()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
                }
                for(Player player1 : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    String message1 = ChatManager.formatMessage(ChatManager.colorMessage("In-game.Messages.Admin-Messages.Removed-Zombies"), new Player[] {(player1)});
                    player1.sendMessage(ChatManager.PLUGINPREFIX + message1);
                }
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Wave needs to be number! Do /villagedefense set wave <number>");
                return true;
            }

        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("forcestart")) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                return false;
            InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
            if (invasionInstance.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                invasionInstance.setGameState(GameState.STARTING);
                for(Player p : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Admin-Messages.Force-Start-game"));
                }
                return true;
            }
            if (invasionInstance.getGameState() == GameState.STARTING) {
                invasionInstance.setTimer(0);
                for(Player p : gameAPI.getGameInstanceManager().getGameInstance(player).getPlayers()) {
                    p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-game.Messages.Admin-Messages.Set-Starting-In-To-0"));
                }
                return true;
            }
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("respawn")) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                return false;
            InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
            player.setGameMode(GameMode.SURVIVAL);
            User user = UserManager.getUser(player.getUniqueId());
            user.setFakeDead(false);
            user.setSpectator(false);


            invasionInstance.teleportToStartLocation(player);
            player.setFlying(false);
            player.setAllowFlight(false);
            invasionInstance.showPlayer(player);
            player.sendMessage(ChatManager.colorMessage("In-game.Back-In-game"));
            return true;
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("respawn")) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) == null)
                return false;
            InvasionInstance invasionInstance = (InvasionInstance) gameAPI.getGameInstanceManager().getGameInstance(player);
            boolean b = false;
            for (Player getplayer : invasionInstance.getPlayers()) {
                if (strings[1].equalsIgnoreCase(getplayer.getName())) {
                    getplayer.setGameMode(GameMode.SURVIVAL);
                    User user = UserManager.getUser(getplayer.getUniqueId());
                    user.setFakeDead(false);
                    user.setSpectator(false);

                    player.sendMessage(ChatColor.GREEN + "Player respawned!");
                    invasionInstance.teleportToStartLocation(getplayer);
                    getplayer.setFlying(false);
                    getplayer.setAllowFlight(false);
                    invasionInstance.showPlayer(getplayer);
                    getplayer.sendMessage(ChatManager.colorMessage("In-game.Back-In-game"));
                    return true;
                }
            }
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        return false;
    }


    public boolean isSpyChatEnabled(Player player) {
        if (!spyChatEnabled.containsKey(player.getUniqueId()))
            return false;
        else if (spyChatEnabled.get(player.getUniqueId()) == null)
            return false;
        return spyChatEnabled.get(player.getUniqueId());
    }

    public FileStats getFileStats() {
        return fileStats;
    }

    public MySQLDatabase getMySQLDatabase() {
        return database;
    }

    public void getMySQLDatabase(MySQLDatabase database) {
        this.database = database;
    }

    @Override
    public void onDisable() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            User user = UserManager.getUser(player.getUniqueId());
            List<String> temp = new ArrayList<>();
            temp.add("gamesplayed");
            temp.add("kills");
            temp.add("deaths");
            temp.add("highestwave");
            temp.add("xp");
            temp.add("level");
            temp.add("orbs");
            for (String s : temp) {
                if (this.isDatabaseActivated()) {
                    this.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                } else {
                    this.getFileStats().saveStat(player, s);
                }
            }


            UserManager.removeUser(player.getUniqueId());

        }
        for (GameInstance invasionInstance : gameAPI.getGameInstanceManager().getGameInstances()) {
            for (Player player : invasionInstance.getPlayers()) {
                invasionInstance.teleportToEndLocation(player);
                if (gameAPI.isInventoryManagerEnabled())
                    gameAPI.getInventoryManager().loadInventory(player);


            }
            if (invasionInstance instanceof InvasionInstance)
                ((InvasionInstance) invasionInstance).clearVillagers();
            ((InvasionInstance) invasionInstance).stopGame();
            ((InvasionInstance) invasionInstance).clearVillagers();

            invasionInstance.teleportAllToEndLocation();


        }

    }
    
    private void setupNMSEntities() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if (this.getVersion().equalsIgnoreCase("v1_7_R4")) {
            gameAPI.registerEntity1_7_10("Zombie", 54, me.tomthedeveloper.creatures.v1_7_R4.FastZombie.class);
            gameAPI.registerEntity1_7_10("Zombie", 54, me.tomthedeveloper.creatures.v1_7_R4.BabyZombie.class);
            gameAPI.registerEntity1_7_10("Zombie", 54, me.tomthedeveloper.creatures.v1_7_R4.PlayerBuster.class);
            gameAPI.registerEntity1_7_10("Zombie", 54, me.tomthedeveloper.creatures.v1_7_R4.GolemBuster.class);
            gameAPI.registerEntity1_7_10("Zombie", 54, me.tomthedeveloper.creatures.v1_7_R4.HardZombie.class);
            gameAPI.registerEntity1_7_10("Villager", 120, me.tomthedeveloper.creatures.v1_7_R4.RidableVillager.class);
            gameAPI.registerEntity1_7_10("VillagerGolem", 99, me.tomthedeveloper.creatures.v1_7_R4.RidableIronGolem.class);
            gameAPI.registerEntity1_7_10("Wolf", 95, me.tomthedeveloper.creatures.v1_7_R4.WorkingWolf.class);
        }
        if (this.getVersion().equalsIgnoreCase("v1_8_R3")) {
            gameAPI.registerEntity("Zombie", 54, me.tomthedeveloper.creatures.v1_8_R3.FastZombie.class);
            gameAPI.registerEntity("Zombie", 54, me.tomthedeveloper.creatures.v1_8_R3.BabyZombie.class);
            gameAPI.registerEntity("Zombie", 54, me.tomthedeveloper.creatures.v1_8_R3.PlayerBuster.class);
            gameAPI.registerEntity("Zombie", 54, me.tomthedeveloper.creatures.v1_8_R3.GolemBuster.class);
            gameAPI.registerEntity("Zombie", 54, me.tomthedeveloper.creatures.v1_8_R3.HardZombie.class);
            gameAPI.registerEntity("Villager", 120, me.tomthedeveloper.creatures.v1_8_R3.RidableVillager.class);
            gameAPI.registerEntity("VillagerGolem", 99, me.tomthedeveloper.creatures.v1_8_R3.RidableIronGolem.class);
            gameAPI.registerEntity("Zombie", 54,  me.tomthedeveloper.creatures.v1_8_R3.TankerZombie.class);
            gameAPI.registerEntity("Wolf", 95, me.tomthedeveloper.creatures.v1_8_R3.WorkingWolf.class);
        }
        if (this.getVersion().equalsIgnoreCase("v1_9_R1")) {
            gameAPI.register1_9_R1_Entity("Zombie", 54, me.tomthedeveloper.creatures.v1_9_R1.FastZombie.class);
            gameAPI.register1_9_R1_Entity("Zombie", 54, me.tomthedeveloper.creatures.v1_9_R1.BabyZombie.class);
            gameAPI.register1_9_R1_Entity("Zombie", 54, me.tomthedeveloper.creatures.v1_9_R1.GolemBuster.class);
            gameAPI.register1_9_R1_Entity("Zombie", 54, me.tomthedeveloper.creatures.v1_9_R1.HardZombie.class);
            gameAPI.register1_9_R1_Entity("Zombie", 54, me.tomthedeveloper.creatures.v1_9_R1.PlayerBuster.class);
            gameAPI.register1_9_R1_Entity("Wolf", 95, me.tomthedeveloper.creatures.v1_9_R1.WorkingWolf.class);
            gameAPI.register1_9_R1_Entity("VillagerGolem", 99, me.tomthedeveloper.creatures.v1_9_R1.RidableIronGolem.class);
            gameAPI.register1_9_R1_Entity("Villager", 120, me.tomthedeveloper.creatures.v1_9_R1.RidableVillager.class);
        }
        if (this.getVersion().equalsIgnoreCase("v1_12_R1")) {
            NMSUtils.registerEntity(this, "FastZombie", NMSUtils.Type.ZOMBIE, FastZombie.class, false);
            NMSUtils.registerEntity(this, "GolemBuster", NMSUtils.Type.ZOMBIE, GolemBuster.class, false);
            NMSUtils.registerEntity(this, "HardZombie", NMSUtils.Type.ZOMBIE, HardZombie.class, false);
            NMSUtils.registerEntity(this, "PlayerBuster", NMSUtils.Type.ZOMBIE, PlayerBuster.class, false);
            NMSUtils.registerEntity(this, "BabyZombie", NMSUtils.Type.ZOMBIE, BabyZombie.class, false);
            NMSUtils.registerEntity(this, "IronGolem", NMSUtils.Type.IRON_GOLEM, RidableIronGolem.class, false);
            NMSUtils.registerEntity(this, "WorkingWolf", NMSUtils.Type.WOLF, WorkingWolf.class, false);
            NMSUtils.registerEntity(this, "RidableVillager", NMSUtils.Type.VILLAGER, RidableVillager.class, false);

            /*gameAPI.register1_12_R1_Entity("Zombie",54, me.tomthedeveloper.creatures.v1_12_R1.BabyZombie.class);
            gameAPI.register1_12_R1_Entity("Zombie",54, me.tomthedeveloper.creatures.v1_12_R1.GolemBuster.class);
            gameAPI.register1_12_R1_Entity("Zombie",54, me.tomthedeveloper.creatures.v1_12_R1.HardZombie.class);
            gameAPI.register1_12_R1_Entity("Zombie",54, me.tomthedeveloper.creatures.v1_12_R1.PlayerBuster.class);
            gameAPI.register1_12_R1_Entity("Wolf",95, me.tomthedeveloper.creatures.v1_12_R1.WorkingWolf.class);
            gameAPI.register1_12_R1_Entity("VillagerGolem",99, me.tomthedeveloper.creatures.v1_12_R1.RidableIronGolem.class);
            gameAPI.register1_12_R1_Entity("Villager",120, me.tomthedeveloper.creatures.v1_12_R1.RidableVillager.class); */

        }
    }

    private void setupGameKits() {
        KnightKit knightkit = new KnightKit();
        gameAPI.getKitHandler().registerKit(knightkit);
        
        LightTankKit lightTankKit = new LightTankKit();
        gameAPI.getKitHandler().registerKit(lightTankKit);
        
        ZombieFinderKit zombieFinderKitKit = new ZombieFinderKit(this);
        gameAPI.getKitHandler().registerKit(zombieFinderKitKit);
        this.getServer().getPluginManager().registerEvents(zombieFinderKitKit, this);
        
        ArcherKit archerKit = new ArcherKit();
        gameAPI.getKitHandler().registerKit(archerKit);
        
        PuncherKit puncherKit = new PuncherKit();
        gameAPI.getKitHandler().registerKit(puncherKit);
        
        HealerKit healerkit = new HealerKit();
        gameAPI.getKitHandler().registerKit(healerkit);
        
        LooterKit looterKit = new LooterKit(this);
        gameAPI.getKitHandler().registerKit(looterKit);
        this.getServer().getPluginManager().registerEvents(looterKit, this);
        this.getServer().getPluginManager().registerEvents(new SuperArcherKit(), this);
        
        RunnerKit runnerKit = new RunnerKit();
        gameAPI.getKitHandler().registerKit(runnerKit);
        
        MediumTankKit mediumTankKit = new MediumTankKit();
        gameAPI.getKitHandler().registerKit(mediumTankKit);
        
        WorkerKit doorRepairKit = new WorkerKit();
        gameAPI.getKitHandler().registerKit(doorRepairKit);
        
        GolemFriendKit golemFriendKitKit = new GolemFriendKit(this);
        gameAPI.getKitHandler().registerKit(golemFriendKitKit);
        
        TerminatorKit strenghtKit = new TerminatorKit();
        gameAPI.getKitHandler().registerKit(strenghtKit);
        
        HardcoreKit hardcoreKit = new HardcoreKit();
        gameAPI.getKitHandler().registerKit(hardcoreKit);
        
        SuperArcherKit superArcherKit = new SuperArcherKit();
        gameAPI.getKitHandler().registerKit(superArcherKit);
        
        CleanerKit cleanerKit = new CleanerKit(this);
        gameAPI.getKitHandler().registerKit(cleanerKit);
        this.getServer().getPluginManager().registerEvents(cleanerKit, this);
        
        TeleporterKit teleporterKit = new TeleporterKit(this);
        gameAPI.getKitHandler().registerKit(teleporterKit);
        this.getServer().getPluginManager().registerEvents(teleporterKit, this);
        
        // JumperKit jumperkit = new JumperKit();
        //gameAPI.getKitHandler().registerKit(jumperkit);
        HeavyTankKit heavyTankKit = new HeavyTankKit();
        gameAPI.getKitHandler().registerKit(heavyTankKit);
        
        ShotBowKit shotBowKit = new ShotBowKit();
        gameAPI.getKitHandler().registerKit(shotBowKit);
        this.getServer().getPluginManager().registerEvents(shotBowKit, this);
        
        DogFriendKit dogFriendKit = new DogFriendKit(this);
        gameAPI.getKitHandler().registerKit(dogFriendKit);
        
        PremiumHardcoreKit premiumHardcoreKit = new PremiumHardcoreKit();
        gameAPI.getKitHandler().registerKit(premiumHardcoreKit);
        
        TornadoKit tornadoKit = new TornadoKit(this);
        gameAPI.getKitHandler().registerKit(tornadoKit);
        this.getServer().getPluginManager().registerEvents(tornadoKit, this);
        
        BlockerKit blockerKit = new BlockerKit(this);
        gameAPI.getKitHandler().registerKit(blockerKit);
        this.getServer().getPluginManager().registerEvents(blockerKit, this);
        
        MedicKit medicKit = new MedicKit(this);
        gameAPI.getKitHandler().registerKit(medicKit);
       /* NakedKit nakedKit = new NakedKit();
        getKitHandler().registerKit(nakedKit);
        this.getServer().getPluginManager().registerEvents(nakedKit, this); */

        gameAPI.getKitHandler().setDefaultKit(knightkit);
        gameAPI.getKitMenuHandler().setMaterial(Material.NETHER_STAR);
        gameAPI.getKitMenuHandler().setItemName(ChatManager.colorMessage("kits.Kit-Menu-Item-Name"));
        gameAPI.getKitMenuHandler().setMenuName(ChatManager.colorMessage("kits.Kit-Menu.Title"));
        gameAPI.getKitMenuHandler().setDescription(new String[]{ChatManager.colorMessage("kits.Open-Kit-Menu")});
    }
    
    public void loadInstances() {
        if (gameAPI.getGameInstanceManager().getGameInstances() != null) {
            if (gameAPI.getGameInstanceManager().getGameInstances().size() > 0) {
                for (GameInstance gameInstance : gameAPI.getGameInstanceManager().getGameInstances()) {
                    InvasionInstance invasionInstance = (InvasionInstance) gameInstance;
                    invasionInstance.clearZombies();
                    invasionInstance.clearVillagers();
                    invasionInstance.clearWolfs();
                    invasionInstance.clearGolems();
                    gameAPI.getSignManager().removeSign(gameInstance);
                }
            }
        }
        gameAPI.getGameInstanceManager().getGameInstances().clear();
        if (!this.getConfig().contains("instances")) {
        	if(isDebugged()) {
        		System.out.print(ChatColor.RED + "[Village Debugger] There are no instances in config.yml!");
        	}
            return;
        }

        for (String ID : this.getConfig().getConfigurationSection("instances").getKeys(false)) {
            InvasionInstance invasionInstance;
            String s = "instances." + ID + ".";
            if (s.contains("default"))
                continue;

            if (this.is1_8_R3()) {
                invasionInstance = new InvasionInstance1_8_R3(ID);
            } else if (this.is1_9_R1()) {
                invasionInstance = new InvasionInstance1_9_R1(ID);
            } else {
                invasionInstance = new InvasionInstance1_12_R1(ID);
            }


            if (getConfig().contains(s + "minimumplayers"))
                invasionInstance.setMIN_PLAYERS(getConfig().getInt(s + "minimumplayers"));
            else
                invasionInstance.setMIN_PLAYERS(getConfig().getInt("instances.default.minimumplayers"));
            if (getConfig().contains(s + "maximumplayers"))
                invasionInstance.setMAX_PLAYERS(getConfig().getInt(s + "maximumplayers"));
            else
                invasionInstance.setMAX_PLAYERS(getConfig().getInt("instances.default.maximumplayers"));
            if (getConfig().contains(s + "mapname"))
                invasionInstance.setMapName(getConfig().getString(s + "mapname"));
            else
                invasionInstance.setMapName(getConfig().getString("instances.default.mapname"));
            if (getConfig().contains(s + "lobbylocation"))
                invasionInstance.setLobbyLocation(gameAPI.getLocation(s + "lobbylocation"));
            if (getConfig().contains(s + "Startlocation"))
                invasionInstance.setStartLocation(gameAPI.getLocation(s + "Startlocation"));
            if (getConfig().contains(s + "Endlocation"))
                invasionInstance.setEndLocation(gameAPI.getLocation(s + "Endlocation"));
            if (this.getConfig().contains(s + "zombiespawns")) {
                for (String string : this.getConfig().getConfigurationSection(s + "zombiespawns").getKeys(false)) {
                    String path = s + "zombiespawns." + string;
                    invasionInstance.addZombieSpawn(gameAPI.getLocation(path));
                }
            } else {
            	if(isDebugged()) {
            		System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + " DOESN'T HAS ZOMBIESPAWN(S)!");
            	}
                gameAPI.getGameInstanceManager().registerGameInstance(invasionInstance);

                continue;
            }

            if (this.getConfig().contains(s + "villagerspawns")) {
                for (String string : this.getConfig().getConfigurationSection(s + "villagerspawns").getKeys(false)) {
                    String path = s + "villagerspawns." + string;
                    invasionInstance.addVillagerSpawn(gameAPI.getLocation(path));
                }
            } else {
            	if(isDebugged()) {
            		System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + " DOESN'T HAS VILLAGERSPAWN(S)!");
            	}
                gameAPI.getGameInstanceManager().registerGameInstance(invasionInstance);

                continue;
            }
            if (this.getConfig().contains(s + "doors")) {
                for (String string : this.getConfig().getConfigurationSection(s + "doors").getKeys(false)) {
                    String path = s + "doors." + string + ".";
                    Location location = gameAPI.getLocation(path + "location");

                    invasionInstance.addDoor(gameAPI.getLocation(path + "location"), (byte) this.getConfig().getInt(path + "byte"));

                }
            } else {
            	if(isDebugged()) {
            		System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + "DOESN'T HAS DOORS?");
            	}
                gameAPI.getGameInstanceManager().registerGameInstance(invasionInstance);
                continue;
            }

            gameAPI.getGameInstanceManager().registerGameInstance(invasionInstance);


            invasionInstance.start();
            this.getServer().getPluginManager().registerEvents(invasionInstance, this);
            if(isDebugged()) {
            	System.out.print(ChatColor.RED + "[Village Debugger] INSTANCE " + ID + " STARTED!");
            }
        }

    }

    public boolean isDatabaseActivated() {
        return databaseActivated;
    }

    @EventHandler
    public void onAddSpawn(PlayerAddSpawnCommandEvent event) {
        if (event.getSpawnName().equalsIgnoreCase("zombie")) {
            int i;
            if (!this.getConfig().contains("instances." + event.getArenaID() + ".zombiespawns")) {
                i = 0;
            } else {
                i = this.getConfig().getConfigurationSection("instances." + event.getArenaID() + ".zombiespawns").getKeys(false).size();
            }
            i++;
            gameAPI.saveLoc("instances." + event.getArenaID() + ".zombiespawns." + i, event.getPlayer().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Zombie spawn added!");
            return;
        }
        if (event.getSpawnName().equalsIgnoreCase("villager")) {
            int i;
            if (!this.getConfig().contains("instances." + event.getArenaID() + ".villagerspawns")) {
                i = 0;
            } else {
                i = this.getConfig().getConfigurationSection("instances." + event.getArenaID() + ".villagerspawns").getKeys(false).size();
            }

            i++;
            gameAPI.saveLoc("instances." + event.getArenaID() + ".villagerspawns." + i, event.getPlayer().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Villager spawn added!");
        }
    }

    @EventHandler
    public void onAddCommand(PlayerAddCommandEvent event) {
        String ID = event.getArenaID();
        int counter = 0;
        int i = 0;
        if (!event.getArguments()[2].equalsIgnoreCase("doors"))
            return;

        if (gameAPI.getWorldEditPlugin().getSelection(event.getPlayer()) == null)
            return;
        if (!this.getConfig().contains("instances." + ID + ".doors")) {
            i = 0;
        } else {
            i = this.getConfig().getConfigurationSection("instances." + ID + ".doors").getKeys(false).size();
        }
        i++;
        Selection selection = gameAPI.getWorldEditPlugin().getSelection(event.getPlayer());
        if (selection instanceof CuboidSelection) {
            CuboidSelection cuboidSelection = (CuboidSelection) selection;
            Vector min = cuboidSelection.getNativeMinimumPoint();
            Vector max = cuboidSelection.getNativeMaximumPoint();
            for (int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                        Location tmpblock = new Location(event.getPlayer().getWorld(), x, y, z);
                        if (tmpblock.getBlock().getType() == Material.WOODEN_DOOR) {
                            gameAPI.saveLoc("instances." + ID + ".doors." + i + ".location", tmpblock);
                            this.getConfig().set("instances." + ID + ".doors." + i + ".byte", tmpblock.getBlock().getData());
                            counter++;
                            i++;
                        }

                    }
                }
            }

        } else {
            if (selection.getMaximumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                gameAPI.saveLoc("instances." + ID + ".doors" + i + ".location", selection.getMaximumPoint());
                this.getConfig().set("instances." + ID + ".doors." + i + ".byte", selection.getMaximumPoint().getBlock().getData());
                counter++;
                i++;
            }
            if (selection.getMinimumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                gameAPI.saveLoc("instances." + ID + ".doors" + i + ".location", selection.getMinimumPoint());
                this.getConfig().set("instances." + ID + ".doors." + i + ".byte", selection.getMinimumPoint().getBlock().getData());
                counter++;
                i++;
            }
        }
        this.saveConfig();
        event.getPlayer().sendMessage(ChatColor.GREEN + "" + Math.ceil(counter / 2) + " doors added!");
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!event.getEntity().getWorld().getName().contains("VD"))
            return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            event.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setprice")) {
            if (sender.isOp() && args.length == 1) {
                Player p = (Player) sender;
                ItemStack item = p.getItemInHand();
                //check any price from lore
	            if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
		            for(int i = 0; i < item.getItemMeta().getLore().size(); i++){
			            if(item.getItemMeta().getLore().get(i).contains(ChatManager.colorMessage("In-game.Messages.Shop-Messages.Currency-In-Shop"))){
				            item.getItemMeta().getLore().set(i,  ChatColor.GOLD + args[0] + " " + ChatManager.colorMessage("In-game.Messages.Shop-Messages.Currency-In-Shop"));
				            p.getItemInHand().setItemMeta(item.getItemMeta());
				            p.sendMessage(ChatColor.GREEN + "Command succesfully executed, item updated!");
				            p.updateInventory();
				            return true;
			            }
		            }
		            Util.addLore(item, ChatColor.GOLD + args[0] + " " + ChatManager.colorMessage("In-game.Messages.Shop-Messages.Currency-In-Shop"));
		            p.sendMessage(ChatColor.GREEN + "Command succesfully executed, added lore!");
		            p.updateInventory();
		            return true;
	            } else {
		            Util.addLore(item, ChatColor.GOLD + args[0] + " " + ChatManager.colorMessage("In-game.Messages.Shop-Messages.Currency-In-Shop"));
		            p.sendMessage(ChatColor.GREEN + "Command succesfully executed!");
		            p.updateInventory();
		            return true;
	            }
            }
        }
        return true;
    }

    public void loadStatsForPlayersOnline() {
        for (final Player player : getServer().getOnlinePlayers()) {
            if (gameAPI.isBungeeActivated())
                gameAPI.getGameInstanceManager().getGameInstances().get(0).teleportToLobby(player);
            if (!this.isDatabaseActivated()) {
                List<String> temp = new ArrayList<>();
                temp.add("gamesplayed");
                temp.add("kills");
                temp.add("deaths");
                temp.add("highestwave");
                temp.add("xp");
                temp.add("level");
                temp.add("orbs");
                for (String s : temp) {
                    this.getFileStats().loadStat(player, s);
                }
                return;
            }
            User user = UserManager.getUser(player.getUniqueId());


            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {


                final String playername = player.getUniqueId().toString();

                @Override
                public void run() {
                    MySQLConnectionUtils.loadPlayerStats(player, Main.this);
                }
            });
        }
    }

}
