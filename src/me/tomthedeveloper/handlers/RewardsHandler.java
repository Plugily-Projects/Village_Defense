package me.tomthedeveloper.handlers;

import me.tomthedeveloper.InvasionInstance;
import me.tomthedeveloper.Main;
import me.tomthedeveloper.game.GameInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsHandler {


    private FileConfiguration config;
    private Main plugin;
    private boolean enabled = false;

    public RewardsHandler(Main plugin) {
        this.plugin = plugin;

        if(!plugin.getConfig().contains("Rewards-Enabled")) {
            plugin.getConfig().set("Rewards-Enabled", false);
            plugin.saveConfig();
        }
        enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
        File rewards = new File(plugin.getDataFolder(), "rewards.yml");
        if(!rewards.exists()) {
            plugin.saveResource("rewards.yml", false);
            plugin.getLogger().info("Creating rewards.yml because it does not exist!");
        }
        config = ConfigurationManager.getConfig("rewards");
    }

    public void performEndGameRewards(InvasionInstance gameInstance) {
        if(!enabled)
            return;
        for(String string : config.getStringList("rewards.endgame")) {
            performCommand(gameInstance, string);
        }
    }

    public void performEndWaveRewards(InvasionInstance invasionInstance, int wave) {
        if(!enabled)
            return;
        if(!config.contains("rewards.endwave." + wave))
            return;
        for(String string : config.getStringList("rewards.endwave." + wave))
            performCommand(invasionInstance, string);
    }

    public void performZombieKillReward(Player player) {
        if(!enabled)
            return;
        for(String string : config.getStringList("rewards.zombiekill")) {
            performCommand(player, string);
        }
    }


    private void performCommand(InvasionInstance gameInstance, String string) {
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
        GameInstance gameInstance = plugin.getGameAPI().getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null)
            return;
        String command = string.replaceAll("%ARENA-ID%", gameInstance.getID())
                .replaceAll("%MAPNAME%", gameInstance.getMapName())
                .replaceAll("%PLAYERAMOUNT%", String.valueOf(gameInstance.getPlayers().size()))
                .replaceAll("%WAVE%", String.valueOf(((InvasionInstance) gameInstance).getWave()));
        if(command.contains("p:")) {
            player.performCommand(command.substring(2, command.length())
                    .replaceAll("%PLAYER%", player.getName()));
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
        }
    }
}
