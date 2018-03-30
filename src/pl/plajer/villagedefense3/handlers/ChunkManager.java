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

    private Main plugin;
    private List<Chunk> chunks = new ArrayList<>();

    public ChunkManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void keepLoaded(Chunk chunk) {
        if(!chunk.isLoaded())
            chunk.load();
        chunks.add(chunk);
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(chunks.contains(event.getChunk()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for(Entity entity : event.getChunk().getEntities()) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(entity.getWorld().getName().equals(arena.getStartLocation().getWorld().getName()) && entity.getLocation().distance(arena.getStartLocation()) < 300) {
                    if(entity instanceof Player || entity instanceof Wolf || entity instanceof IronGolem || entity instanceof Villager || entity instanceof Zombie) {
                        entity.remove();
                    }
                }
            }
        }
    }
}
