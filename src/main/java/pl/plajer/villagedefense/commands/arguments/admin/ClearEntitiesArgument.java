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
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ClearEntitiesArgument {

  public ClearEntitiesArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new CommandArgument("clear", "villagedefense.admin.clear", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!registry.getPlugin().getMainCommand().checkIsInGameInstance((Player) sender)) {
          return;
        }
        if (args.length == 1) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        String clearMessage;
        switch (args[1].toLowerCase()) {
          case "villager":
            if (arena.getVillagers() == null || arena.getVillagers().isEmpty()) {
              sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
              return;
            }
            for (Villager villager : arena.getVillagers()) {
              villager.getWorld().spawnParticle(Particle.LAVA, villager.getLocation(), 20);
              villager.remove();
            }
            arena.getVillagers().clear();
            Utils.playSound(((Player) sender).getLocation(), "ENTITY_VILLAGER_DEATH", "ENTITY_VILLAGER_DEATH");
            clearMessage = ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Villagers");
            break;
          case "zombie":
            if (arena.getZombies() == null || arena.getZombies().isEmpty()) {
              sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
              return;
            }
            for (Zombie zombie : arena.getZombies()) {
              zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
              zombie.remove();
            }
            arena.getZombies().clear();
            Utils.playSound(((Player) sender).getLocation(), "ENTITY_ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH");
            clearMessage = ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies");
            break;
          case "golem":
            if (arena.getIronGolems() == null || arena.getIronGolems().isEmpty()) {
              sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
              return;
            }
            for (IronGolem golem : arena.getIronGolems()) {
              golem.getWorld().spawnParticle(Particle.LAVA, golem.getLocation(), 20);
              golem.remove();
            }
            arena.getIronGolems().clear();
            Utils.playSound(((Player) sender).getLocation(), "ENTITY_IRONGOLEM_DEATH", "ENTITY_IRON_GOLEM_DEATH");
            clearMessage = ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Golems");
            break;
          default:
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
            return;
          //todo add wolves
        }
        for (Player loopPlayer : arena.getPlayers()) {
          String message = ChatManager.formatMessage(arena, clearMessage, loopPlayer);
          loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
      }
    });
  }

}
