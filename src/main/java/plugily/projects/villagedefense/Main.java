/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.permissions.Permission;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionCategory;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaEvents;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.arena.managers.enemy.spawner.EnemySpawnerRegistry;
import plugily.projects.villagedefense.arena.managers.enemy.spawner.EnemySpawnerRegistryLegacy;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.creatures.DoorBreakListener;
import plugily.projects.villagedefense.events.PluginEvents;
import plugily.projects.villagedefense.handlers.powerup.PowerupHandler;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.handlers.upgrade.EntityUpgradeMenu;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.Upgrade;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.UpgradeBuilder;
import plugily.projects.villagedefense.kits.free.KnightKit;
import plugily.projects.villagedefense.kits.free.LightTankKit;
import plugily.projects.villagedefense.kits.level.ArcherKit;
import plugily.projects.villagedefense.kits.level.GolemFriendKit;
import plugily.projects.villagedefense.kits.level.HardcoreKit;
import plugily.projects.villagedefense.kits.level.HealerKit;
import plugily.projects.villagedefense.kits.level.LooterKit;
import plugily.projects.villagedefense.kits.level.MediumTankKit;
import plugily.projects.villagedefense.kits.level.PuncherKit;
import plugily.projects.villagedefense.kits.level.RunnerKit;
import plugily.projects.villagedefense.kits.level.TerminatorKit;
import plugily.projects.villagedefense.kits.level.WorkerKit;
import plugily.projects.villagedefense.kits.level.ZombieFinderKit;
import plugily.projects.villagedefense.kits.premium.BlockerKit;
import plugily.projects.villagedefense.kits.premium.CleanerKit;
import plugily.projects.villagedefense.kits.premium.DogFriendKit;
import plugily.projects.villagedefense.kits.premium.HeavyTankKit;
import plugily.projects.villagedefense.kits.premium.MedicKit;
import plugily.projects.villagedefense.kits.premium.NakedKit;
import plugily.projects.villagedefense.kits.premium.PremiumHardcoreKit;
import plugily.projects.villagedefense.kits.premium.ShotBowKit;
import plugily.projects.villagedefense.kits.premium.TeleporterKit;
import plugily.projects.villagedefense.kits.premium.TornadoKit;
import plugily.projects.villagedefense.kits.premium.WizardKit;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Tom on 12/08/2014.
 * Updated by Tigerpanzer_02 on 03.12.2021
 */
public class Main extends PluginMain {

