package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.game.GameInstance;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 14
 * <p>
 * Called when game instance has started.
 */
public class VillageGameStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final GameInstance gameInstance;

    public VillageGameStartEvent(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
