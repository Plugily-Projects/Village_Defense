package me.tomthedeveloper.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.BlockFence;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalDoorInteract;


/**
 * Created by Tom on 12/08/2014.
 */
public class PathfinderGoalFenceInteract extends PathfinderGoalDoorInteract {


    protected BlockFence e;

    public PathfinderGoalFenceInteract(EntityInsentient entityInsentient) {
        super(entityInsentient);
    }

  /*  @Override
    public boolean a() {
        if (!this.a.positionChanged) {
            return false;
        } else {
            Navigation navigation = this.a.getNavigation();
            PathEntity pathentity = navigation.e();

            if (pathentity != null && !pathentity.b() && navigation.c()) {
                for (int i = 0; i < Math.min(pathentity.e() + 2, pathentity.d()); ++i) {
                    PathPoint pathpoint = pathentity.a(i);

                    this.b = pathpoint.a;
                    this.c = pathpoint.b + 1;
                    this.d = pathpoint.c;
                    if (this.a.e((double) this.b, this.a.locY, (double) this.d) <= 15.25D) {
                        this.e = this.a(this.b, this.c, this.d);
                        if (this.e != null) {
                            return true;
                        }
                    }
                }

                this.b = MathHelper.floor(this.a.locX);
                this.c = MathHelper.floor(this.a.locY + 1.0D);
                this.d = MathHelper.floor(this.a.locZ);
                this.e = this.a(this.b, this.c, this.d);
                return this.e != null;
            } else {
                return false;
            }
        }
    }

    private BlockFence a(int i, int j, int k) {
        Block block = this.a.world.getType(i, j, k);
        return block != Blocks.FENCE ? null : (BlockFence) block;
    } */


}
