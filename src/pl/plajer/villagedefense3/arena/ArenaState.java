package pl.plajer.villagedefense3.arena;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
public enum ArenaState {
    WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Finishing"), RESTARTING("Restarting");

    String formattedName;

    ArenaState(String formattedName){
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return formattedName;
    }
}
