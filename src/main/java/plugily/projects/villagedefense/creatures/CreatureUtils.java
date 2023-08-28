/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.string.StringFormatUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;

import java.util.concurrent.ThreadLocalRandom;

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

  private CreatureUtils() {
  }

  public static void init(Main plugin) {
    CreatureUtils.plugin = plugin;
    villagerNames = new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_VILLAGER_NAMES").asKey().build().split(",");
    creatureInitializer = new plugily.projects.villagedefense.creatures.v1_9_UP.CreatureInitializer();
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
   * health multiplier and follow range) to target zombie.
   *
   * @param zombie zombie to apply attributes for
   * @param arena  arena to get health multiplier from
   */
  public static void applyAttributes(Creature zombie, Arena arena) {
    creatureInitializer.applyFollowRange(zombie);
    VersionUtils.setMaxHealth(zombie, VersionUtils.getMaxHealth(zombie) + arena.getArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER"));
    zombie.setHealth(VersionUtils.getMaxHealth(zombie));
    if(plugin.getConfigPreferences().getOption("ZOMBIE_HEALTHBAR")) {
      zombie.setCustomNameVisible(true);
      zombie.setCustomName(StringFormatUtils.getProgressBar((int) zombie.getHealth(), (int) VersionUtils.getMaxHealth(zombie), 50, "|",
          ChatColor.YELLOW + "", ChatColor.GRAY + ""));
    }
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
