/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.arena.managers;

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
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.utils.Debugger;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeManager implements Listener {

  private final Main plugin;
  private final FileConfiguration config;
  private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private final String MOTD;

  public BungeeManager(Main plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "bungee");
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Inactive", "Inactive")));
    gameStateToString.put(ArenaState.STARTING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Starting", "Starting")));
    gameStateToString.put(ArenaState.IN_GAME, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.In-Game", "In-Game")));
    gameStateToString.put(ArenaState.ENDING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Ending", "Ending")));
    gameStateToString.put(ArenaState.RESTARTING, plugin.getChatManager().colorRawMessage(config.getString("MOTD.Game-States.Restarting", "Restarting")));
    MOTD = plugin.getChatManager().colorRawMessage(config.getString("MOTD.Message", "The actual game state of vd is %state%"));
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


  private String getHubServerName() {
    return config.getString("Hub");
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerListPing(ServerListPingEvent event) {
    if (!config.getBoolean("MOTD.Manager", false) || ArenaRegistry.getArenas().isEmpty()) {
      return;
    }
    Arena arena = ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena());
    event.setMaxPlayers(arena.getMaximumPlayers());
    event.setMotd(MOTD.replace("%state%", gameStateToString.get(arena.getArenaState())));
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    event.setJoinMessage("");
    if (!ArenaRegistry.getArenas().isEmpty()) {
      ArenaManager.joinAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    event.setQuitMessage("");
    if (ArenaRegistry.getArena(event.getPlayer()) != null && !ArenaRegistry.getArenas().isEmpty()) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()));
    }

  }

}
