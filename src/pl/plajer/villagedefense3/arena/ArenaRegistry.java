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

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_8_R3;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.ShopManager;
import pl.plajer.villagedefense3.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    @Getter
    private static List<Arena> arenas = new ArrayList<>();

    /**
     * Checks if player is in any arena
     *
     * @param player player to check
     * @return [b]true[/b] when player is in arena, [b]false[/b] if otherwise
     */
    public static boolean isInArena(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns arena where the player is
     *
     * @param p target player
     * @return Arena or null if not playing
     * @see #isInArena(Player) to check if player is playing
     */
    public static Arena getArena(Player p) {
        Arena arena = null;
        if(p == null)
            return null;
        if(!p.isOnline())
            return null;
        for(Arena loopArena : arenas) {
            for(Player player : loopArena.getPlayers()) {
                if(player.getUniqueId() == p.getUniqueId()) {
                    arena = loopArena;
                    break;
                }
            }
        }
        return arena;
    }

    public static void registerArena(Arena arena) {
        Main.debug("Registering new game instance, " + arena.getID(), System.currentTimeMillis());
        arenas.add(arena);
    }

    public static void unregisterArena(Arena arena) {
        Main.debug("Unegistering game instance, " + arena.getID(), System.currentTimeMillis());
        arenas.remove(arena);
    }

    /**
     * Returns arena based by ID
     *
     * @param ID name of arena
     * @return Arena or null if not found
     */
    public static Arena getArena(String ID) {
        Arena arena = null;
        for(Arena loopArena : arenas) {
            if(loopArena.getID().equalsIgnoreCase(ID)) {
                arena = loopArena;
                break;
            }
        }
        return arena;
    }

    public static void registerArenas() {
        Main.debug("Initial arenas registration", System.currentTimeMillis());
        if(ArenaRegistry.getArenas() != null) {
            if(ArenaRegistry.getArenas().size() > 0) {
                for(Arena arena : ArenaRegistry.getArenas()) {
                    arena.clearZombies();
                    arena.clearVillagers();
                    arena.clearWolfs();
                    arena.clearGolems();
                }
            }
        }
        ArenaRegistry.getArenas().clear();
        if(!ConfigurationManager.getConfig("arenas").contains("instances")) {
            Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.No-Instances-Created"));
            return;
        }

        for(String ID : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances").getKeys(false)) {
            Arena arena;
            String s = "instances." + ID + ".";
            if(s.contains("default")) continue;
            if(plugin.is1_8_R3()) {
                arena = new ArenaInitializer1_8_R3(ID, plugin);
            } else if(plugin.is1_9_R1()) {
                arena = new ArenaInitializer1_9_R1(ID, plugin);
            } else if(plugin.is1_11_R1()) {
                arena = new ArenaInitializer1_11_R1(ID, plugin);
            } else if(plugin.is1_12_R1()) {
                arena = new ArenaInitializer1_12_R1(ID, plugin);
            } else {
                arena = new ArenaInitializer1_13_R1(ID, plugin);
            }
            arena.setMinimumPlayers(ConfigurationManager.getConfig("arenas").getInt(s + "minimumplayers"));
            arena.setMaximumPlayers(ConfigurationManager.getConfig("arenas").getInt(s + "maximumplayers"));
            arena.setMapName(ConfigurationManager.getConfig("arenas").getString(s + "mapname"));
            arena.setLobbyLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "lobbylocation")));
            arena.setStartLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "Startlocation")));
            arena.setEndLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(s + "Endlocation")));

            if(!ConfigurationManager.getConfig("arenas").getBoolean(s + "isdone")) {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "NOT VALIDATED"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }

            if(ConfigurationManager.getConfig("arenas").contains(s + "zombiespawns")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "zombiespawns").getKeys(false)) {
                    String path = s + "zombiespawns." + string;
                    arena.addZombieSpawn(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "ZOMBIE SPAWNS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }

            if(ConfigurationManager.getConfig("arenas").contains(s + "villagerspawns")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "villagerspawns").getKeys(false)) {
                    String path = s + "villagerspawns." + string;
                    arena.addVillagerSpawn(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "VILLAGER SPAWNS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }
            if(ConfigurationManager.getConfig("arenas").contains(s + "doors")) {
                for(String string : ConfigurationManager.getConfig("arenas").getConfigurationSection(s + "doors").getKeys(false)) {
                    String path = s + "doors." + string + ".";
                    arena.addDoor(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "location")), (byte) ConfigurationManager.getConfig("arenas").getInt(path + "byte"));
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replaceAll("%arena%", ID).replaceAll("%error%", "DOORS"));
                arena.setReady(false);
                ArenaRegistry.registerArena(arena);
                continue;
            }
            ArenaRegistry.registerArena(arena);
            arena.start();
            Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Instance-Started").replaceAll("%arena%", ID));
        }
        new ShopManager();
        Main.debug("Arenas registration completed", System.currentTimeMillis());
    }

}
