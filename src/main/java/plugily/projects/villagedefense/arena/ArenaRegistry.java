/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena;

import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaRegistry;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.villagedefense.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry extends PluginArenaRegistry {

  private final Main plugin;

  public ArenaRegistry(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }


  @Override
  public PluginArena getNewArena(String id) {
    return new Arena(id);
  }

  @Override
  public boolean additionalValidatorChecks(ConfigurationSection section, PluginArena arena, String id) {
    boolean checks = super.additionalValidatorChecks(section, arena, id);
    if(!checks) return false;

    if(!section.getBoolean(id + ".isdone")) {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION", "NOT VALIDATED", arena));
      return false;
    }

    ConfigurationSection zombieSection = section.getConfigurationSection(id + ".zombiespawns");
    if(zombieSection != null) {
      for(String string : zombieSection.getKeys(false)) {
        ((Arena) arena).addZombieSpawn(LocationSerializer.getLocation(zombieSection.getString(string)));
      }
    } else {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION", "ZOMBIE SPAWNS", arena));
      return false;
    }

    ConfigurationSection villagerSection = section.getConfigurationSection(id + ".villagerspawns");
    if(villagerSection != null) {
      for(String string : villagerSection.getKeys(false)) {
        ((Arena) arena).addVillagerSpawn(LocationSerializer.getLocation(villagerSection.getString(string)));
      }
    } else {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION", "VILLAGER SPAWNS", arena));
      return false;
    }

    ConfigurationSection doorSection = section.getConfigurationSection(id + ".doors");
    if(doorSection != null) {
      for(String string : doorSection.getKeys(false)) {
        ((Arena) arena).getMapRestorerManager().addDoor(LocationSerializer.getLocation(doorSection.getString(string + ".location")),
            (byte) doorSection.getInt(string + ".byte"));
      }
    } else {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION", "DOORS", arena));
      return false;
    }


    if(arena.getStartLocation().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
      plugin.getDebugger().sendConsoleMsg(plugin.getChatManager().colorMessage("VALIDATOR_INVALID_ARENA_CONFIGURATION", "THERE IS A WRONG " +
          "DIFFICULTY -> SET IT TO ANOTHER ONE THAN PEACEFUL", arena));
      return false;
    }
    return true;
  }

  @Override
  public @Nullable Arena getArena(Player player) {
    PluginArena pluginArena = super.getArena(player);
    if(pluginArena instanceof Arena) {
      return (Arena) pluginArena;
    }
    return null;
  }

  @Override
  public @Nullable Arena getArena(String id) {
    PluginArena pluginArena = super.getArena(id);
    if(pluginArena instanceof Arena) {
      return (Arena) pluginArena;
    }
    return null;
  }

  public @NotNull List<Arena> getPluginArenas() {
    List<Arena> arenas = new ArrayList<>();
    for(PluginArena pluginArena : super.getArenas()) {
      if(pluginArena instanceof Arena) {
        arenas.add((Arena) pluginArena);
      }
    }
    return arenas;
  }
}
