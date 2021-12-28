package plugily.projects.villagedefense.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.minigamesbox.classic.api.event.PlugilyEvent;
import plugily.projects.villagedefense.arena.Arena;

/**
 * Called when the player respawns on wave end
 */
public class VillagePlayerRespawnEvent extends PlugilyEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private boolean isCancelled = false;

  public VillagePlayerRespawnEvent(Player player, Arena arena) {
    super(arena);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
