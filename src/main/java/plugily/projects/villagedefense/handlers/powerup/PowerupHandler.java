package plugily.projects.villagedefense.handlers.powerup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerPowerupPickupEvent;
import plugily.projects.minigamesbox.classic.handlers.powerup.BasePowerup;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 20.12.2021
 */
public class PowerupHandler implements Listener {

  private final Main plugin;

  public PowerupHandler(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPowerUpPickup(PlugilyPlayerPowerupPickupEvent event) {
    BasePowerup powerup = event.getPowerup();
    Arena arena = plugin.getArenaRegistry().getArena(event.getArena().getId());
    if(arena == null) {
      return;
    }
    switch(powerup.getKey().toLowerCase()) {
      case "map-clean":
        ArenaUtils.removeSpawnedEnemies(arena);
        break;
      case "golem-raid":
        for(int i = 0; i < (plugin.getPowerupRegistry().getLongestEffect(powerup) == 0 ? 3 : plugin.getPowerupRegistry().getLongestEffect(powerup)); i++) {
          arena.spawnGolem(arena.getStartLocation(), event.getPlayer());
        }
        break;
      default:
        break;
    }
  }

}
