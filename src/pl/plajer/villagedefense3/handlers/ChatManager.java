package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;
import pl.plajer.villagedefense3.utils.BigTextUtils;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

    public static final String PLUGINPREFIX = colorMessage("In-Game.Plugin-Prefix");
    public static ChatColor PREFIX = ChatColor.GOLD;
    public static ChatColor NORMAL = ChatColor.GRAY;
    public static ChatColor HIGHLIGHTED = ChatColor.AQUA;
    private static GameInstance gameInstance;

    public ChatManager(GameInstance gameInstance) {
        ChatManager.gameInstance = gameInstance;
    }

    public static String colorMessage(String message) {
        try {
            return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
        } catch(NullPointerException e1) {
            e1.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Game message not found!");
            Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
            Bukkit.getConsoleSender().sendMessage("Access string: " + message);
            return "ERR_MESSAGE_NOT_FOUND";
        }
    }

    public static String formatMessage(String message, Player[] players) {
        String returnstring = message;
        for(Player player : players) {
            returnstring = returnstring.replaceFirst("%PLAYER%", player.getName());
        }
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));

        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message, int integer) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(integer));

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));

        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message) {
        String returnstring = message;

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message, Player player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message, Kit kit) {
        String returnstring = message;
        returnstring = returnstring.replaceFirst("%KIT%", kit.getName());
        return returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }

    public void broadcastJoinMessage(Player p) {
        String message = formatMessage(ChatManager.colorMessage("In-Game.Messages.Join"), p);
        for(Player player : gameInstance.getPlayers()) {
            player.sendMessage(PLUGINPREFIX + message);
        }
    }

    public void broadcastLeaveMessage(Player p) {
        String message = formatMessage(ChatManager.colorMessage("In-Game.Messages.Leave"), p);
        for(Player player : gameInstance.getPlayers()) {
            player.sendMessage(PLUGINPREFIX + message);
        }
    }

    public void broadcastDeathMessage(Player player) {
        String message = formatMessage(ChatManager.colorMessage("In-Game.Messages.Death"), player);
        for(Player p : gameInstance.getPlayers()) {
            p.sendMessage(PLUGINPREFIX + message);
        }
    }

    public String formatMessage(String message, OfflinePlayer player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String formatMessage(String message, String playername) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", playername);
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

}

