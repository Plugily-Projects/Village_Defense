package plugily.projects.villagedefense.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

public class Events1_9 implements Listener {

  @EventHandler
  public void onPickup(EntityPickupItemEvent e) {
    if (e.getEntityType() != EntityType.PLAYER) {
      return;
    }

    Arena arena = ArenaRegistry.getArena((Player) e.getEntity());
    if (arena == null) {
      return;
    }

    if (JavaPlugin.getPlugin(Main.class).getUserManager().getUser((Player) e.getEntity()).isSpectator()) {
      e.setCancelled(true);
    }

    arena.removeDroppedFlesh(e.getItem());
  }
}
