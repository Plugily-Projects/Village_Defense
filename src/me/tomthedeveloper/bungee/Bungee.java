package me.tomthedeveloper.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.tomthedeveloper.GameAPI;
import me.tomthedeveloper.Main;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.game.GameState;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tom on 31/08/2014.
 */
public class Bungee implements Listener {

    public static GameAPI plugin;

    private FileConfiguration motdsconfig;
    private HashMap<GameState, String> motds = new HashMap<>();

    public Bungee() {
        motdsconfig = ConfigurationManager.getConfig("MOTD");
        if(!motdsconfig.contains("WAITING_FOR_PLAYERS")) {
            motds.put(GameState.WAITING_FOR_PLAYERS, "WAITING_FOR_PLAYERS");
            motds.put(GameState.STARTING, "STARTING");
            motds.put(GameState.INGAME, "INGAME");
            motds.put(GameState.ENDING, "ENDING");
            motds.put(GameState.RESTARTING, "RESTARTING");
            motdsconfig.set("WAITING_FOR_PLAYERS", "WAITING_FOR_PLAYERS");
            motdsconfig.set("STARTING", "STARTING");
            motdsconfig.set("INGAME", "INGAME");
            motdsconfig.set("ENDING", "ENDING");
            motdsconfig.set("RESTARTING", "RESTARTING");
            try {
                motdsconfig.save(ConfigurationManager.getFile("MOTD"));
            } catch(IOException e) {
                ChatManager.sendErrorHeader("saving MOTD file");
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- create blank file named MOTD.yml if it doesn't exists");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- disable bungee option in config (Bungeecord support will not work)");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
            }
        } else {
            motds.put(GameState.WAITING_FOR_PLAYERS, motdsconfig.getString("WAITING_FOR_PLAYERS"));
            motds.put(GameState.STARTING, motdsconfig.getString("STARTING"));
            motds.put(GameState.INGAME, motdsconfig.getString("INGAME"));
            motds.put(GameState.ENDING, motdsconfig.getString("ENDING"));
            motds.put(GameState.RESTARTING, motdsconfig.getString("RESTARTING"));
        }
    }

    public static void connectToHub(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getHubServerName());
        player.sendPluginMessage(plugin.getPlugin(), "BungeeCord", out.toByteArray());
    }

    private String getMotD() {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstances().get(0);
        if(gameInstance.getGameState() == GameState.STARTING && (gameInstance.getTimer() <= 3)) {
            return motds.get(GameState.INGAME);
        } else {
            return motds.get(gameInstance.getGameState());
        }
    }


    public static String getHubServerName() {
        return ConfigurationManager.getConfig("bungee").getString("Hub");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if(plugin.getGameInstanceManager() == null)
            return;
        if(plugin.getGameInstanceManager().getGameInstances().isEmpty())
            return;
        if(plugin.getGameInstanceManager().getGameInstances() == null) {
            if(Main.isDebugged()) {
                System.out.print("[Village Debugger] NO GAMEINSTANCE FOUND! FIRST CONFIGURE AN ARENA BEFORE ACTIVATING BUNGEEEMODE!");
            }
            return;
        }
        event.setMaxPlayers(plugin.getGameInstanceManager().getGameInstances().get(0).getMAX_PLAYERS());
        event.setMotd(this.getMotD());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        plugin.getPlugin().getServer().getScheduler().runTaskLater(plugin.getPlugin(), () -> {
            plugin.getGameInstanceManager().getGameInstances().get(0).joinAttempt(event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) != null)
            plugin.getGameInstanceManager().getGameInstances().get(0).leaveAttempt(event.getPlayer());

    }

}
