package me.tomthedeveloper;

import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.kitapi.basekits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private Scoreboard scoreboard;
    private static long COOLDOWNCOUNTER = 0;
    private UUID uuid;
    private boolean fakedead = false;
    private boolean spectator = false;
    public static GameAPI plugin;
    private Kit kit;
    private HashMap<String, Integer> ints = new HashMap<>();
    private HashMap<String, Long> cooldowns = new HashMap<>();
    private HashMap<String, Object> objects = new HashMap<>();


    public User(UUID uuid) {
        scoreboard = scoreboardManager.getNewScoreboard();
        this.uuid = uuid;

        kit = plugin.getKitHandler().getDefaultKit();
    }

    public void setScoreboard(Scoreboard scoreboard) {
        Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
    }

    public Kit getKit() {
        if(kit == null) {
            throw new NullPointerException("User has no kit!");
        } else
            return kit;
    }

    public Object getObject(String s) {
        if(objects.containsKey(s))
            return objects.get(s);
        return null;
    }

    public void setObject(Object object, String s) {
        objects.put(s, object);
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public boolean isInInstance() {
        return plugin.getGameInstanceManager().isInGameInstance(Bukkit.getPlayer(uuid));
    }

    public GameInstance getGameInstance() {
        return plugin.getGameInstanceManager().getGameInstance(Bukkit.getPlayer(uuid));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setFakeDead(boolean b) {
        fakedead = b;
    }

    public boolean isFakeDead() {
        return fakedead;
    }

    public Player toPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

    public void setSpectator(boolean b) {
        spectator = b;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public int getInt(String s) {
        if(!ints.containsKey(s)) {
            ints.put(s, 0);
            return 0;
        } else if(ints.get(s) == null) {
            return 0;
        }
        return ints.get(s);
    }

    public void removeScoreboard() {
        this.toPlayer().setScoreboard(scoreboardManager.getNewScoreboard());

    }

    public void setInt(String s, int i) {
        ints.put(s, i);

    }

    public void addInt(String s, int i) {
        ints.put(s, getInt(s) + i);
    }

    public static void handleCooldowns() {
        COOLDOWNCOUNTER++;
    }

    public void setCooldown(String s, int seconds) {
        cooldowns.put(s, seconds + COOLDOWNCOUNTER);
    }


    public long getCooldown(String s) {
        if(!cooldowns.containsKey(s))
            return 0;
        if(cooldowns.get(s) <= COOLDOWNCOUNTER)
            return 0;
        return cooldowns.get(s) - COOLDOWNCOUNTER;
    }

    public void removeInt(String string, int i) {
        if(ints.containsKey(string)) {
            ints.put(string, ints.get(string) - i);
        }
    }

}
