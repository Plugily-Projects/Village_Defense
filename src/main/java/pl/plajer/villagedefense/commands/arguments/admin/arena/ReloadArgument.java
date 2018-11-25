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

package pl.plajer.villagedefense.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ReloadArgument {

  public ReloadArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("reload", "villagedefense.admin.reload", CommandArgument.ExecutorType.BOTH,
        new LabelData("/vda reload", "/vda reload","&7Reload all game arenas\n&7&lThey will be stopped!\n" +
            "&c&lNot recommended!\n&6Permission: &7villagedefense.admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        for (Arena arena : ArenaRegistry.getArenas()) {
          ArenaManager.stopGame(true, arena);
        }
        ArenaRegistry.registerArenas();
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Success-Reload"));
      }
    });
  }

}
