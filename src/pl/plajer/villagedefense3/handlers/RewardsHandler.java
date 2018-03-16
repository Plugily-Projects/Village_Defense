package pl.plajer.villagedefense3.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsHandler {


    private FileConfiguration config;
    private Main plugin;
    private boolean enabled;

    public RewardsHandler(Main plugin) {
        this.plugin = plugin;
        enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
        config = ConfigurationManager.getConfig("rewards");
    }

    public void performEndGameRewards(Arena arena) {
        if(!enabled) return;
        for(String string : config.getStringList("rewards.endgame")) {
            performCommand(arena, string);
        }
    }

    public void performEndWaveRewards(Arena arena, int wave) {
        if(!enabled) return;
        if(!config.contains("rewards.endwave." + wave)) return;
        for(String string : config.getStringList("rewards.endwave." + wave))
            performCommand(arena, string);
    }

    public void performZombieKillReward(Player player) {
        if(!enabled) return;
        for(String string : config.getStringList("rewards.zombiekill")) {
            performCommand(player, string);
        }
    }


    private void performCommand(Arena arena, String string) {
        if(!enabled) return;
        String command = string.replaceAll("%ARENA-ID%", arena.getID())
                .replaceAll("%MAPNAME%", arena.getMapName())
                .replaceAll("%PLAYERAMOUNT%", String.valueOf(arena.getPlayers().size()))
                .replaceAll("%WAVE%", String.valueOf(arena.getWave()));
        if(command.contains("p:") || command.contains("%PLAYER%")) {
            for(Player player : arena.getPlayers()) {
                if(command.contains("p:")) {
                    player.performCommand(command.substring(2, command.length())
                            .replaceAll("%PLAYER%", player.getName()));
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
                }
            }
        }
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
    }

    private void performCommand(Player player, String string) {
        if(!enabled) return;
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        String command = string.replaceAll("%ARENA-ID%", arena.getID())
                .replaceAll("%MAPNAME%", arena.getMapName())
                .replaceAll("%PLAYERAMOUNT%", String.valueOf(arena.getPlayers().size()))
                .replaceAll("%WAVE%", String.valueOf(arena.getWave()));
        if(command.contains("p:")) {
            player.performCommand(command.substring(2, command.length())
                    .replaceAll("%PLAYER%", player.getName()));
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
        }
    }
}
