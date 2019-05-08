/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

import me.tigerhix.lib.scoreboard.ScoreboardLib;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaEvents;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.creatures.CreatureUtils;
import pl.plajer.villagedefense.creatures.DoorBreakListener;
import pl.plajer.villagedefense.creatures.EntityRegistry;
import pl.plajer.villagedefense.creatures.upgrades.EntityUpgradeMenu;
import pl.plajer.villagedefense.events.ChatEvents;
import pl.plajer.villagedefense.events.Events;
import pl.plajer.villagedefense.events.JoinEvent;
import pl.plajer.villagedefense.events.LobbyEvents;
import pl.plajer.villagedefense.events.QuitEvent;
import pl.plajer.villagedefense.events.spectator.SpectatorEvents;
import pl.plajer.villagedefense.events.spectator.SpectatorItemEvents;
import pl.plajer.villagedefense.handlers.BungeeManager;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.HolidayManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.PlaceholderManager;
import pl.plajer.villagedefense.handlers.SignManager;
import pl.plajer.villagedefense.handlers.items.SpecialItem;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.powerup.PowerupRegistry;
import pl.plajer.villagedefense.handlers.reward.RewardsFactory;
import pl.plajer.villagedefense.handlers.setup.SetupInventoryEvents;
import pl.plajer.villagedefense.kits.KitManager;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.user.data.MysqlManager;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajer.villagedefense.utils.ExceptionLogHandler;
import pl.plajer.villagedefense.utils.LegacyDataFixer;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajer.villagedefense.utils.UpdateChecker;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import pl.plajerlair.services.ServiceRegistry;


/**
 * Created by Tom on 12/08/2014.
 */
public class Main extends JavaPlugin {

  private ExceptionLogHandler exceptionLogHandler;
  private ChatManager chatManager;
  private UserManager userManager;
  private ConfigPreferences configPreferences;
  private MysqlDatabase database;
  private ArgumentsRegistry registry;
  private SignManager signManager;
  private BungeeManager bungeeManager;
  private KitManager kitManager;
  private PowerupRegistry powerupRegistry;
  private RewardsFactory rewardsHandler;
  private HolidayManager holidayManager;
  private EntityUpgradeMenu entityUpgradeMenu;
  private boolean forceDisable = false;
  private String version;

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

  public BungeeManager getBungeeManager() {
    return bungeeManager;
  }

  public SignManager getSignManager() {
    return signManager;
  }

  public KitManager getKitManager() {
    return kitManager;
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
    exceptionLogHandler = new ExceptionLogHandler();
    LanguageManager.init(this);
    saveDefaultConfig();
    Debugger.setEnabled(getConfig().getBoolean("Debug", false));
    Debugger.debug(Debugger.Level.INFO, "Main setup start");
    chatManager = new ChatManager(ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage("In-Game.Plugin-Prefix")));
    configPreferences = new ConfigPreferences(this);
    setupFiles();
    new LegacyDataFixer(this);
    initializeClasses();
    checkUpdate();

    if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      FileConfiguration config = ConfigUtils.getConfig(this, "mysql");
      database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"));
    }
    userManager = new UserManager(this);
    new DoorBreakListener(this);
    KitRegistry.init();

    SpecialItem.loadAll();
    ArenaRegistry.registerArenas();
    //we must start it after instances load!
    signManager = new SignManager(this);

    PermissionsManager.init();
    Debugger.debug(Debugger.Level.INFO, "Main setup done");
  }

  private boolean validateIfPluginShouldStart() {
    version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    if (!(version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2") ||
        version.equalsIgnoreCase("v1_14_R1"))) {
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

  private void initializeClasses() {
    Utils.init(this);
    ScoreboardLib.setPluginInstance(this);
    CreatureUtils.init(this);
    if (getConfig().getBoolean("BungeeActivated", false)) {
      bungeeManager = new BungeeManager(this);
    }
    registry = new ArgumentsRegistry(this);
    entityUpgradeMenu = new EntityUpgradeMenu(this);
    new EntityRegistry(this);
    new ArenaEvents(this);
    kitManager = new KitManager(this);
    new SpectatorEvents(this);
    new QuitEvent(this);
    new SetupInventoryEvents(this);
    new JoinEvent(this);
    new ChatEvents(this);
    setupPluginMetrics();
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      Debugger.debug(Debugger.Level.INFO, "Hooking into PlaceholderAPI");
      new PlaceholderManager().register();
    }
    new Events(this);
    new LobbyEvents(this);
    new SpectatorItemEvents(this);
    powerupRegistry = new PowerupRegistry(this);
    rewardsHandler = new RewardsFactory(this);
    holidayManager = new HolidayManager(this);
    User.cooldownHandlerTask();
  }

  private void setupPluginMetrics() {
    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("database_enabled", () -> String.valueOf(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED))));
    metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> String.valueOf(configPreferences.getOption(ConfigPreferences.Option.BUNGEE_ENABLED))));
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
    for (String fileName : Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "kits")) {
      File file = new File(getDataFolder() + File.separator + fileName + ".yml");
      if (!file.exists()) {
        saveResource(fileName + ".yml", false);
      }
    }
  }

  public EntityUpgradeMenu getEntityUpgradeMenu() {
    return entityUpgradeMenu;
  }

  public ChatManager getChatManager() {
    return chatManager;
  }

  public UserManager getUserManager() {
    return userManager;
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
    Debugger.debug(Debugger.Level.INFO, "System disable init");
    Bukkit.getLogger().removeHandler(exceptionLogHandler);
    for (Arena arena : ArenaRegistry.getArenas()) {
      arena.getScoreboardManager().stopAllScoreboards();
      for (Player player : arena.getPlayers()) {
        arena.doBarAction(Arena.BarAction.REMOVE, player);
        arena.teleportToEndLocation(player);
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
    if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
      for (Hologram holo : HologramsAPI.getHolograms(this)) {
        holo.delete();
      }
    }
    if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      getMysqlDatabase().shutdownConnPool();
    }
    Debugger.debug(Debugger.Level.INFO, "System disable finalize");
  }

  private void saveAllUserStatistics() {
    for (Player player : getServer().getOnlinePlayers()) {
      User user = userManager.getUser(player);

      //copy of userManager#saveStatistic but without async database call that's not allowed in onDisable method.
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        if (!stat.isPersistent()) {
          continue;
        }
        if (userManager.getDatabase() instanceof MysqlManager) {
          ((MysqlManager) userManager.getDatabase()).getDatabase().executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
          continue;
        }
        userManager.getDatabase().saveStatistic(user, stat);
      }
    }
  }

}
