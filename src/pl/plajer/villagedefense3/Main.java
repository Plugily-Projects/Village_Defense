package pl.plajer.villagedefense3;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.commands.MainCommand;
import pl.plajer.villagedefense3.creatures.BreakFenceListener;
import pl.plajer.villagedefense3.creatures.EntityRegistry;
import pl.plajer.villagedefense3.database.FileStats;
import pl.plajer.villagedefense3.database.MySQLDatabase;
import pl.plajer.villagedefense3.events.*;
import pl.plajer.villagedefense3.events.customevents.PlayerAddSpawnCommandEvent;
import pl.plajer.villagedefense3.events.customevents.SetupInventoryEvents;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.*;
import pl.plajer.villagedefense3.items.SpecialItem;
import pl.plajer.villagedefense3.kits.*;
import pl.plajer.villagedefense3.kits.kitapi.KitManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.utils.BigTextUtils;
import pl.plajer.villagedefense3.utils.ItemUtils;
import pl.plajer.villagedefense3.utils.MetricsLite;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.versions.ArenaInstance1_11_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_12_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_8_R3;
import pl.plajer.villagedefense3.versions.ArenaInstance1_9_R1;
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
    private GameInstanceManager gameInstanceManager;
    private BungeeManager bungeeManager;
    private KitRegistry kitRegistry;
    private KitManager kitManager;
    private ChunkManager chunkManager;
    private boolean bungeeEnabled;
    private boolean chatFormat = true;
    private RewardsHandler rewardsHandler;
    private boolean inventoryManagerEnabled = false;
    private List<String> fileNames = Arrays.asList("bungee", "rewards", "stats", "lobbyitems", "mysql", "kits");
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

    public GameInstanceManager getGameInstanceManager() {
        return gameInstanceManager;
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
        LanguageManager.saveDefaultLanguageFile();
        saveDefaultConfig();
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
        if(LanguageManager.getLanguageFile().isSet("STATS-AboveLine") && LanguageManager.getLanguageFile().isSet("SCOREBOARD-Zombies")){
            migrateToNewFormat();
        }
        //check if using releases 2.1.0+
        if(LanguageManager.getLanguageFile().isSet("File-Version") && getConfig().isSet("Config-Version")){
            migrateToNewFormat();
        }
        debug = getConfig().getBoolean("Debug");
        if(Main.isDebugged()) {
            System.out.println("[Village Debugger] Village Defense setup started!");
        }
        setupLocale();
        setupFiles();
        debugChecker();
        LanguageMigrator.languageFileUpdate();
        ArenaInstance.plugin = this;
        ItemUtils.villageDefense = this;
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
            fileStats.loadStatsForPlayersOnline();
        }

        getServer().getPluginManager().registerEvents(this, this);

        BreakFenceListener listener = new BreakFenceListener();
        listener.runTaskTimer(this, 1L, 20L);

        setupGameKits();

        SpecialItem.loadAll();
        loadInstances();
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
        StatsStorage.plugin = this;
        setupPermissions();
    }

    private void initializeClasses() {
        gameInstanceManager = new GameInstanceManager();
        GameInstance.plugin = this;
        bungeeEnabled = getConfig().getBoolean("BungeeActivated");
        if(getConfig().getBoolean("BungeeActivated")) bungeeManager = new BungeeManager(this);
        User.plugin = this;
        new Util(this);
        new MainCommand(this, true);
        new GolemEvents(this);
        new EntityRegistry(this);
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
        new ShopManager(this);
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
        String locale = getConfig().getString("locale");
        if(locale.equalsIgnoreCase("default") || locale.equalsIgnoreCase("english")) {
            pluginLocale = VDLocale.DEFAULT;
        } else if(locale.equalsIgnoreCase("de") || locale.equalsIgnoreCase("deutsch")) {
            pluginLocale = VDLocale.DEUTSCH;
            if(!ConfigurationManager.getConfig("language_de").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_de").get("Language-Version"))){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is outdated! Not every message will be in german.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else if(locale.equalsIgnoreCase("pl") || locale.equalsIgnoreCase("polski")) {
            pluginLocale = VDLocale.POLSKI;
            if(!ConfigurationManager.getConfig("language_pl").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_pl").get("Language-Version"))){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is outdated! Not every message will be in polish.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
                return;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
            pluginLocale = VDLocale.DEFAULT;
        }
    }

    private void migrateToNewFormat(){
        BigTextUtils.gottaMigrate();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Village Defense 3 is migrating all files to the new file format...");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
        for(String file : migratable){
            if(ConfigurationManager.getFile(file).exists()){
                ConfigurationManager.getFile(file).renameTo(new File("VD2_" + file));
                ConfigurationManager.getConfig(file);
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
        for(GameInstance invasionInstance : gameInstanceManager.getGameInstances()) {
            for(Player player : invasionInstance.getPlayers()) {
                invasionInstance.teleportToEndLocation(player);
                if(inventoryManagerEnabled)
                    inventoryManager.loadInventory(player);
            }
            ((ArenaInstance) invasionInstance).stopGame(true);
            ((ArenaInstance) invasionInstance).clearVillagers();
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

        getKitRegistry().setDefaultKit(knightkit);
        getKitManager().setMaterial(Material.NETHER_STAR);
        getKitManager().setItemName(ChatManager.colorMessage("Kits.Kit-Menu-Item-Name"));
        getKitManager().setMenuName(ChatManager.colorMessage("Kits.Kit-Menu.Title"));
        getKitManager().setDescription(new String[]{ChatManager.colorMessage("Kits.Open-Kit-Menu")});
    }

    public void loadInstances() {
        if(gameInstanceManager.getGameInstances() != null) {
            if(gameInstanceManager.getGameInstances().size() > 0) {
                for(GameInstance gameInstance : gameInstanceManager.getGameInstances()) {
                    ArenaInstance arenaInstance = (ArenaInstance) gameInstance;
                    arenaInstance.clearZombies();
                    arenaInstance.clearVillagers();
                    arenaInstance.clearWolfs();
                    arenaInstance.clearGolems();
                }
            }
        }
        gameInstanceManager.getGameInstances().clear();
        if(!getConfig().contains("instances")) {
            if(isDebugged()) {
                System.out.print(ChatColor.RED + "[Village Debugger] There are no instances in config.yml!");
            }
            return;
        }

        for(String ID : getConfig().getConfigurationSection("instances").getKeys(false)) {
            ArenaInstance arenaInstance;
            String s = "instances." + ID + ".";
            if(s.contains("default"))
                continue;

            if(is1_8_R3()) {
                arenaInstance = new ArenaInstance1_8_R3(ID);
            } else if(is1_9_R1()) {
                arenaInstance = new ArenaInstance1_9_R1(ID);
            } else if(is1_11_R1()) {
                arenaInstance = new ArenaInstance1_11_R1(ID);
            } else {
                arenaInstance = new ArenaInstance1_12_R1(ID);
            }
            arenaInstance.setMIN_PLAYERS(getConfig().getInt(s + "minimumplayers"));
            arenaInstance.setMAX_PLAYERS(getConfig().getInt(s + "maximumplayers"));
            arenaInstance.setMapName(getConfig().getString(s + "mapname"));
            arenaInstance.setLobbyLocation(Util.getLocation(true, s + "lobbylocation"));
            arenaInstance.setStartLocation(Util.getLocation(true, s + "Startlocation"));
            arenaInstance.setEndLocation(Util.getLocation(true, s + "Endlocation"));

            if(getConfig().contains(s + "zombiespawns")) {
                for(String string : getConfig().getConfigurationSection(s + "zombiespawns").getKeys(false)) {
                    String path = s + "zombiespawns." + string;
                    arenaInstance.addZombieSpawn(Util.getLocation(true, path));
                }
            } else {
                if(isDebugged()) {
                    System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + " DOESN'T HAS ZOMBIESPAWN(S)!");
                }
                gameInstanceManager.registerGameInstance(arenaInstance);
                continue;
            }

            if(getConfig().contains(s + "villagerspawns")) {
                for(String string : getConfig().getConfigurationSection(s + "villagerspawns").getKeys(false)) {
                    String path = s + "villagerspawns." + string;
                    arenaInstance.addVillagerSpawn(Util.getLocation(true, path));
                }
            } else {
                if(isDebugged()) {
                    System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + " DOESN'T HAS VILLAGERSPAWN(S)!");
                }
                gameInstanceManager.registerGameInstance(arenaInstance);

                continue;
            }
            if(getConfig().contains(s + "doors")) {
                for(String string : getConfig().getConfigurationSection(s + "doors").getKeys(false)) {
                    String path = s + "doors." + string + ".";

                    arenaInstance.addDoor(Util.getLocation(true, path + "location"), (byte) getConfig().getInt(path + "byte"));

                }
            } else {
                if(isDebugged()) {
                    System.out.print(ChatColor.RED + "[Village Debugger] ARENA " + ID + "DOESN'T HAS DOORS?");
                }
                gameInstanceManager.registerGameInstance(arenaInstance);
                continue;
            }

            gameInstanceManager.registerGameInstance(arenaInstance);


            arenaInstance.start();
            getServer().getPluginManager().registerEvents(arenaInstance, this);
            if(isDebugged()) {
                System.out.print(ChatColor.RED + "[Village Debugger] INSTANCE " + ID + " STARTED!");
            }
        }

    }

    @EventHandler
    public void onAddSpawn(PlayerAddSpawnCommandEvent event) {
        if(event.getSpawnName().equalsIgnoreCase("zombie")) {
            int i;
            if(!getConfig().contains("instances." + event.getArenaID() + ".zombiespawns")) {
                i = 0;
            } else {
                i = getConfig().getConfigurationSection("instances." + event.getArenaID() + ".zombiespawns").getKeys(false).size();
            }
            i++;
            Util.saveLoc("instances." + event.getArenaID() + ".zombiespawns." + i, event.getPlayer().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Zombie spawn added!");
            return;
        }
        if(event.getSpawnName().equalsIgnoreCase("villager")) {
            int i;
            if(!getConfig().contains("instances." + event.getArenaID() + ".villagerspawns")) {
                i = 0;
            } else {
                i = getConfig().getConfigurationSection("instances." + event.getArenaID() + ".villagerspawns").getKeys(false).size();
            }

            i++;
            Util.saveLoc("instances." + event.getArenaID() + ".villagerspawns." + i, event.getPlayer().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Villager spawn added!");
        }
        if(event.getSpawnName().equalsIgnoreCase("doors")) {
            String ID = event.getArenaID();
            int counter = 0;
            int i;
            if(getWorldEditPlugin().getSelection(event.getPlayer()) == null)
                return;
            if(!getConfig().contains("instances." + ID + ".doors")) {
                i = 0;
            } else {
                i = getConfig().getConfigurationSection("instances." + ID + ".doors").getKeys(false).size();
            }
            i++;
            Selection selection = getWorldEditPlugin().getSelection(event.getPlayer());
            if(selection instanceof CuboidSelection) {
                CuboidSelection cuboidSelection = (CuboidSelection) selection;
                Vector min = cuboidSelection.getNativeMinimumPoint();
                Vector max = cuboidSelection.getNativeMaximumPoint();
                for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                    for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                        for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                            Location temporaryBlock = new Location(event.getPlayer().getWorld(), x, y, z);
                            if(temporaryBlock.getBlock().getType() == Material.WOODEN_DOOR) {
                                Util.saveLoc("instances." + ID + ".doors." + i + ".location", temporaryBlock);
                                getConfig().set("instances." + ID + ".doors." + i + ".byte", temporaryBlock.getBlock().getData());
                                counter++;
                                i++;
                            }

                        }
                    }
                }
            } else {
                if(selection.getMaximumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                    Util.saveLoc("instances." + ID + ".doors" + i + ".location", selection.getMaximumPoint());
                    getConfig().set("instances." + ID + ".doors." + i + ".byte", selection.getMaximumPoint().getBlock().getData());
                    counter++;
                    i++;
                }
                if(selection.getMinimumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                    Util.saveLoc("instances." + ID + ".doors" + i + ".location", selection.getMinimumPoint());
                    getConfig().set("instances." + ID + ".doors." + i + ".byte", selection.getMinimumPoint().getBlock().getData());
                    counter++;
                    i++;
                }
            }
            saveConfig();
            event.getPlayer().sendMessage(ChatColor.GREEN + "" + Math.ceil(counter / 2) + " doors added!");
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
