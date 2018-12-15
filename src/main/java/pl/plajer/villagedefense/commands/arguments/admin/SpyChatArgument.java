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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SpyChatArgument {

  private Set<UUID> spyChatters = new HashSet<>();

  public SpyChatArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("spychat", "villagedefense.admin.spychat", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda spychat", "/vda spychat", "&7Toggles spy chat for all available arenas\n" +
            "&7You will see all messages from these games\n&6Permission: &7villagedefense.admin.spychat")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        UUID uuid = ((Player) sender).getUniqueId();
        if (spyChatters.contains(uuid)) {
          spyChatters.remove(uuid);
        } else {
          spyChatters.add(uuid);
        }
        sender.sendMessage(ChatManager.getPrefix() + ChatColor.GREEN + "Game spy chat toggled to " + spyChatters.contains(uuid));
      }
    });
  }

  public boolean isSpyChatEnabled(Player player) {
    return spyChatters.contains(player.getUniqueId());
  }
}
