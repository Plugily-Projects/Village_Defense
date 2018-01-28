package me.tomthedeveloper.creatures.v1_8_R3;

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
        if (this.a.bc().nextInt(8) == 0) {
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
            this.a.world.triggerEffect(2001, this.b, Block.getId(this.c));
        }
    }

}

