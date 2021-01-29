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
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;

import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class LeaveArgument {

  public LeaveArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefense", new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command", false)) {
          Player player = (Player) sender;
          if(!Utils.checkIsInGameInstance((Player) sender)) {
            return;
          }
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TELEPORTED_TO_THE_LOBBY));
          Arena arena = ArenaRegistry.getArena(player);
          assert arena != null;
          if(registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            registry.getPlugin().getBungeeManager().connectToHub(player);
            Debugger.debug(Level.INFO, "{0} has left the arena {1}! Teleported to the Hub server.", player.getName(), arena.getId());
          } else {
            arena.teleportToEndLocation(player);
            ArenaManager.leaveAttempt(player, arena);
            Debugger.debug(Level.INFO, "{0} has left the arena {1}! Teleported to end location.", player.getName(), arena.getId());
          }
        }
      }
    });
  }
}
