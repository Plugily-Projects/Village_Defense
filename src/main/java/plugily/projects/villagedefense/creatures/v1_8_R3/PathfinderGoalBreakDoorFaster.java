/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalBreakDoor;


public class PathfinderGoalBreakDoorFaster extends PathfinderGoalBreakDoor {

  private int i = 0;
  private int j = -1;

  public PathfinderGoalBreakDoorFaster(EntityInsentient entityinsentient) {
    super(entityinsentient);
  }

  @Override
  public void e() {
    super.e();
    a.world.triggerEffect(1010, b, 0);

    ++i;
    int i = (int) (this.i / 240.0F * 10.0F);

    if(i != j) {
      a.world.c(a.getId(), b, i);
      j = i;
    }

    if(this.i == 70) {
      a.world.setAir(b);
      a.world.triggerEffect(1012, b, 0);
      a.world.triggerEffect(2001, b, Block.getId(c));
    }
  }

}

