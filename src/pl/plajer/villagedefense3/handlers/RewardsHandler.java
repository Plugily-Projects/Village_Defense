/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        if(command.contains("chance(")){
            int loc = command.indexOf(")");
            if(loc == -1){
                plugin.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition!");
                return;
            }
            String chanceStr = command.substring(0, loc).replaceAll("[^0-9]+", "");
            Bukkit.broadcastMessage(chanceStr + " before");
            int chance = Integer.parseInt(chanceStr);
            command = command.replace("chance(" + chanceStr + "):", "");
            if(ThreadLocalRandom.current().nextInt(0, 100) > chance) return;
        }
        if(command.contains("p:") || command.contains("%PLAYER%")) {
            for(Player player : arena.getPlayers()) {
                if(command.contains("p:")) {
                    player.performCommand(command.replaceFirst("p:", "")
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
        if(command.contains("chance(")){
            int loc = command.indexOf(")");
            if(loc == -1){
                plugin.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition!");
                return;
            }
            String chanceStr = command.substring(0, loc).replaceAll("[^0-9]+", "");
            Bukkit.broadcastMessage(chanceStr + " before");
            int chance = Integer.parseInt(chanceStr);
            command = command.replace("chance(" + chanceStr + "):", "");
            if(ThreadLocalRandom.current().nextInt(0, 100) > chance) return;
        }
        if(command.contains("p:")) {
            player.performCommand(command.replaceFirst("p:", "")
                    .replaceAll("%PLAYER%", player.getName()));
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%PLAYER%", player.getName()));
        }
    }
}
