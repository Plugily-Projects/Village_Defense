package plugily.projects.villagedefense.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

@SuppressWarnings("deprecation")
public class Events1_8 implements Listener {

  @EventHandler
  public void onDropPickup(PlayerPickupItemEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }

    if (JavaPlugin.getPlugin(Main.class).getUserManager().getUser(e.getPlayer()).isSpectator()) {
      e.setCancelled(true);
    }

    arena.removeDroppedFlesh(e.getItem());
  }
}
