package pl.plajer.villagedefense3.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;

import java.util.HashMap;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeManager implements Listener {

    private Main plugin;

    public BungeeManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void connectToHub(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getHubServerName());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    private String getMotD() {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstances().get(0);
        if(gameInstance.getGameState() == GameState.STARTING && (gameInstance.getTimer() <= 3)) {
            return GameState.IN_GAME.toString();
        } else {
            return gameInstance.getGameState().toString();
        }
    }


    public String getHubServerName() {
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
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getGameInstanceManager().getGameInstances().get(0).joinAttempt(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) != null)
            plugin.getGameInstanceManager().getGameInstances().get(0).leaveAttempt(event.getPlayer());

    }

}
