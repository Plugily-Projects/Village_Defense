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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

/**
 * @author Plajer
 * <p>
 * Created at 23.11.2018
 */
public class Reward {

  private RewardExecutor executor;
  private RewardType type;
  private String executableCode;
  private double chance;
  private int waveExecute;

  public Reward(RewardType type, String rawCode, int waveExecute) {
    this.type = type;
    this.waveExecute = waveExecute;
    String processedCode = rawCode;

    //set reward executor based on provided code
    if (rawCode.contains("p:")) {
      this.executor = RewardExecutor.PLAYER;
      processedCode = StringUtils.replace(processedCode, "p:", "");
    } else if (rawCode.contains("script:")) {
      this.executor = RewardExecutor.SCRIPT;
      processedCode = StringUtils.replace(processedCode, "script:", "");
    } else {
      this.executor = RewardExecutor.CONSOLE;
    }

    //search for chance modifier
    if (processedCode.contains("chance(")) {
      int loc = processedCode.indexOf(")");
      //modifier is invalid
      if (loc == -1) {
        Bukkit.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition! Command: " + rawCode);
        //invalid code, 0% chance to execute
        this.chance = 0.0;
        return;
      }
      String chanceStr = processedCode;
      chanceStr = chanceStr.substring(0, loc).replaceAll("[^0-9]+", "");
      double chance = Double.parseDouble(chanceStr);
      processedCode = StringUtils.replace(processedCode, "chance(" + chanceStr + "):", "");
      this.chance = chance;
    } else {
      this.chance = 100.0;
    }
    this.executableCode = processedCode;
  }

  public Reward(RewardType type, String rawCode) {
    this(type, rawCode, -1);
  }

  public RewardExecutor getExecutor() {
    return executor;
  }

  public RewardType getType() {
    return type;
  }

  public String getExecutableCode() {
    return executableCode;
  }

  public double getChance() {
    return chance;
  }

  public int getWaveExecute() {
    return waveExecute;
  }

  public enum RewardExecutor {
    CONSOLE, PLAYER, SCRIPT
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
