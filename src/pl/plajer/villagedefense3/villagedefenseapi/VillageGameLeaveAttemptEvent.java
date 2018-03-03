package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.game.GameInstance;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when player is attempting to leave game instance.
 */
public class VillageGameLeaveAttemptEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final GameInstance gameInstance;

    public VillageGameLeaveAttemptEvent(Player player, GameInstance gameInstance) {
        this.player = player;
        this.gameInstance = gameInstance;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
