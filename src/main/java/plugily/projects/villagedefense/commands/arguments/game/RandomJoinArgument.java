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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RandomJoinArgument {

  public RandomJoinArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefense", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {

      @Override
      public void execute(CommandSender sender, String[] args) {
        //first random get method
        Map<Arena, Integer> arenas = new HashMap<>();
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()) {
            arenas.put(arena, arena.getPlayers().size());
          }
        }
        if(arenas.size() > 0) {
          Stream<Map.Entry<Arena, Integer>> sorted = arenas.entrySet().stream().sorted(Map.Entry.comparingByValue());
          Arena arena = sorted.findFirst().get().getKey();
          if(arena != null) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }

        //fallback safe method
        for(Arena arena : ArenaRegistry.getArenas()) {
          if((arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
              && arena.getPlayers().size() < arena.getMaximumPlayers()) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_FREE_ARENAS));
      }
    });
  }
}
