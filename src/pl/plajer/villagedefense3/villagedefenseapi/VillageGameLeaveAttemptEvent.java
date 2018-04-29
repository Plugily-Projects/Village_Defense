package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.arena.Arena;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when player is attempting to leave arena.
 */
public class VillageGameLeaveAttemptEvent extends VillageEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Arena arena;

    public VillageGameLeaveAttemptEvent(Player player, Arena arena) {
        super(arena);
        this.player = player;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
