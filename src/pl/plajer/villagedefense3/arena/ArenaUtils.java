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

package pl.plajer.villagedefense3.arena;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;

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

}
