package me.tomthedeveloper.handlers;

import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.kitapi.basekits.Kit;
import me.tomthedeveloper.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

	public static ChatColor PREFIX = ChatColor.GOLD;
	public static ChatColor NORMAL = ChatColor.GRAY;
	public static ChatColor HIGHLIGHTED = ChatColor.AQUA;
	public static String ERRORPREFIX = "╔═══╗────────────────────────────╔╗\r\n" + 
			"║╔══╝────────────────────────────║║\r\n" + 
			"║╚══╦═╦═╦══╦═╗╔══╦══╦══╦╗╔╦═╦══╦═╝║\r\n" + 
			"║╔══╣╔╣╔╣╔╗║╔╝║╔╗║╔═╣╔═╣║║║╔╣║═╣╔╗║\r\n" + 
			"║╚══╣║║║║╚╝║║─║╚╝║╚═╣╚═╣╚╝║║║║═╣╚╝║\r\n" + 
			"╚═══╩╝╚╝╚══╩╝─╚══╩══╩══╩══╩╝╚══╩══╝\r\n";
	public static final String PLUGINPREFIX = colorMessage("In-game.Plugin-Prefix");

	private static GameInstance gameInstance;

	public ChatManager(GameInstance gameInstance) {
		this.gameInstance = gameInstance;
	}
	
	public static void sendErrorHeader(String problem) {
		Bukkit.getConsoleSender().sendMessage(ChatManager.ERRORPREFIX);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-------------------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "It seems that you've occured an error with " + problem + "!");
	}

	public void broadcastJoinMessage(Player p){
		String message = formatMessage(ChatManager.colorMessage("In-game.Messages.Join"), p);
		for(Player player:gameInstance.getPlayers()) {
			player.sendMessage(PLUGINPREFIX + message);
		}
	}

	public void broadcastLeaveMessage(Player p){
		String message = formatMessage(ChatManager.colorMessage("In-game.Messages.Leave"), p);
		for(Player player:gameInstance.getPlayers()) {
			player.sendMessage(PLUGINPREFIX + message);
		}
	}

	public void broadcastDeathMessage(Player player){
		String message = formatMessage(ChatManager.colorMessage("In-game.Messages.Death"), player);
		for(Player p : gameInstance.getPlayers()) {
			p.sendMessage(PLUGINPREFIX + message);
		}
	}

	public static String colorMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
	}

	public static String formatMessage(String message, Player[] players){
		String returnstring = message;
		for(Player player:players){
			returnstring = returnstring.replaceFirst("%PLAYER%", player.getName());
		}
		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));

		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public static String formatMessage(String message, int integer){
		String returnstring = message;
		returnstring = returnstring.replaceAll("%NUMBER%",Integer.toString(integer));

		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));

		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public static String formatMessage(String message){
		String returnstring = message;

		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public static String formatMessage(String message, Player player){
		String returnstring = message;
		returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public String formatMessage(String message, OfflinePlayer player){
		String returnstring = message;
		returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public String formatMessage(String message, String playername){
		String returnstring = message;
		returnstring = returnstring.replaceAll("%PLAYER%", playername);
		returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
		returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
		returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		returnstring =  returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
		returnstring =  returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
		returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
		returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
		returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
		returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
		returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
		return returnstring;
	}

	public static String formatMessage(String message, Kit kit){
		String returnstring = message;
		returnstring = returnstring.replaceFirst("%KIT%", kit.getName());
		return returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}

}

