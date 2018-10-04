/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense4.creatures;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense4.Main;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 17 lis 2017
 */
public class CreatureUtils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static Object getPrivateField(String fieldName, Class clazz, Object object) {
    Field field;
    Object o = null;
    try {
      field = clazz.getDeclaredField(fieldName);

      field.setAccessible(true);

      o = field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return o;
  }

  public static void applyHealthBar(Zombie zombie) {
    if (plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled", true)) {
      zombie.setCustomNameVisible(true);
      zombie.setCustomName(MinigameUtils.getProgressBar((int) zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(),
              (int) zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 50, "|",
              ChatColor.YELLOW + "", ChatColor.GRAY + ""));
    }
  }

}
