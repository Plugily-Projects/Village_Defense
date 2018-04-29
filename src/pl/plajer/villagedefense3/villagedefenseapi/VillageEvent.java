package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.event.Event;
import pl.plajer.villagedefense3.arena.Arena;

/**
 * Represents Village Defense game related events.
 */
public abstract class VillageEvent extends Event {

    protected Arena arena;

    public VillageEvent(Arena eventArena) {
        arena = eventArena;
    }

    /**
     * Returns event arena
     *
     * @return event arena
     */
    public Arena getArena() {
        return arena;
    }
}
