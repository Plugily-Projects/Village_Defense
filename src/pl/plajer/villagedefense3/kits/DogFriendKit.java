package pl.plajer.villagedefense3.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.*;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 18/07/2015.
 */
public class DogFriendKit extends PremiumKit {

    private Main plugin;

    public DogFriendKit(Main invasion) {
        this.plugin = invasion;
        this.setName(ChatManager.colorMessage("Kits.Dog-Friend.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Dog-Friend.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        KitRegistry.registerKit(this);
    }


    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite()) || player.hasPermission("villagedefense.kit.dogfriend");
    }

    @Override
    public void giveKitItems(Player player) {

        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        ArmorHelper.setArmor(player, ArmorHelper.ArmorType.LEATHER);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(plugin.is1_8_R3()) {
            ArenaInitializer1_8_R3 initializer = (ArenaInitializer1_8_R3) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_9_R1()) {
            ArenaInitializer1_9_R1 initializer = (ArenaInitializer1_9_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_11_R1()) {
            ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_12_R1()) {
            ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
            initializer.spawnWolf(initializer.getStartLocation(), player);
        }
    }

    @Override
    public Material getMaterial() {
        return Material.BONE;
    }

    @Override
    public void reStock(Player player) {
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(plugin.is1_8_R3()) {
            ArenaInitializer1_8_R3 initializer = (ArenaInitializer1_8_R3) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_9_R1()) {
            ArenaInitializer1_9_R1 initializer = (ArenaInitializer1_9_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_11_R1()) {
            ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
        } else if(plugin.is1_12_R1()) {
            ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
            initializer.spawnWolf(initializer.getStartLocation(), player);
        }
    }
}
