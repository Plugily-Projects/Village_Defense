/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.api.event.player.VillagePlayerRespawnEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaUtils extends PluginArenaUtils {

  private ArenaUtils() {
    super();
  }

  public static void bringDeathPlayersBack(Arena arena) {
    List<Player> left = arena.getPlayersLeft();
    org.bukkit.Location startLoc = arena.getStartLocation();

    for(Player player : arena.getPlayers()) {
      if(left.contains(player)) {
        continue;
      }

      User user = getPlugin().getUserManager().getUser(player);
      if(user.isPermanentSpectator() && !getPlugin().getConfigPreferences().getOption("RESPAWN_IN_GAME_JOIN")) {
        continue;
      }

      VillagePlayerRespawnEvent event = new VillagePlayerRespawnEvent(player, arena);
      Bukkit.getPluginManager().callEvent(event);
      if(event.isCancelled()) {
        continue;
      }

      user.setSpectator(false);

      VersionUtils.teleport(player, startLoc);
      player.setFlying(false);
      player.setAllowFlight(false);
      //the default fly speed
      player.setFlySpeed(0.1f);
      player.setGameMode(GameMode.SURVIVAL);
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.removePotionEffect(PotionEffectType.SPEED);
      player.getInventory().clear();
      ArenaUtils.showPlayer(player, arena);
      user.getKit().giveKitItems(player);
      player.updateInventory();
      new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_RESPAWNED").asKey().player(player).arena(arena).sendPlayer();
    }
  }

  public static void removeSpawnedEnemies(Arena arena) {
    removeSpawnedEnemies(arena, arena.getEnemies().size(), Double.MAX_VALUE);
  }

  public static void removeSpawnedEnemies(Arena arena, int amount, double maxHealthToRemove) {
    List<Creature> toRemove = new ArrayList<>(arena.getEnemies());
    toRemove.removeIf(creature -> creature.getHealth() > maxHealthToRemove);
    if(toRemove.size() > amount) {
      Collections.shuffle(toRemove, ThreadLocalRandom.current());
      while(toRemove.size() > amount && !toRemove.isEmpty()) {
        toRemove.remove(0);
      }
    }
    arena.getEnemies().removeAll(toRemove);

    boolean eachThree = toRemove.size() > 70;
    for(int i = 0; i < toRemove.size(); i++) {
      Creature creature = toRemove.get(i);
      if(!eachThree || (i % 3) == 0) {
        VersionUtils.sendParticles("LAVA", arena.getPlayers(), creature.getLocation(), 20);
      }
      creature.remove();
    }
  }

}
