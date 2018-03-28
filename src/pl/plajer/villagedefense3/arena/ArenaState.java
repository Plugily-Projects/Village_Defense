package pl.plajer.villagedefense3.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
@Getter
@AllArgsConstructor
public enum ArenaState {
    WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Finishing"), RESTARTING("Restarting");

    String formattedName;
}
