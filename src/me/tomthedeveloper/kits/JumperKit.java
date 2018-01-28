package me.tomthedeveloper.kits;

import me.tomthedeveloper.User;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.PremiumKit;
import me.tomthedeveloper.permissions.PermissionsManager;
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
 * Created by Tom on 19/08/2014.
 */
public class JumperKit extends PremiumKit {

    public JumperKit() {
        setName(ChatManager.colorMessage("kits.Bunny.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Bunny.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));

    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVIP()) || player.hasPermission(PermissionsManager.getMVP()) || player.hasPermission(PermissionsManager.getELITE()) || player.hasPermission("villagedefense.kit.jumper");
    }

    @Override
    public void giveKitItems(Player player) {
        User user = UserManager.getUser(player.getUniqueId());
        //user.setAllowDoubleJump(true);
        ArmorHelper.setColouredArmor(Color.AQUA, player);
        player.getInventory().addItem(WeaponHelper.getEnchantedBow(new Enchantment[]{
                Enchantment.DURABILITY, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK}, new int[]{10, 4, 2}));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
    }

    @Override
    public Material getMaterial() {
        return Material.CARROT_ITEM;
    }

    @Override
    public void reStock(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ARROW, 16));
    }
}
