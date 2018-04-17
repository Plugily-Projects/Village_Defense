package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.arena.Arena;

/**
 * @author Plajer
 * @since 3.0.0-ALPHA build 13
 * <p>
 * Called when wave in arena has ended.
 */
public class VillageWaveEndEvent extends VillageEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Integer waveNumber;
    private final Arena arena;

    public VillageWaveEndEvent(Arena arena, Integer waveNumber) {
        super(arena);
        this.arena = arena;
        this.waveNumber = waveNumber;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Integer getWaveNumber() {
        return waveNumber;
    }

    public Arena getArena() {
        return arena;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
