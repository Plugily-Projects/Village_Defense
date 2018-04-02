package pl.plajer.villagedefense3.creatures;

import org.bukkit.Bukkit;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2018
 */
public class EntityRegistry {

    public EntityRegistry(Main plugin) {
        List<String> classes = Arrays.asList("FastZombie", "BabyZombie", "PlayerBuster", "GolemBuster", "HardZombie", "RidableVillager", "RidableIronGolem", "WorkingWolf", "TankerZombie");
        String version = plugin.getVersion();
        if(version.equalsIgnoreCase("v1_8_R3") || version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_9_R1") || version.equalsIgnoreCase("v1_12_R1")) {
            try {
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[0]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[1]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[2]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[3]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[4]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageVillager", 120, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[5]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageVillagerGolem", 99, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[6]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageWolf", 95, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[7]));
                this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class).invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[8]));
            } catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public void registerv1_8_R3Entity(String name, int id, Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient> customClass) {
        try {

            List<Map<?, ?>> dataMaps = new ArrayList<>();
            for(Field f : net.minecraft.server.v1_8_R3.EntityTypes.class.getDeclaredFields()) {
                if(f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }

            ((Map<Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
            ((Map<Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);

        } catch(Exception e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Entities has failed to register!");
            Bukkit.getConsoleSender().sendMessage("Restart server or change your server version!");
        }
    }

    @SuppressWarnings("unused")
    public void registerv1_9_R1Entity(String name, int id, Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient> customClass) {
        try {
            List<Map<?, ?>> dataMaps = new ArrayList<>();
            for(Field f : net.minecraft.server.v1_9_R1.EntityTypes.class.getDeclaredFields()) {
                if(f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }
            ((Map<Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
            ((Map<Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
        } catch(Exception e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Entities has failed to register!");
            Bukkit.getConsoleSender().sendMessage("Restart server or change your server version!");
        }
    }

    @SuppressWarnings("unused")
    public static void registerv1_11_R1Entity(String name, int id, final Class<? extends net.minecraft.server.v1_11_R1.EntityInsentient> customClass) {
        final net.minecraft.server.v1_11_R1.MinecraftKey key = new net.minecraft.server.v1_11_R1.MinecraftKey(name);
        net.minecraft.server.v1_11_R1.EntityTypes.b.a(id, key, customClass);
        if(!net.minecraft.server.v1_11_R1.EntityTypes.d.contains(key)) {
            net.minecraft.server.v1_11_R1.EntityTypes.d.add(key);
        }
    }

    @SuppressWarnings("unused")
    public void registerv1_12_R1Entity(String name, int id, Class<? extends net.minecraft.server.v1_12_R1.EntityInsentient> customClass) {
        final net.minecraft.server.v1_12_R1.MinecraftKey key = new net.minecraft.server.v1_12_R1.MinecraftKey(name);
        net.minecraft.server.v1_12_R1.EntityTypes.b.a(id, key, customClass);
        if(!net.minecraft.server.v1_12_R1.EntityTypes.d.contains(key)) {
            net.minecraft.server.v1_12_R1.EntityTypes.d.add(key);
        }
    }

}
