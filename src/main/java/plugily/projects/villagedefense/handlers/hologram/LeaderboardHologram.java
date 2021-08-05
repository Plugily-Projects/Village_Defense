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
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.handlers.hologram.messages.LanguageMessage;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class LeaderboardHologram extends BukkitRunnable {

  private final Main plugin;
  private final int id;
  private final StatsStorage.StatisticType statistic;
  private final int topAmount;
  private final Hologram hologram;
  private final Location location;
  private final List<TextLine> lines;

  public LeaderboardHologram(Main plugin, int id, StatsStorage.StatisticType statistic, int amount, Location location) {
    this.plugin = plugin;
    this.id = id;
    this.statistic = statistic;
    this.topAmount = amount;
    this.location = location;
    this.hologram = HologramsAPI.createHologram(plugin, location);
    this.lines = new ArrayList<>();

    String header = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_HEADER.getAccessor()));
    header = StringUtils.replace(header, "%amount%", Integer.toString(topAmount));
    header = StringUtils.replace(header, "%statistic%", statisticToMessage() != null ? color(plugin.getLanguageConfig().getString(statisticToMessage().getAccessor())) : "null");
    hologram.appendTextLine(header);

    for (int i = 0; i < topAmount; i++) {
      lines.add(hologram.appendTextLine(""));
    }
  }

  public void initUpdateTask() {
    runTaskTimerAsynchronously(plugin, 0, 100);
  }

  @Override
  public void run() {
    if (!plugin.isEnabled()) {
      cancel();
      return;
    }

    java.util.Map<UUID, Integer> values = StatsStorage.getStats(statistic);
    List<UUID> reverseKeys = new ArrayList<>(values.keySet());
    Collections.reverse(reverseKeys);

    for (int i = 0; i < lines.size(); i++) {
      TextLine textLine = lines.get(i);
      String text;
      if (i < reverseKeys.size()) {
        UUID uuid = reverseKeys.get(i);
        text = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT.getAccessor()));
        text = StringUtils.replace(text, "%nickname%", getPlayerNameSafely(uuid));
        text = StringUtils.replace(text, "%value%", String.valueOf(values.get(uuid)));
      } else {
        text = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT_EMPTY.getAccessor()));
      }
      text = StringUtils.replace(text, "%place%", Integer.toString(i + 1));
      textLine.setText(text);
    }
  }

  @Override
  public synchronized void cancel() {
    super.cancel();
    hologram.delete();
  }

  private String getPlayerNameSafely(UUID uuid) {
    String name = plugin.getUserManager().getDatabase().getPlayerName(uuid);
    return name != null ? name : color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_UNKNOWN_PLAYER.getAccessor()));
  }

  private LanguageMessage statisticToMessage() {
    switch(statistic) {
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

  private String color(String message) {
    return plugin.getChatManager().colorRawMessage(message);
  }
}
