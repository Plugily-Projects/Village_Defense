/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.TestOnly;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.*;
import pl.plajer.villagedefense.arena.managers.BungeeManager;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.creatures.CreatureUtils;
import pl.plajer.villagedefense.creatures.DoorBreakListener;
import pl.plajer.villagedefense.creatures.EntityRegistry;
import pl.plajer.villagedefense.events.*;
import pl.plajer.villagedefense.events.bungee.MiscEvents;
import pl.plajer.villagedefense.events.spectator.SpectatorEvents;
import pl.plajer.villagedefense.events.spectator.SpectatorItemEvents;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.HolidayManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.PlaceholderManager;
import pl.plajer.villagedefense.handlers.hologram.HologramsRegistry;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.handlers.party.PartyHandler;
import pl.plajer.villagedefense.handlers.party.PartySupportInitializer;
import pl.plajer.villagedefense.handlers.powerup.PowerupRegistry;
import pl.plajer.villagedefense.handlers.reward.RewardsFactory;
import pl.plajer.villagedefense.handlers.setup.SetupInventory;
import pl.plajer.villagedefense.handlers.sign.ArenaSign;
import pl.plajer.villagedefense.handlers.sign.SignManager;
import pl.plajer.villagedefense.handlers.upgrade.EntityUpgradeMenu;
import pl.plajer.villagedefense.handlers.upgrade.upgrades.Upgrade;
import pl.plajer.villagedefense.handlers.upgrade.upgrades.UpgradeBuilder;
import pl.plajer.villagedefense.kits.KitMenuHandler;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.Kit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.user.data.MysqlManager;
import pl.plajer.villagedefense.utils.*;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;
import pl.plajer.villagedefense.utils.constants.Constants;
import pl.plajer.villagedefense.utils.services.ServiceRegistry;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

