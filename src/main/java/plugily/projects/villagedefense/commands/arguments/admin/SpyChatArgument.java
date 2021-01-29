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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SpyChatArgument {

  private final Set<Player> spyChatters = new HashSet<>();

  public SpyChatArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("spychat", "villagedefense.admin.spychat", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda spychat", "/vda spychat", "&7Toggles spy chat for all available arenas\n"
            + "&7You will see all messages from these games\n&6Permission: &7villagedefense.admin.spychat")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(isSpyChatEnabled(player)) {
          disableSpyChat(player);
        } else {
          spyChatters.add(player);
        }
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() +
            registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_SPYCHAT_TOGGLED)
                .replace("%value%", String.valueOf(isSpyChatEnabled(player))));
      }
    });
  }

  public boolean isSpyChatEnabled(Player player) {
    return spyChatters.contains(player);
  }

  public void disableSpyChat(Player player) {
    spyChatters.remove(player);
  }

}
