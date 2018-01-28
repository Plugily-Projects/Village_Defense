package me.tomthedeveloper.versions;

import me.tomthedeveloper.InvasionInstance;
import me.tomthedeveloper.creatures.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Created by TomVerschueren on 9/06/2017.
 */
public class InvasionInstance1_12_R1 extends InvasionInstance {

    public InvasionInstance1_12_R1(String ID) {
        super(ID);
        
    }

    public void spawnFastZombie(Random random) {

        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        FastZombie fastZombie = new FastZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiestospawn--;
    }

    @Override
    public void spawnHalfInvisibleZombie(Random random) {

        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        FastZombie fastZombie = new FastZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiestospawn--;
    }

    @Override
    public void spawnKnockbackResistantZombies(Random random) {

        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
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
        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiestospawn--;
    }

    public void spawnBabyZombie(Random random) {
        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        BabyZombie fastZombie = new BabyZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();

        zombie.setRemoveWhenFarAway(false);
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.addZombie((Zombie) fastZombie.getBukkitEntity());

        zombiestospawn--;
    }

    public void spawnHardZombie(Random random) {
        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        HardZombie fastZombie = new HardZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.setRemoveWhenFarAway(false);
        this.addZombie(zombie);
        zombiestospawn--;
    }

    @Override
    public void spawnSoftHardZombie(Random random) {
        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        HardZombie fastZombie = new HardZombie(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.setRemoveWhenFarAway(false);
        this.addZombie(zombie);
        zombiestospawn--;
    }

    public void spawnGolemBuster(Random random) {
        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        GolemBuster fastZombie = new GolemBuster(location.getWorld());
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
        zombie.getEquipment().setHelmetDropChance(0.0F);
        zombie.getEquipment().setItemInHandDropChance(0F);
        zombie.setRemoveWhenFarAway(false);
        this.addZombie(zombie);

        zombiestospawn--;
    }

    public void spawnPlayerBuster(Random random) {
        Location location = zombiespawns.get(random.nextInt(zombiespawns.size()));
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
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
        this.addZombie(zombie);

        zombiestospawn--;
    }

    public void spawnVillager(Location location) {
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        RidableVillager ridableVillager = new RidableVillager(location.getWorld());
        ridableVillager.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(ridableVillager, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Villager villager = (Villager) ridableVillager.getBukkitEntity();
        villager.setRemoveWhenFarAway(false);
        this.addVillager((Villager) ridableVillager.getBukkitEntity());
    }

    public void spawnGolem(Location location) {
        Random random = new Random();
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        RidableIronGolem ridableIronGolem = new RidableIronGolem(location.getWorld());
        ridableIronGolem.setPosition(location.getX(), location.getY(), location.getZ());
        ridableIronGolem.setCustomName("Guard");
        ridableIronGolem.setCustomNameVisible(true);


        McWorld.addEntity(ridableIronGolem, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void spawnGolem(Location location, Player player) {
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        RidableIronGolem ridableIronGolem = new RidableIronGolem(location.getWorld());
        ridableIronGolem.setPosition(location.getX(), location.getY(), location.getZ());
        ridableIronGolem.setCustomName(player.getName() + "'s Golem");
        ridableIronGolem.setCustomNameVisible(true);

        McWorld.addEntity(ridableIronGolem, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.addIronGolem((IronGolem) ridableIronGolem.getBukkitEntity());
    }

    public void spawnWolf(Location location, Player player) {
        net.minecraft.server.v1_12_R1.World McWorld = ((CraftWorld) location.getWorld()).getHandle();
        WorkingWolf wolf = new WorkingWolf(location.getWorld());
        wolf.setPosition(location.getX(), location.getY(), location.getZ());
        McWorld.addEntity(wolf, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.setCustomName(player.getName() + "'s Wolf");
        wolf.setCustomNameVisible(true);
        wolf.setInvisible(false);
        ((Wolf) wolf.getBukkitEntity()).setOwner(player);

        this.addWolf((Wolf) wolf.getBukkitEntity());
    }
}
