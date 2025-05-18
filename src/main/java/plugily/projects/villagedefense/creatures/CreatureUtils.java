/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2025 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.creatures;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.metadata.FixedMetadataValue;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 2017
 */
public class CreatureUtils {

  private static String[] villagerNames = ("Jagger,Kelsey,Kelton,Haylie,Harlow,Howard,Wulffric,Winfred,Ashley,Bailey,Beckett,Alfredo,Alfred,Adair,Edgar,ED,Eadwig,Edgaras,Buckley,Stanley,Nuffley,"
      + "Mary,Jeffry,Rosaly,Elliot,Harry,Sam,Rosaline,Tom,Ivan,Kevin,Adam,Emma,Mira,Jeff,Isac,Nico").split(",");
  private static Main plugin;
  private static BaseCreatureInitializer creatureInitializer;
  private static final List<CachedObject> cachedObjects = new ArrayList<>();

  private CreatureUtils() {
  }

  public static void init(Main plugin) {
    CreatureUtils.plugin = plugin;
    villagerNames = new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_VILLAGER_NAMES").asKey().build().split(",");
    creatureInitializer = initCreatureInitializer();
  }

  public static BaseCreatureInitializer initCreatureInitializer() {
    switch(ServerVersion.Version.getCurrent()) {
      case v1_8_8:
        return new plugily.projects.villagedefense.creatures.v1_8_R3.CreatureInitializer();
      default:
        return new plugily.projects.villagedefense.creatures.v1_9_UP.CreatureInitializer();
    }
  }

  public static Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
    for(CachedObject cachedObject : cachedObjects) {
      if(cachedObject.getClazz().equals(clazz) && cachedObject.getFieldName().equals(fieldName)) {
        return cachedObject.getObject();
      }
    }
    try {
      Field field = clazz.getDeclaredField(fieldName);

      AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
        field.setAccessible(true);
        return null;
      });

      Object o = field.get(object);
      cachedObjects.add(new CachedObject(fieldName, clazz, o));
      return o;
    } catch(NoSuchFieldException | IllegalAccessException e) {
      plugin.getLogger().log(Level.WARNING, "Failed to retrieve private field of object " + object.getClass() + "!");
      plugin.getLogger().log(Level.WARNING, e.getMessage() + " (fieldName " + fieldName + ", class " + clazz.getName() + ")");
    }
    return null;
  }

  /**
   * Check if the given entity is a arena's enemy.
   * We define the enemy as it's not the player, the villager, the wolf and the iron golem
   *
   * @param entity the entity
   * @return true if it is
   */
  public static boolean isEnemy(Entity entity) {
    return entity instanceof Creature && !(entity instanceof Player || entity instanceof Villager || entity instanceof Wolf || entity instanceof IronGolem);
  }

  /**
   * Applies attributes (i.e. health bar (if enabled),
   * health multiplier and follow range) to target creature.
   *
   * @param creature creature to apply attributes for
   * @param arena  arena to get health multiplier from
   */
  public static void applyAttributes(Creature creature, Arena arena) {
    creatureInitializer.applyFollowRange(creature);
    VersionUtils.setMaxHealth(creature, VersionUtils.getMaxHealth(creature) + arena.getArenaOption("CREATURE_DIFFICULTY_MULTIPLIER"));
    creature.setHealth(VersionUtils.getMaxHealth(creature));
    if(plugin.getConfigPreferences().getOption("CREATURES_HEALTHBAR")) {
      creature.setCustomNameVisible(true);
      creature.setMetadata(CreatureUtils.getCreatureInitializer().getCreatureCustomNameMetadata(), new FixedMetadataValue(plugin, ""));
      creature.setCustomName(CreatureUtils.getHealthNameTag(creature));
      // old method
      // creature.setCustomName(StringFormatUtils.getProgressBar((int) creature.getHealth(), (int) VersionUtils.getMaxHealth(creature), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
    }
  }

  /**
   * In damage events, health is modified after all events are listened to
   * we must apply health bar change pre damage event
   *
   * @param creature target to generate health bar for
   * @param damage   final damage taken by enemy before all events have finished
   * @return health bar adjusted to the events' damage
   */
  public static String getHealthNameTagPreDamage(Creature creature, double damage) {
    double health = creature.getHealth() - damage;
    if(health < 0) {
      health = 0;
    }
    double maxHealth = VersionUtils.getMaxHealth(creature);
    ChatColor hpColor;
    if(health >= maxHealth * 0.75) {
      hpColor = ChatColor.GREEN;
    } else if(health >= maxHealth * 0.5) {
      hpColor = ChatColor.GOLD;
    } else if(health >= maxHealth * 0.25) {
      hpColor = ChatColor.YELLOW;
    } else {
      hpColor = ChatColor.RED;
    }
    String name = creature.getMetadata(creatureInitializer.getCreatureCustomNameMetadata()).get(0).asString();
    return name + " " + hpColor + "" + ChatColor.BOLD + "" + Math.round(health) + ChatColor.GREEN + "" + ChatColor.BOLD + "/" + Math.round(maxHealth) + " ❤";
  }

  public static String getHealthNameTag(Creature creature) {
    return getHealthNameTagPreDamage(creature, 0);
  }

  public static float getZombieSpeed() {
    return 1.3f;
  }

  public static float getBabyZombieSpeed() {
    return 2.0f;
  }

  public static String[] getVillagerNames() {
    return villagerNames.clone();
  }

  public static String getRandomVillagerName() {
    return getVillagerNames()[villagerNames.length == 1 ? 0 : ThreadLocalRandom.current().nextInt(villagerNames.length)];
  }

  public static Main getPlugin() {
    return plugin;
  }

  public static BaseCreatureInitializer getCreatureInitializer() {
    return creatureInitializer;
  }
}
