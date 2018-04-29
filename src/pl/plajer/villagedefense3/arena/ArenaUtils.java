package pl.plajer.villagedefense3.arena;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaUtils {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static void hidePlayer(Player p, Arena arena) {
        for(Player player : arena.getPlayers()) {
            player.hidePlayer(p);
        }
    }

    public static void showPlayer(Player p, Arena arena) {
        for(Player player : arena.getPlayers()) {
            player.showPlayer(p);
        }
    }

    public static void hidePlayersOutsideTheGame(Player player, Arena arena) {
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(arena.getPlayers().contains(players)) continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    public static void bringDeathPlayersBack(Arena arena) {
        for(Player player : arena.getPlayers()) {
            if(!arena.getPlayersLeft().contains(player)) {
                User user = UserManager.getUser(player.getUniqueId());
                user.setFakeDead(false);
                user.setSpectator(false);

                arena.teleportToStartLocation(player);
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setGameMode(GameMode.SURVIVAL);
                arena.showPlayers();
                player.getInventory().clear();
                user.getKit().giveKitItems(player);
                player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
            }
        }
    }

    public static void updateLevelStat(Player player, Arena arena) {
        User user = UserManager.getUser(player.getUniqueId());
        if(Math.pow(50 * user.getInt("level"), 1.5) < user.getInt("xp")) {
            user.addInt("level", 1);
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.You-Leveled-Up"), user.getInt("level")));
        }
    }

}
