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

package plugily.projects.villagedefense.handlers.language;

import plugily.projects.villagedefense.Main;

/**
 * Enum with all plugin messages accessors.
 * Used for getting game messages safely and for integrity checks.
 * <p>
 * Getting messages from config can be hard to control for thousands of strings
 * so we must use centralized method which is always constant.
 *
 * @author Plajer
 * @since 4.2.0
 */
public enum Messages {

  COMMANDS_DID_YOU_MEAN("Commands.Did-You-Mean"),
  COMMANDS_COMMAND_EXECUTED("Commands.Command-Executed"),
  COMMANDS_TELEPORTED_TO_THE_LOBBY("Commands.Teleported-To-The-Lobby"),
  COMMANDS_REMOVED_GAME_INSTANCE("Commands.Removed-Game-Instance"),
  COMMANDS_NO_ARENA_LIKE_THAT("Commands.No-Arena-Like-That"),
  COMMANDS_LOOK_AT_SIGN("Commands.Look-Sign"),
  COMMANDS_TYPE_ARENA_NAME("Commands.Type-Arena-Name"),
  COMMANDS_HOLD_ANY_ITEM("Commands.Hold-Any-Item"),
  COMMANDS_NO_FREE_ARENAS("Commands.No-Free-Arenas"),
  COMMANDS_ONLY_BY_PLAYER("Commands.Only-By-Player"),
  COMMANDS_NOT_PLAYING("Commands.Not-Playing"),
  COMMANDS_NO_PERMISSION("Commands.No-Permission"),
  COMMANDS_INVALID_NUMBER("Commands.Invalid-Number"),
  COMMANDS_TARGET_PLAYER_NOT_FOUND("Commands.Target-Player-Not-Found"),
  COMMANDS_TELEPORT_LOCATION_INVALID("Commands.Location-Teleport-Invalid"),
  COMMANDS_WRONG_USAGE("Commands.Wrong-Usage"),

  COMMANDS_ADMIN_ADDED_ORBS("Commands.Admin-Commands.Added-Orbs"),
  COMMANDS_ADMIN_RECEIVED_ORBS("Commands.Admin-Commands.Received-Orbs"),
  COMMANDS_ADMIN_ADDED_LEVEL("Commands.Admin-Commands.Added-Level"),
  COMMANDS_ADMIN_SUCCESS_RELOAD("Commands.Admin-Commands.Success-Reload"),
  COMMANDS_ADMIN_LIST_HEADER("Commands.Admin-Commands.List-Command.Header"),
  COMMANDS_ADMIN_LIST_FORMAT("Commands.Admin-Commands.List-Command.Format"),
  COMMANDS_ADMIN_LIST_NO_ARENAS("Commands.Admin-Commands.List-Command.No-Arenas"),
  COMMANDS_ADMIN_SPYCHAT_TOGGLED("Commands.Admin-Commands.Spychat-Command.Toggled"),

  COMMANDS_STATISTICS_HEADER("Commands.Stats-Command.Header"),
  COMMANDS_STATISTICS_HEADER_OTHER("Commands.Stats-Command.Header-Other"),
  COMMANDS_STATISTICS_FOOTER("Commands.Stats-Command.Footer"),
  COMMANDS_STATISTICS_KILLS("Commands.Stats-Command.Kills"),
  COMMANDS_STATISTICS_DEATHS("Commands.Stats-Command.Deaths"),
  COMMANDS_STATISTICS_GAMES_PLAYED("Commands.Stats-Command.Games-Played"),
  COMMANDS_STATISTICS_HIGHEST_WAVE("Commands.Stats-Command.Highest-Wave"),
  COMMANDS_STATISTICS_LEVEL("Commands.Stats-Command.Level"),
  COMMANDS_STATISTICS_EXP("Commands.Stats-Command.Exp"),
  COMMANDS_STATISTICS_ORBS("Commands.Stats-Command.Orbs"),
  COMMANDS_STATISTICS_NEXT_LEVEL_EXP("Commands.Stats-Command.Next-Level-Exp"),

