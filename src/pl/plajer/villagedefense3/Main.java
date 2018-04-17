package pl.plajer.villagedefense3;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaEvents;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.commands.MainCommand;
import pl.plajer.villagedefense3.creatures.BreakFenceListener;
import pl.plajer.villagedefense3.creatures.EntityRegistry;
import pl.plajer.villagedefense3.database.FileStats;
import pl.plajer.villagedefense3.database.MySQLDatabase;
import pl.plajer.villagedefense3.events.ChatEvents;
import pl.plajer.villagedefense3.events.CombustDayLightEvent;
import pl.plajer.villagedefense3.events.Events;
import pl.plajer.villagedefense3.events.GolemEvents;
import pl.plajer.villagedefense3.events.JoinEvent;
import pl.plajer.villagedefense3.events.LobbyEvents;
import pl.plajer.villagedefense3.events.QuitEvent;
import pl.plajer.villagedefense3.events.SetupInventoryEvents;
import pl.plajer.villagedefense3.events.SpectatorEvents;
import pl.plajer.villagedefense3.events.SpectatorItemEvents;
import pl.plajer.villagedefense3.handlers.BungeeManager;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ChunkManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.InventoryManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.PowerupManager;
import pl.plajer.villagedefense3.handlers.RewardsHandler;
import pl.plajer.villagedefense3.handlers.ShopManager;
import pl.plajer.villagedefense3.handlers.SignManager;
import pl.plajer.villagedefense3.items.SpecialItem;
import pl.plajer.villagedefense3.kits.kitapi.KitManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.language.LanguageManager;
import pl.plajer.villagedefense3.language.LanguageMigrator;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.utils.MetricsLite;
import pl.plajer.villagedefense3.utils.MySQLConnectionUtils;
import pl.plajer.villagedefense3.utils.UpdateChecker;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin implements Listener {

    public static int STARTING_TIMER_TIME = 60;
    public static float MINI_ZOMBIE_SPEED;
    public static float ZOMBIE_SPEED;
    private static boolean debug;
    private boolean forceDisable = false;
    private boolean databaseActivated = false;
    private MySQLDatabase database;
    private FileStats fileStats;
    private SignManager signManager;
    private InventoryManager inventoryManager;
    private ArenaRegistry arenaRegistry;
    private BungeeManager bungeeManager;
    private KitManager kitManager;
    private ChunkManager chunkManager;
    private PowerupManager powerupManager;
    private boolean bungeeEnabled;
    private boolean chatFormat = true;
    private boolean bossbarEnabled;
    private RewardsHandler rewardsHandler;
    private boolean inventoryManagerEnabled = false;
    private List<String> fileNames = Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "kits");
    private Map<String, Integer> customPermissions = new HashMap<>();
    private HashMap<UUID, Boolean> spyChatEnabled = new HashMap<>();
    private String version;

    public static void debug(String thing, long millis) {
        long elapsed = System.currentTimeMillis() - millis;
        ChatColor color;
        if(elapsed < 5) {
            color = ChatColor.GRAY;
        } else if(elapsed > 5 && elapsed < 10) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }
        if(debug) {
            Bukkit.getConsoleSender().sendMessage(color + "[Village Debugger] Task '" + thing + "' took " + elapsed + "ms");
        }
        if(elapsed > 15){
            Bukkit.getConsoleSender().sendMessage(color + "[Village Defense] Slow system response for task '" + thing + "' took over " + elapsed + "ms");
        }
    }

    public boolean is1_8_R3() {
        return getVersion().equalsIgnoreCase("v1_8_R3");
    }

    public boolean is1_9_R1() {
        return getVersion().equalsIgnoreCase("v1_9_R1");
    }

    public boolean is1_11_R1() {
        return getVersion().equalsIgnoreCase("v1_11_R1");
    }

    public boolean is1_12_R1() {
        return getVersion().equalsIgnoreCase("v1_12_R1");
    }

    public boolean isInventoryManagerEnabled() {
        return inventoryManagerEnabled;
    }

    public boolean isBossbarEnabled() {
        return bossbarEnabled;
    }

    public boolean isBungeeActivated() {
        return bungeeEnabled;
    }

    public BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public Map<String, Integer> getCustomPermissions() {
        return customPermissions;
    }

    public ArenaRegistry getArenaRegistry() {
        return arenaRegistry;
    }

    private void debugChecker() {
        if(!getConfig().isSet("Debug")) {
            getConfig().set("Debug", false);
            saveConfig();
        }
        debug = getConfig().getBoolean("Debug");
    }

    public String getVersion() {
        return version;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    @Override
    public void onEnable() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        new ConfigurationManager(this);
        LanguageManager.init(this);
        if(!(getVersion().equalsIgnoreCase("v1_8_R3") || getVersion().equalsIgnoreCase("v1_9_R1") || getVersion().equalsIgnoreCase("v1_11_R1") || getVersion().equalsIgnoreCase("v1_12_R1"))) {
            MessageUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by Village Defense!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider changing your server version?");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch(Exception e) {
            MessageUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Village Defense!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //check if using releases before 2.1.0
        if(LanguageManager.getLanguageFile().isSet("STATS-AboveLine") && LanguageManager.getLanguageFile().isSet("SCOREBOARD-Zombies")) {
            LanguageMigrator.migrateToNewFormat();
        }
        //check if using releases 2.1.0+
        if(LanguageManager.getLanguageFile().isSet("File-Version") && getConfig().isSet("Config-Version")) {
            LanguageMigrator.migrateToNewFormat();
        }
        LanguageManager.saveDefaultLanguageFile();
        saveDefaultConfig();
        debug = getConfig().getBoolean("Debug");
        debug("Setup started", System.currentTimeMillis());
        setupFiles();
        debugChecker();
        LanguageMigrator.languageFileUpdate();
        initializeClasses();

        String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("VillageDefense").getDescription().getVersion();
        if(getConfig().getBoolean("Update-Notifier.Enabled")) {
            try {
                UpdateChecker.checkUpdate(currentVersion);
                String latestVersion = UpdateChecker.getLatestVersion();
                if(latestVersion != null) {
                    latestVersion = "v" + latestVersion;
                    if(latestVersion.contains("b")) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Current version %old%, latest version %new%".replaceAll("%old%", currentVersion).replaceAll("%new%", latestVersion));
                    } else {
                        MessageUtils.updateIsHere();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Village Defense plugin is up to date! Download it to keep with latest changes and fixes.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                    }
                }
            } catch(Exception ex) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] An error occured while checking for update!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
            }
        }

        STARTING_TIMER_TIME = getConfig().getInt("Starting-Waiting-Time");
        MINI_ZOMBIE_SPEED = (float) getConfig().getDouble("Mini-Zombie-Speed");
        ZOMBIE_SPEED = (float) getConfig().getDouble("Zombie-Speed");
        databaseActivated = getConfig().getBoolean("DatabaseActivated");
        inventoryManagerEnabled = getConfig().getBoolean("InventoryManager");
        if(databaseActivated) {
            database = new MySQLDatabase(this);
        } else {
            fileStats = new FileStats(this);
        }
        bossbarEnabled = getConfig().getBoolean("Bossbar-Enabled");
        if(is1_8_R3()) {
            bossbarEnabled = false;
        }

        getServer().getPluginManager().registerEvents(this, this);

        BreakFenceListener listener = new BreakFenceListener();
        listener.runTaskTimer(this, 1L, 20L);

        KitRegistry.init();

        SpecialItem.loadAll();
        ArenaRegistry.registerArenas();
        new ShopManager();
        //we must start it after instances load!
        signManager = new SignManager(this);

        chatFormat = getConfig().getBoolean("ChatFormat-Enabled");

        ConfigurationSection cs = getConfig().getConfigurationSection("CustomPermissions");
        for(String key : cs.getKeys(false)) {
            customPermissions.put(key, getConfig().getInt("CustomPermissions." + key));
            debug("Loaded custom permission " + key, System.currentTimeMillis());
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            UserManager.registerUser(p.getUniqueId());
        }
        if(databaseActivated) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                MySQLConnectionUtils.loadPlayerStats(p, this);
            }
        } else {
            fileStats.loadStatsForPlayersOnline();
        }
        StatsStorage.plugin = this;
        PermissionsManager.init();
        debug("Setup done", System.currentTimeMillis());
    }

    private void initializeClasses() {
        arenaRegistry = new ArenaRegistry();
        bungeeEnabled = getConfig().getBoolean("BungeeActivated");
        if(getConfig().getBoolean("BungeeActivated")) bungeeManager = new BungeeManager(this);
        new ChatManager(ChatManager.colorMessage("In-Game.Plugin-Prefix"));
        new Util(this);
        new MainCommand(this, true);
        new GolemEvents(this);
        new EntityRegistry(this);
        new ArenaEvents(this);
        inventoryManager = new InventoryManager(this);
        kitManager = new KitManager(this);
        new SpectatorEvents(this);
        new QuitEvent(this);
        new SetupInventoryEvents(this);
        new JoinEvent(this);
        new ChatEvents(this);
        new MetricsLite(this);
        new Events(this);
        new CombustDayLightEvent(this);
        new LobbyEvents(this);
        new SpectatorItemEvents(this);
        powerupManager = new PowerupManager(this);
        chunkManager = new ChunkManager(this);
        rewardsHandler = new RewardsHandler(this);
    }

    private void setupFiles() {
        for(String fileName : fileNames) {
            File file = new File(getDataFolder() + File.separator + fileName + ".yml");
            if(!file.exists()) {
                saveResource(fileName + ".yml", false);
            }
        }
    }

    public RewardsHandler getRewardsHandler() {
        return rewardsHandler;
    }

    public boolean isChatFormatEnabled() {
        return chatFormat;
    }

    public boolean isSpyChatEnabled(Player player) {
        return spyChatEnabled.containsKey(player.getUniqueId());
    }

    public HashMap<UUID, Boolean> getSpyChatEnabled() {
        return spyChatEnabled;
    }

    public FileStats getFileStats() {
        return fileStats;
    }

    public boolean isDatabaseActivated() {
        return databaseActivated;
    }

    public MySQLDatabase getMySQLDatabase() {
        return database;
    }

    public PowerupManager getPowerupManager() {
        return powerupManager;
    }

    @Override
    public void onDisable() {
        if(forceDisable) return;
        for(Player player : getServer().getOnlinePlayers()) {
            User user = UserManager.getUser(player.getUniqueId());
            for(String s : FileStats.STATISTICS) {
                if(isDatabaseActivated()) {
                    getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                } else {
                    getFileStats().saveStat(player, s);
                }
            }
            UserManager.removeUser(player.getUniqueId());
        }
        for(Arena invasionInstance : ArenaRegistry.getArenas()) {
            for(Player player : invasionInstance.getPlayers()) {
                invasionInstance.teleportToEndLocation(player);
                if(inventoryManagerEnabled) {
                    inventoryManager.loadInventory(player);
                } else {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    for(PotionEffect pe : player.getActivePotionEffects()) {
                        player.removePotionEffect(pe.getType());
                    }
                }
            }
            invasionInstance.clearVillagers();
            invasionInstance.stopGame(true);
            invasionInstance.teleportAllToEndLocation();
        }
        if(isDatabaseActivated()) getMySQLDatabase().closeDatabase();
    }

    public WorldEditPlugin getWorldEditPlugin() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        return null;
    }

}
