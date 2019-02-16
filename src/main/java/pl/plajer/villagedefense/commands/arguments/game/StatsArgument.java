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

package pl.plajer.villagedefense.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.user.User;

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
        if (player == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.Player-Not-Found"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if (player.equals(sender)) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Header"));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", player.getName()));
        }
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Kills") + user.getStat(StatsStorage.StatisticType.KILLS));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Deaths") + user.getStat(StatsStorage.StatisticType.DEATHS));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Highest-Wave") + user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Level") + user.getStat(StatsStorage.StatisticType.LEVEL));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Exp") + user.getStat(StatsStorage.StatisticType.XP));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5)));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Footer"));
      }
    });
  }

}