  COMMANDS_MAIN_HEADER("Commands.Main-Command.Header"),
  COMMANDS_MAIN_DESCRIPTION("Commands.Main-Command.Description"),
  COMMANDS_MAIN_ADMIN_BONUS_DESCRIPTION("Commands.Main-Command.Admin-Bonus-Description"),
  COMMANDS_MAIN_FOOTER("Commands.Main-Command.Footer"),

  LEADERBOARD_TYPE_NAME("Commands.Statistics.Type-Name"),
  LEADERBOARD_INVALID_NAME("Commands.Statistics.Invalid-Name"),
  LEADERBOARD_HEADER("Commands.Statistics.Header"),
  LEADERBOARD_FORMAT("Commands.Statistics.Format"),

  //contents are lists of strings, not applicable here
  /*SCOREBOARD_TITLE("Scoreboard.Title"),
  SCOREBOARD_CONTENT("Scoreboard.Content"),
  SCOREBOARD_CONTENT_PLAYING("Scoreboard.Content.Playing"),
  SCOREBOARD_CONTENT_PLAYING_WAITING("Scoreboard.Content.Playing-Waiting"),
  SCOREBOARD_CONTENT_WAITING("Scoreboard.Content.Waiting"),
  SCOREBOARD_CONTENT_STARTING("Scoreboard.Content.Starting")*/

  BOSSBAR_MAIN_TITLE("Bossbar.Main-Title"),
  BOSSBAR_STARTING_IN("Bossbar.Starting-In"),
  BOSSBAR_WAITING_FOR_PLAYERS("Bossbar.Waiting-For-Players"),
  BOSSBAR_IN_GAME_WAVE("Bossbar.In-Game-Wave"),
  BOSSBAR_IN_GAME_INFO("Bossbar.In-Game-Info"),
  BOSSBAR_GAME_ENDED("Bossbar.Game-Ended"),

  KITS_MENU_TITLE("Kits.Kit-Menu.Title"),
  KITS_MENU_UNLOCKED_LORE("Kits.Kit-Menu.Unlocked-Kit-Lore"),
  KITS_MENU_LOCKED_LORE("Kits.Kit-Menu.Locked-Lores.Locked-Lore"),
  KITS_MENU_LOCKED_UNLOCK_AT_LEVEL("Kits.Kit-Menu.Locked-Lores.Unlock-At-Level"),
  KITS_MENU_LOCKED_UNLOCK_IN_STORE("Kits.Kit-Menu.Locked-Lores.Unlock-In-Store"),
  KITS_NOT_UNLOCKED_MESSAGE("Kits.Not-Unlocked-Message"),
  KITS_CHOOSE_MESSAGE("Kits.Choose-Message"),
  KITS_OPEN_KIT_MENU("Kits.Open-Kit-Menu"),
  KITS_ABILITY_STILL_ON_COOLDOWN("Kits.Ability-Still-On-Cooldown"),

  KITS_CLEANER_NAME("Kits.Cleaner.Kit-Name"),
  KITS_CLEANER_DESCRIPTION("Kits.Cleaner.Kit-Description"),
  KITS_CLEANER_GAME_ITEM_NAME("Kits.Cleaner.Game-Item-Name"),
  KITS_CLEANER_GAME_ITEM_LORE("Kits.Cleaner.Game-Item-Lore"),
  KITS_CLEANER_CLEANED_MAP("Kits.Cleaner.Cleaned-Map"),
  KITS_CLEANER_NOTHING_TO_CLEAN("Kits.Cleaner.Nothing-To-Clean"),

  KITS_ZOMBIE_TELEPORTER_NAME("Kits.Zombie-Teleporter.Kit-Name"),
  KITS_ZOMBIE_TELEPORTER_DESCRIPTION("Kits.Zombie-Teleporter.Kit-Description"),
  KITS_ZOMBIE_TELEPORTER_GAME_ITEM_NAME("Kits.Zombie-Teleporter.Game-Item-Name"),
  KITS_ZOMBIE_TELEPORTER_GAME_ITEM_LORE("Kits.Zombie-Teleporter.Game-Item-Lore"),
  KITS_ZOMBIE_TELEPORTER_ZOMBIE_TELEPORTED("Kits.Zombie-Teleporter.Zombie-Teleported"),
  KITS_ZOMBIE_TELEPORTER_NO_AVAILABLE_ZOMBIES("Kits.Zombie-Teleporter.No-Available-Zombies"),