  private FileConfiguration entityUpgradesConfig;
  private EnemySpawnerRegistryLegacy enemySpawnerRegistry;
  private ArenaRegistry arenaRegistry;
  private ArenaManager arenaManager;
  private ArgumentsRegistry argumentsRegistry;

  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();
    registerLocales();
    super.onEnable();
    getDebugger().debug("[System] [Plugin] Initialization start");
    registerPlaceholders();
    addMessages();
    addAdditionalValues();
    initializePluginClasses();
    addKits();
    getDebugger().debug("Full {0} plugin enabled", getName());
    getDebugger().debug("[System] [Plugin] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void initializePluginClasses() {
    addFileName("powerups");
    addFileName("creatures");
    addArenaOptions();
    Arena.init(this);
    ArenaUtils.init(this);
    new ArenaEvents(this);
    arenaManager = new ArenaManager(this);
    arenaRegistry = new ArenaRegistry(this);
    arenaRegistry.registerArenas();
    getSignManager().loadSigns();
    getSignManager().updateSigns();
    argumentsRegistry = new ArgumentsRegistry(this);
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      enemySpawnerRegistry = new EnemySpawnerRegistryLegacy(this);
    } else {
      enemySpawnerRegistry = new EnemySpawnerRegistry(this);
    }
    if(getConfigPreferences().getOption("UPGRADES")) {
      entityUpgradesConfig = ConfigUtils.getConfig(this, "entity_upgrades");
      Upgrade.init(this);
      UpgradeBuilder.init(this);
      new EntityUpgradeMenu(this);
    }
    new DoorBreakListener(this);
    CreatureUtils.init(this);
    new PowerupHandler(this);
    new PluginEvents(this);
    addPluginMetrics();
  }

  public void addAdditionalValues() {
    getConfigPreferences().registerOption("UPGRADES", new ConfigOption("Entity-Upgrades", true));
    getConfigPreferences().registerOption("RESPAWN_AFTER_WAVE", new ConfigOption("Respawn.After-Wave", true));
    getConfigPreferences().registerOption("RESPAWN_IN_GAME_JOIN", new ConfigOption("Respawn.In-Game-Join", true));
    getConfigPreferences().registerOption("LIMIT_WAVE_UNLIMITED", new ConfigOption("Limit.Wave.Unlimited", true));
    getConfigPreferences().registerOption("LIMIT_ENTITY_BUY_AFTER_DEATH", new ConfigOption("Limit.Wave.Entity-Buy-After-Death", true));
    getConfigPreferences().registerOption("ZOMBIE_HEALTHBAR", new ConfigOption("Zombies.Health-Bar", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_GOLEM", new ConfigOption("Name-Visibility.Golem", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_WOLF", new ConfigOption("Name-Visibility.Wolf", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_VILLAGER", new ConfigOption("Name-Visibility.Villager", true));

    getStatsStorage().registerStatistic("KILLS", new StatisticType("kills", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("DEATHS", new StatisticType("deaths", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("HIGHEST_WAVE", new StatisticType("highest_wave", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("ORBS", new StatisticType("orbs", false, "int(11) NOT NULL DEFAULT '0'"));

    getPermissionsManager().registerPermissionCategory("ORBS_BOOSTER", new PermissionCategory("Orbs-Boost", null));
    getPermissionsManager().registerPermissionCategory("PLAYER_SPAWN_LIMIT_WOLVES", new PermissionCategory("Spawn-Limit.Wolves", null));
    getPermissionsManager().registerPermissionCategory("PLAYER_SPAWN_LIMIT_GOLEMS", new PermissionCategory("Spawn-Limit.Golems", null));
    getPermissionsManager().registerPermission("KIT_PREMIUM_UNLOCK", new Permission("Basic.Premium-Kits", "villagedefense.kits.premium"));

    getRewardsHandler().registerRewardType("START_WAVE", new RewardType("start-wave"));
    getRewardsHandler().registerRewardType("END_WAVE", new RewardType("end-wave"));
    getRewardsHandler().registerRewardType("ZOMBIE_KILL", new RewardType("zombie-kill"));
    getRewardsHandler().registerRewardType("VILLAGER_DEATH", new RewardType("villager-death"));
    getRewardsHandler().registerRewardType("PLAYER_DEATH", new RewardType("player-death"));

    getSpecialItemManager().registerSpecialItem("KIT_SELECTOR_MENU", "Kit-Menu");
  }

  public void addKits() {
    long start = System.currentTimeMillis();
    getDebugger().debug("Adding kits...");
    addFileName("kits");
    Class<?>[] classKitNames = new Class[]{KnightKit.class, LightTankKit.class, ZombieFinderKit.class, ArcherKit.class, PuncherKit.class, HealerKit.class, LooterKit.class, RunnerKit.class,
        MediumTankKit.class, WorkerKit.class, GolemFriendKit.class, TerminatorKit.class, HardcoreKit.class, CleanerKit.class, TeleporterKit.class, HeavyTankKit.class, ShotBowKit.class,
        DogFriendKit.class, PremiumHardcoreKit.class, TornadoKit.class, BlockerKit.class, MedicKit.class, NakedKit.class, WizardKit.class};
    for(Class<?> kitClass : classKitNames) {
      try {
        kitClass.getDeclaredConstructor().newInstance();
      } catch(Exception e) {
        getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
        getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        e.printStackTrace();
      }
    }
    getDebugger().debug("Kit adding finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }

  public void addMessages() {
    getMessageManager().registerMessage("COMMANDS_ADMIN_ADDED_ORBS", new Message("Commands.Admin.Added-Orbs", ""));
    getMessageManager().registerMessage("COMMANDS_ADMIN_RECEIVED_ORBS", new Message("Commands.Admin.Received-Orbs", ""));


    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_PLAYERS", new Message("In-Game.Messages.Game-End.Placeholders.Died.Players", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_VILLAGERS", new Message("In-Game.Messages.Game-End.Placeholders.Died.Villagers", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_SURVIVED", new Message("In-Game.Messages.Game-End.Placeholders.Survived", ""));

    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_REMOVED_VILLAGERS", new Message("In-Game.Messages.Admin.Removed.Villagers", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_REMOVED_GOLEMS", new Message("In-Game.Messages.Admin.Removed.Golems", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_REMOVED_ZOMBIES", new Message("In-Game.Messages.Admin.Removed.Zombies", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_REMOVED_WOLVES", new Message("In-Game.Messages.Admin.Removed.Wolves", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_CHANGED_WAVE", new Message("In-Game.Messages.Admin.Changed.Wave", ""));


    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_ROTTEN_FLESH_LEVEL_UP", new Message("In-Game.Messages.Village.Rotten-Flesh-Level-Up", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_FEEL_REFRESHED", new Message("In-Game.Messages.Village.You-Feel-Refreshed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_VILLAGER_DIED", new Message("In-Game.Messages.Village.Villager.Died", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_VILLAGER_NAMES", new Message("In-Game.Messages.Village.Villager.Names", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_STUCK_ZOMBIES", new Message("In-Game.Messages.Village.Wave.Stuck-Zombies", ""));
    /*unused*/
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_SPECTATOR_WARNING", new Message("In-Game.Messages.Village.Wave.Spectator-Warning", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_RESPAWN_ON_NEXT", new Message("In-Game.Messages.Village.Wave.Respawn-On-Next", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_RESPAWNED", new Message("In-Game.Messages.Village.Wave.Respawned", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_NEXT_IN", new Message("In-Game.Messages.Village.Wave.Next-In", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_STARTED", new Message("In-Game.Messages.Village.Wave.Started", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_TITLE_START", new Message("In-Game.Messages.Village.Wave.Title.Start", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_TITLE_END", new Message("In-Game.Messages.Village.Wave.Title.End", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ORBS_PICKUP", new Message("In-Game.Messages.Village.Orbs.Pickup", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_WOLF_SPAWN", new Message("In-Game.Messages.Village.Entities.Wolf.Spawn", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_WOLF_NAME", new Message("In-Game.Messages.Village.Entities.Wolf.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_WOLF_DEATH", new Message("In-Game.Messages.Village.Entities.Wolf.Death", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_GOLEM_SPAWN", new Message("In-Game.Messages.Village.Entities.Golem.Spawn", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_GOLEM_NAME", new Message("In-Game.Messages.Village.Entities.Golem.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_GOLEM_CANT_RIDE_OTHER", new Message("In-Game.Messages.Village.Entities.Golem.Cant-Ride-Other", ""));


    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_GUI", new Message("In-Game.Messages.Village.Shop.GUI", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_GOLEM_ITEM", new Message("In-Game.Messages.Village.Shop.Golem-Item-Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_WOLF_ITEM", new Message("In-Game.Messages.Village.Shop.Wolf-Item-Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_MOB_LIMIT_REACHED", new Message("In-Game.Messages.Village.Shop.Mob-Limit-Reached", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_NOT_ENOUGH_CURRENCY", new Message("In-Game.Messages.Village.Shop.Not-Enough-Currency", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY", new Message("In-Game.Messages.Village.Shop.Currency", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_NOT_DEFINED", new Message("In-Game.Messages.Village.Shop.Not-Defined", ""));


    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_ORBS", new Message("Leaderboard.Statistics.Orbs", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_HIGHEST_WAVE", new Message("Leaderboard.Statistics.Highest-Wave", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_KILLS", new Message("Leaderboard.Statistics.Kills", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_DEATHS", new Message("Leaderboard.Statistics.Deaths", ""));


    getMessageManager().registerMessage("UPGRADE_MENU_TITLE", new Message("Upgrade-Menu.Title", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADED_ENTITY", new Message("Upgrade-Menu.Upgraded-Entity", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_CANNOT_AFFORD", new Message("Upgrade-Menu.Cannot-Afford", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_MAX_TIER", new Message("Upgrade-Menu.Max-Tier", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_STATS_ITEM_NAME", new Message("Upgrade-Menu.Stats-Item.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_STATS_ITEM_DESCRIPTION", new Message("Upgrade-Menu.Stats-Item.Description", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_HEALTH_NAME", new Message("Upgrade-Menu.Upgrades.Health.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_HEALTH_DESCRIPTION", new Message("Upgrade-Menu.Upgrades.Health.Description", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_DAMAGE_NAME", new Message("Upgrade-Menu.Upgrades.Damage.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_DAMAGE_DESCRIPTION", new Message("Upgrade-Menu.Upgrades.Damage.Description", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_SPEED_NAME", new Message("Upgrade-Menu.Upgrades.Speed.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_SPEED_DESCRIPTION", new Message("Upgrade-Menu.Upgrades.Speed.Description", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_SWARM_NAME", new Message("Upgrade-Menu.Upgrades.Swarm-Awareness.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_SWARM_DESCRIPTION", new Message("Upgrade-Menu.Upgrades.Swarm-Awareness.Description", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_DEFENSE_NAME", new Message("Upgrade-Menu.Upgrades.Final-Defense.Name", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_UPGRADES_DEFENSE_DESCRIPTION", new Message("Upgrade-Menu.Upgrades.Final-Defense.Description", ""));


    //CLEANER KIT

    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_NAME", new Message("Kit.Content.Cleaner.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_DESCRIPTION", new Message("Kit.Content.Cleaner.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_GAME_ITEM_NAME", new Message("Kit.Content.Cleaner.Game-Item.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_GAME_ITEM_DESCRIPTION", new Message("Kit.Content.Cleaner.Game-Item.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_CLEANED_MAP", new Message("Kit.Content.Cleaner.Cleaned.Map", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_CLEANED_NOTHING", new Message("Kit.Content.Cleaner.Cleaned.Nothing", ""));

//ZOMBIE_TELEPORTER KIT

    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_NAME", new Message("Kit.Content.Zombie-Teleporter.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_DESCRIPTION", new Message("Kit.Content.Zombie-Teleporter.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_NAME", new Message("Kit.Content.Zombie-Teleporter.Game-Item.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_DESCRIPTION", new Message("Kit.Content.Zombie-Teleporter.Game-Item.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_GUI", new Message("Kit.Content.Zombie-Teleporter.Game-Item.GUI", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_ZOMBIE", new Message("Kit.Content.Zombie-Teleporter.Teleport.Zombie", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_NOT_FOUND", new Message("Kit.Content.Zombie-Teleporter.Teleport.Not-Found", ""));

//KNIGHT

    getMessageManager().registerMessage("KIT_CONTENT_KNIGHT_NAME", new Message("Kit.Content.Knight.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_KNIGHT_DESCRIPTION", new Message("Kit.Content.Knight.Description", ""));

//LIGHT_TANK

    getMessageManager().registerMessage("KIT_CONTENT_LIGHT_TANK_NAME", new Message("Kit.Content.Light-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_LIGHT_TANK_DESCRIPTION", new Message("Kit.Content.Light-Tank.Description", ""));

//ARCHER

    getMessageManager().registerMessage("KIT_CONTENT_ARCHER_NAME", new Message("Kit.Content.Archer.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ARCHER_DESCRIPTION", new Message("Kit.Content.Archer.Description", ""));

//PUNCHER

    getMessageManager().registerMessage("KIT_CONTENT_PUNCHER_NAME", new Message("Kit.Content.Puncher.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_PUNCHER_DESCRIPTION", new Message("Kit.Content.Puncher.Description", ""));

//HEALER

    getMessageManager().registerMessage("KIT_CONTENT_HEALER_NAME", new Message("Kit.Content.Healer.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HEALER_DESCRIPTION", new Message("Kit.Content.Healer.Description", ""));

//LOOTER

    getMessageManager().registerMessage("KIT_CONTENT_LOOTER_NAME", new Message("Kit.Content.Looter.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_LOOTER_DESCRIPTION", new Message("Kit.Content.Looter.Description", ""));

//RUNNER

    getMessageManager().registerMessage("KIT_CONTENT_RUNNER_NAME", new Message("Kit.Content.Runner.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_RUNNER_DESCRIPTION", new Message("Kit.Content.Runner.Description", ""));

//MEDIUM_TANK

    getMessageManager().registerMessage("KIT_CONTENT_MEDIUM_TANK_NAME", new Message("Kit.Content.Medium-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_MEDIUM_TANK_DESCRIPTION", new Message("Kit.Content.Medium-Tank.Description", ""));

//WORKER

    getMessageManager().registerMessage("KIT_CONTENT_WORKER_NAME", new Message("Kit.Content.Worker.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WORKER_DESCRIPTION", new Message("Kit.Content.Worker.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WORKER_GAME_ITEM_CHAT", new Message("Kit.Content.Worker.Game-Item.Chat", ""));

//DOG_FRIEND

    getMessageManager().registerMessage("KIT_CONTENT_DOG_FRIEND_NAME", new Message("Kit.Content.Dog-Friend.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_DOG_FRIEND_DESCRIPTION", new Message("Kit.Content.Dog-Friend.Description", ""));

//HARDCORE

    getMessageManager().registerMessage("KIT_CONTENT_HARDCORE_NAME", new Message("Kit.Content.Hardcore.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HARDCORE_DESCRIPTION", new Message("Kit.Content.Hardcore.Description", ""));

//GOLEM_FRIEND

    getMessageManager().registerMessage("KIT_CONTENT_GOLEM_FRIEND_NAME", new Message("Kit.Content.Golem-Friend.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_GOLEM_FRIEND_DESCRIPTION", new Message("Kit.Content.Golem-Friend.Description", ""));

//TORNADO

    getMessageManager().registerMessage("KIT_CONTENT_TORNADO_NAME", new Message("Kit.Content.Tornado.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TORNADO_DESCRIPTION", new Message("Kit.Content.Tornado.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TORNADO_GAME_ITEM_NAME", new Message("Kit.Content.Tornado.Game-Item.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TORNADO_GAME_ITEM_DESCRIPTION", new Message("Kit.Content.Tornado.Game-Item.Description", ""));

//TERMINATOR

    getMessageManager().registerMessage("KIT_CONTENT_TERMINATOR_NAME", new Message("Kit.Content.Terminator.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TERMINATOR_DESCRIPTION", new Message("Kit.Content.Terminator.Description", ""));

//TELEPORTER

    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_NAME", new Message("Kit.Content.Teleporter.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_DESCRIPTION", new Message("Kit.Content.Teleporter.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_GAME_ITEM_NAME", new Message("Kit.Content.Teleporter.Game-Item.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_GAME_ITEM_DESCRIPTION", new Message("Kit.Content.Teleporter.Game-Item.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_GAME_ITEM_GUI", new Message("Kit.Content.Teleport.Game-Item.GUI", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_VILLAGER", new Message("Kit.Content.Teleporter.Teleport.Villager", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_WARNING", new Message("Kit.Content.Teleporter.Teleport.Warning", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_PLAYER", new Message("Kit.Content.Teleporter.Teleport.Player", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_NOT_FOUND", new Message("Kit.Content.Teleporter.Teleport.Not-Found", ""));

//HEAVY_TANK

    getMessageManager().registerMessage("KIT_CONTENT_HEAVY_TANK_NAME", new Message("Kit.Content.Heavy-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HEAVY_TANK_DESCRIPTION", new Message("Kit.Content.Heavy-Tank.Description", ""));

//SHOT_BOW

    getMessageManager().registerMessage("KIT_CONTENT_SHOT_BOW_NAME", new Message("Kit.Content.Shot-Bow.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_SHOT_BOW_DESCRIPTION", new Message("Kit.Content.Shot-Bow.Description", ""));

//BLOCKER

    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_NAME", new Message("Kit.Content.Blocker.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_DESCRIPTION", new Message("Kit.Content.Blocker.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_GAME_ITEM_NAME", new Message("Kit.Content.Blocker.Game-Item.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_GAME_ITEM_DESCRIPTION", new Message("Kit.Content.Blocker.Game-Item.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_PLACE_SUCCESS", new Message("Kit.Content.Blocker.Place.Success", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_PLACE_FAIL", new Message("Kit.Content.Blocker.Place.Fail", ""));

//PREMIUM_HARDCORE

    getMessageManager().registerMessage("KIT_CONTENT_PREMIUM_HARDCORE_NAME", new Message("Kit.Content.Premium-Hardcore.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_PREMIUM_HARDCORE_DESCRIPTION", new Message("Kit.Content.Premium-Hardcore.Description", ""));

//MEDIC

    getMessageManager().registerMessage("KIT_CONTENT_MEDIC_NAME", new Message("Kit.Content.Medic.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_MEDIC_DESCRIPTION", new Message("Kit.Content.Medic.Description", ""));

//WILD_NAKED

    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_NAME", new Message("Kit.Content.Wild-Naked.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_DESCRIPTION", new Message("Kit.Content.Wild-Naked.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_CANNOT_WEAR_ARMOR", new Message("Kit.Content.Wild-Naked.Cannot-Wear-Armor", ""));

//WIZARD

    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_NAME", new Message("Kit.Content.Wizard.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_DESCRIPTION", new Message("Kit.Content.Wizard.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_NAME", new Message("Kit.Content.Wizard.Game-Item.Essence.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_DESCRIPTION", new Message("Kit.Content.Wizard.Game-Item.Essence.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_NAME", new Message("Kit.Content.Wizard.Game-Item.Wand.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_DESCRIPTION", new Message("Kit.Content.Wizard.Game-Item.Wand.Description", ""));

  }

  public void registerPlaceholders() {
    getPlaceholderManager().registerPlaceholder(new Placeholder("wave", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getWave());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getWave());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_player", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        int wave = pluginArena.getWave();
        String summaryEnding;
        if(pluginArena.getPlugin().getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= pluginArena.getPlugin().getConfig().getInt("Limit.Wave.Game-End", 25)) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
        } else {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE").asKey().arena(pluginArena).build();
        }
        return summaryEnding;
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        int wave = pluginArena.getWave();
        String summaryEnding;
        if(pluginArena.getPlugin().getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= pluginArena.getPlugin().getConfig().getInt("Limit.Wave.Game-End", 25)) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_SURVIVED").asKey().arena(pluginArena).build();
        } else if(!arena.getPlayersLeft().isEmpty()) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_VILLAGERS").asKey().arena(pluginArena).build();
        } else {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_PLAYERS").asKey().arena(pluginArena).build();
        }
        return summaryEnding;
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("villager_size", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getVillagers().size());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getVillagers().size());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("zombie_size_left", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getZombiesLeft());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getZombiesLeft());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("rotten_flesh_amount", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(arena.getArenaOption("ROTTEN_FLESH_AMOUNT"));
      }

      @Override
      public String getValue(PluginArena arena) {
        return Integer.toString(arena.getArenaOption("ROTTEN_FLESH_AMOUNT"));
      }
    });
  }


  private void addPluginMetrics() {
    getMetrics().addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
      if(getServer().getPluginManager().getPlugin("VillageDefense-Enhancements") != null) {
        return "Enhancements";
      }
      return "None";
    }));
  }

  private void addArenaOptions() {
    /**
     * Current arena wave.
     */
    getArenaOptionManager().registerArenaOption("WAVE", new ArenaOption("null", 1));
    /**
     * Current bonus hearts level based on rotten fleshes
     * donated by players to secret well
     */
    getArenaOptionManager().registerArenaOption("ROTTEN_FLESH_LEVEL", new ArenaOption("null", 0));
    /**
     * Amount of rotten fleshes donated to secret well
     */
    getArenaOptionManager().registerArenaOption("ROTTEN_FLESH_AMOUNT", new ArenaOption("null", 0));
    /**
     * Total amount of orbs (in game currency) spent by all players
     * in that arena in one game
     */
    getArenaOptionManager().registerArenaOption("TOTAL_ORBS_SPENT", new ArenaOption("null", 0));
    /**
     * Total amount of zombies killed by all players
     * in that arena in one game
     */
    getArenaOptionManager().registerArenaOption("TOTAL_KILLED_ZOMBIES", new ArenaOption("null", 0));
    /**
     * Amount of zombies that game still need to spawn before
     * ending current wave and start another
     */
    getArenaOptionManager().registerArenaOption("ZOMBIES_TO_SPAWN", new ArenaOption("null", 0));
    /**
     * Value used to check all alive zombies if they weren't glitched on map
     * i.e. still stay near spawn position but cannot move.
     * <p>
     * Arena itself checks this value each time it reaches 60 (so each 60 seconds).
     */
    getArenaOptionManager().registerArenaOption("ZOMBIE_GLITCH_CHECKER", new ArenaOption("null", 0));
    /**
     * Value that describes progress of zombies spawning in wave in arena.
     * <p>
     * It's counting up to 20 and resets to 0.
     * If value is equal 5 or 15 and wave is enough high special
     * zombie units will be spawned in addition to standard ones.
     */
    getArenaOptionManager().registerArenaOption("ZOMBIE_SPAWN_COUNTER", new ArenaOption("null", 0));
    /**
     * Value describes how many seconds zombie spawn system should halt and not spawn any entity.
     * This value reduces server load and lag preventing spawning hordes at once.
     * Example when wave is 30 counter will set value to 2 halting zombies spawn for 2 seconds
     * Algorithm: floor(wave / 15)
     */
    getArenaOptionManager().registerArenaOption("ZOMBIE_IDLE_PROCESS", new ArenaOption("null", 0));
    /**
     * Value that describes the multiplier of extra health zombies will receive.
     * Current health + multiplier.
     * <p>
     * Since 4.0.0 there is maximum amount of 750 to spawn in wave.
     * The more value will be above 750 the stronger zombies will be.
     * <p>
     * Zombies amount is based on algorithm: ceil((players * 0.5) * (wave * wave) / 2)
     * Difficulty multiplier is based on: ceil((ceil((players * 0.5) * (wave * wave) / 2) - 750) / 15)
     * Example: 12 players in wave 20 will receive 30 difficulty multiplier.
     * So each zombie will get 30 HP more, harder!
     */
    getArenaOptionManager().registerArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER", new ArenaOption("null", 1));
  }


  public FileConfiguration getEntityUpgradesConfig() {
    return entityUpgradesConfig;
  }

  public EnemySpawnerRegistryLegacy getEnemySpawnerRegistry() {
    return enemySpawnerRegistry;
  }

  @Override
  public ArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  @Override
  public ArgumentsRegistry getArgumentsRegistry() {
    return argumentsRegistry;
  }

  @Override
  public ArenaManager getArenaManager() {
    return arenaManager;
  }

  @Override
  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player) {
    return new SetupInventory(this, arena, player);
  }

  @Override
  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    return new SetupInventory(this, arena, player, inventoryStage);
  }
}
