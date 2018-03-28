package pl.plajer.villagedefense3.handlers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.utils.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

    @Getter
    private static Map<Arena, Inventory> arenaShop = new HashMap<>();

    public ShopManager(){
        for(Arena a : ArenaRegistry.getArenas()){
            if(ConfigurationManager.getConfig("arenas").isSet("instances." + a.getID() + ".shop")){
                registerShop(a);
            }
        }
    }

    public static void registerShop(Arena a){
        Location location = Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString("instances." + a.getID() + ".shop"));
        if(!(location.getBlock().getState() instanceof Chest)) {
            if(Main.isDebugged()) {
                System.out.print("[Village Debugger] Location for shop isn't a chest!");
            }
            return;
        }
        int i = ((Chest) location.getBlock().getState()).getInventory().getContents().length;
        Inventory inventory = Bukkit.createInventory(null, Util.serializeInt(i), ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"));
        i = 0;
        for(ItemStack itemStack : ((Chest) location.getBlock().getState()).getInventory().getContents()) {
            if(itemStack != null && itemStack.getType() != Material.REDSTONE_BLOCK)
                inventory.setItem(i, itemStack);
            i++;
        }
        arenaShop.put(a, inventory);
    }

    public static void openShop(Player player) {
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(arenaShop.get(arena) == null) {
            player.sendMessage(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.No-Shop-Defined"));
            return;
        }
        player.openInventory(arenaShop.get(arena));
    }


}
