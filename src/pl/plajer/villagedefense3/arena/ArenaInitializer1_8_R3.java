package pl.plajer.villagedefense3.arena;

import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.creatures.v1_8_R3.*;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.utils.PercentageUtils;

import java.util.Random;

/**
 * Created by Tom on 10/07/2015.
 */
public class ArenaInitializer1_8_R3 extends Arena {

    private Main plugin;

    public ArenaInitializer1_8_R3(String ID, Main plugin) {
        super(ID, plugin);
        this.plugin = plugin;
    }

    public void spawnFastZombie(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        FastZombie fastZombie = new FastZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiesToSpawn--;
    }

    @Override
    public void spawnHalfInvisibleZombie(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        FastZombie fastZombie = new FastZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiesToSpawn--;
    }

    @Override
    public void spawnKnockbackResistantZombies(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        TankerZombie fastZombie = new TankerZombie(location.getWorld());
        fastZombie.getAttributeInstance(GenericAttributes.c).setValue(Double.MAX_VALUE);
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setItemInHand(new ItemStack(Material.GOLD_AXE));
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.setRemoveWhenFarAway(false);
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiesToSpawn--;
    }

    public void spawnBabyZombie(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        BabyZombie fastZombie = new BabyZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }

        zombie.setRemoveWhenFarAway(false);
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiesToSpawn--;
    }

    public void spawnHardZombie(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        HardZombie fastZombie = new HardZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.setRemoveWhenFarAway(false);
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie(zombie);
        zombiesToSpawn--;
    }

    @Override
    public void spawnSoftHardZombie(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        HardZombie fastZombie = new HardZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.setRemoveWhenFarAway(false);
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie(zombie);
        zombiesToSpawn--;
    }

    public void spawnGolemBuster(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        GolemBuster fastZombie = new GolemBuster(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
        zombie.getEquipment().setHelmetDropChance(0.0F);
        zombie.getEquipment().setItemInHandDropChance(0F);
        zombie.setRemoveWhenFarAway(false);
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie(zombie);

        zombiesToSpawn--;
    }

    public void spawnPlayerBuster(Random random) {
        Location location = zombieSpawns.get(random.nextInt(zombieSpawns.size() - 1));
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        PlayerBuster fastZombie = new PlayerBuster(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
        zombie.getEquipment().setHelmetDropChance(0.0F);
        zombie.getEquipment().setItemInHandDropChance(0F);
        zombie.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
        this.addZombie(zombie);

        zombiesToSpawn--;
    }

    public void spawnVillager(Location location) {
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        RidableVillager ridableVillager = new RidableVillager(location.getWorld());
        ridableVillager.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(ridableVillager, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Villager villager = (Villager) ridableVillager.getBukkitEntity();
        villager.setRemoveWhenFarAway(false);
        this.addVillager((Villager) ridableVillager.getBukkitEntity());
    }

    public void spawnGolem(Location location, Player player) {
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        IronGolem ironGolem = new IronGolem(location.getWorld());
        ironGolem.setPosition(location.getX(), location.getY(), location.getZ());
        ironGolem.setCustomName(ChatManager.colorMessage("In-Game.Spawned-Golem-Name").replaceAll("%player%", player.getName()));
        ironGolem.setCustomNameVisible(true);

        McWorld.addEntity(ironGolem, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.addIronGolem((org.bukkit.entity.IronGolem) ironGolem.getBukkitEntity());
    }

    public void spawnWolf(Location location, Player player) {
        net.minecraft.server.v1_8_R3.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        WorkingWolf wolf = new WorkingWolf(location.getWorld());
        wolf.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(wolf, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.setCustomName(ChatManager.colorMessage("In-Game.Spawned-Wolf-Name").replaceAll("%player%", player.getName()));
        wolf.setCustomNameVisible(true);
        wolf.setInvisible(false);
        ((Wolf) wolf.getBukkitEntity()).setOwner(player);


        this.addWolf((Wolf) wolf.getBukkitEntity());
    }
}
