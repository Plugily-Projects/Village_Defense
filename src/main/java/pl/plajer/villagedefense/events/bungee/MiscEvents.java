package pl.plajer.villagedefense.events.bungee;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.event.game.VillageGameStateChangeEvent;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.handlers.PermissionsManager;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class MiscEvents implements Listener {

  private Main plugin;

  public MiscEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent e) {
    if (!plugin.getServer().hasWhitelist() || e.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) {
      return;
    }
    if (e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames())) {
      e.setResult(PlayerLoginEvent.Result.ALLOWED);
    }

    if (!ArenaRegistry.getArenas().isEmpty()) {
      e.getPlayer().teleport(ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()).getLobbyLocation());
    }
  }

  @EventHandler
  public void onGameStateChange(VillageGameStateChangeEvent e) {
    switch (e.getArenaState()) {
      case WAITING_FOR_PLAYERS:
        plugin.getServer().setWhitelist(false);
        break;
      case IN_GAME:
        plugin.getServer().setWhitelist(e.getArena().getMaximumPlayers() <= e.getArena().getPlayers().size());
        break;
      case ENDING:
        plugin.getServer().setWhitelist(false);
        break;
      case STARTING:
      case RESTARTING:
      default:
        break;
    }
    if (e.getArenaState() == ArenaState.ENDING) {
      plugin.getServer().setWhitelist(false);
    }
  }

}
