/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaEvents;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.MainCommand;
import pl.plajer.villagedefense.creatures.DoorBreakListener;
import pl.plajer.villagedefense.creatures.EntityRegistry;
import pl.plajer.villagedefense.database.FileStats;
import pl.plajer.villagedefense.database.MySQLConnectionUtils;
import pl.plajer.villagedefense.database.MySQLManager;
import pl.plajer.villagedefense.events.ChatEvents;
import pl.plajer.villagedefense.events.Events;
import pl.plajer.villagedefense.events.GolemEvents;
import pl.plajer.villagedefense.events.JoinEvent;
import pl.plajer.villagedefense.events.LobbyEvents;
import pl.plajer.villagedefense.events.QuitEvent;
import pl.plajer.villagedefense.events.spectator.SpectatorEvents;
import pl.plajer.villagedefense.events.spectator.SpectatorItemEvents;
import pl.plajer.villagedefense.handlers.BungeeManager;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.ChunkManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.PlaceholderManager;
import pl.plajer.villagedefense.handlers.PowerupManager;
import pl.plajer.villagedefense.handlers.RewardsHandler;
import pl.plajer.villagedefense.handlers.ShopManager;
import pl.plajer.villagedefense.handlers.SignManager;
import pl.plajer.villagedefense.handlers.items.SpecialItem;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.LanguageMigrator;
import pl.plajer.villagedefense.handlers.setup.SetupInventoryEvents;
import pl.plajer.villagedefense.kits.kitapi.KitManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajerlair.core.database.MySQLDatabase;
import pl.plajerlair.core.services.ServiceRegistry;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.InventoryUtils;
import pl.plajerlair.core.utils.UpdateChecker;


