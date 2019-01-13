/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;

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
import pl.plajerlair.core.utils.GameScoreboard;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ScoreboardManager {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private String boardTitle = plugin.getChatManager().colorMessage("Scoreboard.Title");
  private Arena arena;

  public ScoreboardManager(Arena arena) {
    this.arena = arena;
  }

  /**
   * Updates scoreboard to all players in arena
   */
  public void updateScoreboard() {
    if (arena.getPlayers().size() == 0 || arena.getArenaState() == ArenaState.RESTARTING) {
      return;
    }
    GameScoreboard scoreboard;
    for (Player p : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(p);
      if (arena.getArenaState() == ArenaState.ENDING) {
        user.removeScoreboard();
        return;
      }
      scoreboard = new GameScoreboard("PL_VD3", "PL_CR", boardTitle);
      List<String> lines;
      if (arena.getArenaState() == ArenaState.IN_GAME) {
        lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing" + (arena.isFighting() ? "" : "-Waiting"));
      } else {
        lines = LanguageManager.getLanguageList("Scoreboard.Content." + arena.getArenaState().getFormattedName());
      }
      for (String line : lines) {
        scoreboard.addRow(formatScoreboardLine(line, user));
      }
      scoreboard.finish();
      scoreboard.display(p);
    }
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
