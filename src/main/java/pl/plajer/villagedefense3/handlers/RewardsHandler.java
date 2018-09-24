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

package pl.plajer.villagedefense3.handlers;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsHandler {

  private FileConfiguration config;
  private Main plugin;
  private boolean enabled;

  public RewardsHandler(Main plugin) {
    this.plugin = plugin;
    enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
    config = ConfigUtils.getConfig(plugin, "rewards");
  }

  public void performReward(Arena arena, RewardType type) {
    if (!enabled || (type == RewardType.END_WAVE && !config.contains("rewards.endwave." + arena.getWave()))) {
      return;
    }
    for (String string : config.getStringList("rewards." + type.getPath())) {
      performCommand(arena, string);
    }
  }

  public void performReward(Player player, RewardType type) {
    if (!enabled) {
      return;
    }
    for (String string : config.getStringList("rewards." + type.getPath())) {
      performCommand(player, string);
    }
  }

  private void performCommand(Arena arena, String string) {
    if (!enabled) {
      return;
    }
    String command = formatCommandPlaceholders(string, arena);
    if (command.contains("chance(")) {
      int loc = command.indexOf(")");
      if (loc == -1) {
        plugin.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition!");
        return;
      }
      String chanceStr = command.substring(0, loc).replaceAll("[^0-9]+", "");
      int chance = Integer.parseInt(chanceStr);
      command = command.replace("chance(" + chanceStr + "):", "");
      if (ThreadLocalRandom.current().nextInt(0, 100) > chance) {
        return;
      }
    }
    for (Player player : arena.getPlayers()) {
      if (command.contains("p:")) {
        player.performCommand(command.replaceFirst("p:", "").replace("%PLAYER%", player.getName()));
      } else {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%PLAYER%", player.getName()));
      }
    }
  }

  private void performCommand(Player player, String string) {
    if (!enabled) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    String command = formatCommandPlaceholders(string, arena);
    if (command.contains("chance(")) {
      int loc = command.indexOf(")");
      if (loc == -1) {
        plugin.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition!");
        return;
      }
      String chanceStr = command.substring(0, loc).replaceAll("[^0-9]+", "");
      int chance = Integer.parseInt(chanceStr);
      command = command.replace("chance(" + chanceStr + "):", "");
      if (ThreadLocalRandom.current().nextInt(0, 100) > chance) {
        return;
      }
    }
    if (command.contains("p:")) {
      player.performCommand(command.replaceFirst("p:", "").replace("%PLAYER%", player.getName()));
    } else {
      plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%PLAYER%", player.getName()));
    }
  }

  private String formatCommandPlaceholders(String command, Arena arena) {
    String formatted = command;
    formatted = StringUtils.replace(formatted, "%ARENA-ID%", arena.getID());
    formatted = StringUtils.replace(formatted, "%MAPNAME%", arena.getMapName());
    formatted = StringUtils.replace(formatted, "%PLAYERAMOUNT%", String.valueOf(arena.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%WAVE%", String.valueOf(arena.getWave()));
    return formatted;
  }

  public enum RewardType {
    END_GAME("endgame"), END_WAVE("endwave"), ZOMBIE_KILL("zombiekill");

    private String path;

    RewardType(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }

  }

}