  KITS_KNIGHT_NAME("Kits.Knight.Kit-Name"),
  KITS_KNIGHT_DESCRIPTION("Kits.Knight.Kit-Description"),

  KITS_LIGHT_TANK_NAME("Kits.Light-Tank.Kit-Name"),
  KITS_LIGHT_TANK_DESCRIPTION("Kits.Light-Tank.Kit-Description"),

  KITS_ARCHER_NAME("Kits.Archer.Kit-Name"),
  KITS_ARCHER_DESCRIPTION("Kits.Archer.Kit-Description"),

  KITS_PUNCHER_NAME("Kits.Puncher.Kit-Name"),
  KITS_PUNCHER_DESCRIPTION("Kits.Puncher.Kit-Description"),

  KITS_HEALER_NAME("Kits.Healer.Kit-Name"),
  KITS_HEALER_DESCRIPTION("Kits.Healer.Kit-Description"),

  KITS_LOOTER_NAME("Kits.Looter.Kit-Name"),
  KITS_LOOTER_DESCRIPTION("Kits.Looter.Kit-Description"),

  KITS_RUNNER_NAME("Kits.Runner.Kit-Name"),
  KITS_RUNNER_DESCRIPTION("Kits.Runner.Kit-Description"),

  KITS_MEDIUM_TANK_NAME("Kits.Medium-Tank.Kit-Name"),
  KITS_MEDIUM_TANK_DESCRIPTION("Kits.Medium-Tank.Kit-Description"),

  KITS_WORKER_NAME("Kits.Worker.Kit-Name"),
  KITS_WORKER_DESCRIPTION("Kits.Worker.Kit-Description"),
  KITS_WORKER_GAME_ITEM_PLACE_MESSAGE("Kits.Worker.Game-Item-Place-Message"),

  KITS_DOG_FRIEND_NAME("Kits.Dog-Friend.Kit-Name"),
  KITS_DOG_FRIEND_DESCRIPTION("Kits.Dog-Friend.Kit-Description"),

  KITS_HARDCORE_NAME("Kits.Hardcore.Kit-Name"),
  KITS_HARDCORE_DESCRIPTION("Kits.Hardcore.Kit-Description"),

  KITS_GOLEM_FRIEND_NAME("Kits.Golem-Friend.Kit-Name"),
  KITS_GOLEM_FRIEND_DESCRIPTION("Kits.Golem-Friend.Kit-Description"),

  KITS_TORNADO_NAME("Kits.Tornado.Kit-Name"),
  KITS_TORNADO_DESCRIPTION("Kits.Tornado.Kit-Description"),
  KITS_TORNADO_GAME_ITEM_NAME("Kits.Tornado.Game-Item-Name"),
  KITS_TORNADO_GAME_ITEM_LORE("Kits.Tornado.Game-Item-Lore"),

  KITS_TERMINATOR_NAME("Kits.Terminator.Kit-Name"),
  KITS_TERMINATOR_DESCRIPTION("Kits.Terminator.Kit-Description"),

  KITS_TELEPORTER_NAME("Kits.Teleporter.Kit-Name"),
  KITS_TELEPORTER_DESCRIPTION("Kits.Teleporter.Kit-Description"),
  KITS_TELEPORTER_GAME_ITEM_NAME("Kits.Teleporter.Game-Item-Name"),
  KITS_TELEPORTER_GAME_ITEM_LORE("Kits.Teleporter.Game-Item-Lore"),
  KITS_TELEPORTER_GAME_ITEM_MENU_NAME("Kits.Teleporter.Game-Item-Menu-Name"),
  KITS_TELEPORTER_TELEPORTED_TO_VILLAGER("Kits.Teleporter.Teleported-To-Villager"),
  KITS_TELEPORTER_VILLAGER_WARNING("Kits.Teleporter.Villager-Warning"),
  KITS_TELEPORTER_TELEPORTED_TO_PLAYER("Kits.Teleporter.Teleported-To-Player"),

