package me.tomthedeveloper.events;

import me.tomthedeveloper.GameAPI;
import me.tomthedeveloper.handlers.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

/**
 * Created by Tom on 1/08/2014.
 */
public class onSpectate implements Listener {

    private GameAPI plugin;

    public onSpectate(GameAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpectatorTarget(EntityTargetEvent e){
        if(!(e.getTarget() instanceof Player)){
            return;
        }
        if(UserManager.getUser(e.getTarget().getUniqueId()).isSpectator()){
            e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDropItem(PlayerDropItemEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEntityEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShear(PlayerShearEntityEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConsume(PlayerItemConsumeEvent event){
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!UserManager.getUser(player.getUniqueId()).isSpectator())
            return;
        if(plugin.getGameInstanceManager().getGameInstance(player) == null)
            return;
        if(player.getLocation().getY() < 1)
            player.teleport(plugin.getGameInstanceManager().getGameInstance(player).getStartLocation());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByBlock(EntityDamageByBlockEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(PlayerPickupItemEvent event){
    	if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
    		event.setCancelled(true);
    }

}
