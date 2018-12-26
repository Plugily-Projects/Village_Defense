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

import pl.plajerlair.core.rewards.Reward;

/**
 * @author Plajer
 * <p>
 * Created at 23.11.2018
 */
public class GameReward extends Reward {

  private RewardType type;
  private int waveExecute;

  public GameReward(RewardType type, String rawCode, int waveExecute) {
    super(rawCode);
    this.type = type;
    this.waveExecute = waveExecute;
  }

  public GameReward(RewardType type, String rawCode) {
    this(type, rawCode, -1);
  }

  public RewardType getType() {
    return type;
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
