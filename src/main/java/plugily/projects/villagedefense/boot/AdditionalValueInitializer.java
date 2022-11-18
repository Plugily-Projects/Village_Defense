package plugily.projects.villagedefense.boot;

import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.api.StatsStorage;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOptionManager;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemManager;
import plugily.projects.minigamesbox.classic.handlers.permissions.Permission;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionCategory;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionsManager;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.preferences.ConfigPreferences;
import plugily.projects.villagedefense.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class AdditionalValueInitializer {

  private final Main plugin;

  public AdditionalValueInitializer(Main plugin) {
    this.plugin = plugin;
    registerConfigOptions();
    registerStatistics();
    registerPermission();
    registerRewards();
    registerSpecialItems();
    registerArenaOptions();
  }

  private void registerConfigOptions() {
    getConfigPreferences().registerOption("UPGRADES", new ConfigOption("Entity-Upgrades", true));
    getConfigPreferences().registerOption("RESPAWN_AFTER_WAVE", new ConfigOption("Respawn.After-Wave", true));
    getConfigPreferences().registerOption("RESPAWN_IN_GAME_JOIN", new ConfigOption("Respawn.In-Game-Join", true));
    getConfigPreferences().registerOption("LIMIT_WAVE_UNLIMITED", new ConfigOption("Limit.Wave.Unlimited", true));
    getConfigPreferences().registerOption("LIMIT_ENTITY_BUY_AFTER_DEATH", new ConfigOption("Limit.Wave.Entity-Buy-After-Death", true));
    getConfigPreferences().registerOption("ZOMBIE_HEALTHBAR", new ConfigOption("Zombies.Health-Bar", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_GOLEM", new ConfigOption("Name-Visibility.Golem", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_WOLF", new ConfigOption("Name-Visibility.Wolf", true));
    getConfigPreferences().registerOption("NAME_VISIBILITY_VILLAGER", new ConfigOption("Name-Visibility.Villager", true));
  }

  private void registerStatistics() {
    getStatsStorage().registerStatistic("KILLS", new StatisticType("kills", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("DEATHS", new StatisticType("deaths", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("HIGHEST_WAVE", new StatisticType("highest_wave", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("ORBS", new StatisticType("orbs", false, "int(11) NOT NULL DEFAULT '0'"));
  }

  private void registerPermission() {
    getPermissionsManager().registerPermissionCategory("ORBS_BOOSTER", new PermissionCategory("Orbs-Boost", null));
    getPermissionsManager().registerPermissionCategory("PLAYER_SPAWN_LIMIT_WOLVES", new PermissionCategory("Spawn-Limit.Wolves", null));
    getPermissionsManager().registerPermissionCategory("PLAYER_SPAWN_LIMIT_GOLEMS", new PermissionCategory("Spawn-Limit.Golems", null));
    getPermissionsManager().registerPermission("KIT_PREMIUM_UNLOCK", new Permission("Basic.Premium-Kits", "villagedefense.kits.premium"));
  }

  private void registerRewards() {
    getRewardsHandler().registerRewardType("START_WAVE", new RewardType("start-wave"));
    getRewardsHandler().registerRewardType("END_WAVE", new RewardType("end-wave"));
    getRewardsHandler().registerRewardType("ZOMBIE_KILL", new RewardType("zombie-kill"));
    getRewardsHandler().registerRewardType("VILLAGER_DEATH", new RewardType("villager-death"));
    getRewardsHandler().registerRewardType("PLAYER_DEATH", new RewardType("player-death"));
  }

  private void registerSpecialItems() {
    getSpecialItemManager().registerSpecialItem("KIT_SELECTOR_MENU", "Kit-Menu");
  }

  private void registerArenaOptions() {
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

  private ConfigPreferences getConfigPreferences() {
    return plugin.getConfigPreferences();
  }

  private StatsStorage getStatsStorage() {
    return plugin.getStatsStorage();
  }

  private PermissionsManager getPermissionsManager() {
    return plugin.getPermissionsManager();
  }

  private RewardsFactory getRewardsHandler() {
    return plugin.getRewardsHandler();
  }

  private SpecialItemManager getSpecialItemManager() {
    return plugin.getSpecialItemManager();
  }

  private ArenaOptionManager getArenaOptionManager() {
    return plugin.getArenaOptionManager();
  }

}
