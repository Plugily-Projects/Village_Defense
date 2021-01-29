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

package plugily.projects.villagedefense.arena.states;

import org.bukkit.entity.Player;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.reward.Reward;
import plugily.projects.villagedefense.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public class EndingState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    arena.getScoreboardManager().stopAllScoreboards();
    if(arena.getTimer() <= 0) {
      arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_GAME_ENDED));

      for(Player player : arena.getPlayers()) {
        ArenaUtils.resetPlayerAfterGame(player);
        arena.doBarAction(Arena.BarAction.REMOVE, player);
        plugin.getUserManager().addStat(player, StatsStorage.StatisticType.GAMES_PLAYED);
      }
      arena.getPlayers().forEach(arena::teleportToEndLocation);
      plugin.getChatManager().broadcast(arena, Messages.COMMANDS_TELEPORTED_TO_THE_LOBBY);

      for(User user : plugin.getUserManager().getUsers(arena)) {
        user.setSpectator(false);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
      }
      plugin.getRewardsHandler().performReward(arena, Reward.RewardType.END_GAME);
      arena.setArenaState(ArenaState.RESTARTING);
    }
    arena.setTimer(arena.getTimer() - 1);
  }

}
