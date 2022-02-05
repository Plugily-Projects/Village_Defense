/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;

import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class AddOrbsArgument {

  //could be removed as adjuststatistic argument would also handle it
  public AddOrbsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("addorbs", Arrays.asList("villagedefense.admin.addorbs", "villagedefense.admin.addorbs.others"),
        CommandArgument.ExecutorType.BOTH, new LabelData("/vda addorbs &6<amount> &c[player]",
        "/vda addorbs <amount>", "&7Add orbs (game currency) to yourself or target player\n&7Can be used from console too\n"
        + "&6Permission: &7villagedefense.admin.addorbs (for yourself)\n&6Permission: &7villagedefense.admin.addorbs.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder(ChatColor.RED + "Please type number of orbs to give!").prefix().send(sender);
          return;
        }

        Player target = null;
        if(args.length == 3) {
          if(!sender.hasPermission("villagedefense.admin.addorbs.others")) {
            return;
          }

          if((target = Bukkit.getPlayerExact(args[2])) == null || !registry.getPlugin().getArenaRegistry().isInArena(target)) {
            new MessageBuilder("COMMANDS_PLAYER_NOT_FOUND").asKey().prefix().send(sender);
            return;
          }
        } else if(sender instanceof Player) {
          target = (Player) sender;
        }

        if(target == null)
          return;

        java.util.Optional<Integer> opt = NumberUtils.parseInt(args[1]);

        if(opt.isPresent()) {
          User user = registry.getPlugin().getUserManager().getUser(target);
          user.setStatistic(registry.getPlugin().getStatsStorage().getStatisticType("ORBS"), user.getStatistic("ORBS") + opt.get());
          new MessageBuilder("COMMANDS_ADMIN_ADDED_ORBS").asKey().prefix().send(sender);
          new MessageBuilder("COMMANDS_ADMIN_RECEIVED_ORBS").asKey().prefix().integer(opt.get()).send(target);
        } else {
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().prefix().value("/vda addorbs <amount> (player)").send(sender);
        }
      }
    });
  }

}
