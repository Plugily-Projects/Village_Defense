package plugily.projects.villagedefense.creatures.v1_8_R3;

import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import plugily.projects.villagedefense.creatures.BaseCreatureInitializer;
import plugily.projects.villagedefense.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreatureInitializer implements BaseCreatureInitializer {

    private static final UUID uId = UUID.fromString("206a89dc-ae78-4c4d-b42c-3b31db3f5a7e");

    public CreatureInitializer() {
        registerEntity("VillageZombie", 54, FastZombie.class);
        registerEntity("VillageZombie", 54, BabyZombie.class);
        registerEntity("VillageZombie", 54, PlayerBuster.class);
        registerEntity("VillageZombie", 54, GolemBuster.class);
        registerEntity("VillageZombie", 54, HardZombie.class);
        registerEntity("VillageZombie", 54, TankerZombie.class);
        registerEntity("VillageZombie", 54, VillagerSlayer.class);
        registerEntity("VillageVillager", 120, RidableVillager.class);
        registerEntity("VillageVillagerGolem", 99, RidableIronGolem.class);
        registerEntity("VillageWolf", 95, WorkingWolf.class);
        registerEntity("VillageZombie", 54, VillagerBuster.class);
    }

    private void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass) {
        try {
            List<Map<?, ?>> dataMaps = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().isAssignableFrom(Map.class)) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }
            ((Map<Class<? extends EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
            ((Map<Class<? extends EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.errorOccurred();
            Bukkit.getConsoleSender().sendMessage("[VillageDefense] Entities has failed to register!");
            Bukkit.getConsoleSender().sendMessage("[VillageDefense] Restart server or change your server version!");
        }
    }

    private World getWorld(Location location) {
        return ((CraftWorld) location.getWorld()).getHandle();
    }

    @Override
    public Villager spawnVillager(Location location) {
        RidableVillager ridableVillager = new RidableVillager(location.getWorld());
        ridableVillager.setPosition(location.getX(), location.getY(), location.getZ());
        getWorld(location).addEntity(ridableVillager, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Villager villager = (Villager) ridableVillager.getBukkitEntity();
        villager.setRemoveWhenFarAway(false);
        return villager;
    }

    @Override
    public Wolf spawnWolf(Location location) {
        WorkingWolf wolf = new WorkingWolf(location.getWorld());
        wolf.setPosition(location.getX(), location.getY(), location.getZ());
        getWorld(location).addEntity(wolf, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.setCustomNameVisible(true);
        wolf.setInvisible(false);
        return (Wolf) wolf.getBukkitEntity();
    }

    @Override
    public IronGolem spawnGolem(Location location) {
        RidableIronGolem ironGolem = new RidableIronGolem(location.getWorld());
        ironGolem.setPosition(location.getX(), location.getY(), location.getZ());
        ironGolem.setCustomNameVisible(true);
        getWorld(location).addEntity(ironGolem, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (IronGolem) ironGolem.getBukkitEntity();
    }

    @Override
    public Zombie spawnFastZombie(Location location) {
        World world = getWorld(location);
        FastZombie fastZombie = new FastZombie(world);
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        world.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnBabyZombie(Location location) {
        World world = getWorld(location);
        BabyZombie babyZombie = new BabyZombie(world);
        babyZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) babyZombie.getBukkitEntity();
        world.addEntity(babyZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnHardZombie(Location location) {
        World world = getWorld(location);
        HardZombie hardZombie = new HardZombie(world);
        hardZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) hardZombie.getBukkitEntity();
        world.addEntity(hardZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnPlayerBuster(Location location) {
        World world = getWorld(location);
        PlayerBuster playerBuster = new PlayerBuster(world);
        playerBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) playerBuster.getBukkitEntity();
        world.addEntity(playerBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnGolemBuster(Location location) {
        World world = getWorld(location);
        GolemBuster golemBuster = new GolemBuster(world);
        golemBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) golemBuster.getBukkitEntity();
        world.addEntity(golemBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnVillagerBuster(Location location) {
        World world = getWorld(location);
        VillagerBuster villagerBuster = new VillagerBuster(world);
        villagerBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) villagerBuster.getBukkitEntity();
        world.addEntity(villagerBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnKnockbackResistantZombies(Location location) {
        World world = getWorld(location);
        TankerZombie tankerZombie = new TankerZombie(world);
        tankerZombie.getAttributeInstance(GenericAttributes.c).setValue(Double.MAX_VALUE);
        tankerZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) tankerZombie.getBukkitEntity();
        world.addEntity(tankerZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnVillagerSlayer(Location location) {
        World world = getWorld(location);
        VillagerSlayer villagerSlayer = new VillagerSlayer(world);
        villagerSlayer.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) villagerSlayer.getBukkitEntity();
        world.addEntity(villagerSlayer, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public void applyFollowRange(Zombie zombie) {
        EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) zombie).getHandle();
        AttributeInstance attributes = nmsEntity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        if (attributes.a(uId) == null) { // Check if the attribute is not set
            attributes.b(new AttributeModifier(uId, "follow range multiplier", 200.0D, 1));
        }
    }
}
