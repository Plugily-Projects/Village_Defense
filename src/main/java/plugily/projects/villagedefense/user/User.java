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

package plugily.projects.villagedefense.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.api.event.player.VillagePlayerStatisticChangeEvent;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.Kit;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin;
  private static long cooldownCounter = 0;
  private final Player player;
  private boolean spectator = false;
  private boolean permanentSpectator = false;
  private Kit kit = KitRegistry.getDefaultKit();
  private final Map<StatsStorage.StatisticType, Integer> stats = new EnumMap<>(StatsStorage.StatisticType.class);
  private final Map<String, Long> cooldowns = new HashMap<>();

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
    if(!stats.containsKey(s)) {
      stats.put(s, 0);
      return 0;
    } else if(stats.get(s) == null) {
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
    if(getCooldown(cooldown) <= 0) {
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
    return (!cooldowns.containsKey(s) || cooldowns.get(s) <= cooldownCounter) ? 0 : cooldowns.get(s) - cooldownCounter;
  }

}
