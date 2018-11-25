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

package pl.plajer.villagedefense.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class JoinArguments {

  public JoinArguments(ArgumentsRegistry registry) {
    //join argument
    registry.mapArgument("villagedefense", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        if (ArenaRegistry.isInArena(((Player) sender))) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
          return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (args[1].equalsIgnoreCase(arena.getID())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      }
    });

    //random join argument, register only for multi arena
    if (!registry.getPlugin().isBungeeActivated()) {
      registry.mapArgument("villagedefense", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {
        @Override
        public void execute(CommandSender sender, String[] args) {
          if (ArenaRegistry.isInArena(((Player) sender))) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
            return;
          }
          for (Arena arena : ArenaRegistry.getArenas()) {
            if (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
              ArenaManager.joinAttempt((Player) sender, arena);
              return;
            }
          }
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Free-Arenas"));
        }
      });
    }
  }

}
