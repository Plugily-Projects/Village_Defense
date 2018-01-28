package me.tomthedeveloper.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityInsentient;

/**
 * Created by Tom on 12/08/2014.
 */
public class PathfinderGoalBreakFence extends PathfinderGoalFenceInteract {

    private int i = 0;
    private int j = -1;

    public PathfinderGoalBreakFence(EntityInsentient entityinsentient) {
        super(entityinsentient);

    }


   /* @Override
    public void e() {
        super.e();
        if (this.a.aI().nextInt(20) == 0) {
         thishis.a.world.triggerEffect(this,thissthisthis.c, this.d, 0);
      this
        ++this.i;
        int ithisint) ((float) this.i / 240.0F * 10.0Fthis        if (i !thisis.j) {
this        tthisathislthisthis.a.getId(), tthisb, this.c, this.d, i);
     this   this.j = i;
       this        if (tthisithis2this{
            this.a.world.setAir(this.b, ththis,thissthis
            thisthisorld.triggerEffect(1012, tthisbthisithis this.d, 0);
        thisthis.a.world.triggerEffect(20thisthis.b, this.c, this.d, BlocthistthisBthis) this.e));

            destroyFence(this.a.world.getWorld().getBlockAt(this.b, this.c, this.d));
        }
    }

    public void destroyFence(org.bukkit.block.Block block) {
        for (BlockFace blockFace : BlockFace.values()) {
            if (block.getRelative(blockFace).getType() == Material.FENCE) {
                block.getRelative(blockFace).setType(Material.AIR);
                destroyFence(block.getRelative(blockFace));


            }
        }
        ;
    } */
}
