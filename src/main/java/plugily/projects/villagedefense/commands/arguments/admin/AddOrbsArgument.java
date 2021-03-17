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

package plugily.projects.villagedefense.commands.arguments.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.number.NumberUtils;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;

import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class AddOrbsArgument {

  public AddOrbsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("addorbs", Arrays.asList("villagedefense.admin.addorbs", "villagedefense.admin.addorbs.others"),
        CommandArgument.ExecutorType.BOTH, new LabelData("/vda addorbs &6<amount> &c[player]",
        "/vda addorbs <amount>", "&7Add orbs (game currency) to yourself or target player\n&7Can be used from console too\n"
        + "&6Permission: &7villagedefense.admin.addorbs (for yourself)\n&6Permission: &7villagedefense.admin.addorbs.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of orbs to give!");
          return;
        }

        Player target;
        if(args.length == 3) {
          if(!sender.hasPermission("villagedefense.admin.addorbs.others")) {
            return;
          }
          Player p = Bukkit.getPlayerExact(args[2]);
          if(p == null || !ArenaRegistry.isInArena(p)) {
            sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TARGET_PLAYER_NOT_FOUND));
            return;
          }
          target = p;
        } else {
          target = (Player) sender;
        }

        if(NumberUtils.isInteger(args[1])) {
          User user = registry.getPlugin().getUserManager().getUser(target);
          user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) + Integer.parseInt(args[1]));
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_ADDED_ORBS));
          target.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_RECEIVED_ORBS).replace("%orbs%", args[1]));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_WRONG_USAGE).replace("%correct%", "/vda addorbs <amount> (player)"));
        }
      }
    });
  }

}
