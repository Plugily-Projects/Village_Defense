package pl.plajer.villagedefense3.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;

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
        if(plugin.getGameInstanceManager().getGameInstance(player) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance.getGameState() == GameState.STARTING || gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS)
            event.setCancelled(true);
    }

    @EventHandler
    public void onLobbyHurt(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getEntity();
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null || gameInstance.getGameState() == GameState.IN_GAME)
            return;
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
    }

}
