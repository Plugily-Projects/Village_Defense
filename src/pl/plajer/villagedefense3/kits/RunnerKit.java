package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class RunnerKit extends LevelKit {

    public RunnerKit(Main plugin) {
        setLevel(ConfigurationManager.getConfig("kits").getInt("Required-Level.Runner"));
        setName(ChatManager.colorMessage("Kits.Runner.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Runner.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.isOp() || player.hasPermission("villagefense.kit.runner");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STICK), new Enchantment[]{
                Enchantment.KNOCKBACK, Enchantment.DAMAGE_UNDEAD, Enchantment.DURABILITY}, new int[]{2, 1, 10}));
        ArmorHelper.setColouredArmor(Color.BLUE, player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
    }

    @Override
    public Material getMaterial() {
        return Material.FIREWORK;
    }

    @Override
    public void reStock(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
    }
}
