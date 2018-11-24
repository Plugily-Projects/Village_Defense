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

package pl.plajer.villagedefense.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 11.05.2018
 */
public class TabCompletion implements TabCompleter {

  private Main plugin;

  public TabCompletion(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return null;
    }
    if (cmd.getName().equalsIgnoreCase("villagedefenseadmin") && args.length == 1) {
      return Arrays.asList("stop", "list", "forcestart", "respawn", "spychat",
          "reload", "delete", "setprice", "tp", "clear", "addorbs", "setwave");
    }
    if (cmd.getName().equalsIgnoreCase("villagedefense")) {
      if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
        List<String> arenaIds = new ArrayList<>();
        for (Arena arena : ArenaRegistry.getArenas()) {
          arenaIds.add(arena.getID());
        }
        return arenaIds;
      }
      if (args.length == 1) {
        if (plugin.isBungeeActivated()) {
          return Arrays.asList("join", "leave", "stats", "top", "create", "selectkit");
        } else {
          return Arrays.asList("join", "randomjoin", "leave", "stats", "top", "create", "selectkit");
        }
      }
    }
    return null;
  }
}