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
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ListArenasArgument {

  public ListArenasArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("list", "villagedefense.admin.list", CommandArgument.ExecutorType.BOTH,
        new LabelData("/vda list", "/vda list",
            "&7Shows list with all loaded arenas\n&6Permission: &7villagedefense.admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_LIST_HEADER));
        int i = 0;
        for(Arena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_LIST_FORMAT).replace("%arena%", arena.getId())
              .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
              .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if(i == 0) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_LIST_NO_ARENAS));
          sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&e&lTIP: &7You can get free maps with configs at our wiki! Just head to https://wiki.plugily.xyz/minecraft/villagedefense/free_maps.php"));
        }
      }
    });
  }

}
