/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
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
        if (args.length == 0) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of wave to set!");
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (Utils.isInteger(args[1])) {
          arena.setWave(Integer.parseInt(args[1]) - 1);
          ArenaManager.endWave(arena);
          String message = registry.getPlugin().getChatManager().formatMessage(arena, registry.getPlugin().getChatManager().colorMessage("In-Game.Messages.Admin-Messages.Changed-Wave"), arena.getWave());
          for (Player player1 : arena.getPlayers()) {
            player1.sendMessage(registry.getPlugin().getChatManager().getPrefix() + message);
          }
          if (arena.getZombies() != null) {
            for (Zombie zombie : arena.getZombies()) {
              zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
              zombie.remove();
            }
            arena.getZombies().clear();
            arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
          } else {
            sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
          }
          Utils.playSound(((Player) sender).getLocation(), "ENTITY_ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH");
          String msg = registry.getPlugin().getChatManager().formatMessage(arena, registry.getPlugin().getChatManager().colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies"),
              (Player) sender);
          for (Player loopPlayer : arena.getPlayers()) {
            loopPlayer.sendMessage(registry.getPlugin().getChatManager().getPrefix() + msg);
          }
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Invalid-Number").replace("%correct%", "/vda setwave <number>"));
        }
      }
    });
  }

}
