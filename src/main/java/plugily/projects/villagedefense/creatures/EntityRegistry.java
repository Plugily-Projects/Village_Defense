/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.Debugger;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
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

    List<String> classes = Arrays.asList("FastZombie", "BabyZombie", "PlayerBuster", "GolemBuster", "HardZombie", "TankerZombie", "VillagerSlayer", "RidableVillager", "RidableIronGolem",
        "WorkingWolf", "VillagerBuster");
    String version = plugin.getVersion();
    if (version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1")
        || version.equalsIgnoreCase("v1_13_R2") || version.equalsIgnoreCase("v1_14_R1") || version.equalsIgnoreCase("v1_15_R1") || version.equalsIgnoreCase("v1_16_R1")) {
      if (version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2") || version.equalsIgnoreCase("v1_14_R1") || version.equalsIgnoreCase("v1_15_R1") || version.equalsIgnoreCase("v1_16_R1")) {
        Debugger.debug("[EntityRegistry] Registry skipped for 1.13, 1.14, 1.15 and 1.16 took {0}ms", System.currentTimeMillis() - start);
        return;
      }
      try {
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[0]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[1]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[2]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[3]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[4]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[5]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[6]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageVillager", 120, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[7]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageVillagerGolem", 99, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[8]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageWolf", 95, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[9]));
      this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
          .invoke(this, "VillageZombie", 54, Class.forName("plugily.projects.villagedefense.creatures." + version + "." + classes.toArray()[10]));
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Could not register custom mobs in version 1.11-1.12! Plugin won't work properly!");
        plugin.getLogger().log(Level.WARNING, "Cause: " + e.getMessage());
      }
    }
    Debugger.debug("[EntityRegistry] Registry job finished took {0}ms", System.currentTimeMillis() - start);
  }

  @SuppressWarnings("unused")
  public static void registerv1_11_R1Entity(String name, int id, final Class<? extends net.minecraft.server.v1_11_R1.EntityInsentient> customClass) {
    final net.minecraft.server.v1_11_R1.MinecraftKey key = new net.minecraft.server.v1_11_R1.MinecraftKey(name);
    net.minecraft.server.v1_11_R1.EntityTypes.b.a(id, key, customClass);
    net.minecraft.server.v1_11_R1.EntityTypes.d.add(key);
  }

  @SuppressWarnings("unused")
  public void registerv1_12_R1Entity(String name, int id, Class<? extends net.minecraft.server.v1_12_R1.EntityInsentient> customClass) {
    final net.minecraft.server.v1_12_R1.MinecraftKey key = new net.minecraft.server.v1_12_R1.MinecraftKey(name);
    net.minecraft.server.v1_12_R1.EntityTypes.b.a(id, key, customClass);
    net.minecraft.server.v1_12_R1.EntityTypes.d.add(key);
  }

}
