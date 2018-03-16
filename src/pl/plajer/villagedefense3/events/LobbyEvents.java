package pl.plajer.villagedefense3.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.ArenaState;

/**
 * Created by Tom on 16/06/2015.
 */
public class LobbyEvents implements Listener {

    private Main plugin;

    public LobbyEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLose(FoodLevelChangeEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getEntity();
        if(ArenaRegistry.getArena(player) == null)
            return;
        Arena arena = ArenaRegistry.getArena(player);
        if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) event.setCancelled(true);
    }

    @EventHandler
    public void onLobbyHurt(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) return;
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
    }

    @EventHandler
    public void onLobbyDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) return;
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
    }

}
