/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;

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
        if (!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command", false)) {
          Player p = (Player) sender;
          if (!registry.getPlugin().getMainCommand().checkIsInGameInstance((Player) sender)) {
            return;
          }
          p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
          if (registry.getPlugin().isBungeeActivated()) {
            registry.getPlugin().getBungeeManager().connectToHub(p);
            Main.debug(Main.LogLevel.INFO, p.getName() + " was teleported to the Hub server");
          } else {
            ArenaRegistry.getArena(p).teleportToEndLocation(p);
            ArenaManager.leaveAttempt(p, ArenaRegistry.getArena(p));
            Main.debug(Main.LogLevel.INFO, p.getName() + " has left the arena! He is teleported to the end location.");
          }
        }
      }
    });
  }

}