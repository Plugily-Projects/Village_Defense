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

package plugily.projects.villagedefense.creatures;

import org.bukkit.Bukkit;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2018
 */
public class EntityRegistry {

  public EntityRegistry(Main plugin) {
    Debugger.debug("[EntityRegistry] Registry startup");
    long start = System.currentTimeMillis();

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_13_R1)) {
      Debugger.debug("[EntityRegistry] Registry skipped for 1.13, 1.14, 1.15 and 1.16 took {0}ms", System.currentTimeMillis() - start);
      return;
    }

    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
      String[] classes = {"FastZombie", "BabyZombie", "PlayerBuster", "GolemBuster", "HardZombie", "TankerZombie", "VillagerSlayer", "RidableVillager", "RidableIronGolem",
          "WorkingWolf", "VillagerBuster"};

      String version = ServerVersion.Version.getPackageVersion()[3];
      try {
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[0]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[1]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[2]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[3]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[4]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[5]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[6]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageVillager", 120, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[7]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageVillagerGolem", 99, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[8]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageWolf", 95, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[9]));
        this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
            .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes[10]));
      } catch(Exception e) {
        plugin.getLogger().log(Level.WARNING, "Could not register custom mobs in version 1.8.8-1.12! Plugin won't work properly!");
        plugin.getLogger().log(Level.WARNING, "Cause: " + e.getMessage());
      }
    }
    Debugger.debug("[EntityRegistry] Registry job finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void registerv1_8_R3Entity(String name, int id, Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient> customClass) {
    try {
      List<Map<?, ?>> dataMaps = new ArrayList<>();
      for (Field f : net.minecraft.server.v1_8_R3.EntityTypes.class.getDeclaredFields()) {
        if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
          f.setAccessible(true);
          dataMaps.add((Map<?, ?>) f.get(null));
        }
      }
      ((Map<Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
      ((Map<Class<? extends net.minecraft.server.v1_8_R3.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
    } catch (Exception e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Entities has failed to register!");
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Restart server or change your server version!");
    }
  }

  @SuppressWarnings("unused")
  public void registerv1_9_R1Entity(String name, int id, Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient> customClass) {
    try {
      List<Map<?, ?>> dataMaps = new ArrayList<>();
      for (Field f : net.minecraft.server.v1_9_R1.EntityTypes.class.getDeclaredFields()) {
        if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
          f.setAccessible(true);
          dataMaps.add((Map<?, ?>) f.get(null));
        }
      }
      ((Map<Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
      ((Map<Class<? extends net.minecraft.server.v1_9_R1.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
    } catch (Exception e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Entities has failed to register!");
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Restart server or change your server version!");
    }
  }

  @SuppressWarnings("unused")
  public void registerv1_9_R2Entity(String name, int id, Class<? extends net.minecraft.server.v1_9_R2.EntityInsentient> customClass) {
    try {
      List<Map<?, ?>> dataMaps = new ArrayList<>();
      for (Field f : net.minecraft.server.v1_9_R2.EntityTypes.class.getDeclaredFields()) {
        if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
          f.setAccessible(true);
          dataMaps.add((Map<?, ?>) f.get(null));
        }
      }
      ((Map<Class<? extends net.minecraft.server.v1_9_R2.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
      ((Map<Class<? extends net.minecraft.server.v1_9_R2.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
    } catch (Exception e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Entities has failed to register!");
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Restart server or change your server version!");
    }
  }

  @SuppressWarnings("unused")
  public static void registerv1_10_R1Entity(String name, int id, final Class<? extends net.minecraft.server.v1_10_R1.EntityInsentient> customClass) {
    try {
      List<Map<?, ?>> dataMaps = new ArrayList<>();
      for (Field f : net.minecraft.server.v1_10_R1.EntityTypes.class.getDeclaredFields()) {
        if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
          f.setAccessible(true);
          dataMaps.add((Map<?, ?>) f.get(null));
        }
      }
      ((Map<Class<? extends net.minecraft.server.v1_10_R1.EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
      ((Map<Class<? extends net.minecraft.server.v1_10_R1.EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);
    } catch (Exception e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Entities has failed to register!");
      Bukkit.getConsoleSender().sendMessage("[VillageDefense] Restart server or change your server version!");
    }
  }

  public static void registerv1_11_R1Entity(String name, int id, final Class<? extends net.minecraft.server.v1_11_R1.EntityInsentient> customClass) {
    final net.minecraft.server.v1_11_R1.MinecraftKey key = new net.minecraft.server.v1_11_R1.MinecraftKey(name);
    net.minecraft.server.v1_11_R1.EntityTypes.b.a(id, key, customClass);
    net.minecraft.server.v1_11_R1.EntityTypes.d.add(key);
  }

  public void registerv1_12_R1Entity(String name, int id, Class<? extends net.minecraft.server.v1_12_R1.EntityInsentient> customClass) {
    final net.minecraft.server.v1_12_R1.MinecraftKey key = new net.minecraft.server.v1_12_R1.MinecraftKey(name);
    net.minecraft.server.v1_12_R1.EntityTypes.b.a(id, key, customClass);
    net.minecraft.server.v1_12_R1.EntityTypes.d.add(key);
  }

}
