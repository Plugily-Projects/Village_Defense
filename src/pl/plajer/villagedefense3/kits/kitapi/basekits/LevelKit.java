package pl.plajer.villagedefense3.kits.kitapi.basekits;

import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 14/08/2014.
 */
public abstract class LevelKit extends Kit {

    int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.colorMessage("Kits.Kit-Menu.Locked-Lores.Unlock-At-Level").replaceAll("%NUMBER%", Integer.toString(getLevel())));
        return itemStack;
    }
}
