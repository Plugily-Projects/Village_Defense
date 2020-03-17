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

package pl.plajer.villagedefense.handlers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajerlair.commonsbox.string.StringFormatUtils;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  private Main plugin;
  private String prefix;

  public ChatManager(Main plugin, String prefix) {
    this.plugin = plugin;
    this.prefix = prefix;
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return prefix;
  }

  public String colorRawMessage(String message) {
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
    for (Player p : arena.getPlayers()) {
      p.sendMessage(prefix + message.getMessage());
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
    for (Player p : arena.getPlayers()) {
      p.sendMessage(prefix + message);
    }
  }

  public String colorMessage(Messages message) {
      return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message.getAccessor()));
  }

  public String formatMessage(Arena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%NUMBER%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  private String formatPlaceholders(String message, Arena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public void broadcastAction(Arena a, Player p, ActionType action) {
    switch (action) {
      case JOIN:
        broadcastMessage(a, formatMessage(a, colorMessage(Messages.JOIN), p));
        break;
      case LEAVE:
        broadcastMessage(a, formatMessage(a, colorMessage(Messages.LEAVE), p));
        break;
      case DEATH:
        broadcastMessage(a, formatMessage(a, colorMessage(Messages.DEATH), p));
        break;
      default:
        break;
    }
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}

