/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajer.villagedefense.utils.constants.Constants;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

  private static List<Arena> arenas = new ArrayList<>();
  private static Main plugin;

  private ArenaRegistry() {
  }

  public static void init(Main plugin) {
    ArenaRegistry.plugin = plugin;
  }

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return true when player is in arena, false if otherwise
   */
  public static boolean isInArena(@NotNull Player player) {
    for (Arena arena : arenas) {
      if (arena.getPlayers().contains(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns arena where the player is
   *
   * @param player target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  @Nullable
  public static Arena getArena(Player player) {
    Arena arena = null;
    if (player == null || !player.isOnline()) {
      return null;
    }
    for (Arena loopArena : arenas) {
      for (Player arenaPlayer : loopArena.getPlayers()) {
        if (arenaPlayer.getUniqueId().equals(player.getUniqueId())) {
          arena = loopArena;
          break;
        }
      }
    }
    return arena;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  @Nullable
  public static Arena getArena(String id) {
    Arena arena = null;
    for (Arena loopArena : arenas) {
      if (loopArena.getId().equalsIgnoreCase(id)) {
        arena = loopArena;
        break;
      }
    }
    return arena;
  }

  public static void registerArena(Arena arena) {
    Debugger.debug(Level.INFO, "[[0}] Instance registered", arena.getId());
    arenas.add(arena);
  }

  public static void unregisterArena(Arena arena) {
    Debugger.debug(Level.INFO, "[{0}] Instance unregistered", arena.getId());
    arenas.remove(arena);
  }

  public static void registerArenas() {
    Debugger.debug(Level.INFO, "[ArenaRegistry] Initial arenas registration");
    long start = System.currentTimeMillis();

    if (ArenaRegistry.getArenas() != null && !ArenaRegistry.getArenas().isEmpty()) {
      for (Arena arena : new ArrayList<>(ArenaRegistry.getArenas())) {
        arena.getMapRestorerManager().clearZombiesFromArena();
        arena.getMapRestorerManager().clearVillagersFromArena();
        arena.getMapRestorerManager().clearWolvesFromArena();
        arena.getMapRestorerManager().clearGolemsFromArena();
        unregisterArena(arena);
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.ARENAS.getName());

    ConfigurationSection section = config.getConfigurationSection("instances");
    if (section == null) {
      Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_NO_INSTANCES_CREATED));
      return;
    }
    for (String id : section.getKeys(false)) {
      Arena arena = ArenaUtils.initializeArena(id);
      String key = "instances." + id + ".";
      if (key.contains("default")) {
        continue;
      }
      arena.setMinimumPlayers(config.getInt(key + "minimumplayers", 1));
      arena.setMaximumPlayers(config.getInt(key + "maximumplayers", 2));
      arena.setMapName(config.getString(key + "mapname", "none"));
      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(key + "lobbylocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setStartLocation(LocationSerializer.getLocation(config.getString(key + "Startlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString(key + "Endlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      ArenaUtils.setWorld(arena);
      if (arena.getStartLocation().getWorld().getDifficulty() == Difficulty.PEACEFUL){
        Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INVALID_ARENA_CONFIGURATION).replace("%arena%", id).replace("%error%", "THERE IS A WRONG " +
            "DIFFICULTY -> SET IT TO ANOTHER ONE THAN PEACEFUL"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      if (!config.getBoolean(key + "isdone", false)) {
        Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INVALID_ARENA_CONFIGURATION).replace("%arena%", id).replace("%error%", "NOT VALIDATED"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }

      if (config.isSet(key + "zombiespawns")) {
        for (String string : config.getConfigurationSection(key + "zombiespawns").getKeys(false)) {
          String path = key + "zombiespawns." + string;
          arena.addZombieSpawn(LocationSerializer.getLocation(config.getString(path)));
        }
      } else {
        Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INVALID_ARENA_CONFIGURATION).replace("%arena%", id).replace("%error%", "ZOMBIE SPAWNS"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }

      if (config.isSet(key + "villagerspawns")) {
        for (String string : config.getConfigurationSection(key + "villagerspawns").getKeys(false)) {
          String path = key + "villagerspawns." + string;
          arena.addVillagerSpawn(LocationSerializer.getLocation(config.getString(path)));
        }
      } else {
        Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INVALID_ARENA_CONFIGURATION).replace("%arena%", id).replace("%error%", "VILLAGER SPAWNS"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      if (config.isSet(key + "doors")) {
        for (String string : config.getConfigurationSection(key + "doors").getKeys(false)) {
          String path = key + "doors." + string + ".";
          arena.getMapRestorerManager().addDoor(LocationSerializer.getLocation(config.getString(path + "location")),
              (byte) config.getInt(path + "byte"));
        }
      } else {
        Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INVALID_ARENA_CONFIGURATION).replace("%arena%", id).replace("%error%", "DOORS"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
      Bukkit.getConsoleSender().sendMessage(plugin.getChatManager().colorMessage(Messages.VALIDATOR_INSTANCE_STARTED).replace("%arena%", id));
    }
    Debugger.debug(Level.INFO, "[ArenaRegistry] Arenas registration completed took {0}ms", System.currentTimeMillis() - start);
  }

  public static List<Arena> getArenas() {
    return arenas;
  }
}
