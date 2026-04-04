/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import plugily.projects.villagedefense.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class MessageInitializer {
  private final Main plugin;

  public MessageInitializer(Main plugin) {
    this.plugin = plugin;
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
    getMessageManager().registerMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_CANT_RIDE_OTHER", new Message("In-Game.Messages.Village.Entities.Cant.Ride-Other", ""));

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
    getMessageManager().registerMessage("UPGRADE_MENU_CANNOT_AFFORD", new Message("Upgrade-Menu.Cannot.Afford", ""));
    getMessageManager().registerMessage("UPGRADE_MENU_CANNOT_UPGRADE_OTHER", new Message("Upgrade-Menu.Cannot.Upgrade-Other", ""));
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

    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_CLEANED_MAP", new Message("Kit.Content.Cleaner.Cleaned.Map", ""));
    getMessageManager().registerMessage("KIT_CONTENT_CLEANER_CLEANED_NOTHING", new Message("Kit.Content.Cleaner.Cleaned.Nothing", ""));

    //ZOMBIE_TELEPORTER KIT
    getMessageManager().registerMessage("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_ZOMBIE", new Message("Kit.Content.Teleport.Zombie", ""));

    //WORKER

    getMessageManager().registerMessage("KIT_CONTENT_WORKER_GAME_ITEM_CHAT", new Message("Kit.Content.Worker.Game-Item.Chat", ""));

    //TELEPORTER

    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_GAME_ITEM_GUI", new Message("Kit.Content.Teleporter.GUI", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_VILLAGER", new Message("Kit.Content.Teleporter.Teleport.Villager", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TELEPORTER_TELEPORT_PLAYER", new Message("Kit.Content.Teleporter.Teleport.Player", ""));

    //BLOCKER

    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_PLACE_SUCCESS", new Message("Kit.Content.Blocker.Place.Success", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BLOCKER_PLACE_FAIL", new Message("Kit.Content.Blocker.Place.Fail", ""));

  }

  private MessageManager getMessageManager() {
    return plugin.getMessageManager();
  }

}
