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

import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class RespawnArgument {

  public RespawnArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new CommandArgument("respawn", Arrays.asList("villagedefense.admin.respawn", "villagedefense.admin.respawn.others"),
        CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!registry.getPlugin().getMainCommand().checkIsInGameInstance(player)) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);

        Player target = null;
        if (args.length == 2) {
          if (!sender.hasPermission("villagedefense.admin.respawn.others")) {
            return;
          }
          for (Player loopPlayer : arena.getPlayers()) {
            if (loopPlayer.getName().equalsIgnoreCase(args[1])) {
              target = loopPlayer;
              break;
            }
          }
          if (target == null) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
            return;
          }
        } else {
          target = player;
        }
        User user = UserManager.getUser(target.getUniqueId());
        if (!user.isSpectator()) {
          return;
        }
        target.setGameMode(GameMode.SURVIVAL);
        target.removePotionEffect(PotionEffectType.NIGHT_VISION);
        target.removePotionEffect(PotionEffectType.SPEED);
        user.setSpectator(false);
        arena.teleportToStartLocation(target);
        target.setFlying(false);
        target.setAllowFlight(false);
        ArenaUtils.showPlayer(target, arena);
        target.getInventory().clear();
        user.getKit().giveKitItems(target);
        target.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Back-In-Game"));
      }
    });
  }

}