  KITS_HEAVY_TANK_NAME("Kits.Heavy-Tank.Kit-Name"),
  KITS_HEAVY_TANK_DESCRIPTION("Kits.Heavy-Tank.Kit-Description"),

  KITS_SHOT_BOW_NAME("Kits.Shot-Bow.Kit-Name"),
  KITS_SHOT_BOW_DESCRIPTION("Kits.Shot-Bow.Kit-Description"),

  KITS_BLOCKER_NAME("Kits.Blocker.Kit-Name"),
  KITS_BLOCKER_DESCRIPTION("Kits.Blocker.Kit-Description"),
  KITS_BLOCKER_GAME_ITEM_NAME("Kits.Blocker.Game-Item-Name"),
  KITS_BLOCKER_GAME_ITEM_LORE("Kits.Blocker.Game-Item-Lore"),
  KITS_BLOCKER_GAME_ITEM_PLACE_MESSAGE("Kits.Blocker.Game-Item-Place-Message"),
  KITS_BLOCKER_GAME_ITEM_PLACE_FAIL("Kits.Blocker.Game-Item-Place-Fail"),

  KITS_PREMIUM_HARDCORE_NAME("Kits.Premium-Hardcore.Kit-Name"),
  KITS_PREMIUM_HARDCORE_DESCRIPTION("Kits.Premium-Hardcore.Kit-Description"),

  KITS_MEDIC_NAME("Kits.Medic.Kit-Name"),
  KITS_MEDIC_DESCRIPTION("Kits.Medic.Kit-Description"),

  KITS_WILD_NAKED_NAME("Kits.Wild-Naked.Kit-Name"),
  KITS_WILD_NAKED_DESCRIPTION("Kits.Wild-Naked.Kit-Description"),
  KITS_WILD_NAKED_CANNOT_WEAR_ARMOR("Kits.Wild-Naked.Cannot-Wear-Armor"),

  KITS_WIZARD_NAME("Kits.Wizard.Kit-Name"),
  KITS_WIZARD_DESCRIPTION("Kits.Wizard.Kit-Description"),
  KITS_WIZARD_STAFF_ITEM_NAME("Kits.Wizard.Staff-Item-Name"),
  KITS_WIZARD_STAFF_ITEM_LORE("Kits.Wizard.Staff-Item-Lore"),
  KITS_WIZARD_ESSENCE_ITEM_NAME("Kits.Wizard.Essence-Item-Name"),
  KITS_WIZARD_ESSENCE_ITEM_LORE("Kits.Wizard.Essence-Item-Lore"),

