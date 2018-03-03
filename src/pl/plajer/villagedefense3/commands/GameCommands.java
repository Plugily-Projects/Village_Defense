package pl.plajer.villagedefense3.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 25.02.2018
 */
public class GameCommands extends MainCommand {

    private Main plugin;

    public GameCommands(Main plugin) {
        super(plugin, false);
        this.plugin = plugin;
    }

    public void sendStats(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        User user = UserManager.getUser(((Player) sender).getUniqueId());
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills") + user.getInt("kills"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths") + user.getInt("deaths"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getInt("gamesplayed"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Wave") + user.getInt("highestwave"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Level") + user.getInt("level"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Exp") + user.getInt("xp"));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Next-Level-Exp") + Math.ceil(Math.pow(50 * user.getInt("level"), 1.5)));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
    }

    public void leaveGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!plugin.getConfig().getBoolean("Disable-Leave-Command")) {
            Player p = (Player) sender;
            if(!checkIsInGameInstance((Player) sender)) return;
            p.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(p);
                System.out.print(p.getName() + " is teleported to the Hub Server");
            } else {
                plugin.getGameInstanceManager().getGameInstance(p).teleportToEndLocation(p);
                plugin.getGameInstanceManager().getGameInstance(p).leaveAttempt(p);
                System.out.print(p.getName() + " has left the arena! He is teleported to the end location.");
            }
        }
    }

    public void joinGame(CommandSender sender, String arena) {
        if(checkSenderIsConsole(sender)) return;
        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
            if(arena.equalsIgnoreCase(gameInstance.getID())) {
                gameInstance.joinAttempt((Player) sender);
                return;
            }
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
    }

}
