package pl.plajer.villagedefense3.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;

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

    public void performEndGameRewards(ArenaInstance gameInstance) {
        if(!enabled) return;
        for(String string : config.getStringList("rewards.endgame")) {
            performCommand(gameInstance, string);
        }
    }

    public void performEndWaveRewards(ArenaInstance arenaInstance, int wave) {
        if(!enabled) return;
        if(!config.contains("rewards.endwave." + wave)) return;
        for(String string : config.getStringList("rewards.endwave." + wave))
            performCommand(arenaInstance, string);
    }

    public void performZombieKillReward(Player player) {
        if(!enabled) return;
        for(String string : config.getStringList("rewards.zombiekill")) {
            performCommand(player, string);
        }
    }


    private void performCommand(ArenaInstance gameInstance, String string) {
        if(!enabled)
            return;
        String command = string.replaceAll("%ARENA-ID%", gameInstance.getID())
                .replaceAll("%MAPNAME%", gameInstance.getMapName())
                .replaceAll("%PLAYERAMOUNT%", String.valueOf(gameInstance.getPlayers().size()))
                .replaceAll("%WAVE%", String.valueOf(gameInstance.getWave()));
        for(Player player : gameInstance.getPlayers()) {
            if(command.contains("p:")) {
                player.performCommand(command.substring(2, command.length())
                        .replaceAll("%PLAYER%", player.getName()));
            } else {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
            }
        }

    }

    private void performCommand(Player player, String string) {
        if(!enabled)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null)
            return;
        String command = string.replaceAll("%ARENA-ID%", gameInstance.getID())
                .replaceAll("%MAPNAME%", gameInstance.getMapName())
                .replaceAll("%PLAYERAMOUNT%", String.valueOf(gameInstance.getPlayers().size()))
                .replaceAll("%WAVE%", String.valueOf(((ArenaInstance) gameInstance).getWave()));
        if(command.contains("p:")) {
            player.performCommand(command.substring(2, command.length())
                    .replaceAll("%PLAYER%", player.getName()));
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
        }
    }
}
