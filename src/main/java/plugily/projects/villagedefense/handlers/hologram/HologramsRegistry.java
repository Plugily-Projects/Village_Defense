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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class HologramsRegistry {

  private final List<LeaderboardHologram> leaderboardHolograms = new ArrayList<>();
  private final Main plugin;

  public HologramsRegistry(Main plugin) {
    this.plugin = plugin;
    registerHolograms();
  }

  private void registerHolograms() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "internal/holograms_data");
    org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection("holograms");
    if (section == null) {
      return;
    }

    for(String key : section.getKeys(false)) {
      LeaderboardHologram hologram;

      try {
        hologram = new LeaderboardHologram(plugin, Integer.parseInt(key), StatsStorage.StatisticType.valueOf(section.getString(key + ".statistics")),
          section.getInt(key + ".top-amount"), LocationSerializer.getLocation(section.getString(key + ".location", "")));
      } catch (IllegalArgumentException ex) {
        continue;
      }

      hologram.initUpdateTask();
      registerHologram(hologram);
    }
  }

  public void registerHologram(LeaderboardHologram hologram) {
    leaderboardHolograms.add(hologram);
  }

  public void disableHologram(int id) {
    for(LeaderboardHologram hologram : leaderboardHolograms) {
      if(hologram.getId() == id) {
        hologram.cancel();
        return;
      }
    }
  }

  public void disableHolograms() {
    leaderboardHolograms.forEach(LeaderboardHologram::cancel);
  }

}
