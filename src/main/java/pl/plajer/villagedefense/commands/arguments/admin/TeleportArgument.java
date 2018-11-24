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

package pl.plajer.villagedefense.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class TeleportArgument {

  public TeleportArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new CommandArgument("tp", "villagedefense.admin.teleport", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        if (args.length == 2) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type location type: END, START, LOBBY");
          return;
        }
        Player player = (Player) sender;
        try {
          LocationType.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Location-Teleport-Invalid"));
          return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (arena.getID().equalsIgnoreCase(args[1])) {
            teleport(player, arena, LocationType.valueOf(args[2].toUpperCase()));
          }
        }
      }
    });
  }

  private void teleport(Player player, Arena arena, LocationType locationType) {
    switch (locationType) {
      case LOBBY:
        if (arena.getLobbyLocation() == null) {
          player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
          return;
        }
        arena.teleportToLobby(player);
        player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena " + arena.getID());
        break;
      case START:
        if (arena.getLobbyLocation() == null) {
          player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
          return;
        }
        arena.teleportToStartLocation(player);
        player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena " + arena.getID());
        break;
      case END:
        if (arena.getLobbyLocation() == null) {
          player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
          return;
        }
        arena.teleportToEndLocation(player);
        player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena " + arena.getID());
        break;
      default:
        break; //o.o
    }
  }

  public enum LocationType {
    LOBBY, END, START
  }

}