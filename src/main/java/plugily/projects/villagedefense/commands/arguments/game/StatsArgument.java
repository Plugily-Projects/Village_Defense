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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class StatsArgument {

  public StatsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefense", new CommandArgument("stats", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = args.length == 2 ? Bukkit.getPlayerExact(args[1]) : (Player) sender;
        if(player == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TARGET_PLAYER_NOT_FOUND));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if(player == sender) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_HEADER));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_HEADER_OTHER).replace("%player%", player.getName()));
        }
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_ORBS) + user.getStat(StatsStorage.StatisticType.ORBS));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_KILLS) + user.getStat(StatsStorage.StatisticType.KILLS));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_DEATHS) + user.getStat(StatsStorage.StatisticType.DEATHS));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_GAMES_PLAYED) + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_HIGHEST_WAVE) + user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_LEVEL) + user.getStat(StatsStorage.StatisticType.LEVEL));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_EXP) + user.getStat(StatsStorage.StatisticType.XP));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_NEXT_LEVEL_EXP)
            + Math.ceil(Math.pow(50.0 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5)));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_STATISTICS_FOOTER));
      }
    });
  }

}
