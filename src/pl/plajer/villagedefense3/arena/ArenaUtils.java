package pl.plajer.villagedefense3.arena;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaUtils {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static void hidePlayer(Player p, Arena arena) {
        for(Player player : arena.getPlayers()) {
            player.hidePlayer(p);
        }
    }

    public static void showPlayer(Player p, Arena arena) {
        for(Player player : arena.getPlayers()) {
            player.showPlayer(p);
        }
    }

    public static void hidePlayersOutsideTheGame(Player player, Arena arena) {
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(arena.getPlayers().contains(players)) continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    public static void bringDeathPlayersBack(Arena arena) {
        for(Player player : arena.getPlayers()) {
            if(!arena.getPlayersLeft().contains(player)) {
                User user = UserManager.getUser(player.getUniqueId());
                user.setFakeDead(false);
                user.setSpectator(false);

                arena.teleportToStartLocation(player);
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setGameMode(GameMode.SURVIVAL);
                arena.showPlayers();
                player.getInventory().clear();
                user.getKit().giveKitItems(player);
                player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
            }
        }
    }

    public static void updateLevelStat(Player player, Arena arena) {
        User user = UserManager.getUser(player.getUniqueId());
        if(Math.pow(50 * user.getInt("level"), 1.5) < user.getInt("xp")) {
            user.addInt("level", 1);
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.You-Leveled-Up"), user.getInt("level")));
        }
    }

    public static void updateScoreboard(Arena arena) {
        if(arena.getPlayers().size() == 0) return;
        for(Player p : arena.getPlayers()) {
            User user = UserManager.getUser(p.getUniqueId());
            if(user.getScoreboard().getObjective("vd_state_0") == null) {
                for(ArenaState state : ArenaState.values()) {
                    user.getScoreboard().registerNewObjective("vd_state_" + state.ordinal(), "dummy");
                }
                //fighting stage of IN_GAME state
                user.getScoreboard().registerNewObjective("vd_state_2F", "dummy");
            }
            if(arena.getArenaState() == ArenaState.ENDING){
                user.removeScoreboard();
                return;
            }
            Objective gameObjective;
            if(arena.getArenaState() == ArenaState.IN_GAME) {
                gameObjective = user.getScoreboard().getObjective("vd_state_" + arena.getArenaState().ordinal() + (arena.isFighting() ? "F" : ""));
            } else {
                gameObjective = user.getScoreboard().getObjective("vd_state_" + arena.getArenaState().ordinal());
            }
            if(gameObjective == null) return;
            gameObjective.setDisplayName(ChatManager.colorMessage("Scoreboard.Header"));
            gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            switch(arena.getArenaState()) {
                case WAITING_FOR_PLAYERS:
                    Score playersTotal1 = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Players")));
                    playersTotal1.setScore(arena.getPlayers().size());
                    Score neededPlayers2 = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Minimum-Players")));
                    neededPlayers2.setScore(arena.getMinimumPlayers());
                    break;
                case STARTING:
                    Score timer = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Starting-In")));
                    timer.setScore(arena.getTimer());
                    Score playersTotal = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Players")));
                    playersTotal.setScore(arena.getPlayers().size());
                    Score neededPlayers = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Minimum-Players")));
                    neededPlayers.setScore(arena.getMinimumPlayers());
                    break;
                case IN_GAME:
                    Score playersLeft = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Players-Left")));
                    playersLeft.setScore(arena.getPlayersLeft().size());
                    Score villagersLeft = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Villagers-Left")));
                    villagersLeft.setScore(arena.getVillagers().size());
                    Score orbs = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Orbs")));
                    orbs.setScore(user.getInt("orbs"));
                    if(arena.isFighting()) {
                        Score zombiesLeft = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Zombies-Left")));
                        zombiesLeft.setScore(arena.getZombiesLeft());
                    } else {
                        Score nextWaveIn = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Next-Wave-In")));
                        nextWaveIn.setScore(arena.getTimer());
                    }
                    Score rottenFlesh = gameObjective.getScore(ChatManager.formatMessage(arena, ChatManager.colorMessage("Scoreboard.Rotten-Flesh")));
                    rottenFlesh.setScore(arena.getRottenFlesh());
                    break;
                case RESTARTING:
                    break;
                default:
                    arena.setArenaState(ArenaState.WAITING_FOR_PLAYERS);
                    break;
            }
            Score empty = gameObjective.getScore("");
            empty.setScore(-1);
            Score footer = gameObjective.getScore(ChatManager.colorMessage("Scoreboard.Footer"));
            footer.setScore(-2);
            user.setScoreboard(user.getScoreboard());
        }
    }

}
