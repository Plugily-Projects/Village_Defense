/*
 * Village Defense - Protect villagers from hordes of zombies
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

package pl.plajer.villagedefense.handlers.reward;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.rewards.RewardsScriptEngine;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsFactory {

  private Set<GameReward> rewards = new HashSet<>();
  private FileConfiguration config;
  private boolean enabled;

  public RewardsFactory(Main plugin) {
    enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
    config = ConfigUtils.getConfig(plugin, "rewards");
    registerRewards();
  }

  public void performReward(Arena arena, GameReward.RewardType type) {
    if (!enabled) {
      return;
    }
    for (Player p : arena.getPlayers()) {
      performReward(p, type);
    }
  }

  public void performReward(Player player, GameReward.RewardType type) {
    if (!enabled) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    RewardsScriptEngine engine = new RewardsScriptEngine();
    engine.setValue("player", player);
    engine.setValue("server", Bukkit.getServer());
    engine.setValue("arena", arena);
    for (GameReward reward : rewards) {
      if (reward.getType() == type) {
        //reward isn't for this wave
        if (type == GameReward.RewardType.END_WAVE && reward.getWaveExecute() != arena.getWave()) {
          continue;
        }
        //cannot execute if chance wasn't met
        if (reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
          continue;
        }
        String command = reward.getExecutableCode();
        command = StringUtils.replace(command, "%PLAYER%", player.getName());
        command = formatCommandPlaceholders(command, arena);
        switch (reward.getExecutor()) {
          case CONSOLE:
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            break;
          case PLAYER:
            player.performCommand(command);
            break;
          case SCRIPT:
            engine.execute(command);
            break;
          default:
            break;
        }
      }
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

  private void registerRewards() {
    if (!enabled) {
      return;
    }
    Map<GameReward.RewardType, Integer> registeredRewards = new HashMap<>();
    for (GameReward.RewardType rewardType : GameReward.RewardType.values()) {
      if (rewardType == GameReward.RewardType.END_WAVE) {
        for (String key : config.getConfigurationSection("rewards." + rewardType.getPath()).getKeys(false)) {
          for (String reward : config.getStringList("rewards." + rewardType.getPath() + "." + key)) {
            rewards.add(new GameReward(rewardType, reward, Integer.valueOf(key)));
            registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
          }
        }
        continue;
      }
      for (String reward : config.getStringList("rewards." + rewardType.getPath())) {
        rewards.add(new GameReward(rewardType, reward));
        registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
      }
    }
    for (GameReward.RewardType rewardType : registeredRewards.keySet()) {
      Debugger.debug(LogLevel.INFO, "[RewardsFactory] Registered " + registeredRewards.get(rewardType) + " " + rewardType.name() + " rewards!");
    }
  }

}
