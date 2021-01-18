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

package plugily.projects.villagedefense.commands.arguments.admin;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class RespawnArgument {

  public RespawnArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("respawn", Arrays.asList("villagedefense.admin.respawn", "villagedefense.admin.respawn.others"),
        CommandArgument.ExecutorType.PLAYER, new LabelData("/vda respawn &c[player]", "/vda respawn",
        "&7Respawn yourself or target player in game\n&6Permission: &7villagedefense.admin.respawn (for yourself)\n"
            + "&6Permission: &7villagedefense.admin.respawn.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!Utils.checkIsInGameInstance(player)) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);

        Player target = null;
        if (args.length == 2) {
          if (!Utils.hasPermission(sender, "villagedefense.admin.respawn.others")) {
            return;
          }
          for (Player loopPlayer : arena.getPlayers()) {
            if (loopPlayer.getName().equalsIgnoreCase(args[1])) {
              target = loopPlayer;
              break;
            }
          }
          if (target == null) {
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_TARGET_PLAYER_NOT_FOUND));
            return;
          }
        } else {
          target = player;
        }
        User user = registry.getPlugin().getUserManager().getUser(target);
        if (!user.isSpectator()) {
          return;
        }
        target.setGameMode(GameMode.SURVIVAL);
        target.removePotionEffect(PotionEffectType.NIGHT_VISION);
        target.removePotionEffect(PotionEffectType.SPEED);
        //the default fly speed
        target.setFlySpeed(0.1f);
        user.setSpectator(false);
        target.teleport(arena.getStartLocation());
        target.setFlying(false);
        target.setAllowFlight(false);
        ArenaUtils.showPlayer(target, arena);
        target.getInventory().clear();
        user.getKit().giveKitItems(target);
        target.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.BACK_IN_GAME));
      }
    });
  }

}