  VILLAGER_NAMES("In-Game.Villager-Names"),
  PLUGIN_PREFIX("In-Game.Plugin-Prefix"),
  ALREADY_PLAYING("In-Game.Already-Playing"),
  JOIN_NO_PERMISSION("In-Game.Join-No-Permission"),
  FULL_GAME_NO_PERMISSION("In-Game.Full-Game-No-Permission"),
  NO_SLOTS_FOR_PREMIUM("In-Game.No-Slots-For-Premium"),
  DEAD_TAG_ON_DEATH("In-Game.Dead-Tag-On-Death"),
  GAME_CHAT_FORMAT("In-Game.Game-Chat-Format"),
  JOIN_AS_PARTY_MEMBER("In-Game.Join-As-Party-Member"),
  NOT_ENOUGH_SPACE_FOR_PARTY("In-Game.Messages.Lobby-Messages.Not-Enough-Space-For-Party"),
  DEATH_SCREEN("In-Game.Death-Screen"),
  DIED_RESPAWN_IN_NEXT_WAVE("In-Game.Died-Respawn-In-Next-Wave"),
  BACK_IN_GAME("In-Game.Back-In-Game"),
  YOU_ARE_SPECTATOR("In-Game.You-Are-Spectator"),
  YOU_LEVELED_UP("In-Game.You-Leveled-Up"),
  ORBS_PICKUP("In-Game.Orbs-Pickup"),
  ROTTEN_FLESH_LEVEL_UP("In-Game.Rotten-Flesh-Level-Up"),
  ONLY_COMMAND_IN_GAME_IS_LEAVE("In-Game.Only-Command-Ingame-Is-Leave"),
  SPAWNED_WOLF_NAME("In-Game.Spawned-Wolf-Name"),
  WOLF_DIED("In-Game.Spawned-Wolf-Death"),
  SPAWNED_GOLEM_NAME("In-Game.Spawned-Golem-Name"),
  JOIN_CANCELLED_VIA_API("In-Game.Join-Cancelled-Via-API"),
  ARENA_NOT_CONFIGURED("In-Game.Arena-Not-Configured"),
  JOIN("In-Game.Messages.Join"),
  LEAVE("In-Game.Messages.Leave"),
  DEATH("In-Game.Messages.Death"),
  NEXT_WAVE_IN("In-Game.Messages.Next-Wave-In"),
  WAVE_STARTED("In-Game.Messages.Wave-Started"),
  WAVE_TITLE_START_TIMES("In-Game.Messages.Wave-Title.Start.Times"),
  WAVE_TITLE_START_TITLE("In-Game.Messages.Wave-Title.Start.Title"),
  WAVE_TITLE_START_SUBTITLE("In-Game.Messages.Wave-Title.Start.SubTitle"),
  WAVE_TITLE_END_TIMES("In-Game.Messages.Wave-Title.End.Times"),
  WAVE_TITLE_END_TITLE("In-Game.Messages.Wave-Title.End.Title"),
  WAVE_TITLE_END_SUBTITLE("In-Game.Messages.Wave-Title.End.SubTitle"),
  VILLAGER_DIED("In-Game.Messages.Villager-Died"),
  YOU_FEEL_REFRESHED("In-Game.Messages.You-Feel-Refreshed"),
  CANT_RIDE_OTHERS_GOLEM("In-Game.Messages.Cant-Ride-Others-Golem"),
  GOLEM_SPAWNED("In-Game.Messages.Golem-Spawned"),
  WOLF_SPAWNED("In-Game.Messages.Wolf-Spawned"),
  ZOMBIE_GOT_STUCK_IN_THE_MAP("In-Game.Messages.Zombie-Got-Stuck-In-The-Map"),

  SPECTATOR_MENU_NAME("In-Game.Spectator.Spectator-Menu-Name"),
  SPECTATOR_TARGET_PLAYER_HEALTH("In-Game.Spectator.Target-Player-Health"),
  SPECTATOR_SETTINGS_MENU_INVENTORY_NAME("In-Game.Spectator.Settings-Menu.Inventory-Name"),
  SPECTATOR_SETTINGS_MENU_SPEED_NAME("In-Game.Spectator.Settings-Menu.Speed-Name"),
  SPECTATOR_WARNING("In-Game.Spectator.Spectator-Warning"),

  LOBBY_MESSAGES_START_IN("In-Game.Messages.Lobby-Messages.Start-In"),
  LOBBY_MESSAGES_WAITING_FOR_PLAYERS("In-Game.Messages.Lobby-Messages.Waiting-For-Players"),
  LOBBY_MESSAGES_ENOUGH_PLAYERS_TO_START("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"),
  LOBBY_MESSAGES_GAME_STARTED("In-Game.Messages.Lobby-Messages.Game-Started"),
  LOBBY_MESSAGES_KICKED_FOR_PREMIUM_SLOT("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"),
  LOBBY_MESSAGES_YOU_WERE_KICKED_FOR_PREMIUM_SLOT("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"),

  SHOP_MESSAGES_SHOP_GUI_NAME("In-Game.Messages.Shop-Messages.Shop-GUI-Name"),
  SHOP_MESSAGES_GOLEM_ITEM_NAME("In-Game.Messages.Shop-Messages.Golem-Item-Name"),
  SHOP_MESSAGES_WOLF_ITEM_NAME("In-Game.Messages.Shop-Messages.Wolf-Item-Name"),
  SHOP_MESSAGES_MOB_LIMIT_REACHED("In-Game.Messages.Shop-Messages.Mob-Limit-Reached"),
  SHOP_MESSAGES_NOT_ENOUGH_ORBS("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"),
  SHOP_MESSAGES_CURRENCY_IN_SHOP("In-Game.Messages.Shop-Messages.Currency-In-Shop"),
  SHOP_MESSAGES_NO_SHOP_DEFINED("In-Game.Messages.Shop-Messages.No-Shop-Defined"),

