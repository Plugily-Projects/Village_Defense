/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.boot;

import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.villagedefense.Main;

import java.util.Arrays;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class MessageInitializer {
  private final Main plugin;

  public MessageInitializer(Main plugin) {
    this.plugin = plugin;
    registerLocales();
  }

  public void registerMessages() {
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

  private void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }

  private MessageManager getMessageManager() {
    return plugin.getMessageManager();
  }

}
