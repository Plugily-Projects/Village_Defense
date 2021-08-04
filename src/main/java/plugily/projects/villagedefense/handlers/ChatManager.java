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

package plugily.projects.villagedefense.handlers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.misc.MiscUtils;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.handlers.language.Messages;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  private final String pluginPrefix;
  private final Main plugin;

  public ChatManager(Main plugin) {
    this.plugin = plugin;
    this.pluginPrefix = colorMessage(Messages.PLUGIN_PREFIX);
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return pluginPrefix;
  }

  public String colorMessage(Messages message) {
    return colorRawMessage(LanguageManager.getLanguageMessage(message.getAccessor()));
  }

  public String colorRawMessage(String message) {
    if(message == null) {
      return "";
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && message.contains("#")) {
      message = MiscUtils.matchColorRegex(message);
    }

    return ChatColor.translateAlternateColorCodes('&', message);
  }

  /**
   * Broadcasts constant message to all players in arena
   * Includes game prefix!
   *
   * @param arena   arena to get players from
   * @param message constant message to broadcast
   */
  public void broadcast(Arena arena, Messages message) {
    if (message != null && !message.getMessage().isEmpty()) {
      for(Player p : arena.getPlayers()) {
        p.sendMessage(pluginPrefix + message.getMessage());
      }
    }
  }

  /**
   * Broadcasts message to all players in arena
   * Includes game prefix!
   *
   * @param arena   arena to get players from
   * @param message message to broadcast
   */
  public void broadcastMessage(Arena arena, String message) {
    if (message != null && !message.isEmpty()) {
      for(Player p : arena.getPlayers()) {
        p.sendMessage(pluginPrefix + message);
      }
    }
  }

  public String formatMessage(Arena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%NUMBER%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return returnString;
  }

  private String formatPlaceholders(String message, Arena arena) {
    int timer = arena.getTimer();

    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(timer));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS(timer));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public void broadcastAction(Arena arena, Player player, ActionType action) {
    Messages message;
    switch(action) {
      case JOIN:
        message = Messages.JOIN;
        break;
      case LEAVE:
        message = Messages.LEAVE;
        break;
      case DEATH:
        message = Messages.DEATH;
        break;
      default:
        return; //likely won't ever happen
    }
    broadcastMessage(arena, formatMessage(arena, colorMessage(message), player));
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}

