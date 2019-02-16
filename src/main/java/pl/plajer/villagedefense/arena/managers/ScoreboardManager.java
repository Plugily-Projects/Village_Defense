/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.arena.managers;

import java.util.ArrayList;
import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ScoreboardManager {

  private List<Scoreboard> scoreboards = new ArrayList<>();
  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private String boardTitle = plugin.getChatManager().colorMessage("Scoreboard.Title");
  private Arena arena;

  public ScoreboardManager(Arena arena) {
    this.arena = arena;
  }

  /**
   * Creates arena scoreboard for target user
   *
   * @param user user that represents game player
   * @see User
   */
  public void createScoreboard(User user) {
    Scoreboard scoreboard = ScoreboardLib.createScoreboard(user.getPlayer()).setHandler(new ScoreboardHandler() {
      @Override
      public String getTitle(Player player) {
        return boardTitle;
      }

      @Override
      public List<Entry> getEntries(Player player) {
        return formatScoreboard(user);
      }
    });
    scoreboard.activate();
    scoreboards.add(scoreboard);
  }

  /**
   * Removes scoreboard of user
   *
   * @param user user that represents game player
   * @see User
   */
  public void removeScoreboard(User user) {
    for (Scoreboard board : scoreboards) {
      if (board.getHolder().equals(user.getPlayer())) {
        scoreboards.remove(board);
        board.deactivate();
        return;
      }
    }
  }

  /**
   * Forces all scoreboards to deactivate.
   */
  public void stopAllScoreboards() {
    for (Scoreboard board : scoreboards) {
      board.deactivate();
    }
    scoreboards.clear();
  }

  private List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if (arena.getArenaState() == ArenaState.IN_GAME) {
      lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing" + (arena.isFighting() ? "" : "-Waiting"));
    } else {
      //apply fix
      if (arena.getArenaState() == ArenaState.ENDING) {
        lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing");
      } else {
        lines = LanguageManager.getLanguageList("Scoreboard.Content." + arena.getArenaState().getFormattedName());
      }
    }
    for (String line : lines) {
      builder.next(formatScoreboardLine(line, user));
    }
    return builder.build();
  }

  private String formatScoreboardLine(String line, User user) {
    String formattedLine = line;
    formattedLine = StringUtils.replace(formattedLine, "%TIME%", String.valueOf(arena.getTimer()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS%", String.valueOf(arena.getPlayers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%MIN_PLAYERS%", String.valueOf(arena.getMinimumPlayers()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS_LEFT%", String.valueOf(arena.getPlayersLeft().size()));
    formattedLine = StringUtils.replace(formattedLine, "%VILLAGERS%", String.valueOf(arena.getVillagers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%ORBS%", String.valueOf(user.getStat(StatsStorage.StatisticType.ORBS)));
    formattedLine = StringUtils.replace(formattedLine, "%ZOMBIES%", String.valueOf(arena.getZombiesLeft()));
    formattedLine = StringUtils.replace(formattedLine, "%ROTTEN_FLESH%", String.valueOf(arena.getOption(ArenaOption.ROTTEN_FLESH_AMOUNT)));
    formattedLine = plugin.getChatManager().colorRawMessage(formattedLine);
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      PlaceholderAPI.setPlaceholders(user.getPlayer(), formattedLine);
    }
    return formattedLine;
  }

}
