package pl.plajer.villagedefense3.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;
import pl.plajer.villagedefense3.versions.ArenaInstance1_11_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_12_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_8_R3;
import pl.plajer.villagedefense3.versions.ArenaInstance1_9_R1;

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
        plugin.getKitRegistry().registerKit(this);
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
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null)
            return;
        if(!(gameInstance instanceof ArenaInstance)) {
            return;
        }
        if(plugin.is1_8_R3()) {
            ArenaInstance1_8_R3 invasionInstance1_8_r3 = (ArenaInstance1_8_R3) gameInstance;
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_9_R1()) {
            ArenaInstance1_9_R1 invasionInstance1_8_r3 = (ArenaInstance1_9_R1) gameInstance;
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_11_R1()) {
            ArenaInstance1_11_R1 invasionInstance1_8_r3 = (ArenaInstance1_11_R1) gameInstance;
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_12_R1()) {
            ArenaInstance1_12_R1 invasionInstance1_8_r3 = (ArenaInstance1_12_R1) gameInstance;
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
        }
    }

    @Override
    public Material getMaterial() {
        return Material.BONE;
    }

    @Override
    public void reStock(Player player) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null)
            return;
        if(!(gameInstance instanceof ArenaInstance)) {
            return;
        }
        if(plugin.is1_8_R3()) {
            ArenaInstance1_8_R3 invasionInstance1_8_r3 = (ArenaInstance1_8_R3) gameInstance;
            invasionInstance1_8_r3.spawnWolf(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_9_R1()) {
            ArenaInstance1_9_R1 invasionInstance1_12_R1 = (ArenaInstance1_9_R1) gameInstance;
            invasionInstance1_12_R1.spawnWolf(invasionInstance1_12_R1.getStartLocation(), player);
        }
        if(plugin.is1_11_R1()) {
            ArenaInstance1_11_R1 invasionInstance1_12_R1 = (ArenaInstance1_11_R1) gameInstance;
            invasionInstance1_12_R1.spawnWolf(invasionInstance1_12_R1.getStartLocation(), player);
        }
        if(plugin.is1_12_R1()) {
            ArenaInstance1_12_R1 invasionInstance1_12_R1 = (ArenaInstance1_12_R1) gameInstance;
            invasionInstance1_12_R1.spawnWolf(invasionInstance1_12_R1.getStartLocation(), player);
        }
    }
}
