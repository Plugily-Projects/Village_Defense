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

package plugily.projects.villagedefense.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ForceStartArgument {

  public ForceStartArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("forcestart", "villagedefense.admin.forcestart", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda forcestart", "/vda forcestart",
            "&7Force starts arena you're in\n&6Permission: &7villagedefense.admin.forcestart")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
          arena.setArenaState(ArenaState.STARTING);
          arena.setForceStart(true);
          arena.setTimer(0);
          registry.getPlugin().getChatManager().broadcast(arena, Messages.ADMIN_MESSAGES_SET_STARTING_IN_TO_0);
        }
      }
    });
  }

}
