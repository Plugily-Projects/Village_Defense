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

package plugily.projects.villagedefense.commands.arguments.admin.arena;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;

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
        if(!registry.getPlugin().getBukkitHelper().checkIsInGameInstance((Player) sender)) {
          return;
        }
        if(args.length == 1) {
          new MessageBuilder(ChatColor.RED + "Please type number of wave to set!").prefix().send(sender);
          return;
        }
        java.util.Optional<Integer> opt = NumberUtils.parseInt(args[1]);
        if(!opt.isPresent()) {
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().prefix().value("/vda setwave <number>").send(sender);
          return;
        }
        Arena arena = (Arena) registry.getPlugin().getArenaRegistry().getArena((Player) sender);
        if(arena == null) {
          return;
        }
        arena.setWave(opt.get() - 1);
        ((ArenaManager) registry.getPlugin().getArenaManager()).endWave(arena);
        new MessageBuilder("IN_GAME_MESSAGES_ADMIN_CHANGED_WAVE").asKey().arena(arena).integer(arena.getWave()).sendArena();
        ArenaUtils.removeSpawnedEnemies(arena);
        arena.setArenaOption("ZOMBIES_TO_SPAWN", 0);
      }
    });
  }

}
