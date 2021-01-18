/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_13_R1;

import net.minecraft.server.v1_13_R1.Block;
import net.minecraft.server.v1_13_R1.EntityInsentient;
import net.minecraft.server.v1_13_R1.PathfinderGoalBreakDoor;


public class PathfinderGoalBreakDoorFaster extends PathfinderGoalBreakDoor {

  private int i = 0;
  private int j = -1;

  public PathfinderGoalBreakDoorFaster(EntityInsentient entityinsentient) {
    super(entityinsentient);
  }

  @Override
  public void e() {
    super.e();
    if (this.a.getRandom().nextInt(8) == 0) {
      this.a.world.triggerEffect(1010, this.b, 0);
    }

    ++this.i;
    int i = (int) ((float) this.i / 240.0F * 10.0F);

    if (i != this.j) {
      this.a.world.c(this.a.getId(), this.b, i);
      this.j = i;
    }

    if (this.i == 70) {
      this.a.world.setAir(this.b);
      this.a.world.triggerEffect(1012, this.b, 0);
      this.a.world.triggerEffect(2001, this.b, Block.getCombinedId(this.a.world.getType(this.b)));
    }
  }

}

