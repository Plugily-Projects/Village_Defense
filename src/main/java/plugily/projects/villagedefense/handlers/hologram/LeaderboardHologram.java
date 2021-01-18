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

package plugily.projects.villagedefense.handlers.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.handlers.hologram.messages.LanguageMessage;
import plugily.projects.villagedefense.user.data.MysqlManager;

import java.util.*;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class LeaderboardHologram {

  private final int id;
  private final StatsStorage.StatisticType statistic;
  private final int topAmount;
  private Hologram hologram;
  private final Location location;
  private BukkitTask task;

  public LeaderboardHologram(int id, StatsStorage.StatisticType statistic, int amount, Location location) {
    this.id = id;
    this.statistic = statistic;
    this.topAmount = amount;
    this.location = location;
  }

  public void initHologram(Main plugin) {
    hologram = HologramsAPI.createHologram(plugin, location);
    hologramUpdateTask(plugin);
  }

  private void hologramUpdateTask(Main plugin) {
    task = new BukkitRunnable() {
      @Override
      public void run() {
        hologram.clearLines();
        String header = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_HEADER.getAccessor()));
        header = StringUtils.replace(header, "%amount%", String.valueOf(topAmount));
        header = StringUtils.replace(header, "%statistic%", statisticToMessage() != null ? color(plugin.getLanguageConfig().getString(statisticToMessage().getAccessor())) : "null");
        hologram.appendTextLine(header);
        int limit = topAmount;
        LinkedHashMap<UUID, Integer> values = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statistic);
        List<UUID> reverseKeys = new ArrayList<>(values.keySet());
        Collections.reverse(reverseKeys);
        for (UUID key : reverseKeys) {
          if (limit == 0) {
            break;
          }
          String format = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT.getAccessor()));
          format = StringUtils.replace(format, "%place%", String.valueOf((topAmount - limit) + 1));
          format = StringUtils.replace(format, "%nickname%", getPlayerNameSafely(key, plugin));
          format = StringUtils.replace(format, "%value%", String.valueOf(values.get(key)));
          hologram.appendTextLine(format);
          limit--;
        }
        if (limit > 0) {
          for (int i = 0; i < limit; limit--) {
            String format = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT_EMPTY.getAccessor()));
            format = StringUtils.replace(format, "%place%", String.valueOf((topAmount - limit) + 1));
            hologram.appendTextLine(format);
          }
        }
      }
    }.runTaskTimer(plugin, 0, 100);
  }

  private String getPlayerNameSafely(UUID uuid, Main plugin) {
    try {
      if (plugin.getUserManager().getDatabase() instanceof MysqlManager) {
        return ((MysqlManager) plugin.getUserManager().getDatabase()).getDatabase().executeQuery("Select `name` FROM " + ((MysqlManager) plugin.getUserManager().getDatabase()).getTableName()
                + " WHERE UUID='" + uuid.toString() + "';").toString();
      }
      return Bukkit.getOfflinePlayer(uuid).getName();
    } catch (NullPointerException ex) {
      return color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_UNKNOWN_PLAYER.getAccessor()));
    }
  }

  private LanguageMessage statisticToMessage() {
    switch (statistic) {
      case KILLS:
        return LanguageMessage.STATISTIC_KILLS;
      case DEATHS:
        return LanguageMessage.STATISTIC_DEATHS;
      case GAMES_PLAYED:
        return LanguageMessage.STATISTIC_GAMES_PLAYED;
      case HIGHEST_WAVE:
        return LanguageMessage.STATISTIC_HIGHEST_WAVE;
      case LEVEL:
        return LanguageMessage.STATISTIC_LEVEL;
      case XP:
        return LanguageMessage.STATISTIC_EXP;
      case ORBS:
      default:
        return null;
    }
  }

  public int getId() {
    return id;
  }

  public StatsStorage.StatisticType getStatistic() {
    return statistic;
  }

  public int getTopAmount() {
    return topAmount;
  }

  public Hologram getHologram() {
    return hologram;
  }

  public Location getLocation() {
    return location;
  }

  public void stopLeaderboardUpdateTask() {
    hologram.delete();
    if (task != null && !task.isCancelled()) {
      task.cancel();
    }
  }

  private String color(String message) {
    return JavaPlugin.getPlugin(Main.class).getChatManager().colorRawMessage(message);
  }

}
