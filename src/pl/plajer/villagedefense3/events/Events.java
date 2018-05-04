/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.ArenaState;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.ShopManager;
import pl.plajer.villagedefense3.items.SpecialItemManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 16/08/2014.
 */
public class Events implements Listener {

    private final List<EntityType> VILLAGE_ENTITIES = Arrays.asList(EntityType.PLAYER, EntityType.WOLF, EntityType.IRON_GOLEM, EntityType.VILLAGER);
    private Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(event.getEntity().getWorld().equals(arena.getStartLocation().getWorld())) {
                if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerExpChangeEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null)
            return;
        int amount = (int) Math.ceil(event.getAmount() * 1.6);
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        event.setAmount(amount);
        if(user.isFakeDead()) {
            event.setAmount(0);
            return;
        }
        //bonus orbs with custom permissions
        for(String perm : plugin.getCustomPermissions().keySet()) {
            if(event.getPlayer().hasPermission(perm)) {
                amount = +(int) Math.ceil(event.getAmount() * (plugin.getCustomPermissions().get(perm) / 100));
                user.addInt("orbs", (int) Math.ceil(event.getAmount() * (plugin.getCustomPermissions().get(perm) / 100)));
            }
        }

        if(event.getPlayer().hasPermission(PermissionsManager.getElite())) {
            amount += (int) Math.ceil(event.getAmount() * 1.5);
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 1.5));
        } else if(event.getPlayer().hasPermission(PermissionsManager.getMvp())) {
            amount += (int) Math.ceil(event.getAmount() * 1.0);
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 1.0));
        } else if(event.getPlayer().hasPermission(PermissionsManager.getVip())) {
            amount += (int) Math.ceil(event.getAmount() * 0.5);
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 0.5));
        } else {
            amount += event.getAmount();
            user.addInt("orbs", event.getAmount());
        }
        event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Orbs-Pickup").replaceAll("%number%", String.valueOf(amount)));
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKitMenuItemClick(InventoryClickEvent event) {
        ItemStack inv = event.getCurrentItem();
        Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
        if(arena == null)
            return;
        if(inv == null || !inv.hasItemMeta() || !inv.getItemMeta().hasDisplayName() || inv.getType() != plugin.getKitManager().getMaterial() || !inv.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void KitMenuItemClick(InventoryClickEvent event) {
        ItemStack inv = event.getCursor();
        Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
        if(arena == null)
            return;
        if(inv == null || !inv.hasItemMeta() || !inv.getItemMeta().hasDisplayName() || inv.getType() != plugin.getKitManager().getMaterial() || !inv.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(event.getItemDrop().getItemStack().getType() == Material.SADDLE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ExplosionCancel(EntityExplodeEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getStartLocation().getWorld().getName().equals(event.getLocation().getWorld().getName()) && arena.getStartLocation().distance(event.getLocation()) < 300)
                event.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(event.getPlayer().getItemInHand().getType() == Material.SADDLE) {
            if(event.getRightClicked().getType() == EntityType.IRON_GOLEM || event.getRightClicked().getType() == EntityType.VILLAGER || event.getRightClicked().getType() == EntityType.WOLF) {
                event.getRightClicked().setPassenger(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
        if(event.getRightClicked().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
            ShopManager.openShop(event.getPlayer());
        } else if(event.getRightClicked().getType() == EntityType.IRON_GOLEM) {
            IronGolem ironGolem = (IronGolem) event.getRightClicked();
            if(ironGolem.getCustomName() != null && ironGolem.getCustomName().contains(event.getPlayer().getName())) {
                event.getRightClicked().setPassenger(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Cant-Ride-Others-Golem"));
            }
        } else if(event.getRightClicked().getType() == EntityType.WOLF) {
            Wolf wolf = (Wolf) event.getRightClicked();
            if(wolf.getCustomName() != null && wolf.getCustomName().contains(event.getPlayer().getName())) {
                event.getRightClicked().setPassenger(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(!plugin.getConfig().getBoolean("Block-Commands-In-Game")) return;
        if(event.getMessage().contains("leave") || event.getMessage().contains("stats")) return;
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission(PermissionsManager.getEditGames())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    }

    @EventHandler
    public void onDoorDrop(ItemSpawnEvent event) {
        if(event.getEntity().getItemStack().getType() == Material.WOOD_DOOR) {
            for(Entity entity : Util.getNearbyEntities(event.getLocation(), 20)) {
                if(entity.getType() == EntityType.PLAYER) {
                    if(ArenaRegistry.getArena((Player) entity) != null) {
                        event.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null)
            return;
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null)
            return;
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if(key == null)
            return;
        if(SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Leave")) {
            event.setCancelled(true);
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(event.getPlayer());
            } else {
                arena.leaveAttempt(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFriendHurt(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        Arena arena = ArenaRegistry.getArena((Player) event.getDamager());
        if(arena == null)
            return;
        if(UserManager.getUser(event.getDamager().getUniqueId()).isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(!VILLAGE_ENTITIES.contains(event.getEntityType()))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onZombieHurt(EntityDamageEvent e) {
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            if(!(e.getEntity() instanceof Zombie)) {
                return;
            }
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(arena.getZombies().contains(e.getEntity())) {
                    e.getEntity().setCustomName(Util.getProgressBar((int) ((Zombie) e.getEntity()).getHealth(), (int) ((Zombie) e.getEntity()).getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecond(EntityDamageByEntityEvent event) {
        User user = UserManager.getUser((event.getDamager().getUniqueId()));
        if(user.isFakeDead() || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }
        if(!(event.getDamager() instanceof Arrow))
            return;
        Arrow arrow = (Arrow) event.getDamager();
        if(arrow.getShooter() == null)
            return;
        if(!(arrow.getShooter() instanceof Player))
            return;
        Arena arena = ArenaRegistry.getArena((Player) arrow.getShooter());
        if(arena == null)
            return;
        if(user.isFakeDead() || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }
        if(!VILLAGE_ENTITIES.contains(event.getEntityType()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityLeash(PlayerLeashEntityEvent event) {
        if(event.getEntity() instanceof Villager) {
            ((Villager) event.getEntity()).setLeashHolder(event.getPlayer());
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Arena arena = ArenaRegistry.getArena((Player) event.getEntity());
        if(arena == null)
            return;
        if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.ENDING) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShop(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) e.getWhoClicked();
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null || e.getInventory().getName() == null || !e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name")))
            return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasLore())
            return;
        String string = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0));
        if(!(string.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || string.contains("orbs"))) {
            boolean b = false;
            for(String s : e.getCurrentItem().getItemMeta().getLore()) {
                if(string.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || string.contains("orbs")) {
                    string = s;
                    b = true;
                }
            }
            if(!b)
                return;
        }
        int price = Integer.parseInt(string.split(" ")[0]);
        if(price > UserManager.getUser(player.getUniqueId()).getInt("orbs")) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
            return;
        }
        if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Golem-Item-Name"))) {
                arena.spawnGolem(arena.getStartLocation(), player);
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Golem-Spawned"));
                UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
                return;

            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Wolf-Item-Name"))) {
                arena.spawnWolf(arena.getStartLocation(), player);
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Wolf-Spawned"));
                UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
                return;
            }
        }

        ItemStack itemStack = e.getCurrentItem().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        for(String loopLore : lore) {
            if(loopLore.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
                lore.remove(loopLore);
            }
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
        UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
    }

    @EventHandler(priority = EventPriority.HIGH)
    //highest priority to fully protecc our game (i didn't set it because my test server was destroyed, n-no......)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if(!ArenaRegistry.isInArena(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    //highest priority to fully protecc our game (i didn't set it because my test server was destroyed, n-no......)
    public void onBuild(BlockPlaceEvent event) {
        if(!ArenaRegistry.isInArena(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraft(PlayerInteractEvent event) {
        if(!ArenaRegistry.isInArena(event.getPlayer())) return;
        if(event.getPlayer().getTargetBlock(null, 7).getType() == Material.WORKBENCH)
            event.setCancelled(true);
    }

    @EventHandler
    public void onRottenFleshDrop(InventoryPickupItemEvent event) {
        if(event.getInventory().getType() != InventoryType.HOPPER) {
            return;
        }
        if(event.getItem().getItemStack().getType() != Material.ROTTEN_FLESH) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(event.getItem().getWorld().equals(arena.getStartLocation().getWorld())) {
                    event.getItem().remove();
                    event.getInventory().clear();
                    return;
                }
            }
            return;
        }
        for(Entity entity : Util.getNearbyEntities(event.getItem().getLocation(), 20)) {
            if(!(entity instanceof Player)) {
                continue;
            }
            if(ArenaRegistry.getArena((Player) entity) != null) {
                Arena arena = ArenaRegistry.getArena(((Player) entity));
                if(arena == null) continue;
                arena.addRottenFlesh(event.getItem().getItemStack().getAmount());
                event.getItem().remove();
                event.setCancelled(true);
                event.getInventory().clear();
                event.getItem().getLocation().getWorld().spigot().playEffect(event.getItem().getLocation(), Effect.CLOUD, 0, 0, 2, 2, 2, 1, 50, 100);
                if(arena.checkLevelUpRottenFlesh()) {
                    for(Player player : arena.getPlayers()) {
                        player.setMaxHealth(player.getMaxHealth() + 2.0);
                    }
                    for(Player player1 : arena.getPlayers()) {
                        String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Rotten-Flesh-Level-Up"), player1);
                        player1.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                    }
                }
            }
        }
    }

}
