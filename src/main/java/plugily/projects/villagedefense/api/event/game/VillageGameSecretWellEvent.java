package plugily.projects.villagedefense.api.event.game;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.api.event.VillageEvent;
import plugily.projects.villagedefense.arena.Arena;

/**
 * Called when the player drops the item to the secret well
 */
public class VillageGameSecretWellEvent extends VillageEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final ItemStack item;
    private final Location location;

    public VillageGameSecretWellEvent(Arena arena, ItemStack item, Location location) {
        super(arena);
        this.item = item;
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public ItemStack getItem() {
        return item;
    }

    public Location getLocation() {
        return location;
    }
}
