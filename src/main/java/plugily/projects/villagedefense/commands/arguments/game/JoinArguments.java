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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class JoinArguments {

  private final Random random = new Random();

  public JoinArguments(ArgumentsRegistry registry) {
    //join argument
    registry.mapArgument("villagedefense", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TYPE_ARENA_NAME));
          return;
        }
        if(!ArenaRegistry.getArenas().isEmpty() && args[1].equalsIgnoreCase("maxplayers") && ArenaRegistry.getArena("maxplayers") == null) {
          if(ArenaRegistry.getArenaPlayersOnline() == 0) {
            ArenaManager.joinAttempt((Player) sender, ArenaRegistry.getArenas().get(random.nextInt(ArenaRegistry.getArenas().size())));
            return;
          }

          Map<Arena, Integer> arenas = new HashMap<>();
          for(Arena arena : ArenaRegistry.getArenas()) {
            arenas.put(arena, arena.getPlayers().size());
          }
          arenas.entrySet()
              .stream()
              .max(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .map(Map.Entry::getKey)
              .ifPresent(arena -> ArenaManager.joinAttempt((Player) sender, arena));
          return;
        }
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(args[1].equalsIgnoreCase(arena.getId())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_ARENA_LIKE_THAT));
      }
    });

    //random join argument, disable for bungee
    if(!registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      registry.mapArgument("villagedefense", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {

        @Override
        public void execute(CommandSender sender, String[] args) {
          //check starting arenas -> random
          List<Arena> arenas = ArenaRegistry.getArenas().stream().filter(arena -> arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(!arenas.isEmpty()) {
            ArenaManager.joinAttempt((Player) sender, arenas.get(random.nextInt(arenas.size())));
            return;
          }
          //check waiting arenas -> random
          arenas = ArenaRegistry.getArenas().stream().filter(arena -> (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
              && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
          if(!arenas.isEmpty()) {
            ArenaManager.joinAttempt((Player) sender, arenas.get(random.nextInt(arenas.size())));
            return;
          }
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_FREE_ARENAS));
        }
      });
    }
  }
}
