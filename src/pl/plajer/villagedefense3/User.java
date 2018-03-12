package pl.plajer.villagedefense3;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

    public static Main plugin;
    private static long cooldownCounter = 0;
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private Scoreboard scoreboard;
    private UUID uuid;
    private boolean fakeDead = false;
    private boolean spectator = false;
    private Kit kit;
    private HashMap<String, Integer> ints = new HashMap<>();
    private HashMap<String, Long> cooldowns = new HashMap<>();

    public User(UUID uuid) {
        scoreboard = scoreboardManager.getNewScoreboard();
        this.uuid = uuid;

        kit = plugin.getKitRegistry().getDefaultKit();
    }

    public static void handleCooldowns() {
        cooldownCounter++;
    }

    public Kit getKit() {
        if(kit == null) {
            throw new NullPointerException("User has no kit!");
        } else
            return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
    }

    public Arena getArena() {
        return plugin.getArenaRegistry().getArena(Bukkit.getPlayer(uuid));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isFakeDead() {
        return fakeDead;
    }

    public void setFakeDead(boolean b) {
        fakeDead = b;
    }

    public Player toPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean b) {
        spectator = b;
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

    public void setCooldown(String s, int seconds) {
        cooldowns.put(s, seconds + cooldownCounter);
    }

    public long getCooldown(String s) {
        if(!cooldowns.containsKey(s))
            return 0;
        if(cooldowns.get(s) <= cooldownCounter)
            return 0;
        return cooldowns.get(s) - cooldownCounter;
    }

}
