package pl.plajer.villagedefense3.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import pl.plajer.villagedefense3.Main;

/**
 * Created by Tom on 2/08/2014.
 */
public class ItemUtils {

    public static Main villageDefense;

    public static ItemStack getPotion(PotionType type, int tier, boolean splash, int amount) {
        if(villageDefense.is1_8_R3()) {
            Potion potion = new Potion(type);
            potion.setLevel(tier);
            potion.setSplash(splash);
            return potion.toItemStack(amount);
        } else {
            //FOR 1.9 AND LATER
            ItemStack potion;
            if(!splash) {
                potion = new ItemStack(Material.POTION, 1);
            } else {
                potion = new ItemStack(Material.SPLASH_POTION, 1);
            }

            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            if(tier >= 2 && !splash) {
                meta.setBasePotionData(new PotionData(type, false, true));
            } else {
                meta.setBasePotionData(new PotionData(type, false, false));
            }
            potion.setItemMeta(meta);
            return potion;
        }
    }

}