/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin {

  public static int STARTING_TIMER_TIME = 60;
  public static float MINI_ZOMBIE_SPEED;
  public static float ZOMBIE_SPEED;
  private static boolean debug;
  private MySQLDatabase database;
  private MySQLManager mySQLManager;
  private FileStats fileStats;
  private SignManager signManager;
  private BungeeManager bungeeManager;
  private KitManager kitManager;
  private ChunkManager chunkManager;
  private PowerupManager powerupManager;
  private RewardsHandler rewardsHandler;
  private MainCommand mainCommand;
  private boolean forceDisable = false;
  private boolean databaseActivated = false;
  private boolean bungeeEnabled;
  private boolean chatFormat = true;
  private boolean bossbarEnabled;
  private boolean inventoryManagerEnabled = false;
  private List<String> fileNames = Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "kits");
  private Map<String, Integer> customPermissions = new HashMap<>();
  private HashMap<UUID, Boolean> spyChatEnabled = new HashMap<>();
  private String version;

  public static void debug(LogLevel level, String thing) {
    if (debug) {
      switch (level) {
        case INFO:
          Bukkit.getConsoleSender().sendMessage("[Village Debugger] " + thing);
          break;
        case WARN:
          Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Debugger] " + thing);
          break;
        case ERROR:
          Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Debugger] " + thing);
          break;
        case WTF:
          Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[Village Debugger] [SEVERE]" + thing);
          break;
        case TASK:
          Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Debugger] Running task '" + thing + "'");
          break;
      }
    }
  }

  public boolean is1_11_R1() {
    return version.equalsIgnoreCase("v1_11_R1");
  }

  public boolean is1_12_R1() {
    return version.equalsIgnoreCase("v1_12_R1");
  }

  public boolean is1_13_R1() {
    return version.equalsIgnoreCase("v1_13_R1");
  }

  public boolean is1_13_R2() {
    return version.equalsIgnoreCase("v1_13_R2");
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

  @Override
  public void onEnable() {
    ServiceRegistry.registerService(this);
    try {
      version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      LanguageManager.init(this);
      saveDefaultConfig();
      if (!(version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2"))) {
        MessageUtils.thisVersionIsNotSupported();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by Village Defense!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider changing your server version?");
        forceDisable = true;
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
      try {
        Class.forName("org.spigotmc.SpigotConfig");
      } catch (Exception e) {
        MessageUtils.thisVersionIsNotSupported();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Village Defense!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
        forceDisable = true;
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
      //check if using releases before 2.1.0 or 2.1.0+
      if ((ConfigUtils.getConfig(this, "language").isSet("STATS-AboveLine")
          && ConfigUtils.getConfig(this, "language").isSet("SCOREBOARD-Zombies"))
          || (ConfigUtils.getConfig(this, "language").isSet("File-Version")
          && getConfig().isSet("Config-Version"))) {
        LanguageMigrator.migrateToNewFormat();
      }
      debug = getConfig().getBoolean("Debug", false);
      debug(LogLevel.INFO, "Main setup start");
      setupFiles();
      LanguageMigrator.configUpdate();
      LanguageMigrator.languageFileUpdate();
      initializeClasses();

      if (getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("VillageDefense").getDescription().getVersion();
        try {
          boolean check = UpdateChecker.checkUpdate(this, currentVersion, 41869);
          if (check) {
            String latestVersion = "v" + UpdateChecker.getLatestVersion();
            if (latestVersion.contains("b")) {
              Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
              Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Current version %old%, latest version %new%".replace("%old%", currentVersion)
                  .replace("%new%", latestVersion));
            } else {
              MessageUtils.updateIsHere();
              Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Village Defense plugin is outdated! Download it to keep with latest changes and fixes.");
              Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
              Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
            }
          }
        } catch (Exception ignored) {
        }
      }

      STARTING_TIMER_TIME = getConfig().getInt("Starting-Waiting-Time", 60);
      MINI_ZOMBIE_SPEED = (float) getConfig().getDouble("Mini-Zombie-Speed", 2.0);
      ZOMBIE_SPEED = (float) getConfig().getDouble("Zombie-Speed", 1.3);
      databaseActivated = getConfig().getBoolean("DatabaseActivated", false);
      inventoryManagerEnabled = getConfig().getBoolean("InventoryManager", false);
      if (databaseActivated) {
        FileConfiguration config = ConfigUtils.getConfig(this, "mysql");
        database = new MySQLDatabase(this, config.getString("address"), config.getString("user"), config.getString("password"),
            config.getInt("min-connections"), config.getInt("max-connections"));
        mySQLManager = new MySQLManager(this);
      } else {
        fileStats = new FileStats(this);
      }
      bossbarEnabled = getConfig().getBoolean("Bossbar-Enabled", true);

      DoorBreakListener listener = new DoorBreakListener();
      listener.runTaskTimer(this, 1L, 20L);

      KitRegistry.init();

      SpecialItem.loadAll();
      ArenaRegistry.registerArenas();
      new ShopManager();
      //we must start it after instances load!
      signManager = new SignManager(this);

      chatFormat = getConfig().getBoolean("ChatFormat-Enabled", true);

      ConfigurationSection cs = getConfig().getConfigurationSection("CustomPermissions");
      for (String key : cs.getKeys(false)) {
        customPermissions.put(key, getConfig().getInt("CustomPermissions." + key));
        debug(LogLevel.INFO, "Loaded custom permission " + key);
      }
      for (Player p : Bukkit.getOnlinePlayers()) {
        UserManager.registerUser(p.getUniqueId());
      }
      if (databaseActivated) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Bukkit.getScheduler().runTaskAsynchronously(this, () -> MySQLConnectionUtils.loadPlayerStats(p));
        }
      } else {
        fileStats.loadStatsForPlayersOnline();
      }
      StatsStorage.plugin = this;
      PermissionsManager.init();
      debug(LogLevel.INFO, "Main setup done");
    } catch (Exception ex) {
      new ReportedException(this, ex);
    }
  }

  private void initializeClasses() {
    bungeeEnabled = getConfig().getBoolean("BungeeActivated", false);
    if (getConfig().getBoolean("BungeeActivated", false)) {
      bungeeManager = new BungeeManager(this);
    }
    new ChatManager(ChatManager.colorMessage("In-Game.Plugin-Prefix"));
    mainCommand = new MainCommand(this, true);
    new GolemEvents(this);
    new EntityRegistry(this);
    new ArenaEvents(this);
    kitManager = new KitManager(this);
    new SpectatorEvents(this);
    new QuitEvent(this);
    new SetupInventoryEvents(this);
    new JoinEvent(this);
    new ChatEvents(this);
    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("database_enabled", () -> getConfig().getString("DatabaseActivated", "false")));
    metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> getConfig().getString("BungeeActivated", "false")));
    metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> LanguageManager.getPluginLocale().getPrefix()));
    metrics.addCustomChart(new Metrics.SimplePie("update_notifier", () -> {
      if (getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Enabled with beta notifier";
        } else {
          return "Enabled";
        }
      } else {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Beta notifier only";
        } else {
          return "Disabled";
        }
      }
    }));
    metrics.addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
      if (getServer().getPluginManager().getPlugin("VillageDefense-Enhancements") != null) {
        return "Enhancements";
      } else if (getServer().getPluginManager().getPlugin("VillageDefense-CustomKits") != null) {
        return "Custom Kits";
      }
      return "None";
    }));
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      Main.debug(LogLevel.INFO, "Hooking into PlaceholderAPI");
      new PlaceholderManager().register();
    }
    new Events(this);
    new LobbyEvents(this);
    new SpectatorItemEvents(this);
    powerupManager = new PowerupManager(this);
    chunkManager = new ChunkManager(this);
    rewardsHandler = new RewardsHandler(this);
    User.cooldownHandlerTask();
  }

  private void setupFiles() {
    for (String fileName : fileNames) {
      File file = new File(getDataFolder() + File.separator + fileName + ".yml");
      if (!file.exists()) {
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

  public MySQLManager getMySQLManager() {
    return mySQLManager;
  }

  public PowerupManager getPowerupManager() {
    return powerupManager;
  }

  @Override
  public void onDisable() {
    if (forceDisable) {
      return;
    }
    debug(LogLevel.INFO, "System disable init");
    for (Player player : getServer().getOnlinePlayers()) {
      User user = UserManager.getUser(player.getUniqueId());
      for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
        if (isDatabaseActivated()) {
          getMySQLManager().setStat(player, s, user.getStat(s));
        } else {
          getFileStats().saveStat(player, s);
        }
      }
      UserManager.removeUser(player.getUniqueId());
    }
    for (Arena arena : ArenaRegistry.getArenas()) {
      for (Player player : arena.getPlayers()) {
        arena.doBarAction(Arena.BarAction.REMOVE, player);
        arena.teleportToEndLocation(player);
        if (inventoryManagerEnabled) {
          InventoryUtils.loadInventory(this, player);
        } else {
          player.getInventory().clear();
          player.getInventory().setArmorContents(null);
          for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
          }
        }
      }
      arena.clearVillagers();
      ArenaManager.stopGame(true, arena);
      arena.teleportAllToEndLocation();
    }
    if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
      for (Hologram holo : HologramsAPI.getHolograms(this)) {
        holo.delete();
      }
    }
    if (isDatabaseActivated()) {
      getMySQLDatabase().getManager().shutdownConnPool();
    }
    debug(LogLevel.INFO, "System disable finalize");
  }

  public enum LogLevel {
    INFO, WARN, ERROR, WTF /*what a terrible failure*/, TASK
  }

}
