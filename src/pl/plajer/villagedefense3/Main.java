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

package pl.plajer.villagedefense3;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaEvents;
import pl.plajer.villagedefense3.arena.ArenaManager;
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
import pl.plajer.villagedefense3.handlers.PlaceholderManager;
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
import pl.plajer.villagedefense3.utils.BossBarUtil;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.utils.Metrics;
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
public class Main extends JavaPlugin {

    public static int STARTING_TIMER_TIME = 60;
    public static float MINI_ZOMBIE_SPEED;
    public static float ZOMBIE_SPEED;
    private static boolean debug;
    private MySQLDatabase database;
    private FileStats fileStats;
    private SignManager signManager;
    private InventoryManager inventoryManager;
    private BungeeManager bungeeManager;
    private KitManager kitManager;
    private ChunkManager chunkManager;
    private PowerupManager powerupManager;
    private RewardsHandler rewardsHandler;
    private MainCommand mainCommand;
    private boolean forceDisable = false;
    private boolean databaseActivated = false;
    private boolean bungeeEnabled;
    private boolean dataEnabled = false;
    private boolean chatFormat = true;
    private boolean bossbarEnabled;
    private boolean inventoryManagerEnabled = false;
    private List<String> fileNames = Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "kits");
    private Map<String, Integer> customPermissions = new HashMap<>();
    private HashMap<UUID, Boolean> spyChatEnabled = new HashMap<>();
    private String version;

    public static void debug(String thing, long millis) {
        long elapsed = System.currentTimeMillis() - millis;
        if(debug) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Debugger] Running task '" + thing + "'");
        }
        if(elapsed > 15) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Debugger] Slow server response, games may be affected.");
        }
    }

    public boolean is1_8_R3() {
        return version.equalsIgnoreCase("v1_8_R3");
    }

    public boolean is1_9_R1() {
        return version.equalsIgnoreCase("v1_9_R1");
    }

    public boolean is1_11_R1() {
        return version.equalsIgnoreCase("v1_11_R1");
    }

    public boolean is1_12_R1() {
        return version.equalsIgnoreCase("v1_12_R1");
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

    public MainCommand getMainCommand() {
        return mainCommand;
    }

    public Map<String, Integer> getCustomPermissions() {
        return customPermissions;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public String getVersion() {
        return version;
    }

    public boolean isDataEnabled() {
        return dataEnabled;
    }

    public void setDataEnabled(boolean dataEnabled) {
        this.dataEnabled = dataEnabled;
    }

    @Override
    public void onEnable() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        new ConfigurationManager(this);
        LanguageManager.init(this);
        saveDefaultConfig();
        if(!(version.equalsIgnoreCase("v1_8_R3") || version.equalsIgnoreCase("v1_9_R1") || version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1"))) {
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
        //check if using releases before 2.1.0 or 2.1.0+
        if((ConfigurationManager.getConfig("language").isSet("STATS-AboveLine") && ConfigurationManager.getConfig("language").isSet("SCOREBOARD-Zombies")) ||
                (ConfigurationManager.getConfig("language").isSet("File-Version") && getConfig().isSet("Config-Version"))) {
            LanguageMigrator.migrateToNewFormat();
        }
        debug = getConfig().getBoolean("Debug");
        debug("Main setup start", System.currentTimeMillis());
        setupFiles();
        LanguageMigrator.configUpdate();
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
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Village Defense plugin is outdated! Download it to keep with latest changes and fixes.");
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
        debug("Main setup done", System.currentTimeMillis());
    }

    private void initializeClasses() {
        bungeeEnabled = getConfig().getBoolean("BungeeActivated");
        if(getConfig().getBoolean("BungeeActivated")) bungeeManager = new BungeeManager(this);
        new ChatManager(ChatManager.colorMessage("In-Game.Plugin-Prefix"));
        new Util(this);
        mainCommand = new MainCommand(this, true);
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
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("database_enabled", () -> getConfig().getString("DatabaseActivated", "false")));
        metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> getConfig().getString("BungeeActivated", "false")));
        metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> {
            switch(getConfig().getString("locale", "default")) {
                case "default":
                    return "English";
                case "en":
                    return "English";
                case "pl":
                    return "Polish";
                case "de":
                    return "German";
                default:
                    return "English";
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifier", () -> {
            if(getConfig().getBoolean("Update-Notifier.Enabled", true)) {
                if(getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
                    return "Enabled with beta notifier";
                } else {
                    return "Enabled";
                }
            } else {
                if(getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
                    return "Beta notifier only";
                } else {
                    return "Disabled";
                }
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
            if(getServer().getPluginManager().getPlugin("VillageDefense-CustomKits") != null) {
                return "Custom Kits";
            }
            return "None";
        }));
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Main.debug("Hooking into PlaceholderAPI", System.currentTimeMillis());
            new PlaceholderManager().register();
        }
        new Events(this);
        new CombustDayLightEvent(this);
        new LobbyEvents(this);
        new SpectatorItemEvents(this);
        powerupManager = new PowerupManager(this);
        chunkManager = new ChunkManager(this);
        rewardsHandler = new RewardsHandler(this);
        User.cooldownHandlerTask();
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
        debug("System disable", System.currentTimeMillis());
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
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(Player player : arena.getPlayers()) {
                if(bossbarEnabled) {
                    if(is1_8_R3()) {
                        BossBarUtil.removeBar(player);
                    } else {
                        arena.getGameBar().removePlayer(player);
                    }
                }
                arena.teleportToEndLocation(player);
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
            arena.clearVillagers();
            ArenaManager.stopGame(true, arena);
            arena.teleportAllToEndLocation();
        }
        if(getServer().getPluginManager().isPluginEnabled("HolographicDisplays"))
            for(Hologram holo : HologramsAPI.getHolograms(this)) holo.delete();
        if(isDatabaseActivated()) getMySQLDatabase().closeDatabase();
    }

    public WorldEditPlugin getWorldEditPlugin() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        return null;
    }

}
