/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.creatures;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import pl.plajer.villagedefense3.Main;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2018
 */
public class EntityRegistry {

  public EntityRegistry(Main plugin) {
    try {
      Main.debug(Main.LogLevel.INFO, "Initial entity registry startup");
      List<String> classes = Arrays.asList("FastZombie", "BabyZombie", "PlayerBuster", "GolemBuster", "HardZombie", "TankerZombie", "VillagerSlayer", "RidableVillager", "RidableIronGolem", "WorkingWolf");
      String version = plugin.getVersion();
      if (version.equalsIgnoreCase("v1_11_R1") || version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2")) {
        if (version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2")) {
          Main.debug(Main.LogLevel.INFO, "Skipping entity registering for 1.13");
          return;
        }
        try {
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[0]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[1]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[2]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[3]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[4]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[5]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageZombie", 54, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[6]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageVillager", 120, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[7]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageVillagerGolem", 99, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[8]));
          this.getClass().getMethod("register" + version + "Entity", String.class, int.class, Class.class)
              .invoke(this, "VillageWolf", 95, Class.forName("pl.plajer.villagedefense3.creatures." + version + "." + classes.toArray()[9]));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
      Main.debug(Main.LogLevel.INFO, "Entities registering completed");
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
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
