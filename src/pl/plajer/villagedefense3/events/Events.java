package pl.plajer.villagedefense3.events;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.events.customevents.SetupInventoryClickEvent;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.ShopManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.items.SpecialItemManager;
import pl.plajer.villagedefense3.utils.PercentageUtils;
import pl.plajer.villagedefense3.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        for(GameInstance instance : plugin.getGameInstanceManager().getGameInstances()) {
            if(event.getEntity().getWorld().equals(instance.getStartLocation().getWorld())) {
                if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerExpChangeEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        event.setAmount((int) Math.ceil(event.getAmount() * 1.6));
        if(user.isFakeDead()) {
            event.setAmount(0);
            return;
        }
        //bonus orbs with custom permissions
        for(String perm : plugin.getCustomPermissions().keySet()) {
            if(event.getPlayer().hasPermission(perm)) {
                user.addInt("orbs", (int) Math.ceil(event.getAmount() * (plugin.getCustomPermissions().get(perm) / 100)));
            }
        }

        if(event.getPlayer().hasPermission(PermissionsManager.getElite())) {
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 1.5));
        } else if(event.getPlayer().hasPermission(PermissionsManager.getMvp())) {
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 1.0));
        } else if(event.getPlayer().hasPermission(PermissionsManager.getVip())) {
            user.addInt("orbs", (int) Math.ceil(event.getAmount() * 0.5));
        } else {
            user.addInt("orbs", event.getAmount());
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKitMenuItemClick(InventoryClickEvent event) {
        ItemStack inv = event.getCurrentItem();
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getWhoClicked());
        if(gameInstance == null)
            return;
        if(inv == null || !inv.hasItemMeta() || !inv.getItemMeta().hasDisplayName() || inv.getType() != plugin.getKitManager().getMaterial() || !inv.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void KitMenuItemClick(InventoryClickEvent event) {
        ItemStack inv = event.getCursor();
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getWhoClicked());
        if(gameInstance == null)
            return;
        if(inv == null || !inv.hasItemMeta() || !inv.getItemMeta().hasDisplayName() || inv.getType() != plugin.getKitManager().getMaterial() || !inv.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
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
        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
            if(gameInstance.getStartLocation().getWorld().getName().equals(event.getLocation().getWorld().getName()) && gameInstance.getStartLocation().distance(event.getLocation()) < 300)
                event.blockList().clear();
        }
    }

    @EventHandler
    public void onSetupClick(SetupInventoryClickEvent event) {
        String name = event.getItemStack().getItemMeta().getDisplayName();
        if(name.contains("Add villager")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("vd " + event.getGameInstance().getID() + " addspawn villager");
            event.getPlayer().closeInventory();
            return;

        }
        if(name.contains("Add zombie")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("vd " + event.getGameInstance().getID() + " addspawn zombie");
            event.getPlayer().closeInventory();
        }
        if(name.contains("Add doors")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("vd " + event.getGameInstance().getID() + " addspawn doors");
            event.getPlayer().closeInventory();
            return;

        }
        if(name.contains("Set the chest shop")) {
            event.setCancelled(true);
            Block targetBlock;
            targetBlock = event.getPlayer().getTargetBlock(null, 100);
            if(targetBlock == null || targetBlock.getType() != Material.CHEST) {
                event.getPlayer().sendMessage(ChatColor.RED + "Look at the chest! You are targeting something else!");
                return;
            }
            Util.saveLoc("shop.location", targetBlock.getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "shop for chest set!");
        }
    }

    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(event.getPlayer().getItemInHand().getType() == Material.SADDLE) {
            if(event.getRightClicked().getType() == EntityType.IRON_GOLEM || event.getRightClicked().getType() == EntityType.VILLAGER) {
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
                event.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Cant-Ride-Others-Golem"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCommands(PlayerCommandPreprocessEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null) return;
        if(!plugin.getConfig().getBoolean("Block-Commands-In-Game")) return;
        if(event.getMessage().contains("leave") || event.getMessage().contains("stats")) return;
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission(PermissionsManager.getEditGames())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    }

    @EventHandler
    public void onDoorDrop(ItemSpawnEvent event) {
        if(event.getEntity().getItemStack().getType() == Material.WOOD_DOOR) {
            for(Entity entity : Util.getNearbyEntities(event.getLocation(), 20)) {
                if(entity.getType() == EntityType.PLAYER) {
                    if(plugin.getGameInstanceManager().getGameInstance((Player) entity) != null) {
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
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
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
                gameInstance.leaveAttempt(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if(event.getEntity().getType() == EntityType.ZOMBIE) {
            for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                ArenaInstance instance = (ArenaInstance) gameInstance;
                Zombie zombie = (Zombie) event.getEntity();
                if(instance.getZombies().contains(zombie)) {
                    instance.removeZombie(zombie);
                    if(event.getEntity().getKiller() == null)
                        return;
                    if(event.getEntity().getKiller().getType() == EntityType.PLAYER) {
                        Player player = event.getEntity().getKiller();

                        if(plugin.getGameInstanceManager().getGameInstance(player) != null)
                            plugin.getRewardsHandler().performZombieKillReward(player);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFriendHurt(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getDamager());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(event.getDamager().getUniqueId());
        if(user.isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(!VILLAGE_ENTITIES.contains(event.getEntityType()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onZombieHurt(EntityDamageEvent e) {
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            if(!(e.getEntity() instanceof Zombie)) {
                return;
            }
            for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                ArenaInstance arenaInstance = (ArenaInstance) gameInstance;
                if(arenaInstance.getZombies().contains(e.getEntity())) {
                    e.getEntity().setCustomName(PercentageUtils.getProgressBar((int) ((Zombie) e.getEntity()).getHealth(), (int) ((Zombie) e.getEntity()).getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
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
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) arrow.getShooter());
        if(gameInstance == null)
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
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getEntity());
        if(gameInstance == null)
            return;
        if(gameInstance.getGameState() == GameState.STARTING || gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS || gameInstance.getGameState() == GameState.ENDING) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShop(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getWhoClicked());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(player.getUniqueId());
        if(user.isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        if(inv.getName() == null || !inv.getName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name")))
            return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasLore())
            return;
        String string = event.getCurrentItem().getItemMeta().getLore().get(0);
        string = ChatColor.stripColor(string);
        if(!(string.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || string.contains("orbs"))) {
            boolean b = false;
            for(String s : event.getCurrentItem().getItemMeta().getLore()) {
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
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
            return;
        }
        if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if(event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Golem-Item-Name"))) {
                ((ArenaInstance) gameInstance).spawnGolem(gameInstance.getStartLocation(), player);
                player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Golem-Spawned"));
                UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
                return;

            }
            /*
             * TODO
             * Add translatable message for 'Spawn Wolf' item
             */
            if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Spawn Wolf")) {
                ((ArenaInstance) gameInstance).spawnWolf(gameInstance.getStartLocation(), player);
                player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Wolf-Spawned"));
                UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
                return;
            }
        }

        ItemStack itemStack = event.getCurrentItem().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        Iterator iterator = lore.iterator();
        while(iterator.hasNext()) {
            String s = (String) iterator.next();
            if(s.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
                lore.remove(s);
            }
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
        UserManager.getUser(player.getUniqueId()).setInt("orbs", UserManager.getUser(player.getUniqueId()).getInt("orbs") - price);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onDoorPlace(BlockPlaceEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isSpectator()) {
            event.setCancelled(true);
            return;
        }
        if(event.getPlayer().getItemInHand() == null) {
            event.setCancelled(true);
            return;
        }
        if(!(event.getPlayer().getItemInHand().getType() == Material.WOOD_DOOR || event.getPlayer().getItemInHand().getType() == Material.WOODEN_DOOR)) {
            event.setCancelled(true);
            return;
        }
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance(event.getPlayer());

        if(!arenaInstance.getDoorLocations().containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Worker.Game-Item-Place-Message"));
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead()) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraft(PlayerInteractEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null) return;
        if(event.getPlayer().getTargetBlock(null, 7).getType() == Material.WORKBENCH)
            event.setCancelled(true);
    }


    @EventHandler
    public void onRottenFleshDrop(InventoryPickupItemEvent event) {
        if(event.getInventory().getType() != InventoryType.HOPPER)
            return;
        for(Entity entity : Util.getNearbyEntities(event.getItem().getLocation(), 20)) {
            if(!(entity instanceof Player)) {
                continue;
            }
            if(plugin.getGameInstanceManager().getGameInstance((Player) entity) != null) {
                if(event.getItem().getItemStack().getType() != Material.ROTTEN_FLESH) {
                    continue;
                }
                GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) entity);
                if(gameInstance == null)
                    continue;
                ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance(((Player) entity));
                arenaInstance.addRottenFlesh(event.getItem().getItemStack().getAmount());
                event.getItem().remove();
                event.getInventory().clear();
                event.getItem().getLocation().getWorld().spigot().playEffect(event.getItem().getLocation(), Effect.CLOUD, 0, 0, 2, 2, 2, 1, 50, 100);
                if(arenaInstance.checkLevelUpRottenFlesh()) {
                    for(Player player : arenaInstance.getPlayers()) {
                        player.setMaxHealth(player.getMaxHealth() + 2.0);
                    }
                    for(Player player1 : gameInstance.getPlayers()) {
                        String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Rotten-Flesh-Level-Up"), player1);
                        player1.sendMessage(ChatManager.PLUGINPREFIX + message);
                    }
                }
            }
        }
    }

}
