package pl.plajer.villagedefense3.game;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
public enum GameState {
    WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Finishing"), RESTARTING("Restarting");

    String formattedName;

    GameState(String formattedName){
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return formattedName;
    }
}
