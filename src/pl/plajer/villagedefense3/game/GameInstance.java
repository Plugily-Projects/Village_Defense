package pl.plajer.villagedefense3.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

//import me.confuser.barapi.BarAPI;

/**
 * Created by Tom on 27/07/2014.
 */
public abstract class GameInstance extends BukkitRunnable {

    public static Main plugin;
    private HashSet<Location> signs = new HashSet<>();
    private GameState gameState;
    private int MIN_PLAYERS = 2;
    private int MAX_PLAYERS = 10;
    private String mapName = "";
    private int timer;
    private String ID;

    private Location lobbyLoc = null;
    private Location startLoc = null;
    private Location endLoc = null;

    private HashSet<UUID> players;

    private ChatManager chatManager;

    protected GameInstance(String ID) {
        gameState = GameState.WAITING_FOR_PLAYERS;
        chatManager = new ChatManager(this);

        this.ID = ID;
        players = new HashSet<>();

    }

    public static Main getPlugin() {
        return plugin;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    /**
     * Number of minimum needed players
     *
     * @return number of minimum needed players
     */
    public int getMIN_PLAYERS() {
        return MIN_PLAYERS;
    }

    public void setMIN_PLAYERS(int MIN_PLAYERS) {
        this.MIN_PLAYERS = MIN_PLAYERS;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapname) {
        this.mapName = mapname;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        if(player == null)
            return;
        if(player.getUniqueId() == null)
            return;
        if(players.contains(player.getUniqueId()))
            players.remove(player.getUniqueId());
    }

    public void clearPlayers() {
        players.clear();
    }

    public void addSign(Location location) {
        signs.add(location);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public abstract boolean needsPlayers();

    /**
     * Number of maxium players arena can hold
     *
     * @return number of maximum players arena can hold
     */
    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public void setMAX_PLAYERS(int MAX_PLAYERS) {
        this.MAX_PLAYERS = MAX_PLAYERS;
    }

    /**
     * Game status of game instance.
     *
     * @return GameState of arena
     * @see GameState
     */
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public HashSet<Location> getSigns() {
        return signs;
    }

    /**
     * Method for handling player game join.
     */
    public void joinAttempt(Player p) {}

    public void showPlayers() {
        for(Player player : getPlayers()) {
            for(Player p : getPlayers()) {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
    }

    /**
     * Returns all in game players.
     *
     * @return HashSet of players in game
     */
    public HashSet<Player> getPlayers() {
        HashSet<Player> list = new HashSet<>();
        for(UUID uuid : players) {
            list.add(Bukkit.getPlayer(uuid));
        }

        return list;
    }

    /**
     * Returns all alive players.
     *
     * @return list of alive players in game
     */
    public List<Player> getPlayersLeft() {
        List<Player> players = new ArrayList<>();
        for(User user : UserManager.getUsers(this)) {
            if(!user.isFakeDead())
                players.add(user.toPlayer());
        }
        return players;
    }

    /**
     * Method for handling game leave
     */
    public void leaveAttempt(Player p) {}

    public void hidePlayer(Player p) {
        for(Player player : getPlayers()) {
            player.hidePlayer(p);
        }
    }

    public void showPlayer(Player p) {
        for(Player player : getPlayers()) {
            player.showPlayer(p);
        }
    }

    public void teleportToLobby(Player player) {
        Location location = getLobbyLocation();
        player.setMaxHealth(20.0);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if(location == null) {
            System.out.print("LobbyLocation isn't intialized for arena " + getID());
        }
        player.teleport(location);

    }

    public Location getLobbyLocation() {
        return lobbyLoc;
    }

    public void setLobbyLocation(Location loc) {
        this.lobbyLoc = loc;
    }

    public Location getStartLocation() {
        return startLoc;
    }


    public void setStartLocation(Location location) {
        startLoc = location;
    }

    public void teleportToStartLocation(Player player) {
        if(startLoc != null)
            player.teleport(startLoc);
        else
            System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
    }

    public void teleportAllToStartLocation() {
        for(Player player : getPlayers()) {
            if(startLoc != null)
                player.teleport(startLoc);
            else
                System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
        }
    }

    public void teleportAllToEndLocation() {
        if(plugin.isBungeeActivated()) {
            for(Player player : getPlayers()) {
                plugin.getBungeeManager().connectToHub(player);
            }
            return;
        }
        Location location = getEndLocation();

        if(location == null) {
            location = getLobbyLocation();
            System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
        }
        for(Player player : getPlayers()) {
            player.teleport(location);
        }
    }

    public void teleportToEndLocation(Player player) {
        if(plugin.isBungeeActivated()) {
            plugin.getBungeeManager().connectToHub(player);
            return;
        }
        Location location = getEndLocation();
        if(location == null) {
            location = getLobbyLocation();
            System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
        }

        player.teleport(location);
    }

    public Location getEndLocation() {
        return endLoc;
    }

    public void setEndLocation(Location endLoc) {
        this.endLoc = endLoc;
    }

}



