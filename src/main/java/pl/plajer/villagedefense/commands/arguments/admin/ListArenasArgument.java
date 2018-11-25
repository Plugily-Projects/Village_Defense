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

package pl.plajer.villagedefense.commands.arguments.admin;

import org.bukkit.command.CommandSender;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;

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
        sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for (Arena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getID())
              .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
              .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if (i == 0) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
          sender.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7You can get free maps with configs at our wiki! Just head to https://wiki.plajer.xyz/minecraft/villagedefense/free_maps.php"));
        }
      }
    });
  }

}
