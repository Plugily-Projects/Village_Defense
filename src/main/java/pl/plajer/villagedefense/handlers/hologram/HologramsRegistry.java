package pl.plajer.villagedefense.handlers.hologram;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class HologramsRegistry {

  private List<LeaderboardHologram> leaderboardHolograms = new ArrayList<>();
  private Main plugin;

  public HologramsRegistry(Main plugin) {
    this.plugin = plugin;
    registerHolograms();
  }

  private void registerHolograms() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "internal/holograms_data");
    for (String key : config.getConfigurationSection("holograms").getKeys(false)) {
      String accessor = "holograms." + key + ".";
      LeaderboardHologram hologram = new LeaderboardHologram(Integer.parseInt(key), StatsStorage.StatisticType.valueOf(config.getString(accessor + "statistic")),
          config.getInt(accessor + "top-amount"), LocationSerializer.getLocation(config.getString(accessor + "location")));
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
    for (LeaderboardHologram hologram : leaderboardHolograms) {
      hologram.stopLeaderboardUpdateTask();
    }
  }

}
