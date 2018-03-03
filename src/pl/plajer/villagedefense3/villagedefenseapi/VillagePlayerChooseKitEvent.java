package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;

/**
 * @author TomTheDeveloper, Plajer
 * @since 2.0.0
 * <p>
 * Called when player chose kit in game.
 */
public class VillagePlayerChooseKitEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Kit kit;
    private boolean isCancelled;

    public VillagePlayerChooseKitEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
