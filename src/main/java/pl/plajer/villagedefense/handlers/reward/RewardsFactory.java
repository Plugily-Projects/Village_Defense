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

package pl.plajer.villagedefense.handlers.reward;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsFactory {

  private Set<Reward> rewards = new HashSet<>();
  private FileConfiguration config;
  private boolean enabled;

  public RewardsFactory(Main plugin) {
    enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
    config = ConfigUtils.getConfig(plugin, "rewards");
    registerRewards();
  }

  public void performReward(Arena arena, Reward.RewardType type) {
    if (!enabled) {
      return;
    }
    for (Player p : arena.getPlayers()) {
      performReward(p, type);
    }
  }

  public void performReward(Player player, Reward.RewardType type) {
    if (!enabled) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    RewardsScriptEngine engine = new RewardsScriptEngine();
    engine.setValue("player", player);
    engine.setValue("server", Bukkit.getServer());
    engine.setValue("arena", arena);
    for (Reward reward : rewards) {
      if (reward.getType() == type) {
        //reward isn't for this wave
        if (type == Reward.RewardType.END_WAVE && reward.getWaveExecute() != arena.getWave()) {
          continue;
        }
        //cannot execute if chance wasn't met
        if (reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
          continue;
        }
        String command = reward.getExecutableCode();
        command = StringUtils.replace("%PLAYER%", player.getName(), command);
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
    int i = 0;
    for (Reward.RewardType rewardType : Reward.RewardType.values()) {
      for (String string : config.getStringList("rewards." + rewardType.getPath())) {
        rewards.add(new Reward(rewardType, string));
        i++;
      }
    }
    Main.debug(Main.LogLevel.INFO, "[RewardsFactory] Registered " + i + " rewards!");
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
