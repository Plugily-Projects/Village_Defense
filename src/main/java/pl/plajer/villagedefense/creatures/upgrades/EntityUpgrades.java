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

package pl.plajer.villagedefense.creatures.upgrades;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public enum EntityUpgrades {

  DAMAGE(EntityType.BOTH, 4), FINAL_DEFENSE(EntityType.GOLEM, 2), HEALTH(EntityType.BOTH, 4), SPEED(EntityType.BOTH, 4), SWARM_AWARENESS(EntityType.WOLF, 2);

  private EntityType applicableFor;
  private int maxTierUpgrade;

  EntityUpgrades(EntityType applicableFor, int maxTierUpgrade) {
    this.applicableFor = applicableFor;
    this.maxTierUpgrade = maxTierUpgrade;
  }

  /**
   * @return entity which upgrade can be applied for
   */
  public EntityType getApplicableFor() {
    return applicableFor;
  }

  /**
   * @return maximum tier the entity can be upgraded
   */
  public int getMaxTierUpgrade() {
    return maxTierUpgrade;
  }

  public enum EntityType {
    BOTH, GOLEM, WOLF
  }

}
