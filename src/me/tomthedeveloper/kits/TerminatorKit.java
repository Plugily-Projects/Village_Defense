package me.tomthedeveloper.kits;

import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.LevelKit;
import me.tomthedeveloper.utils.ArmorHelper;
import me.tomthedeveloper.utils.Items;
import me.tomthedeveloper.utils.Util;
import me.tomthedeveloper.utils.WeaponHelper;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.List;

/**
 * Created by Tom on 18/07/2015.
 */
public class TerminatorKit extends LevelKit {


    public TerminatorKit() {
    	setName(ChatManager.colorMessage("kits.Terminator.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Terminator.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        setLevel(20);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.isOp() || player.hasPermission("villagefense.kit.terminator");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.BONE), new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK}, new int[]{3, 7}));
        ArmorHelper.setColouredArmor(Color.BLACK, player);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        player.getInventory().addItem(Items.getPotion(PotionType.STRENGTH, 2, true, 1));
        player.getInventory().addItem(Items.getPotion(PotionType.REGEN, 1, true, 1));

    }

    @Override
    public Material getMaterial() {
        return Material.ANVIL;
    }

    @Override
    public void reStock(Player player) {
        for (int i = 0; i < 2; i++) {
            player.getInventory().addItem(Items.getPotion(PotionType.STRENGTH, 2, true, 1));
        }

    }
}
