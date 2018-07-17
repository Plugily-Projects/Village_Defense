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

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager implements Listener {

  private List<Chunk> chunks = new ArrayList<>();

  public ChunkManager(Main plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void keepLoaded(Chunk chunk) {
    if (!chunk.isLoaded()) {
      chunk.load();
    }
    chunks.add(chunk);
  }

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    if (chunks.contains(event.getChunk())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    for (Entity entity : event.getChunk().getEntities()) {
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (entity.getWorld().getName().equals(arena.getStartLocation().getWorld().getName()) && entity.getLocation().distance(arena.getStartLocation()) < 300) {
          if (entity instanceof Player || entity instanceof Wolf || entity instanceof IronGolem || entity instanceof Villager || entity instanceof Zombie) {
            entity.remove();
          }
        }
      }
    }
  }
}
