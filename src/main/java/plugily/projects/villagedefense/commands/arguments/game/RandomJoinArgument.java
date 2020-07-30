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
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()) {
            arenas.put(arena, arena.getPlayers().size());
          }
        }
        if (arenas.size() > 0) {
          Stream<Map.Entry<Arena, Integer>> sorted = arenas.entrySet().stream().sorted(Map.Entry.comparingByValue());
          Arena arena = sorted.findFirst().get().getKey();
          if (arena != null) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }

        //fallback safe method
        for (Arena arena : ArenaRegistry.getArenas()) {
          if ((arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
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
