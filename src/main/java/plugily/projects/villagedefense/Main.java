/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.TestOnly;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.events.EventsInitializer;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaEvents;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.arena.managers.BungeeManager;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.creatures.DoorBreakListener;
import plugily.projects.villagedefense.creatures.EntityRegistry;
import plugily.projects.villagedefense.events.ChatEvents;
import plugily.projects.villagedefense.events.Events;
import plugily.projects.villagedefense.events.JoinEvent;
import plugily.projects.villagedefense.events.LobbyEvents;
import plugily.projects.villagedefense.events.QuitEvent;
import plugily.projects.villagedefense.events.bungee.MiscEvents;
import plugily.projects.villagedefense.events.spectator.SpectatorEvents;
import plugily.projects.villagedefense.events.spectator.SpectatorItemEvents;
import plugily.projects.villagedefense.handlers.ChatManager;
import plugily.projects.villagedefense.handlers.HolidayManager;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.PlaceholderManager;
import plugily.projects.villagedefense.handlers.hologram.HologramsRegistry;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.party.PartyHandler;
import plugily.projects.villagedefense.handlers.party.PartySupportInitializer;
import plugily.projects.villagedefense.handlers.powerup.PowerupRegistry;
import plugily.projects.villagedefense.handlers.reward.RewardsFactory;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.handlers.sign.SignManager;
import plugily.projects.villagedefense.handlers.upgrade.EntityUpgradeMenu;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.Upgrade;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.UpgradeBuilder;
import plugily.projects.villagedefense.kits.KitMenuHandler;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.user.UserManager;
import plugily.projects.villagedefense.user.data.MysqlManager;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.ExceptionLogHandler;
import plugily.projects.villagedefense.utils.LegacyDataFixer;
import plugily.projects.villagedefense.utils.MessageUtils;
import plugily.projects.villagedefense.utils.UpdateChecker;
import plugily.projects.villagedefense.utils.Utils;
import plugily.projects.villagedefense.utils.constants.Constants;
import plugily.projects.villagedefense.utils.services.ServiceRegistry;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin {

  private ExceptionLogHandler exceptionLogHandler;
  private BungeeManager bungeeManager;
  private ChatManager chatManager;
  private UserManager userManager;
  private ConfigPreferences configPreferences;
  private MysqlDatabase database;
  private ArgumentsRegistry registry;
  private SignManager signManager;
  private SpecialItemManager specialItemManager;
  private KitMenuHandler kitMenuHandler;
  private PartyHandler partyHandler;
  private PowerupRegistry powerupRegistry;
  private RewardsFactory rewardsHandler;
  private HolidayManager holidayManager;
  private FileConfiguration languageConfig;
  private HologramsRegistry hologramsRegistry;
  private FileConfiguration entityUpgradesConfig;

  private boolean forceDisable = false, holographicEnabled = false;

  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  public BungeeManager getBungeeManager() {
    return bungeeManager;
  }

  public SignManager getSignManager() {
    return signManager;
  }

  public KitMenuHandler getKitMenuHandler() {
    return kitMenuHandler;
  }

  public HologramsRegistry getHologramsRegistry() {
    return hologramsRegistry;
  }

  public FileConfiguration getLanguageConfig() {
    return languageConfig;
  }

  public FileConfiguration getEntityUpgradesConfig() {
    return entityUpgradesConfig;
  }

  @Override
  public void onEnable() {
    if(!validateIfPluginShouldStart()) {
      return;
    }

    long start = System.currentTimeMillis();

    ServiceRegistry.registerService(this);
    exceptionLogHandler = new ExceptionLogHandler(this);
    Messages.init(this);
    LanguageManager.init(this);
    saveDefaultConfig();
    Debugger.setEnabled(getDescription().getVersion().contains("debug") || getConfig().getBoolean("Debug"));
    Debugger.debug("[System] Initialization start");
    if(getDescription().getVersion().contains("debug") || getConfig().getBoolean("Developer-Mode")) {
      Debugger.deepDebug(true);
      Debugger.debug(Level.FINE, "Deep debug enabled");

      getConfig().getStringList("Performance-Listenable").forEach(Debugger::monitorPerformance);
    }

    chatManager = new ChatManager(LanguageManager.getLanguageMessage("In-Game.Plugin-Prefix"));
    configPreferences = new ConfigPreferences(this);
    setupFiles();
    new LegacyDataFixer(this);
    languageConfig = ConfigUtils.getConfig(this, "language");
    initializeClasses();
    checkUpdate();
    Debugger.debug("[System] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  private boolean validateIfPluginShouldStart() {
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch(Exception e) {
      MessageUtils.thisVersionIsNotSupported();
      Debugger.sendConsoleMsg("&cYour server software is not supported by Village Defense!");
      Debugger.sendConsoleMsg("&cWe support only Spigot and Spigot forks only! Shutting off...");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_R3)) {
      MessageUtils.thisVersionIsNotSupported();
      Debugger.sendConsoleMsg("&cYour server version is not supported by Village Defense!");
      Debugger.sendConsoleMsg("&cSadly, we must shut off. Maybe you consider changing your server version?");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    return true;
  }

  //order matters
  private void initializeClasses() {
    startInitiableClasses();

    ScoreboardLib.setPluginInstance(this);
    registry = new ArgumentsRegistry(this);
    new EntityRegistry(this);
    new ArenaEvents(this);
    new SpectatorEvents(this);
    new QuitEvent(this);
    new JoinEvent(this);
    new ChatEvents(this);
    setupPluginMetrics();
    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      Debugger.debug("Hooking into PlaceholderAPI");
      new PlaceholderManager().register();
    }
    new Events(this);
    new LobbyEvents(this);
    new SpectatorItemEvents(this);
    powerupRegistry = new PowerupRegistry(this);
    rewardsHandler = new RewardsFactory(this);
    holidayManager = new HolidayManager(this);
    specialItemManager = new SpecialItemManager(this);
    specialItemManager.registerItems();
    kitMenuHandler = new KitMenuHandler(this);
    partyHandler = new PartySupportInitializer().initialize(this);
    KitRegistry.init(this);
    User.cooldownHandlerTask();
    if(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Debugger.debug("Database enabled");
      FileConfiguration config = ConfigUtils.getConfig(this, Constants.Files.MYSQL.getName());
      database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"), config.getLong("maxLifeTime", 1800000));
    }
    if(configPreferences.getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      Debugger.debug("Bungee enabled");
      bungeeManager = new BungeeManager(this);
      new MiscEvents(this);
    }
    if(configPreferences.getOption(ConfigPreferences.Option.HOLOGRAMS_ENABLED)) {
      if(holographicEnabled = Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
        Debugger.debug("Hooking into HolographicDisplays");
        if(!new File(getDataFolder(), "internal/holograms_data.yml").exists()) {
          new File(getDataFolder().getPath() + "/internal").mkdir();
        }
        languageConfig = ConfigUtils.getConfig(this, "language");
        hologramsRegistry = new HologramsRegistry(this);
      } else {
        Debugger.sendConsoleMsg("&cYou need to install HolographicDisplays to use holograms!");
      }
    }
    if(configPreferences.getOption(ConfigPreferences.Option.UPGRADES_ENABLED)) {
      entityUpgradesConfig = ConfigUtils.getConfig(this, "entity_upgrades");
      languageConfig = ConfigUtils.getConfig(this, "language");
      Upgrade.init(this);
      UpgradeBuilder.init(this);
      new EntityUpgradeMenu(this);
    }
    userManager = new UserManager(this);
    new DoorBreakListener(this);

    signManager = new SignManager(this);
    ArenaRegistry.registerArenas();
    signManager.loadSigns();
    signManager.updateSigns();
    new EventsInitializer().initialize(this);
    MiscUtils.sendStartUpMessage(this, "VillageDefense", getDescription(),true, true);
  }

  private void startInitiableClasses() {
    StatsStorage.init(this);
    ArenaRegistry.init(this);
    Utils.init(this);
    CreatureUtils.init(this);
    User.init(this);
    ArenaManager.init(this);
    PermissionsManager.init(this);
    SetupInventory.init(this);
    ArenaUtils.init(this);
    Arena.init(this);
  }

  private void setupPluginMetrics() {
    Metrics metrics = new Metrics(this, 1781);

    metrics.addCustomChart(new org.bstats.charts.SimplePie("database_enabled", () -> String.valueOf(configPreferences
        .getOption(ConfigPreferences.Option.DATABASE_ENABLED))));
    metrics.addCustomChart(new org.bstats.charts.SimplePie("locale_used", () -> LanguageManager.getPluginLocale().getPrefix()));
    metrics.addCustomChart(new org.bstats.charts.SimplePie("update_notifier", () -> {
      if(getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Enabled with beta notifier" : "Enabled";
      }

      return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Beta notifier only" : "Disabled";
    }));
    metrics.addCustomChart(new org.bstats.charts.SimplePie("hooked_addons", () -> {
      if(getServer().getPluginManager().getPlugin("VillageDefense-Enhancements") != null) {
        return "Enhancements";
      }
      if(getServer().getPluginManager().getPlugin("VillageDefense-CustomKits") != null) {
        return "Custom Kits";
      }
      return "None";
    }));
  }

  private void checkUpdate() {
    if(!getConfig().getBoolean("Update-Notifier.Enabled", true)) {
      return;
    }
    UpdateChecker.init(this, 41869).requestUpdateCheck().whenComplete((result, exception) -> {
      if(!result.requiresUpdate()) {
        return;
      }
      if(result.getNewestVersion().contains("b")) {
        if(getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          Debugger.sendConsoleMsg("&c[VillageDefense] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
          Debugger.sendConsoleMsg("&c[VillageDefense] Current version %old%, latest version %new%".replace("%old%", getDescription().getVersion()).replace("%new%",
              result.getNewestVersion()));
        }
        return;
      }
      MessageUtils.updateIsHere();
      Debugger.sendConsoleMsg("&aYour VillageDefense plugin is outdated! Download it to keep with latest changes and fixes.");
      Debugger.sendConsoleMsg("&aDisable this option in config.yml if you wish.");
      Debugger.sendConsoleMsg("&eCurrent version: &c" + getDescription().getVersion() + " &eLatest version: &a" + result.getNewestVersion());
    });
  }

  private void setupFiles() {
    for(String fileName : Arrays.asList("arenas", "rewards", "stats", "special_items", "mysql", "kits")) {
      File file = new File(getDataFolder() + File.separator + fileName + ".yml");
      if(!file.exists()) {
        saveResource(fileName + ".yml", false);
      }
    }
  }

  public ChatManager getChatManager() {
    return chatManager;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public SpecialItemManager getSpecialItemManager() {
    return specialItemManager;
  }

  public RewardsFactory getRewardsHandler() {
    return rewardsHandler;
  }

  public HolidayManager getHolidayManager() {
    return holidayManager;
  }

  public MysqlDatabase getMysqlDatabase() {
    return database;
  }

  public PartyHandler getPartyHandler() {
    return partyHandler;
  }

  public PowerupRegistry getPowerupRegistry() {
    return powerupRegistry;
  }

  public ConfigPreferences getConfigPreferences() {
    return configPreferences;
  }

  public ArgumentsRegistry getArgumentsRegistry() {
    return registry;
  }

  @Override
  public void onDisable() {
    if(forceDisable) {
      return;
    }
    Debugger.debug("System disable initialized");
    long start = System.currentTimeMillis();

    Bukkit.getLogger().removeHandler(exceptionLogHandler);
    for(Arena arena : ArenaRegistry.getArenas()) {
      arena.getScoreboardManager().stopAllScoreboards();

      for(Player player : arena.getPlayers()) {
        arena.teleportToEndLocation(player);
        player.setFlySpeed(0.1f);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        arena.doBarAction(Arena.BarAction.REMOVE, player);
        if(configPreferences.getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
          InventorySerializer.loadInventory(this, player);
        }
      }

      arena.getMapRestorerManager().fullyRestoreArena();
    }
    saveAllUserStatistics();
    if(holographicEnabled) {
      if(configPreferences.getOption(ConfigPreferences.Option.HOLOGRAMS_ENABLED)) {
        hologramsRegistry.disableHolograms();
      }
      HologramsAPI.getHolograms(this).forEach(Hologram::delete);
    }
    if(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      getMysqlDatabase().shutdownConnPool();
    }
    Debugger.debug("System disable finished took {0}ms", System.currentTimeMillis() - start);
  }

  private void saveAllUserStatistics() {
    for(Player player : getServer().getOnlinePlayers()) {
      User user = userManager.getUser(player);
      if(userManager.getDatabase() instanceof MysqlManager) {
        StringBuilder update = new StringBuilder(" SET ");
        for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
          if(!stat.isPersistent()) continue;
          if(update.toString().equalsIgnoreCase(" SET ")) {
            update.append(stat.getName()).append('=').append(user.getStat(stat));
          }
          update.append(", ").append(stat.getName()).append('=').append(user.getStat(stat));
        }
        //copy of userManager#saveStatistic but without async database call that's not allowed in onDisable method.
        ((MysqlManager) userManager.getDatabase()).getDatabase().executeUpdate("UPDATE " + ((MysqlManager) getUserManager().getDatabase()).getTableName()
            + update.toString() + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
        continue;
      }
      for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        userManager.getDatabase().saveStatistic(user, stat);
      }
    }
  }
}
