package pl.plajer.villagedefense3.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.database.MySQLDatabase;
import pl.plajer.villagedefense3.user.UserManager;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Plajer
 * <p>
 * Created at 18 lis 2017
 */
public class MySQLConnectionUtils {

    public static void loadPlayerStats(Player player, Main plugin) {
        boolean b = false;
        MySQLDatabase database = plugin.getMySQLDatabase();
        ResultSet resultSet = database.executeQuery("SELECT UUID from playerstats WHERE UUID='" + player.getUniqueId().toString() + "'");
        try {
            if(!resultSet.next()) {
                database.insertPlayer(player.getUniqueId().toString());
                b = true;
            }

            int gamesplayed;
            int zombiekills;
            int highestwave;
            int deaths;
            int xp;
            int level;
            int orbs;
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
        } catch(SQLException e1) {
            System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
            e1.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
            Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
        }
        if(!b) {
            try {
                if(!resultSet.next()) {
                    database.insertPlayer(player.getUniqueId().toString());
                }

                int gamesplayed;
                int zombiekills;
                int highestwave;
                int deaths;
                int xp;
                int level;
                int orbs;
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
            } catch(SQLException e1) {
                System.out.print("CONNECTION FAILED TWICE FOR PLAYER " + player.getName());
                e1.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
                Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
            }
        }
    }

}
