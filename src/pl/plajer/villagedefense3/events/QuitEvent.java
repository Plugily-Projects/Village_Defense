package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 11/08/2014.
 */
public class QuitEvent implements Listener {

    private Main plugin;

    public QuitEvent(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if(!plugin.isBungeeActivated())
            plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
    }

    @EventHandler
    public void onQuitSaveStats(PlayerQuitEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) != null) {
            plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
        }
        final User user = UserManager.getUser(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();
        List<String> temp = new ArrayList<>();
        temp.add("gamesplayed");
        temp.add("kills");
        temp.add("deaths");
        temp.add("highestwave");
        temp.add("xp");
        temp.add("level");
        temp.add("orbs");
        if(plugin.isDatabaseActivated()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for(final String s : temp) {
                    int i;
                    try {
                        i = plugin.getMySQLDatabase().getStat(player.getUniqueId().toString(), s);
                    } catch(NullPointerException npe) {
                        i = 0;
                        System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                        npe.printStackTrace();
                        BigTextUtils.errorOccured();
                        Bukkit.getConsoleSender().sendMessage("Cannot get stats from MySQL database!");
                        Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
                    }

                    if(i > user.getInt(s)) {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s) + i);
                    } else {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                    }
                }
            });
        } else {
            for(String s : temp) {
                plugin.getFileStats().saveStat(player, s);
            }
        }
    }

}
