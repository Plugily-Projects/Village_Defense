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

import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Zombie;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.language.LanguageManager;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 17 lis 2017
 */
public class CreatureUtils {

  private static float zombieSpeed = 1.3f;
  private static float babyZombieSpeed = 2.0f;
  private static String[] villagerNames = ("Jagger,Kelsey,Kelton,Haylie,Harlow,Howard,Wulffric,Winfred,Ashley,Bailey,Beckett,Alfredo,Alfred,Adair,Edgar,ED,Eadwig,Edgaras,Buckley,Stanley,Nuffley,"
      + "Mary,Jeffry,Rosaly,Elliot,Harry,Sam,Rosaline,Tom,Ivan,Kevin,Adam").split(",");
  private static Main plugin;
  private static final List<CachedObject> cachedObjects = new ArrayList<>();

  private static final UUID uId = UUID.fromString("206a89dc-ae78-4c4d-b42c-3b31db3f5a7e");

  private CreatureUtils() {
  }

  public static void init(Main plugin) {
    CreatureUtils.plugin = plugin;
    zombieSpeed = (float) plugin.getConfig().getDouble("Zombie-Speed", 1.3);
    babyZombieSpeed = (float) plugin.getConfig().getDouble("Mini-Zombie-Speed", 2.0);
    villagerNames = LanguageManager.getLanguageMessage("In-Game.Villager-Names").split(",");
  }

  public static Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
    for(CachedObject cachedObject : cachedObjects) {
      if(cachedObject.getClazz().equals(clazz) && cachedObject.getFieldName().equals(fieldName)) {
        return cachedObject.getObject();
      }
    }
    Field field;
    Object o = null;
    try {
      field = clazz.getDeclaredField(fieldName);

      AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
        field.setAccessible(true);
        return null;
      });

      o = field.get(object);
      cachedObjects.add(new CachedObject(fieldName, clazz, o));
    } catch(NoSuchFieldException | IllegalAccessException e) {
      plugin.getLogger().log(Level.WARNING, "Failed to retrieve private field of object " + object.getClass() + "!");
      plugin.getLogger().log(Level.WARNING, e.getMessage() + " (fieldName " + fieldName + ", class " + clazz.getName() + ")");
    }
    return o;
  }

  /**
   * Applies attributes (i.e. health bar (if enabled),
   * health multiplier and follow range) to target zombie.
   *
   * @param zombie zombie to apply attributes for
   * @param arena  arena to get health multiplier from
   */
  public static void applyAttributes(Zombie zombie, Arena arena) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      MiscUtils.getEntityAttribute(zombie, Attribute.GENERIC_FOLLOW_RANGE).ifPresent(ai -> ai.setBaseValue(200.0D));
    } else {
      EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) zombie).getHandle();
      AttributeInstance attributes = nmsEntity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
      if (attributes.a(uId) == null) { // Check if the attribute is not set
        attributes.b(new AttributeModifier(uId, "follow range multiplier", 200.0D, 1));
      }
    }
    VersionUtils.setMaxHealth(zombie, VersionUtils.getMaxHealth(zombie) + arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
    zombie.setHealth(VersionUtils.getMaxHealth(zombie));
    if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled", true)) {
      zombie.setCustomNameVisible(true);
      zombie.setCustomName(StringFormatUtils.getProgressBar((int) zombie.getHealth(), (int) VersionUtils.getMaxHealth(zombie), 50, "|",
          ChatColor.YELLOW + "", ChatColor.GRAY + ""));
    }
  }

  public static float getZombieSpeed() {
    return zombieSpeed;
  }

  public static float getBabyZombieSpeed() {
    return babyZombieSpeed;
  }

  public static String[] getVillagerNames() {
    return villagerNames.clone();
  }

  public static Main getPlugin() {
    return plugin;
  }
}
