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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.number.NumberUtils;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.arena.options.ArenaOption;
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
public class SetWaveArgument {

  public SetWaveArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("setwave", "villagedefense.admin.setwave", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda setwave &6<number>", "/vda setwave <number>",
            "&7Set wave number in arena you're in\n&6Permission: &7villagedefense.admin.setwave")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of wave to set!");
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(!NumberUtils.isInteger(args[1])) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_INVALID_NUMBER).replace("%correct%", "/vda setwave <number>"));
          return;
        }
        arena.setWave(Integer.parseInt(args[1]) - 1);
        ArenaManager.endWave(arena);
        String message = registry.getPlugin().getChatManager().formatMessage(arena, registry.getPlugin().getChatManager().colorMessage(Messages.ADMIN_MESSAGES_CHANGED_WAVE), arena.getWave());
        for(Player player : arena.getPlayers()) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + message);
        }
        ArenaUtils.removeSpawnedZombies(arena);
        arena.getZombies().clear();
        arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
      }
    });
  }

}
