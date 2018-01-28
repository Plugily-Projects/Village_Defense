package me.tomthedeveloper.kits;

import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.LevelKit;
import me.tomthedeveloper.utils.ArmorHelper;
import me.tomthedeveloper.utils.Util;
import me.tomthedeveloper.utils.WeaponHelper;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Tom on 14/08/2014.
 */
public class ArcherKit extends LevelKit {

    public ArcherKit() {
        this.setLevel(2);
        this.setName(ChatManager.colorMessage("kits.Archer.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Archer.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));


    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.hasPermission("villagefense.kit.archer");

    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setColouredArmor(Color.GREEN, player);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
        player.getInventory().addItem(WeaponHelper.getEnchantedBow(Enchantment.DURABILITY, 10));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    }

    @Override
    public Material getMaterial() {
        return Material.BOW;
    }

    @Override
    public void reStock(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ARROW, 5));
    }
}
