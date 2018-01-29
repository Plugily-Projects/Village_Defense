package me.tomthedeveloper.stats;

import me.tomthedeveloper.Main;
import me.tomthedeveloper.User;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.ConfigurationManager;
import me.tomthedeveloper.handlers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

    public Main plugin;
    private FileConfiguration config;

    public FileStats(Main plugin) {
        this.plugin = plugin;
        config = ConfigurationManager.getConfig("STATS");
    }

    public void saveStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        config.set(player.getUniqueId().toString() + "." + stat, user.getInt(stat));
        try {
            config.save(ConfigurationManager.getFile("STATS"));
        } catch(IOException e) {
            ChatManager.sendErrorHeader("saving STATS.yml file");
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- restart the server");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- create blank file named STATS.yml");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
        }
    }

    public void loadStat(Player player, String stat) {
        User user = UserManager.getUser(player.getUniqueId());
        if(config.contains(player.getUniqueId().toString() + "." + stat))
            user.setInt(stat, config.getInt(player.getUniqueId().toString() + "." + stat));
        else
            user.setInt(stat, 0);
    }

}
