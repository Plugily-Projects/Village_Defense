package me.tomthedeveloper.events;

import me.tomthedeveloper.GameAPI;
import me.tomthedeveloper.Main;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class SpectatorItemEvents implements Listener {

    private GameAPI gameAPI;
    private Main plugin;

    public SpectatorItemEvents(GameAPI gameAPI, Main plugin) {
        this.plugin = plugin;
        this.gameAPI = gameAPI;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSpectatorItemClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gameAPI.getGameInstanceManager().getGameInstance(e.getPlayer()) == null)
                return;
            if (!(e.getPlayer().getItemInHand() == null)) {
                if (e.getPlayer().getItemInHand().hasItemMeta()) {
                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null)
                        return;

                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("In-game.Spectator.Spectator-Item-Name"))) {
                        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer());
                    }
                }
            }
            if (e.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
                e.setCancelled(true);
            }

        }
    }

    public void openSpectatorMenu(World world, Player p) {
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        Inventory inventory = plugin.getServer().createInventory(null, 18, ChatManager.colorMessage("In-game.Spectator.Spectator-Menu-Name"));
        for(Player player : world.getPlayers()) {
            if(gameAPI.getGameInstanceManager().getGameInstance(player) != null && !UserManager.getUser(player.getUniqueId()).isFakeDead()) {
                ItemStack skull = new ItemStack(397, 1, (short) 3);

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(player.getName());
                meta.setDisplayName(player.getName());
                meta.setLore(Arrays.asList(""));
                skull.setItemMeta(meta);
                inventory.addItem(skull);
            }
        }
        p.openInventory(inventory);
    }

    @EventHandler
    public void onSpectatorInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(gameAPI.getGameInstanceManager().getGameInstance(p) == null)
            return;
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        if(e.getCurrentItem() == null)
            return;
        if(!e.getCurrentItem().hasItemMeta())
            return;
        if(!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(!e.getCurrentItem().getItemMeta().hasLore())
            return;
        if(e.getCurrentItem().hasItemMeta()) {
            if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("In-game.Spectator.Spectator-Menu-Name"))) {
                e.setCancelled(true);
                if((e.isLeftClick() || e.isRightClick())) {
                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    for(Player player : gameInstance.getPlayers()) {
                        if(player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
                            p.sendMessage(ChatManager.formatMessage(ChatManager.colorMessage("kits.Teleporter.Teleported-To-Player"), player));
                            p.teleport(player);
                            p.closeInventory();
                            e.setCancelled(true);
                            return;

                        }
                    }
                    p.sendMessage(ChatManager.colorMessage("kits.Teleporter.Player-Not-Found"));
                }
                e.setCancelled(true);
            }
        }
    }
}