import java.io.File;
import java.util.ArrayList;
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
  private boolean forceDisable = false;
  private String version;

  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
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

  public boolean is1_14_R1() {
    return version.equalsIgnoreCase("v1_14_R1");
  }

  public boolean is1_15_R1() {
    return version.equalsIgnoreCase("v1_15_R1");
  }

  public boolean is1_16_R1() {
    return version.equalsIgnoreCase("v1_16_R1");
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

  public String getVersion() {
    return version;
  }

  @Override
  public void onEnable() {
    if (!validateIfPluginShouldStart()) {
      return;
    }

    ServiceRegistry.registerService(this);
    exceptionLogHandler = new ExceptionLogHandler(this);
    Messages.init(this);
    LanguageManager.init(this);
    saveDefaultConfig();
    Debugger.setEnabled(getDescription().getVersion().contains("b") || getConfig().getBoolean("Debug", false));
    Debugger.debug(Level.INFO, "[System] Initialization start");
    if (getDescription().getVersion().contains("b") || getConfig().getBoolean("Developer-Mode", false)) {
      Debugger.deepDebug(true);
      Debugger.debug(Level.FINE, "Deep debug enabled");
      for (String listenable : new ArrayList<>(getConfig().getStringList("Performance-Listenable"))) {
        Debugger.monitorPerformance(listenable);
      }
    }
    long start = System.currentTimeMillis();

    chatManager = new ChatManager(this, ChatColor.translateAlternateColorCodes('&',
        LanguageManager.getLanguageMessage("In-Game.Plugin-Prefix")));
    configPreferences = new ConfigPreferences(this);
    setupFiles();
    new LegacyDataFixer(this);
    this.languageConfig = ConfigUtils.getConfig(this, "language");
    initializeClasses();
    checkUpdate();
    Debugger.debug(Level.INFO, "[System] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  private boolean validateIfPluginShouldStart() {
    version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    if (!(version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2") ||
        version.equalsIgnoreCase("v1_14_R1") || version.equalsIgnoreCase("v1_15_R1") || version.equalsIgnoreCase("v1_16_R1"))) {
      MessageUtils.thisVersionIsNotSupported();
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by Village Defense!");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider changing your server version?");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch (Exception e) {
      MessageUtils.thisVersionIsNotSupported();
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Village Defense!");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
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
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      Debugger.debug(Level.INFO, "Hooking into PlaceholderAPI");
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
    if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Debugger.debug(Level.INFO, "Database enabled");
      FileConfiguration config = ConfigUtils.getConfig(this, Constants.Files.MYSQL.getName());
      database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"));
    }
    if (configPreferences.getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      Debugger.debug(Level.INFO, "Bungee enabled");
      bungeeManager = new BungeeManager(this);
      new MiscEvents(this);
    }
    if (configPreferences.getOption(ConfigPreferences.Option.HOLOGRAMS_ENABLED)) {
      if (Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
        Debugger.debug(Level.INFO, "Hooking into HolographicDisplays");
        if (!new File(getDataFolder(), "internal/holograms_data.yml").exists()) {
          new File(getDataFolder().getPath() + "/internal").mkdir();
        }
        this.languageConfig = ConfigUtils.getConfig(this, "language");
        this.hologramsRegistry = new HologramsRegistry(this);
      } else {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You need to install HolographicDisplays to use holograms!");
      }
    }
    if (configPreferences.getOption(ConfigPreferences.Option.UPGRADES_ENABLED)) {
      this.entityUpgradesConfig = ConfigUtils.getConfig(this, "entity_upgrades");
      this.languageConfig = ConfigUtils.getConfig(this, "language");
      Upgrade.init(this);
      UpgradeBuilder.init(this);
      new EntityUpgradeMenu(this);
    }
    userManager = new UserManager(this);
    new DoorBreakListener(this);

    ArenaRegistry.registerArenas();
    //we must start it after instances load!
    signManager = new SignManager(this);
  }

  private void startInitiableClasses() {
    ArenaSign.init(this);
    StatsStorage.init(this);
    ArenaRegistry.init(this);
    CompatMaterialConstants.init(this);
    Utils.init(this);
    CreatureUtils.init(this);
    User.init(this);
    ArenaManager.init(this);
    Kit.init(this);
    PermissionsManager.init(this);
    SetupInventory.init(this);
    ArenaUtils.init(this);
    Arena.init(this);
  }

  private void setupPluginMetrics() {
    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("database_enabled", () -> String.valueOf(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED))));
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
  }

  private void checkUpdate() {
    if (!getConfig().getBoolean("Update-Notifier.Enabled", true)) {
      return;
    }
    UpdateChecker.init(this, 41869).requestUpdateCheck().whenComplete((result, exception) -> {
      if (!result.requiresUpdate()) {
        return;
      }
      if (result.getNewestVersion().contains("b")) {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
          Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] Current version %old%, latest version %new%".replace("%old%", getDescription().getVersion()).replace("%new%",
              result.getNewestVersion()));
        }
        return;
      }
      MessageUtils.updateIsHere();
      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your VillageDefense plugin is outdated! Download it to keep with latest changes and fixes.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
    });
  }

  private void setupFiles() {
    for (String fileName : Arrays.asList("arenas", "rewards", "stats", "special_items", "mysql", "kits")) {
      File file = new File(getDataFolder() + File.separator + fileName + ".yml");
      if (!file.exists()) {
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
    if (forceDisable) {
      return;
    }
    Debugger.debug(Level.INFO, "System disable initialized");
    long start = System.currentTimeMillis();

    Bukkit.getLogger().removeHandler(exceptionLogHandler);
    for (Arena arena : ArenaRegistry.getArenas()) {
      arena.getScoreboardManager().stopAllScoreboards();
      for (Player player : arena.getPlayers()) {
        arena.doBarAction(Arena.BarAction.REMOVE, player);
        arena.teleportToEndLocation(player);
        player.setFlySpeed(0.1f);
        if (configPreferences.getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
          InventorySerializer.loadInventory(this, player);
          continue;
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for (PotionEffect pe : player.getActivePotionEffects()) {
          player.removePotionEffect(pe.getType());
        }
      }
      arena.getMapRestorerManager().fullyRestoreArena();
      arena.getPlayers().forEach(arena::teleportToEndLocation);
    }
    saveAllUserStatistics();
    if (configPreferences.getOption(ConfigPreferences.Option.HOLOGRAMS_ENABLED)) {
      hologramsRegistry.disableHolograms();
    }
    //hmm? Can be removed?
    if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
      for (Hologram holo : HologramsAPI.getHolograms(this)) {
        holo.delete();
      }
    }
    if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      getMysqlDatabase().shutdownConnPool();
    }
    Debugger.debug(Level.INFO, "System disable finished took {0}ms", System.currentTimeMillis() - start);
  }

  private void saveAllUserStatistics() {
    for (Player player : getServer().getOnlinePlayers()) {
      User user = userManager.getUser(player);
      StringBuilder update = new StringBuilder(" SET ");
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        if (!stat.isPersistent()) {
          continue;
        }
        if (update.toString().equalsIgnoreCase(" SET ")){
          update.append(stat.getName()).append("=").append(user.getStat(stat));
        }
        update.append(", ").append(stat.getName()).append("=").append(user.getStat(stat));
      }
      String finalUpdate = update.toString();
      //copy of userManager#saveStatistic but without async database call that's not allowed in onDisable method.
      if (userManager.getDatabase() instanceof MysqlManager) {
        ((MysqlManager) userManager.getDatabase()).getDatabase().executeUpdate("UPDATE "+((MysqlManager) getUserManager().getDatabase()).getTableName()+ finalUpdate + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
        continue;
      }
      userManager.getDatabase().saveAllStatistic(user);
    }
  }
}
