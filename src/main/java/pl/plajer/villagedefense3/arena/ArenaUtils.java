/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.arena;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_10_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R2;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;
import pl.plajerlair.core.services.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaUtils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static void hidePlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.hidePlayer(p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.showPlayer(p);
    }
  }

  public static void hidePlayersOutsideTheGame(Player player, Arena arena) {
    for (Player players : plugin.getServer().getOnlinePlayers()) {
      if (arena.getPlayers().contains(players)) {
        continue;
      }
      player.hidePlayer(players);
      players.hidePlayer(player);
    }
  }

  public static void bringDeathPlayersBack(Arena arena) {
    try {
      for (Player player : arena.getPlayers()) {
        if (!arena.getPlayersLeft().contains(player)) {
          User user = UserManager.getUser(player.getUniqueId());
          user.setSpectator(false);

          arena.teleportToStartLocation(player);
          player.setFlying(false);
          player.setAllowFlight(false);
          player.setGameMode(GameMode.SURVIVAL);
          player.removePotionEffect(PotionEffectType.NIGHT_VISION);
          arena.showPlayers();
          player.getInventory().clear();
          user.getKit().giveKitItems(player);
          player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
        }
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  public static void updateLevelStat(Player player, Arena arena) {
    try {
      User user = UserManager.getUser(player.getUniqueId());
      if (Math.pow(50 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5) < user.getStat(StatsStorage.StatisticType.XP)) {
        user.addStat(StatsStorage.StatisticType.LEVEL, 1);
        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.You-Leveled-Up"), user.getStat(StatsStorage.StatisticType.LEVEL)));
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  public static Arena initializeArena(String ID) {
    Arena arena;
    if (plugin.is1_9_R1()) {
      arena = new ArenaInitializer1_9_R1(ID, plugin);
    } else if (plugin.is1_10_R1()) {
      arena = new ArenaInitializer1_10_R1(ID, plugin);
    } else if (plugin.is1_11_R1()) {
      arena = new ArenaInitializer1_11_R1(ID, plugin);
    } else if (plugin.is1_12_R1()) {
      arena = new ArenaInitializer1_12_R1(ID, plugin);
    } else if (plugin.is1_13_R1()) {
      arena = new ArenaInitializer1_13_R1(ID, plugin);
    } else {
      arena = new ArenaInitializer1_13_R2(ID, plugin);
    }
    return arena;
  }

}
