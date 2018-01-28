package me.tomthedeveloper.utils;

import me.tomthedeveloper.Main;
import me.tomthedeveloper.User;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.stats.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Plajer
 *
 * Created at 18 lis 2017
 */
public class MySQLConnectionUtils {

	public static void loadPlayerStats(Player player, Main plugin) {
		boolean b = false;
        MySQLDatabase database = plugin.getMySQLDatabase();
        ResultSet resultSet = database.executeQuery("SELECT UUID from playerstats WHERE UUID='" + player.getUniqueId().toString() + "'");
        try {
            if (!resultSet.next()) {
                database.insertPlayer(player.getUniqueId().toString());
                b = true;
            }

            int gamesplayed = 0;
            int zombiekills = 0;
            int highestwave = 0;
            int deaths = 0;
            int xp = 0;
            int level = 0;
            int orbs = 0;
            gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
            zombiekills = database.getStat(player.getUniqueId().toString(), "kills");
            highestwave = database.getStat(player.getUniqueId().toString(), "highestwave");
            deaths = database.getStat(player.getUniqueId().toString(), "deaths");
            xp = database.getStat(player.getUniqueId().toString(), "xp");
            level = database.getStat(player.getUniqueId().toString(), "level");
            orbs = database.getStat(player.getUniqueId().toString(), "orbs");
            User user = UserManager.getUser(player.getUniqueId());
            user.setInt("gamesplayed", gamesplayed);
            user.setInt("kills", zombiekills);
            user.setInt("highestwave", highestwave);
            user.setInt("deaths", deaths);
            user.setInt("xp", xp);
            user.setInt("level", level);
            user.setInt("orbs", orbs);
            b = true;
        } catch (SQLException e1) {
            System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
            ChatManager.sendErrorHeader("saving player data in MySQL database");
            e1.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- check if you configured MySQL username, password etc. correctly");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- disable mysql option (MySQL will not work)");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
            //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (b = false) {
            try {
                if (!resultSet.next()) {
                    database.insertPlayer(player.getUniqueId().toString());
                    b = true;
                }

                int gamesplayed = 0;
                int zombiekills = 0;
                int highestwave = 0;
                int deaths = 0;
                int xp = 0;
                int level = 0;
                int orbs = 0;
                gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
                zombiekills = database.getStat(player.getUniqueId().toString(), "kills");
                highestwave = database.getStat(player.getUniqueId().toString(), "highestwave");
                deaths = database.getStat(player.getUniqueId().toString(), "deaths");
                xp = database.getStat(player.getUniqueId().toString(), "xp");
                level = database.getStat(player.getUniqueId().toString(), "level");
                orbs = database.getStat(player.getUniqueId().toString(), "orbs");
                User user = UserManager.getUser(player.getUniqueId());
                user.setInt("gamesplayed", gamesplayed);
                user.setInt("kills", zombiekills);
                user.setInt("highestwave", highestwave);
                user.setInt("deaths", deaths);
                user.setInt("xp", xp);
                user.setInt("level", level);
                user.setInt("orbs", orbs);
                b = true;
            } catch (SQLException e1) {
                System.out.print("CONNECTION FAILED TWICE FOR PLAYER " + player.getName());
                ChatManager.sendErrorHeader("saving player data in MySQL database");
                e1.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- check if you configured MySQL username, password etc. correctly");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- disable mysql option (MySQL will not work)");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
                //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
	}
	
}
