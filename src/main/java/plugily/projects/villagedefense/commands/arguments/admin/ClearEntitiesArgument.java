/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.commands.completion.CompletableArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;

import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ClearEntitiesArgument {

  public ClearEntitiesArgument(ArgumentsRegistry registry) {
    registry.getTabCompletion().registerCompletion(new CompletableArgument("villagedefenseadmin", "clear", Arrays.asList("zombie", "villager", "golem", "wolf")));
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("clear", "villagedefense.admin.clear", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda clear &6<zombie/villager/golem/wolf>", "/vda clear <mob>",
            "&7Clear specific mob type from arena you're in\n&7Valid mob types:\n&7• ZOMBIE - clear spawned zombies\n"
                + "&7• VILLAGER - clear alive villagers\n&7• GOLEM - clear spawned golems\n&7• WOLF - clear spawned wolves\n&6Permission: &7villagedefense.admin.clear")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!registry.getPlugin().getBukkitHelper().checkIsInGameInstance((Player) sender)) {
          return;
        }
        if(args.length == 1) {
          new MessageBuilder(ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, IRON_GOLEM").prefix().send(sender);
          return;
        }
        Arena arena = (Arena) registry.getPlugin().getArenaRegistry().getArena((Player) sender);
        String clearMessage;
        switch(args[1].toLowerCase()) {
          case "villager":
            if(arena.getVillagers().isEmpty()) {
              new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().send(sender);
              return;
            }
            for(Villager villager : arena.getVillagers()) {
              VersionUtils.sendParticles("LAVA", arena.getPlayers(), villager.getLocation(), 20);
              villager.remove();
            }
            arena.getVillagers().clear();
            VersionUtils.playSound(((Player) sender).getLocation(), "ENTITY_VILLAGER_DEATH");
            clearMessage = new MessageBuilder("IN_GAME_MESSAGES_ADMIN_REMOVED_VILLAGERS").asKey().build();
            break;
          case "zombie":
            if(arena.getEnemies().isEmpty()) {
              new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().send(sender);
              return;
            }
            ArenaUtils.removeSpawnedEnemies(arena);
            arena.setArenaOption("ZOMBIES_TO_SPAWN", 0);
            VersionUtils.playSound(((Player) sender).getLocation(), "ENTITY_ZOMBIE_DEATH");
            clearMessage = new MessageBuilder("IN_GAME_MESSAGES_ADMIN_REMOVED_ZOMBIES").asKey().build();
            break;
          case "golem":
            if(arena.getIronGolems().isEmpty()) {
              new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().send(sender);
              return;
            }
            for(IronGolem golem : arena.getIronGolems()) {
              VersionUtils.sendParticles("LAVA", arena.getPlayers(), golem.getLocation(), 20);
              golem.remove();
            }
            arena.getIronGolems().clear();
            VersionUtils.playSound(((Player) sender).getLocation(), "ENTITY_IRONGOLEM_DEATH");
            clearMessage = new MessageBuilder("IN_GAME_MESSAGES_ADMIN_REMOVED_GOLEMS").asKey().build();
            break;
          case "wolf":
            if(arena.getWolves().isEmpty()) {
              new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().send(sender);
              return;
            }
            for(Wolf wolf : arena.getWolves()) {
              VersionUtils.sendParticles("LAVA", arena.getPlayers(), wolf.getLocation(), 20);
              wolf.remove();
            }
            arena.getWolves().clear();
            VersionUtils.playSound(((Player) sender).getLocation(), "ENTITY_WOLF_DEATH");
            clearMessage = new MessageBuilder("IN_GAME_MESSAGES_ADMIN_REMOVED_WOLVES").asKey().build();
            break;
          default:
            new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("VILLAGER, ZOMBIE, GOLEM, WOLF").send(sender);
            return;
        }
        new MessageBuilder(clearMessage).player((Player) sender).arena(arena).sendArena();
      }
    });
  }

}
