package me.tomthedeveloper.chunks;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager implements Listener {


    private static ChunkManager instance;
    private List<Chunk> chunks = new ArrayList<>();

    private ChunkManager() {
    }

    public static ChunkManager getInstance() {
        if (instance == null)
            instance = new ChunkManager();
        return instance;
    }

    public void keepLoaded(Chunk chunk) {
        if (!chunk.isLoaded())
            chunk.load();
        chunks.add(chunk);
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (chunks.contains(event.getChunk()))
            event.setCancelled(true);
    }

}
