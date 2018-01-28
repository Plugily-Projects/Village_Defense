package me.tomthedeveloper.creatures.v1_8_R3;

import me.tomthedeveloper.utils.ParticleEffect;
import me.tomthedeveloper.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.Random;


/**
 * Created by Tom on 14/08/2014.
 */
public class BreakFenceListener extends BukkitRunnable {

    Random random = new Random();


    @Override
    public void run() {
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity.getType() == EntityType.ZOMBIE))
                    continue;
                Queue<Block> blocks = Util.getLineOfSight((LivingEntity) entity, null, 1, 1);
                for (Block block : blocks) {

                    if (block.getType() == Material.WOOD_DOOR || block.getType() == Material.WOODEN_DOOR /*|| block.getType() == Material.FENCE*/) {
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.WOODEN_DOOR, (byte) 0), (float) 0.1, (float) 0.1, (float) 0.1, 1, 50, block.getLocation(), 100);
                        if (this.is1_9_R1()) {
                            block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, 1, 1);
                        } else {
                            // block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOOD, 5F, 5F);
                        }
                        this.particleDoor(block);
                        if (random.nextInt(15) == 5) {
                            breakDoor(block);
                            if (this.is1_9_R1()) {
                                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 1, 1);
                            } else {
                                //  block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOOD, 5F, 5F);
                            }
                        }
                    }

                }
            }
        }
    }

    public boolean is1_9_R1() {

        return Bukkit.getVersion().equalsIgnoreCase("v1_9_R1");
    }

    public void particleDoor(Block block) {
        for (BlockFace blockFace : BlockFace.values()) {
            if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR || block.getType() == Material.FENCE) {
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.WOODEN_DOOR, (byte) 0), (float) 0.1, (float) 0.1, (float) 0.1, 1, 50, block.getLocation(), 100);


            }
        }
    }

    public void breakDoor(Block block) {
        for (BlockFace blockFace : BlockFace.values()) {
            if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.WOODEN_DOOR, (byte) 0), (float) 0.1, (float) 0.1, (float) 0.1, 1, 50, block.getLocation(), 100);

                block.setType(Material.AIR);


            }
        }
       /* if(block.getType() == Material.FENCE){
                destroyFence(block);
        } */
    }

    public void destroyFence(Block block) {
        for (BlockFace blockFace : BlockFace.values()) {
            if (block.getRelative(blockFace).getType() == Material.FENCE) {
                block.getRelative(blockFace).setType(Material.AIR);
                destroyFence(block.getRelative(blockFace));


            }
        }
    }


}
