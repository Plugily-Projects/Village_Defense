package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

    private static Main plugin;
    private static Inventory invMenu;
    private Chest chest;

    public ShopManager(Main plugin) {
        ShopManager.plugin = plugin;
        setup();
    }

    public static void openShop(Player player) {
        if(invMenu == null) {
            if(Main.isDebugged()) {
                System.out.print("[Village Debugger] Set up the shop or the shopchest first please!");
            }
            player.sendMessage(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.No-Shop-Defined"));
            return;
        }

        player.openInventory(invMenu);
    }

    public static void closeShop(Player player) {
        player.closeInventory();
    }

    private void setup() {
        if(!plugin.getConfig().contains("shop.location")) {
            if(Main.isDebugged()) {
                System.out.print("[Village Debugger] NO SHOP FOUND FOR THE GAME!!!!");
            }
            return;
        }
        Location location = Util.getLocation(true, "shop.location");
        if(!(location.getBlock().getState() instanceof Chest)) {
            if(Main.isDebugged()) {
                System.out.print("[Village Debugger] Location for shop isn't a chest!");
            }
            return;
        }

        chest = (Chest) location.getBlock().getState();
        int i = chest.getInventory().getContents().length;
        invMenu = Bukkit.createInventory(null, Util.serializeInt(i), ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"));
        i = 0;
        for(ItemStack itemStack : chest.getInventory().getContents()) {

            if(itemStack != null && itemStack.getType() != Material.REDSTONE_BLOCK)
                invMenu.setItem(i, itemStack);
            i++;
        }

    }


}
