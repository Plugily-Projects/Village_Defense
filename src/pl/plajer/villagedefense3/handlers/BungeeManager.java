package pl.plajer.villagedefense3.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.ArenaState;

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

    private String getMOTD() {
        Arena arena = ArenaRegistry.getArenas().get(0);
        if(arena.getArenaState() == ArenaState.STARTING && (arena.getTimer() <= 3)) {
            return ArenaState.IN_GAME.toString();
        } else {
            return arena.getArenaState().toString();
        }
    }


    public String getHubServerName() {
        return ConfigurationManager.getConfig("bungee").getString("Hub");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if(plugin.getArenaRegistry() == null)
            return;
        if(ArenaRegistry.getArenas().isEmpty())
            return;
        if(ArenaRegistry.getArenas() == null) {
            Main.debug("No ready arena found! Please create one before activating bungee mode!", System.currentTimeMillis());
            return;
        }
        event.setMaxPlayers(ArenaRegistry.getArenas().get(0).getMaximumPlayers());
        event.setMotd(this.getMOTD());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> ArenaRegistry.getArenas().get(0).joinAttempt(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if(ArenaRegistry.getArena(event.getPlayer()) != null)
            ArenaRegistry.getArenas().get(0).leaveAttempt(event.getPlayer());

    }

}
