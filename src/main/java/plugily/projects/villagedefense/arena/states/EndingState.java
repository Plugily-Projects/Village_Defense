/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginEndingState;
import plugily.projects.minigamesbox.classic.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public class EndingState extends PluginEndingState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    if(arena.getTimer() <= 0) {
      for(Player player : arena.getPlayers()) {
        User user = getPlugin().getUserManager().getUser(player);
        user.setStat("ORBS", 0);
      }
    }
  }
}
