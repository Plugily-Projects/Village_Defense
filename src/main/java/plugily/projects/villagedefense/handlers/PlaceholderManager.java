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

package plugily.projects.villagedefense.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 05.05.2018
 */
public class PlaceholderManager extends PlaceholderExpansion {

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String getIdentifier() {
    return "villagedefense";
  }

  @Override
  public String getAuthor() {
    return "Plajer";
  }

  @Override
  public String getVersion() {
    return "1.0.1";
  }

  @Override
  public String onPlaceholderRequest(Player player, String id) {
    if(player == null) {
      return null;
    }
    switch(id.toLowerCase()) {
      case "kills":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.KILLS));
      case "deaths":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.DEATHS));
      case "games_played":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.GAMES_PLAYED));
      case "highest_wave":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.HIGHEST_WAVE));
      case "level":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LEVEL));
      case "exp":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.XP));
      case "exp_to_next_level":
        return String.valueOf(Math.ceil(Math.pow(50 * StatsStorage.getUserStats(player, StatsStorage.StatisticType.LEVEL), 1.5)));
      default:
        return handleArenaPlaceholderRequest(id);
    }
  }

  private String handleArenaPlaceholderRequest(String id) {
    if(!id.contains(":")) {
      return null;
    }
    String[] data = id.split(":");
    Arena arena = ArenaRegistry.getArena(data[0]);
    if(arena == null) {
      return null;
    }
    switch(data[1].toLowerCase()) {
      case "players":
        return String.valueOf(arena.getPlayers().size());
      case "max_players":
        return String.valueOf(arena.getMaximumPlayers());
      case "state":
        return String.valueOf(arena.getArenaState());
      case "state_pretty":
        return arena.getArenaState().getFormattedName();
      case "wave":
        return String.valueOf(arena.getWave());
      case "mapname":
        return arena.getMapName();
      default:
        return null;
    }
  }

}
