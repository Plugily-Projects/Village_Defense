package pl.plajer.villagedefense3.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.utils.BigTextUtils;
import pl.plajer.villagedefense3.utils.MySQLConnectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

    private Main plugin;
    private FileConfiguration config;

    public FileStats(Main plugin) {
        this.plugin = plugin;
        config = ConfigurationManager.getConfig("stats");
    }

    public void saveStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        config.set(player.getUniqueId().toString() + "." + stat, user.getInt(stat));
        try {
            config.save(ConfigurationManager.getFile("stats"));
        } catch(IOException e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save stats.yml file!");
            Bukkit.getConsoleSender().sendMessage("Restart the server, file COULD BE OVERRIDDEN!");
        }
    }

    public void loadStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        if(config.contains(player.getUniqueId().toString() + "." + stat))
            user.setInt(stat, config.getInt(player.getUniqueId().toString() + "." + stat));
        else
            user.setInt(stat, 0);
    }

    public void loadStatsForPlayersOnline() {
        for(final Player player : plugin.getServer().getOnlinePlayers()) {
            if(plugin.isBungeeActivated())
                plugin.getGameInstanceManager().getGameInstances().get(0).teleportToLobby(player);
            if(!plugin.isDatabaseActivated()) {
                List<String> temp = new ArrayList<>();
                temp.add("gamesplayed");
                temp.add("kills");
                temp.add("deaths");
                temp.add("highestwave");
                temp.add("xp");
                temp.add("level");
                temp.add("orbs");
                for(String s : temp) {
                    loadStat(player, s);
                }
                continue;
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> MySQLConnectionUtils.loadPlayerStats(player, plugin));
        }
    }

}
