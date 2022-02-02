
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_9_UP;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.01.2022
 */
public class Rate {

  private final int phase;
  private final int waveHigher;
  private final int waveLower;
  private final int spawnLower;
  private final int rate;
  private final int division;
  private final int reduce;
  private final RateType rateType;

  public Rate(int phase, int waveHigher, int waveLower, int spawnLower, int rate, int division, int reduce, RateType rateType) {
    this.phase = phase;
    this.waveHigher = waveHigher;
    this.waveLower = waveLower;
    this.spawnLower = spawnLower;
    this.rate = rate;
    this.division = division;
    this.reduce = reduce;
    this.rateType = rateType;
  }

  public boolean isPhase(int number) {
    if(phase == number) {
      return true;
    }
    return phase == 0;
  }

  public int getPhase() {
    return phase;
  }

  public boolean isWaveHigher(int wave) {
    if(waveHigher == 0) {
      return true;
    }
    return wave >= waveHigher;
  }

  public boolean isWaveLower(int wave) {
    if(waveLower == 0) {
      return true;
    }
    return wave <= waveLower;
  }

  public boolean isSpawnLower(int number) {
    if(spawnLower == 0) {
      return true;
    }
    return number <= spawnLower;
  }

  public int getSpawnLower() {
    return spawnLower;
  }

  public int getWaveHigher() {
    return waveHigher;
  }

  public int getWaveLower() {
    return waveLower;
  }

  public double getRate() {
    if(rate == 0) {
      return 1;
    }
    return rate;
  }

  public int getDivision() {
    if(division == 0) {
      return 1;
    }
    return division;
  }

  public int getReduce() {
    return reduce;
  }

  public RateType getRateType() {
    return rateType;
  }

  public enum RateType {
    SPAWN, AMOUNT, CHECK
  }
}
