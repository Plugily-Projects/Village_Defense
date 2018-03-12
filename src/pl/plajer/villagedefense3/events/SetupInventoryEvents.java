package pl.plajer.villagedefense3.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryEvents implements Listener {

    private Main plugin;

    public SetupInventoryEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getWhoClicked().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getWhoClicked();
        if(!player.hasPermission(PermissionsManager.getEditGames()))
            return;
        if(!event.getInventory().getName().contains("Arena:"))
            return;
        if(event.getInventory().getHolder() != null)
            return;
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;

        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        name = ChatColor.stripColor(name);

        Arena arena = plugin.getArenaRegistry().getArena(event.getInventory().getName().replace("Arena: ", ""));
        if(event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor().getType() == Material.NAME_TAG) {
            event.setCancelled(true);
            if(!event.getCursor().hasItemMeta()) {
                player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
                return;
            }
            if(!event.getCursor().getItemMeta().hasDisplayName()) {
                player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
                return;
            }

            player.performCommand("vd " + arena.getID() + " set MAPNAME " + event.getCursor().getItemMeta().getDisplayName());
            event.getCurrentItem().getItemMeta().setDisplayName(ChatColor.GOLD + "Set a mapname (currently: " + event.getCursor().getItemMeta().getDisplayName());
            return;
        }
        ClickType clickType = event.getClick();
        if(name.contains("ending location")) {
            event.setCancelled(true);

            player.performCommand("vd " + arena.getID() + " set ENDLOC");
            return;
        }
        if(name.contains("starting location")) {
            event.setCancelled(true);

            player.performCommand("vd " + arena.getID() + " set STARTLOC");
            return;
        }
        if(name.contains("lobby location")) {
            event.setCancelled(true);
            player.performCommand("vd " + arena.getID() + " set LOBBYLOC");
            return;
        }
        if(name.contains("maximum players")) {
            event.setCancelled(true);
            if(clickType.isRightClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                player.updateInventory();
                player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
            if(clickType.isLeftClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                player.updateInventory();
                player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
        }

        if(name.contains("minimum players")) {
            event.setCancelled(true);
            if(clickType.isRightClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                player.updateInventory();
                player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
            if(clickType.isLeftClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                player.updateInventory();
                player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
        }
        if(name.contains("Add game sign")) {
            event.setCancelled(true);
            player.performCommand("vda addsign " + arena.getID());
            return;
        }
        if(event.getCurrentItem().getType() != Material.NAME_TAG) {
            event.setCancelled(true);
        }
        if(name.contains("Add villager spawn")) {
            event.setCancelled(true);
            player.performCommand("vd " + arena.getID() + " addspawn villager");
            player.closeInventory();
            return;

        }
        if(name.contains("Add zombie spawn")) {
            event.setCancelled(true);
            player.performCommand("vd " + arena.getID() + " addspawn zombie");
            player.closeInventory();
        }
        if(name.contains("Add doors")) {
            event.setCancelled(true);
            player.performCommand("vd " + arena.getID() + " addspawn doors");
            player.closeInventory();
            return;

        }
        if(name.contains("Set chest shop")) {
            event.setCancelled(true);
            Block targetBlock;
            targetBlock = player.getTargetBlock(null, 100);
            if(targetBlock == null || targetBlock.getType() != Material.CHEST) {
                player.sendMessage(ChatColor.RED + "Look at the chest! You are targeting something else!");
                return;
            }
            Util.saveLoc("shop.location", targetBlock.getLocation());
            player.sendMessage(ChatColor.GREEN + "shop for chest set!");
        }


    }
}
