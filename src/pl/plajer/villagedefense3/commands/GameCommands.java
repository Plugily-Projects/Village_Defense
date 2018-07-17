/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.commands;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaManager;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;

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
    if (checkSenderIsConsole(sender)) {
      return;
    }
    User user = UserManager.getUser(((Player) sender).getUniqueId());
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills") + user.getInt("kills"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths") + user.getInt("deaths"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getInt("gamesplayed"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Wave") + user.getInt("highestwave"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Level") + user.getInt("level"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Exp") + user.getInt("xp"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getInt("level"), 1.5)));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void sendStatsOther(CommandSender sender, String p) {
    Player player = Bukkit.getPlayerExact(p);
    if (player == null || UserManager.getUser(player.getUniqueId()) == null) {
      sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
      return;
    }
    User user = UserManager.getUser(player.getUniqueId());
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other").replaceAll("%player%", player.getName()));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills") + user.getInt("kills"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths") + user.getInt("deaths"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getInt("gamesplayed"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Wave") + user.getInt("highestwave"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Level") + user.getInt("level"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Exp") + user.getInt("xp"));
    sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getInt("level"), 1.5)));
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
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replaceAll("%position%", String.valueOf(i + 1))
                  .replaceAll("%name%", Bukkit.getOfflinePlayer(current).getName())
                  .replaceAll("%value%", String.valueOf(stats.get(current)))
                  .replaceAll("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replaceAll("_", " ")))); //Games_played > Games played etc
          stats.remove(current);
        } catch (IndexOutOfBoundsException ex) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replaceAll("%position%", String.valueOf(i + 1))
                  .replaceAll("%name%", "Empty")
                  .replaceAll("%value%", "0")
                  .replaceAll("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replaceAll("_", " "))));
        } catch (NullPointerException ex) {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replaceAll("%position%", String.valueOf(i + 1))
                  .replaceAll("%name%", "Unknown Player")
                  .replaceAll("%value%", String.valueOf(stats.get(current)))
                  .replaceAll("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replaceAll("_", " "))));
        }
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
    }
  }

  public void leaveGame(CommandSender sender) {
    if (checkSenderIsConsole(sender)) {
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
        System.out.print(p.getName() + " is teleported to the Hub Server");
      } else {
        ArenaRegistry.getArena(p).teleportToEndLocation(p);
        ArenaManager.leaveAttempt(p, ArenaRegistry.getArena(p));
        System.out.print(p.getName() + " has left the arena! He is teleported to the end location.");
      }
    }
  }

  public void joinGame(CommandSender sender, String arenaString) {
    if (checkSenderIsConsole(sender)) {
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

  public void openKitMenu(CommandSender sender) {
    if (checkSenderIsConsole(sender) || !checkIsInGameInstance((Player) sender)
            || !hasPermission(sender, "villagedefense.command.selectkit")) {
      return;
    }
    plugin.getKitManager().openKitMenu((Player) sender);
  }

}
