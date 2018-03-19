package pl.plajer.villagedefense3;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.arena.*;
import pl.plajer.villagedefense3.commands.MainCommand;
import pl.plajer.villagedefense3.creatures.BreakFenceListener;
import pl.plajer.villagedefense3.creatures.EntityRegistry;
import pl.plajer.villagedefense3.database.FileStats;
import pl.plajer.villagedefense3.database.MySQLDatabase;
import pl.plajer.villagedefense3.events.*;
import pl.plajer.villagedefense3.handlers.*;
import pl.plajer.villagedefense3.items.SpecialItem;
import pl.plajer.villagedefense3.kits.*;
import pl.plajer.villagedefense3.kits.kitapi.KitManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.utils.BigTextUtils;
import pl.plajer.villagedefense3.utils.MetricsLite;
import pl.plajer.villagedefense3.utils.MySQLConnectionUtils;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin implements Listener {

    public static int STARTING_TIMER_TIME = 60;
    public static float MINI_ZOMBIE_SPEED;
    public static float ZOMBIE_SPEED;
    private static boolean debug;
    private VDLocale pluginLocale;
    private boolean databaseActivated = false;
    private MySQLDatabase database;
    private FileStats fileStats;
    private SignManager signManager;
    private InventoryManager inventoryManager;
    private ArenaRegistry arenaRegistry;
    private BungeeManager bungeeManager;
    private KitRegistry kitRegistry;
    private KitManager kitManager;
    private ChunkManager chunkManager;
    private boolean bungeeEnabled;
    private boolean chatFormat = true;
    private boolean bossbarEnabled;
    private RewardsHandler rewardsHandler;
    private boolean inventoryManagerEnabled = false;
    private List<String> fileNames = Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "kits");
    private List<String> migratable = Arrays.asList("bungee", "config", "kits", "language", "lobbyitems", "mysql");
    private List<Class> classKitNames = Arrays.asList(LightTankKit.class, ZombieFinderKit.class, ArcherKit.class, PuncherKit.class,
            HealerKit.class, LooterKit.class, RunnerKit.class, MediumTankKit.class, WorkerKit.class, GolemFriendKit.class,
            TerminatorKit.class, HardcoreKit.class, CleanerKit.class, TeleporterKit.class,
            HeavyTankKit.class, ShotBowKit.class, DogFriendKit.class, PremiumHardcoreKit.class, TornadoKit.class,
            BlockerKit.class, MedicKit.class, NakedKit.class, WizardKit.class);
    private Map<String, Integer> customPermissions = new HashMap<>();
    private HashMap<UUID, Boolean> spyChatEnabled = new HashMap<>();
    private String version;

    public static boolean isDebugged() {
        return debug;
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

    public VDLocale getPluginLocale() {
        return pluginLocale;
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

    public KitRegistry getKitRegistry() {
        return kitRegistry;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    @Override
    public void onEnable() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        new ConfigurationManager(this);
        LanguageManager.init(this);
        if(!(getVersion().equalsIgnoreCase("v1_8_R3") || getVersion().equalsIgnoreCase("v1_9_R1") ||
                getVersion().equalsIgnoreCase("v1_11_R1") || getVersion().equalsIgnoreCase("v1_12_R1"))) {
            BigTextUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by Village Defense!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider changing your server version?");
            getServer().getPluginManager().disablePlugin(this);
        }
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch(Exception e) {
            BigTextUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Village Defense!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
            getServer().getPluginManager().disablePlugin(this);
        }
        //check if using releases before 2.1.0
        if(LanguageManager.getLanguageFile().isSet("STATS-AboveLine") && LanguageManager.getLanguageFile().isSet("SCOREBOARD-Zombies")) {
            migrateToNewFormat();
        }
        //check if using releases 2.1.0+
        if(LanguageManager.getLanguageFile().isSet("File-Version") && getConfig().isSet("Config-Version")) {
            migrateToNewFormat();
        }
        LanguageManager.saveDefaultLanguageFile();
        saveDefaultConfig();
        debug = getConfig().getBoolean("Debug");
        if(Main.isDebugged()) {
            System.out.println("[Village Debugger] Village Defense setup started!");
        }
        setupLocale();
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
                        BigTextUtils.updateIsHere();
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

        setupGameKits();

        SpecialItem.loadAll();
        registerArenas();
        new ShopManager();
        //we must start it after instances load!
        signManager = new SignManager(this);

        chatFormat = getConfig().getBoolean("ChatFormat-Enabled");

        ConfigurationSection cs = getConfig().getConfigurationSection("CustomPermissions");
        for(String key : cs.getKeys(false)) {
            customPermissions.put(key, getConfig().getInt("CustomPermissions." + key));
            if(isDebugged()) {
                System.out.println("[Village Debugger] Loaded custom permission " + key + "!");
            }
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            UserManager.registerUser(p.getUniqueId());
        }
        if(databaseActivated){
            for(Player p : Bukkit.getOnlinePlayers()){
                MySQLConnectionUtils.loadPlayerStats(p, this);
            }
        } else {
            fileStats.loadStatsForPlayersOnline();
        }
        StatsStorage.plugin = this;
        setupPermissions();
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
        kitRegistry = new KitRegistry();
        kitManager = new KitManager(this);
        new SpectatorEvents(this);
        new QuitEvent(this);
        new SetupInventoryEvents(this);
        new JoinEvent(this);
        new ChatEvents(this);
        new MetricsLite(this);
        new Events(this);
        new MessageHandler(this);
        new CombustDayLightEvent(this);
        new LobbyEvents(this);
        new SpectatorItemEvents(this);
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

    private void setupPermissions() {
        PermissionsManager.setEditGames(getConfig().getString("Basic-Permissions.Arena-Edit-Permission"));
        PermissionsManager.setJoinFullGames(getConfig().getString("Basic-Permissions.Full-Games-Permission"));
        PermissionsManager.setVip(getConfig().getString("Basic-Permissions.Vip-Permission"));
        PermissionsManager.setMvp(getConfig().getString("Basic-Permissions.Mvp-Permission"));
        PermissionsManager.setElite(getConfig().getString("Basic-Permissions.Elite-Permission"));
        PermissionsManager.setJoinPerm(getConfig().getString("Basic-Permissions.Join-Permission"));
        if(Main.isDebugged()) System.out.println("[Village Debugger] Basic permissions from config.yml properly set!");
    }

    private void setupLocale() {
        saveResource("language_de.yml", true);
        saveResource("language_pl.yml", true);
        String locale = getConfig().getString("locale");
        if(locale.equalsIgnoreCase("default") || locale.equalsIgnoreCase("english")) {
            pluginLocale = VDLocale.DEFAULT;
        } else if(locale.equalsIgnoreCase("de") || locale.equalsIgnoreCase("deutsch")) {
            pluginLocale = VDLocale.DEUTSCH;
            if(!ConfigurationManager.getConfig("language_de").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_de").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is outdated! Not every message will be in german.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else if(locale.equalsIgnoreCase("pl") || locale.equalsIgnoreCase("polski")) {
            pluginLocale = VDLocale.POLSKI;
            if(!ConfigurationManager.getConfig("language_pl").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_pl").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is outdated! Not every message will be in polish.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
            pluginLocale = VDLocale.DEFAULT;
        }
    }

    private void migrateToNewFormat() {
        BigTextUtils.gonnaMigrate();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Village Defense 3 is migrating all files to the new file format...");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
        for(String file : migratable) {
            if(ConfigurationManager.getFile(file).exists()) {
                ConfigurationManager.getFile(file).renameTo(new File(getDataFolder(), "VD2_" + file + ".yml"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling VD3...");
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

    @Override
    public void onDisable() {
        for(Player player : getServer().getOnlinePlayers()) {
            User user = UserManager.getUser(player.getUniqueId());
            List<String> temp = new ArrayList<>();
            temp.add("gamesplayed");
            temp.add("kills");
            temp.add("deaths");
            temp.add("highestwave");
            temp.add("xp");
            temp.add("level");
            temp.add("orbs");
            for(String s : temp) {
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
                if(inventoryManagerEnabled)
                    inventoryManager.loadInventory(player);
            }
            invasionInstance.clearVillagers();
            invasionInstance.stopGame(true);
            invasionInstance.teleportAllToEndLocation();
        }
        if(isDatabaseActivated()) getMySQLDatabase().closeDatabase();
    }

    private void setupGameKits() {
        KnightKit knightkit = new KnightKit(this);
        for(Class kitClass : classKitNames) {
            if(ConfigurationManager.getConfig("kits").getBoolean("Enabled-Game-Kits." + kitClass.getSimpleName().replaceAll("Kit", ""))) {
                try {
                    Class.forName(kitClass.getName()).getConstructor(Main.class).newInstance(this);
                } catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    System.out.println("[VillageDefense] FATAL ERROR! COULDN'T REGISTER EXISTING KIT! REPORT THIS TO THE DEVELOPER!");
                }
            }
        }

        KitRegistry.setDefaultKit(knightkit);
        getKitManager().setMaterial(Material.NETHER_STAR);
        getKitManager().setItemName(ChatManager.colorMessage("Kits.Kit-Menu-Item-Name"));
        getKitManager().setMenuName(ChatManager.colorMessage("Kits.Kit-Menu.Title"));
        getKitManager().setDescription(new String[]{ChatManager.colorMessage("Kits.Open-Kit-Menu")});
    }

    public void registerArenas() {
        if(ArenaRegistry.getArenas() != null) {
            if(ArenaRegistry.getArenas().size() > 0) {
                for(Arena arena : ArenaRegistry.getArenas()) {
                    arena.clearZombies();
                    arena.clearVillagers();
                    arena.clearWolfs();
                    arena.clearGolems();
                }
            }
        }
        ArenaRegistry.getArenas().clear();
        if(!ConfigurationManager.getConfig("arenas").contains("instances")) {
            Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.No-Instances-Created"));
            return;
        }

        for(String ID : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances").getKeys(false)) {
            Arena arena;
            String s = "instances." + ID + ".";
            if(s.contains("default"))
                continue;
            if(is1_8_R3()) {
                arena = new ArenaInitializer1_8_R3(ID, this);
            } else if(is1_9_R1()) {
                arena = new ArenaInitializer1_9_R1(ID, this);
            } else if(is1_11_R1()) {
                arena = new ArenaInitializer1_11_R1(ID, this);
            } else {
                arena = new ArenaInitializer1_12_R1(ID, this);
            }
            arena.setMinimumPlayers(ConfigurationManager.getConfig("arenas").getInt(s + "minimumplayers"));
            arena.setMaximumPlayers(ConfigurationManager.getConfig("arenas").getInt(s + "maximumplayers"));
            arena.setMapName(ConfigurationManager.getConfig("arenas").getString(s + "mapname"));
            arena.setLobbyLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "lobbylocation")));
            arena.setStartLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "Startlocation")));
            arena.setEndLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "Endlocation")));

            if(!ConfigurationManager.getConfig("arenas").getBoolean(s + "isdone")) {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "NOT VALIDATED"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }

            if(ConfigurationManager.getConfig("arenas").contains(s + "zombiespawns")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "zombiespawns").getKeys(false)) {
                    String path = s + "zombiespawns." + string;
                    arena.addZombieSpawn(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "ZOMBIE SPAWNS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }

            if(ConfigurationManager.getConfig("arenas").contains(s + "villagerspawns")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "villagerspawns").getKeys(false)) {
                    String path = s + "villagerspawns." + string;
                    arena.addVillagerSpawn(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "VILLAGER SPAWNS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }
            if(ConfigurationManager.getConfig("arenas").contains(s + "doors")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "doors").getKeys(false)) {
                    String path = s + "doors." + string + ".";
                    arena.addDoor(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "location")), (byte) ConfigurationManager.getConfig("arenas").getInt(path + "byte"));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "DOORS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }
            ArenaRegistry.registerArena(arena);
            arena.start();
            Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Instance-Started").replaceAll("%arena%", ID));
        }
    }

    public WorldEditPlugin getWorldEditPlugin() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(p instanceof WorldEditPlugin)
            return (WorldEditPlugin) p;
        return null;
    }

    public enum VDLocale {
        DEFAULT("English", "en-GB"), DEUTSCH("Deutsch", "de-DE"), POLSKI("Polski", "pl-PL");

        String formattedName;
        String countryCode;

        VDLocale(String formattedName, String countryCode) {
            this.formattedName = formattedName;
            this.countryCode = countryCode;
        }

        public String getFormattedName() {
            return formattedName;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

}
