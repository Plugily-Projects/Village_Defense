package pl.plajer.villagedefense3.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
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
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.utils.Util;

import java.util.Collections;

public class SpectatorItemEvents implements Listener {

    private Main plugin;

    public SpectatorItemEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSpectatorItemClick(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(ArenaRegistry.getArena(e.getPlayer()) == null)
                return;
            if(!(e.getPlayer().getItemInHand() == null)) {
                if(e.getPlayer().getItemInHand().hasItemMeta()) {
                    if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null)
                        return;

                    if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"))) {
                        e.setCancelled(true);
                        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer());
                    }
                }
            }
        }
    }

    public void openSpectatorMenu(World world, Player p) {
        Inventory inventory = plugin.getServer().createInventory(null, Util.serializeInt(ArenaRegistry.getArena(p).getPlayers().size()), ChatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
        for(Player player : world.getPlayers()) {
            if(ArenaRegistry.getArena(player) != null && !UserManager.getUser(player.getUniqueId()).isFakeDead()) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(player.getName());
                meta.setDisplayName(player.getName());
                meta.setLore(Collections.singletonList(""));
                skull.setDurability((short) SkullType.PLAYER.ordinal());
                skull.setItemMeta(meta);
                inventory.addItem(skull);
            }
        }
        p.openInventory(inventory);
    }

    @EventHandler
    public void onSpectatorInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(ArenaRegistry.getArena(p) == null)
            return;
        Arena arena = ArenaRegistry.getArena(p);
        if(e.getCurrentItem() == null)
            return;
        if(!e.getCurrentItem().hasItemMeta())
            return;
        if(!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(!e.getCurrentItem().getItemMeta().hasLore())
            return;
        if(e.getCurrentItem().hasItemMeta()) {
            if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"))) {
                e.setCancelled(true);
                if((e.isLeftClick() || e.isRightClick())) {
                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    for(Player player : arena.getPlayers()) {
                        if(player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
                            p.sendMessage(ChatManager.formatMessage(arena, ChatManager.colorMessage("Kits.Teleporter.Teleported-To-Player"), player));
                            p.teleport(player);
                            p.closeInventory();
                            e.setCancelled(true);
                            return;

                        }
                    }
                    p.sendMessage(ChatManager.colorMessage("Kits.Teleporter.Player-Not-Found"));
                }
                e.setCancelled(true);
            }
        }
    }
}