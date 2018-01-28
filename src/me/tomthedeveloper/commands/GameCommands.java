package me.tomthedeveloper.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.tomthedeveloper.GameAPI;
import me.tomthedeveloper.User;
import me.tomthedeveloper.bungee.Bungee;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.game.GameState;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.permissions.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommands {

	private GameAPI plugin;

	public GameCommands(GameAPI plugin) {
		this.plugin = plugin;
		if (!plugin.getPlugin().getConfig().getBoolean("Disable-Leave-Command")) {
			plugin.getPlugin().getCommand("leave").setExecutor(plugin.getPlugin());
		}
		plugin.getPlugin().getCommand("smartstop").setExecutor(plugin.getPlugin());
		plugin.getPlugin().getCommand("setshopchest").setExecutor(plugin.getPlugin());
		plugin.getPlugin().getCommand("stats").setExecutor(plugin.getPlugin());
	}

	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		/*
		 *
		 * LEAVE COMMAND
		 *
		 */
		if (command.getName().equalsIgnoreCase("leave")) {
			if (!(commandSender instanceof Player))
				return true;
			Player player = (Player) commandSender;
			if (plugin.getGameInstanceManager().getGameInstance(player) == null) {
				System.out.print(player.getName() + " tried /leave but isn't in an arena!");
				return true;
			}
			if (plugin.isBungeeActivated()) {
				Bungee.connectToHub(player);
				System.out.print(player.getName() + " is teleported to the Hub Server");
				return true;
			} else {

				plugin.getGameInstanceManager().getGameInstance(player).teleportToEndLocation(player);
				plugin.getGameInstanceManager().getGameInstance(player).leaveAttempt(player);
				System.out.print(player.getName() + " has left the arena! He is teleported to the end location.");
				return true;
			}
		}
		/*
		 *
		 * STATS COMMAND
		 *
		 */
		if (command.getName().equalsIgnoreCase("stats")) {
			if (commandSender instanceof ConsoleCommandSender) {
				return true;
			}
			Player player = (Player) commandSender;
			User user = UserManager.getUser(player.getUniqueId());
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Header"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Kills"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Deaths"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Games-Played"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Highest-Wave") + user.getInt("highestwave"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Level") + user.getInt("level"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Exp") + user.getInt("xp"));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getInt("level"), 1.5)));
			player.sendMessage(ChatManager.colorMessage("commands.Stats-Command.Footer"));
			return true;
		}
		/*
		 *
		 * SMARTSTOP COMMAND
		 *
		 */
		if (command.getName().equalsIgnoreCase("smartstop") && commandSender.isOp()) {
			GameAPI.setRestart();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.sendMessage(ChatColor.DARK_GREEN + "RESTARTING THE SERVER AFTER ALL THE GAMES ENDED!");
				commandSender.sendMessage(ChatColor.GRAY + "Restarting process started!");

				Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin.getPlugin(), () -> {
					boolean b = true;
					for (GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {

						if (gameInstance.getGameState() == GameState.INGAME || gameInstance.getGameState() == GameState.STARTING)
							b = false;
					}
					if (b) {
						plugin.getPlugin().getServer().shutdown();
					}
					b = true;

				}, 20L, 1L);
			}
			return true;
		}
		/*
		 *
		 * SETSHOPCHEST COMMAND
		 *
		 */
		if (command.getName().equalsIgnoreCase("setshopchest")) {
			if (!(commandSender instanceof Player))
				return true;
			Player player = (Player) commandSender;
			if (!(player.isOp() || player.hasPermission(PermissionsManager.getEditGames())))
				return true;
			if (plugin.getWorldEditPlugin().getSelection(player) == null)
				return true;
			Selection selection = plugin.getWorldEditPlugin().getSelection(player);
			if ((selection.getMaximumPoint().getX() != selection.getMinimumPoint().getX() ||
					selection.getMaximumPoint().getY() != selection.getMaximumPoint().getY() ||
					selection.getMaximumPoint().getZ() != selection.getMinimumPoint().getZ()))
				return true;
			plugin.saveLoc("shop.location", plugin.getWorldEditPlugin().getSelection(player).getMinimumPoint());
			player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
			return true;
		}
		return false;
	}

}
