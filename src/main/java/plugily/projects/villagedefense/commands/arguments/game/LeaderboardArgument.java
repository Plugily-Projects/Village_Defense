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

package plugily.projects.villagedefense.commands.arguments.game;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.completion.CompletableArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.data.MysqlManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class LeaderboardArgument {

  private final ArgumentsRegistry registry;

  public LeaderboardArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    List<String> stats = new ArrayList<>();
    for(StatsStorage.StatisticType val : StatsStorage.StatisticType.values()) {
      if(!val.isPersistent() || val == StatsStorage.StatisticType.XP) {
        continue;
      }
      stats.add(val.name().toLowerCase());
    }
    registry.getTabCompletion().registerCompletion(new CompletableArgument("villagedefense", "top", stats));
    registry.mapArgument("villagedefense", new CommandArgument("top", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.LEADERBOARD_TYPE_NAME));
          return;
        }
        try {
          StatsStorage.StatisticType statisticType = StatsStorage.StatisticType.valueOf(args[1].toUpperCase());
          if(statisticType == StatsStorage.StatisticType.XP) {
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.LEADERBOARD_INVALID_NAME));
            return;
          }
          printLeaderboard(sender, statisticType);
        } catch(IllegalArgumentException e) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.LEADERBOARD_INVALID_NAME));
        }
      }
    });
  }

  private void printLeaderboard(CommandSender sender, StatsStorage.StatisticType statisticType) {
    LinkedHashMap<UUID, Integer> stats = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statisticType);
    sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.LEADERBOARD_HEADER));
    String statistic = StringUtils.capitalize(statisticType.toString().toLowerCase().replace('_', ' '));
    for(int i = 0; i < 10; i++) {
      try {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        sender.sendMessage(formatMessage(statistic, Bukkit.getOfflinePlayer(current).getName(), i + 1, stats.get(current)));
        stats.remove(current);
      } catch(IndexOutOfBoundsException ex) {
        sender.sendMessage(formatMessage(statistic, "Empty", i + 1, 0));
      } catch(NullPointerException ex) {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        if(registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
          try(Connection connection = registry.getPlugin().getMysqlDatabase().getConnection();
              Statement statement = connection.createStatement();
              ResultSet set = statement.executeQuery("SELECT name FROM " + ((MysqlManager) registry.getPlugin().getUserManager().getDatabase()).getTableName() + " WHERE UUID='" + current.toString() + "'")) {
            if(set.next()) {
              sender.sendMessage(formatMessage(statistic, set.getString(1), i + 1, stats.get(current)));
              continue;
            }
          } catch(SQLException ignored) {
            //it has failed second time, cannot continue
          }
        }
        sender.sendMessage(formatMessage(statistic, "Unknown Player", i + 1, stats.get(current)));
      }
    }
  }

  private String formatMessage(String statisticName, String playerName, int position, int value) {
    String message = registry.getPlugin().getChatManager().colorMessage(Messages.LEADERBOARD_FORMAT);
    message = StringUtils.replace(message, "%position%", String.valueOf(position));
    message = StringUtils.replace(message, "%name%", playerName);
    message = StringUtils.replace(message, "%value%", String.valueOf(value));
    message = StringUtils.replace(message, "%statistic%", statisticName);
    return message;
  }

}
