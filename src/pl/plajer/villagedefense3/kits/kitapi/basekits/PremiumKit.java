package pl.plajer.villagedefense3.kits.kitapi.basekits;

import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class PremiumKit extends Kit {

    protected PremiumKit() {}

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.colorMessage("Kits.Kit-Menu.Locked-Lores.Unlock-In-Store"));
        return itemStack;
    }
}