  END_MESSAGES_SUMMARY_PLAYERS_DIED("In-Game.Messages.Game-End-Messages.Summary-Players-Died"),
  END_MESSAGES_SUMMARY_VILLAGERS_DIED("In-Game.Messages.Game-End-Messages.Summary-Villagers-Died"),
  END_MESSAGES_SUMMARY_WIN_GAME("In-Game.Messages.Game-End-Messages.Summary-Win-Game"),

  ADMIN_MESSAGES_SET_STARTING_IN_TO_0("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"),
  ADMIN_MESSAGES_REMOVED_VILLAGERS("In-Game.Messages.Admin-Messages.Removed-Villagers"),
  ADMIN_MESSAGES_REMOVED_GOLEMS("In-Game.Messages.Admin-Messages.Removed-Golems"),
  ADMIN_MESSAGES_REMOVED_ZOMBIES("In-Game.Messages.Admin-Messages.Removed-Zombies"),
  ADMIN_MESSAGES_REMOVED_WOLVES("In-Game.Messages.Admin-Messages.Removed-Wolves"),
  ADMIN_MESSAGES_CHANGED_WAVE("In-Game.Messages.Admin-Messages.Changed-Wave"),

  POWERUPS_MAP_CLEAN_NAME("Powerups.Map-Clean-Powerup.Name"),
  POWERUPS_MAP_CLEAN_DESCRIPTION("Powerups.Map-Clean-Powerup.Description"),

  POWERUPS_DOUBLE_DAMAGE_NAME("Powerups.Double-Damage-Powerup.Name"),
  POWERUPS_DOUBLE_DAMAGE_DESCRIPTION("Powerups.Double-Damage-Powerup.Description"),

  POWERUPS_HEALING_NAME("Powerups.Healing-Powerup.Name"),
  POWERUPS_HEALING_DESCRIPTION("Powerups.Healing-Powerup.Description"),

  POWERUPS_GOLEM_RAID_NAME("Powerups.Golem-Raid-Powerup.Name"),
  POWERUPS_GOLEM_RAID_DESCRIPTION("Powerups.Golem-Raid-Powerup.Description"),

  POWERUPS_ONE_SHOT_ONE_KILL_NAME("Powerups.One-Shot-One-Kill-Powerup.Name"),
  POWERUPS_ONE_SHOT_ONE_KILL_DESCRIPTION("Powerups.One-Shot-One-Kill-Powerup.Description"),

  POWERUPS_POWERUP_ENDED_TITLE_MESSAGE("Powerups.Powerup-Ended-Title-Message"),

  UPGRADES_MENU_TITLE("Upgrade-Menu.Title"),

  UPGRADES_STATS_ITEM_NAME("Upgrade-Menu.Stats-Item.Name"),
  UPGRADES_STATS_ITEM_DESCRIPTION("Upgrade-Menu.Stats-Item.Description"),

  UPGRADES_HEALTH("Upgrade-Menu.Upgrades.Health"),
  UPGRADES_HEALTH_NAME("Upgrade-Menu.Upgrades.Health.Name"),
  UPGRADES_HEALTH_DESCRIPTION("Upgrade-Menu.Upgrades.Health.Description"),

  UPGRADES_DAMAGE("Upgrade-Menu.Upgrades.Damage"),
  UPGRADES_DAMAGE_NAME("Upgrade-Menu.Upgrades.Damage.Name"),
  UPGRADES_DAMAGE_DESCRIPTION("Upgrade-Menu.Upgrades.Damage.Description"),

  UPGRADES_SPEED("Upgrade-Menu.Upgrades.Speed"),
  UPGRADES_SPEED_NAME("Upgrade-Menu.Upgrades.Speed.Name"),
  UPGRADES_SPEED_DESCRIPTION("Upgrade-Menu.Upgrades.Speed.Description"),

