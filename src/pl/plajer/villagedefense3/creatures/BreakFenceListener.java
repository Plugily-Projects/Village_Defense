package pl.plajer.villagedefense3.creatures;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.Util;

import java.util.Queue;
import java.util.Random;

/**
 * Created by Tom on 14/08/2014.
 */
public class BreakFenceListener extends BukkitRunnable {

    private Random random = new Random();
    private Main plugin = JavaPlugin.getPlugin(Main.class);

    @Override
    public void run() {
        for(World world : Bukkit.getServer().getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity.getType() == EntityType.ZOMBIE)) continue;
                Queue<Block> blocks = Util.getLineOfSight((LivingEntity) entity, null, 1, 1);
                for(Block block : blocks) {
                    if(block.getType() == Material.WOOD_DOOR || block.getType() == Material.WOODEN_DOOR /*|| block.getType() == Material.FENCE*/) {
                        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                            block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
                        } else {
                            block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, 20, new MaterialData(Material.WOODEN_DOOR).getItemTypeId());
                        }
                        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                            block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, 1, 1);
                        } else {
                            block.getWorld().playSound(block.getLocation(), Sound.valueOf("ZOMBIE_WOOD"), 5F, 5F);
                        }
                        this.particleDoor(block);
                        if(random.nextInt(20) == 5) {
                            breakDoor(block);
                            if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 1, 1);
                            } else {
                                block.getWorld().playSound(block.getLocation(), Sound.valueOf("ZOMBIE_WOOD"), 5F, 5F);
                            }
                        }
                    }
                }
            }
        }
    }

    private void particleDoor(org.bukkit.block.Block block) {
        for(BlockFace blockFace : BlockFace.values()) {
            if(block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
                if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                    block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
                } else {
                    block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, 20, new MaterialData(Material.WOODEN_DOOR).getItemTypeId());
                }
            }
        }
    }

    private void breakDoor(org.bukkit.block.Block block) {
        for(BlockFace blockFace : BlockFace.values()) {
            if(block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
                if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                    block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
                } else {
                    block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, 20, new MaterialData(Material.WOODEN_DOOR).getItemTypeId());
                }
                block.setType(Material.AIR);
            }
        }
    }

}
