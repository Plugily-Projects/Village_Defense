/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.user;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.api.event.player.VillagePlayerStatisticChangeEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.Kit;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin;
  private static long cooldownCounter = 0;
  private Player player;
  private boolean spectator = false;
  private boolean permanentSpectator = false;
  private Kit kit = KitRegistry.getDefaultKit();
  private Map<StatsStorage.StatisticType, Integer> stats = new EnumMap<>(StatsStorage.StatisticType.class);
  private Map<String, Long> cooldowns = new HashMap<>();

  public User(Player player) {
    this.player = player;
  }

  public static void init(Main plugin) {
    User.plugin = plugin;
  }

  public static void cooldownHandlerTask() {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> cooldownCounter++, 20, 20);
  }

  public Kit getKit() {
    return kit;
  }

  public void setKit(Kit kit) {
    this.kit = kit;
  }

  public Arena getArena() {
    return ArenaRegistry.getArena(player);
  }

  public Player getPlayer() {
    return player;
  }

  public boolean isSpectator() {
    return spectator;
  }

  public void setSpectator(boolean b) {
    spectator = b;
  }

  public boolean isPermanentSpectator() {
    return permanentSpectator;
  }

  public void setPermanentSpectator(boolean permanentSpectator) {
    this.permanentSpectator = permanentSpectator;
  }

  public int getStat(StatsStorage.StatisticType s) {
    if (!stats.containsKey(s)) {
      stats.put(s, 0);
      return 0;
    } else if (stats.get(s) == null) {
      return 0;
    }
    return stats.get(s);
  }

  public void setStat(StatsStorage.StatisticType s, int i) {
    stats.put(s, i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      VillagePlayerStatisticChangeEvent event = new VillagePlayerStatisticChangeEvent(getArena(), player, s, i);
      Bukkit.getPluginManager().callEvent(event);
    });
  }

  public void addStat(StatsStorage.StatisticType s, int i) {
    stats.put(s, getStat(s) + i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      VillagePlayerStatisticChangeEvent event = new VillagePlayerStatisticChangeEvent(getArena(), player, s, getStat(s));
      Bukkit.getPluginManager().callEvent(event);
    });
  }

  public boolean checkCanCastCooldownAndMessage(String cooldown) {
    if (getCooldown(cooldown) <= 0) {
      return true;
    }
    String message = plugin.getChatManager().colorMessage(Messages.KITS_ABILITY_STILL_ON_COOLDOWN);
    message = message.replaceFirst("%COOLDOWN%", Long.toString(getCooldown(cooldown)));
    player.sendMessage(message);
    return false;
  }

  public void setCooldown(String s, int seconds) {
    cooldowns.put(s, seconds + cooldownCounter);
  }

  public long getCooldown(String s) {
    if (!cooldowns.containsKey(s) || cooldowns.get(s) <= cooldownCounter) {
      return 0;
    }
    return cooldowns.get(s) - cooldownCounter;
  }

}
