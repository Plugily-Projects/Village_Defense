package pl.plajer.villagedefense3.arena;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

    @Getter
    private static List<Arena> arenas = new ArrayList<>();

    /**
     * Checks if player is in any arena
     *
     * @param player player to check
     * @return [b]true[/b] when player is in arena, [b]false[/b] if otherwise
     */
    public static boolean isInGameInstance(Player player) {
        boolean b = false;
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player)) {
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * Returns arena where the player is
     *
     * @param p target player
     * @return Arena or null if not playing
     * @see #isInGameInstance(Player) to check if player is playing
     */
    public static Arena getArena(Player p) {
        Arena arena = null;
        if(p == null)
            return null;
        if(!p.isOnline())
            return null;
        for(Arena loopArena : arenas) {
            for(Player player : loopArena.getPlayers()) {
                if(player.getUniqueId() == p.getUniqueId()) {
                    arena = loopArena;
                    break;
                }
            }
        }
        return arena;
    }

    public static void registerArena(Arena arena) {
        arenas.add(arena);
    }

    public static void unregisterArena(Arena arena) {
        arenas.remove(arena);
    }

    /**
     * Returns arena based by ID
     *
     * @param ID name of arena
     * @return Arena or null if not found
     */
    public static Arena getArena(String ID) {
        Arena arena = null;
        for(Arena loopArena : arenas) {
            if(loopArena.getID().equalsIgnoreCase(ID)) {
                arena = loopArena;
                break;
            }
        }
        return arena;
    }

}
