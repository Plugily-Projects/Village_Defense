package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.arena.ArenaState;
import pl.plajer.villagedefense3.handlers.UserManager;

/**
 * Created by Tom on 1/08/2014.
 */
public class SpectatorEvents implements Listener {

    private Main plugin;

    public SpectatorEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorTarget(EntityTargetEvent e) {
        if(!(e.getTarget() instanceof Player)) {
            return;
        }
        if(UserManager.getUser(e.getTarget().getUniqueId()).isSpectator()) {
            e.setCancelled(true);
            e.setTarget(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player)) {
            return;
        }
        if(UserManager.getUser(e.getTarget().getUniqueId()).isSpectator()) {
            e.setCancelled(true);
            e.setTarget(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDropItem(PlayerDropItemEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEntityEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShear(PlayerShearEntityEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConsume(PlayerItemConsumeEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!UserManager.getUser(player.getUniqueId()).isSpectator())
            return;
        if(plugin.getArenaRegistry().getArena(player) == null)
            return;
        if(player.getLocation().getY() < 1)
            player.teleport(plugin.getArenaRegistry().getArena(player).getStartLocation());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByBlock(EntityDamageByBlockEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        if(UserManager.getUser(player.getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(PlayerPickupItemEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTarget(EntityTargetEvent e) {
        if(!(e.getTarget() instanceof Player)) return;
        if(UserManager.getUser(e.getTarget().getUniqueId()).isSpectator() || UserManager.getUser(e.getTarget().getUniqueId()).isFakeDead()) {
            if(e.getEntity() instanceof ExperienceOrb || e.getEntity() instanceof Zombie || e.getEntity() instanceof Wolf) {
                e.setCancelled(true);
                e.setTarget(null);
            }
        }
    }

    //this will spawn orb at spec location when it's taken by spectator
    @EventHandler
    public void onPickup(PlayerExpChangeEvent e) {
        if(UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            Location loc = e.getPlayer().getLocation();
            e.setAmount(0);
            Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB), 30);
        }
    }

    @EventHandler
    public void onSpectate(PlayerPickupItemEvent event) {
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

    @EventHandler
    public void onSpectate(PlayerDropItemEvent event) {
        Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
        if(arena == null)
            return;
        if(arena.getArenaState() != ArenaState.IN_GAME)
            event.setCancelled(true);
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead())
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntityInteract(PlayerInteractEntityEvent event) {
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead() || user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
        if(arena != null && UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator())
            event.setCancelled(true);
    }

}
