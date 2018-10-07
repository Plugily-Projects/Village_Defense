/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense4.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.api.StatsStorage;
import pl.plajer.villagedefense4.arena.Arena;
import pl.plajer.villagedefense4.arena.ArenaManager;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajer.villagedefense4.arena.ArenaState;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.user.User;
import pl.plajer.villagedefense4.user.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 25.02.2018
 */
public class GameCommands extends MainCommand {

  private Main plugin;

  public GameCommands(Main plugin) {
    super(plugin, false);
    this.plugin = plugin;
  }

  public void sendStats(CommandSender sender) {
    if (!checkSenderPlayer(sender)) {
      return;
    }
    User user = UserManager.getUser(((Player) sender).getUniqueId());
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills") + user.getStat(StatsStorage.StatisticType.KILLS));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths") + user.getStat(StatsStorage.StatisticType.DEATHS));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Wave") + user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Level") + user.getStat(StatsStorage.StatisticType.LEVEL));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Exp") + user.getStat(StatsStorage.StatisticType.XP));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5)));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void sendStatsOther(CommandSender sender, String p) {
    Player player = Bukkit.getPlayerExact(p);
    if (player == null || UserManager.getUser(player.getUniqueId()) == null) {
      sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
      return;
    }
    User user = UserManager.getUser(player.getUniqueId());
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", player.getName()));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills") + user.getStat(StatsStorage.StatisticType.KILLS));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths") + user.getStat(StatsStorage.StatisticType.DEATHS));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Wave") + user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Level") + user.getStat(StatsStorage.StatisticType.LEVEL));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Exp") + user.getStat(StatsStorage.StatisticType.XP));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5)));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void sendTopStatistics(CommandSender sender, String stat) {
    try {
      StatsStorage.StatisticType statisticType = StatsStorage.StatisticType.valueOf(stat.toUpperCase());
      if (statisticType == StatsStorage.StatisticType.XP) {
        sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
        return;
      }
      LinkedHashMap<UUID, Integer> stats = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statisticType);
      sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Header"));
      for (int i = 0; i < 10; i++) {
        try {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          String message = ChatManager.colorMessage("Commands.Statistics.Format");
          message = StringUtils.replace(message, "%position%", String.valueOf(i + 1));
          message = StringUtils.replace(message, "%name%", Bukkit.getOfflinePlayer(current).getName());
          message = StringUtils.replace(message, "%value%", String.valueOf(stats.get(current)));
          message = StringUtils.replace(message, "%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))); //Games_played > Games played etc
          sender.sendMessage(message);
          stats.remove(current);
        } catch (IndexOutOfBoundsException ex) {
          String message = ChatManager.colorMessage("Commands.Statistics.Format");
          message = StringUtils.replace(message, "%position%", String.valueOf(i + 1));
          message = StringUtils.replace(message, "%name%", "Empty");
          message = StringUtils.replace(message, "%value%", "0");
          message = StringUtils.replace(message, "%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))); //Games_played > Games played etc
          sender.sendMessage(message);
        } catch (NullPointerException ex) {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          if (plugin.isDatabaseActivated()) {
            ResultSet set = plugin.getMySQLDatabase().executeQuery("SELECT name FROM playerstats WHERE UUID='" + current.toString() + "'");
            try {
              if (set.next()) {
                String message = ChatManager.colorMessage("Commands.Statistics.Format");
                message = StringUtils.replace(message, "%position%", String.valueOf(i + 1));
                message = StringUtils.replace(message, "%name%", set.getString(1));
                message = StringUtils.replace(message, "%value%", String.valueOf(stats.get(current)));
                message = StringUtils.replace(message, "%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))); //Games_played > Games played etc
                sender.sendMessage(message);
                return;
              }
            } catch (SQLException ignored) {
            }
          }
          String message = ChatManager.colorMessage("Commands.Statistics.Format");
          message = StringUtils.replace(message, "%position%", String.valueOf(i + 1));
          message = StringUtils.replace(message, "%name%", "Unknown Player");
          message = StringUtils.replace(message, "%value%", String.valueOf(stats.get(current)));
          message = StringUtils.replace(message, "%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))); //Games_played > Games played etc
          sender.sendMessage(message);
        }
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
    }
  }

  public void leaveGame(CommandSender sender) {
    if (!checkSenderPlayer(sender)) {
      return;
    }
    if (!plugin.getConfig().getBoolean("Disable-Leave-Command", false)) {
      Player p = (Player) sender;
      if (!checkIsInGameInstance((Player) sender)) {
        return;
      }
      p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
      if (plugin.isBungeeActivated()) {
        plugin.getBungeeManager().connectToHub(p);
        Main.debug(Main.LogLevel.INFO, p.getName() + " was teleported to the Hub server");
      } else {
        ArenaRegistry.getArena(p).teleportToEndLocation(p);
        ArenaManager.leaveAttempt(p, ArenaRegistry.getArena(p));
        Main.debug(Main.LogLevel.INFO, p.getName() + " has left the arena! He is teleported to the end location.");
      }
    }
  }

  public void joinGame(CommandSender sender, String arenaString) {
    if (!checkSenderPlayer(sender)) {
      return;
    }
    if (ArenaRegistry.isInArena(((Player) sender))) {
      sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
      return;
    }
    for (Arena arena : ArenaRegistry.getArenas()) {
      if (arenaString.equalsIgnoreCase(arena.getID())) {
        ArenaManager.joinAttempt((Player) sender, arena);
        return;
      }
    }
    sender.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
  }

  public void joinRandomGame(CommandSender sender) {
    if (!checkSenderPlayer(sender)) {
      return;
    }
    if (plugin.isBungeeActivated()) {
      return;
    }
    for (Arena arena : ArenaRegistry.getArenas()) {
      if (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
        ArenaManager.joinAttempt((Player) sender, arena);
        return;
      }
    }
    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Free-Arenas"));
  }

  public void openKitMenu(CommandSender sender) {
    if (!checkSenderPlayer(sender) || !checkIsInGameInstance((Player) sender)
            || !hasPermission(sender, "villagedefense.command.selectkit")) {
      return;
    }
    plugin.getKitManager().openKitMenu((Player) sender);
  }

}
