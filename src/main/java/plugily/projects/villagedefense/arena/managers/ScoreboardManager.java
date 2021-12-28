package plugily.projects.villagedefense.arena.managers;

import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.managers.PluginScoreboardManager;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.scoreboard.common.EntryBuilder;
import plugily.projects.minigamesbox.classic.utils.scoreboard.type.Entry;
import plugily.projects.villagedefense.arena.Arena;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.12.2021
 */
public class ScoreboardManager extends PluginScoreboardManager {
  public ScoreboardManager(PluginArena arena) {
    super(arena);
  }

  @Override
  public List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if(user.getArena().getArenaState() == ArenaState.FULL_GAME) {
      lines = user.getArena().getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content.In-Game" + (((Arena) user.getArena()).isFighting() ? "" : "-Waiting"));
    } else {
      lines = user.getArena().getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + user.getArena().getArenaState().getFormattedName());
    }
    for(String line : lines) {
      builder.next(formatScoreboardLine(line, user));
    }
    return builder.build();
  }
}
