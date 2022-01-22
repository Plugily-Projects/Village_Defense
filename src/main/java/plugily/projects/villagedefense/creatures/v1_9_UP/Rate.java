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
