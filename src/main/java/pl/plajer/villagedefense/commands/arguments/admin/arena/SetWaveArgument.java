/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SetWaveArgument {

  public SetWaveArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("setwave", "villagedefense.admin.setwave", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda setwave &6<number>", "/vda setwave <number>",
            "&7Set wave number in arena you're in\n&6Permission: &7villagedefense.admin.setwave")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        if (args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of wave to set!");
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (!Utils.isInteger(args[1])) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_INVALID_NUMBER).replace("%correct%", "/vda setwave <number>"));
          return;
        }
        arena.setWave(Integer.parseInt(args[1]) - 1);
        ArenaManager.endWave(arena);
        String message = registry.getPlugin().getChatManager().formatMessage(arena, registry.getPlugin().getChatManager().colorMessage(Messages.ADMIN_MESSAGES_CHANGED_WAVE), arena.getWave());
        for (Player player : arena.getPlayers()) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + message);
        }
        if (arena.getZombies() != null) {
          ArenaUtils.removeSpawnedZombies(arena);
          arena.getZombies().clear();
          arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
        }
      }
    });
  }

}
