package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;
import pl.plajer.villagedefense3.language.LanguageManager;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

    public static String PLUGIN_PREFIX;
    public final static ChatColor PREFIX = ChatColor.GOLD;
    public final static ChatColor NORMAL = ChatColor.GRAY;
    public final static ChatColor HIGHLIGHTED = ChatColor.AQUA;

    public ChatManager(String prefix){
        PLUGIN_PREFIX = prefix;
    }

    public static String colorRawMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String colorMessage(String message) {
        try {
            return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
        } catch(NullPointerException e1) {
            e1.printStackTrace();
            MessageUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Game message not found!");
            Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
            Bukkit.getConsoleSender().sendMessage("Access string: " + message);
            return "ERR_MESSAGE_NOT_FOUND";
        }
    }

    public static String formatMessage(Arena arena, String message, Player[] players) {
        String returnstring = message;
        for(Player player : players) {
            returnstring = returnstring.replaceFirst("%PLAYER%", player.getName());
        }
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = colorRawMessage(returnstring);
        return returnstring;
    }

    public static String formatMessage(Arena arena, String message, int integer) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(integer));

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = colorRawMessage(returnstring);
        return returnstring;
    }

    public static String formatMessage(Arena arena, String message) {
        String returnstring = message;

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = colorRawMessage(returnstring);
        return returnstring;
    }

    public static String formatMessage(Arena arena, String message, Player player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = colorRawMessage(returnstring);
        return returnstring;
    }

    public static String formatMessage(String message, Kit kit) {
        String returnstring = message;
        returnstring = returnstring.replaceFirst("%KIT%", kit.getName());
        return returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }

    public static void broadcastJoinMessage(Arena arena, Player p) {
        String message = formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Join"), p);
        for(Player player : arena.getPlayers()) {
            player.sendMessage(PLUGIN_PREFIX + message);
        }
    }

    public static void broadcastLeaveMessage(Arena arena, Player p) {
        String message = formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Leave"), p);
        for(Player player : arena.getPlayers()) {
            player.sendMessage(PLUGIN_PREFIX + message);
        }
    }

    public static void broadcastDeathMessage(Arena arena, Player player) {
        String message = formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Death"), player);
        for(Player p : arena.getPlayers()) {
            p.sendMessage(PLUGIN_PREFIX + message);
        }
    }

}

