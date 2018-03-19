package pl.plajer.villagedefense3.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.MessageHandler;
import pl.plajer.villagedefense3.handlers.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaEvents implements Listener {

    private Main plugin;

    public ArenaEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDieEntity(EntityDeathEvent event) {
        if(event.getEntity().getType() == EntityType.ZOMBIE) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(arena.getZombies().contains(event.getEntity()))
                    arena.removeZombie((Zombie) event.getEntity());
                if(event.getEntity().getKiller() != null) {
                    if(ArenaRegistry.getArena(event.getEntity().getKiller()) != null) {
                        arena.addStat(event.getEntity().getKiller(), "kills");
                        arena.addStat(event.getEntity().getKiller(), 2);
                    }
                }
            }
        }
        if(event.getEntity().getType() == EntityType.VILLAGER) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(arena.getVillagers().contains(event.getEntity())) {
                    arena.getStartLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                    arena.removeVillager((Villager) event.getEntity());
                    for(Player p : arena.getPlayers()) {
                        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Villager-Died"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        Arena arena = ArenaRegistry.getArena(e.getEntity());
        if(arena == null) return;
        if(arena.getPlayers().contains(e.getEntity())) this.onDeath(e.getEntity(), arena);
        if(e.getEntity().isDead()) e.getEntity().setHealth(e.getEntity().getMaxHealth());
        e.setDeathMessage("");
        e.getDrops().clear();
        e.setDroppedExp(0);
    }

    private void onDeath(final Player player, Arena arena) {
        if(arena.getArenaState() == ArenaState.STARTING) {
            player.teleport(arena.getStartLocation());
            return;
        }
        if(arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
            player.getInventory().clear();
            player.setFlying(false);
            player.setAllowFlight(false);
            User user = UserManager.getUser(player.getUniqueId());
            user.setInt("orbs", 0);
            player.teleport(arena.getEndLocation());
            return;
        }
        User user = UserManager.getUser(player.getUniqueId());
        arena.addStat(player, "deaths");

        if(user.isFakeDead()) {
            player.setAllowFlight(true);
            player.setGameMode(GameMode.SURVIVAL);
            arena.teleportToStartLocation(player);
        } else {
            arena.teleportToStartLocation(player);
            user.setSpectator(true);
            player.setGameMode(GameMode.SURVIVAL);
            user.setFakeDead(true);
            user.setInt("orbs", 0);
            ArenaUtils.hidePlayer(player, arena);
            player.setAllowFlight(true);
            player.getInventory().clear();
            MessageHandler.sendTitle(player, ChatColor.stripColor(ChatManager.formatMessage(arena, "In-Game.Death-Screen")), 0, 5 * 20, 0, ChatColor.RED);
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(user.isSpectator()) {
                    MessageHandler.sendActionBar(player, ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Died-Respawn-In-Next-Wave")));
                }
            }, 20, 20);
            ChatManager.broadcastDeathMessage(arena, player);

            arena.teleportToStartLocation(player);

            player.setAllowFlight(true);
            player.setFlying(true);

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
            spectatorItem.setItemMeta(spectatorMeta);
            player.getInventory().addItem(spectatorItem);
        }
        //tryin to untarget dead player bcuz they will still target him
        for(Zombie zombie : arena.getZombies()) {
            if(zombie.getTarget() != null) {
                if(zombie.getTarget().equals(player)) {
                    //set new target as villager so zombies won't stay still waiting for nothing
                    for(Villager villager : arena.getVillagers()) {
                        zombie.setTarget(villager);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getPlayers().contains(event.getPlayer())) {
            this.onRespawn(event.getPlayer(), arena);
            event.setRespawnLocation(arena.getStartLocation());
        }
    }

    private void onRespawn(Player player, Arena arena) {
        User user = UserManager.getUser(player.getUniqueId());
        if(user.isFakeDead()) {
            arena.teleportToStartLocation(player);
            player.setAllowFlight(true);
            player.setFlying(true);

        } else {
            arena.teleportToStartLocation(player);
            user.setSpectator(true);
            player.setGameMode(GameMode.SURVIVAL);
            user.setFakeDead(true);
            player.setAllowFlight(true);
            player.setFlying(true);
            user.setInt("orbs", 0);
        }
    }

}
