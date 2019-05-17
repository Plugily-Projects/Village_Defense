/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.commands.arguments.admin.arena;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class DeleteArgument {

  private Set<CommandSender> confirmations = new HashSet<>();

  public DeleteArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("delete", "villagedefense.admin.delete", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda delete &6<arena>", "/vda delete <arena>",
            "&7Deletes specified arena\n&6Permission: &7villagedefense.admin.delete")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TYPE_ARENA_NAME));
          return;
        }
        Arena arena = ArenaRegistry.getArena(args[1]);
        if (arena == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_NO_ARENA_LIKE_THAT));
          return;
        }
        if (!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20 * 10);
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix()
              + registry.getPlugin().getChatManager().colorRawMessage("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!"));
          return;
        }
        confirmations.remove(sender);
        ArenaManager.stopGame(false, arena);
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        config.set("instances." + args[1], null);
        ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
        ArenaRegistry.unregisterArena(arena);
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_REMOVED_GAME_INSTANCE));
      }
    });
  }

}