  UPGRADES_SWARM_AWARENESS("Upgrade-Menu.Upgrades.Swarm-Awareness"),
  UPGRADES_SWARM_AWARENESS_NAME("Upgrade-Menu.Upgrades.Swarm-Awareness.Name"),
  UPGRADES_SWARM_AWARENESS_DESCRIPTION("Upgrade-Menu.Upgrades.Swarm-Awareness.Description"),

  UPGRADES_FINAL_DEFENSE("Upgrade-Menu.Upgrades.Final-Defense"),
  UPGRADES_FINAL_DEFENSE_NAME("Upgrade-Menu.Upgrades.Final-Defense.Name"),
  UPGRADES_FINAL_DEFENSE_DESCRIPTION("Upgrade-Menu.Upgrades.Final-Defense.Description"),

  UPGRADES_UPGRADED_ENTITY("Upgrade-Menu.Upgraded-Entity"),
  UPGRADES_CANNOT_AFFORD("Upgrade-Menu.Cannot-Afford"),
  UPGRADES_MAX_TIER("Upgrade-Menu.Max-Tier"),


  SIGNS_PLEASE_TYPE_ARENA_NAME("Signs.Please-Type-Arena-Name"),
  SIGNS_ARENA_DOESNT_EXISTS("Signs.Arena-Doesnt-Exists"),
  SIGNS_SIGN_CREATED("Signs.Sign-Created"),
  SIGNS_SIGN_REMOVED("Signs.Sign-Removed"),
  SIGNS_GAME_STATES_INACTIVE("Signs.Game-States.Inactive"),
  SIGNS_GAME_STATES_IN_GAME("Signs.Game-States.In-Game"),
  SIGNS_GAME_STATES_STARTING("Signs.Game-States.Starting"),
  SIGNS_GAME_STATES_FULL_GAME("Signs.Game-States.Full-Game"),
  SIGNS_GAME_STATES_ENDING("Signs.Game-States.Ending"),
  SIGNS_GAME_STATES_RESTARTING("Signs.Game-States.Restarting"),

  ARENA_SELECTOR_INV_TITLE("Arena-Selector.Inv-Title"),
  ARENA_SELECTOR_ITEM_LORE("Arena-Selector.Item.Lore"),

  VALIDATOR_INVALID_ARENA_CONFIGURATION("Validator.Invalid-Arena-Configuration"),
  VALIDATOR_INSTANCE_STARTED("Validator.Instance-Started"),
  VALIDATOR_NO_INSTANCES_CREATED("Validator.No-Instances-Created"),

  HOLOGRAMS_HEADER("Leaderboard-Holograms.Header"),
  HOLOGRAMS_FORMAT("Leaderboard-Holograms.Format"),
  HOLOGRAMS_FORMAT_EMPTY("Leaderboard-Holograms.Format-Empty"),
  HOLOGRAMS_UNKNOWN_PLAYER("Leaderboard-Holograms.Unknown-Player"),
  STATISTIC_ORBS("Leaderboard-Holograms.Statistics.Orbs"),
  STATISTIC_KILLS("Leaderboard-Holograms.Statistics.Kills"),
  STATISTIC_DEATHS("Leaderboard-Holograms.Statistics.Deaths"),
  STATISTIC_GAMES_PLAYED("Leaderboard-Holograms.Statistics.Games-Played"),
  STATISTIC_HIGHEST_WAVE("Leaderboard-Holograms.Statistics.Highest-Wave"),
  STATISTIC_LEVEL("Leaderboard-Holograms.Statistics.Level"),
  STATISTIC_EXP("Leaderboard-Holograms.Statistics.Xp");

  private static Main plugin;
  private final String accessor;

  Messages(String accessor) {
    this.accessor = accessor;
  }

  public static void init(Main plugin) {
    Messages.plugin = plugin;
  }

  public String getAccessor() {
    return accessor;
  }

  public String getMessage() {
    return plugin.getChatManager().colorMessage(this);
  }

}
