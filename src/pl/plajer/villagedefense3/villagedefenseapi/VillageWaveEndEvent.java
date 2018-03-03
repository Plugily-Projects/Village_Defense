package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.game.GameInstance;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when wave in game instance has ended.
 */
public class VillageWaveEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Integer waveNumber;
    private final GameInstance gameInstance;

    public VillageWaveEndEvent(GameInstance gameInstance, Integer waveNumber) {
        this.gameInstance = gameInstance;
        this.waveNumber = waveNumber;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Integer getWaveNumber() {
        return waveNumber;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
