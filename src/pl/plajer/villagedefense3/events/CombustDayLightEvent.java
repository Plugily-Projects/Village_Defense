package pl.plajer.villagedefense3.events;

import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;

/**
 * Created by TomVerschueren on 6/02/2018.
 */
public class CombustDayLightEvent implements Listener {
    //class used to stop zombies from burning in daylight

    private Main plugin;

    public CombustDayLightEvent(Main main) {
        this.plugin = main;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Triggers when something combusts in the world.
     * Thanks to @HomieDion for part of this class!
     */
    @EventHandler(ignoreCancelled = true)
    public void onCombust(final EntityCombustEvent e) {
        // Ignore if this is caused by an event lower down the chain.
        if(e instanceof EntityCombustByEntityEvent || e instanceof EntityCombustByBlockEvent) return;
        if(!(e.getEntity() instanceof Zombie)) return;
        if(e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL) return;

        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
            ArenaInstance arenaInstance = (ArenaInstance) gameInstance;
            if(arenaInstance.getZombies().contains(e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }
}
