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

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;

import java.util.ArrayList;
import java.util.List;

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
    for(String key : config.getConfigurationSection("holograms").getKeys(false)) {
      String accessor = "holograms." + key + ".";
      LeaderboardHologram hologram = new LeaderboardHologram(Integer.parseInt(key), StatsStorage.StatisticType.valueOf(config.getString(accessor + "statistics")),
          config.getInt(accessor + "top-amount"), LocationSerializer.getLocation(config.getString(accessor + "location", "")));
      hologram.initHologram(plugin);
      leaderboardHolograms.add(hologram);
    }
  }

  public void registerHologram(LeaderboardHologram hologram) {
    leaderboardHolograms.add(hologram);
  }

  public void disableHologram(int id) {
    for(LeaderboardHologram hologram : leaderboardHolograms) {
      if(hologram.getId() == id) {
        hologram.stopLeaderboardUpdateTask();
        return;
      }
    }
  }

  public void disableHolograms() {
    leaderboardHolograms.forEach(LeaderboardHologram::stopLeaderboardUpdateTask);
  }

}
