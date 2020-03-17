package pl.plajer.villagedefense.arena.managers;

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
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeManager implements Listener {

  private Main plugin;
  private FileConfiguration config;
  private Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private String MOTD;

  public BungeeManager(Main plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "bungee");
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Inactive")));
    gameStateToString.put(ArenaState.STARTING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Starting")));
    gameStateToString.put(ArenaState.IN_GAME, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.In-Game")));
    gameStateToString.put(ArenaState.ENDING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Ending")));
    gameStateToString.put(ArenaState.RESTARTING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Restarting")));
    MOTD = plugin.getChatManager().colorRawMessage(config.getString("MOTD.Message"));
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void connectToHub(Player player) {
    if (!config.getBoolean("Connect-To-Hub", true)) {
      return;
    }
    Debugger.debug(Level.INFO, "Server name that we try to connect {0} ({1})", getHubServerName(), player.getName());
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(getHubServerName());
    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
  }

  private ArenaState getArenaState() {
    Arena arena = ArenaRegistry.getArenas().get(0);
    return arena.getArenaState();
  }


  private String getHubServerName() {
    return config.getString("Hub");
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerListPing(ServerListPingEvent event) {
    if (!config.getBoolean("MOTD.Manager", false)) {
      return;
    }
    if (ArenaRegistry.getArenas().isEmpty()) {
      return;
    }
    event.setMaxPlayers(ArenaRegistry.getArenas().get(0).getMaximumPlayers());
    event.setMotd(MOTD.replace("%state%", gameStateToString.get(getArenaState())));
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(final PlayerJoinEvent event) {
    event.setJoinMessage("");
    ArenaManager.joinAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(0));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    event.setQuitMessage("");
    if (ArenaRegistry.getArena(event.getPlayer()) != null) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(0));
    }

  }

}
