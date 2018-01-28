package me.tomthedeveloper.kitapi.basekits;

import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.utils.Util;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class PremiumKit extends Kit {

    private int pointsNeeded;

    protected PremiumKit() {
    }


    public int getPointsNeeded() {
        return pointsNeeded;
    }



    public void setPointsNeeded(int pointsNeeded) {
        this.pointsNeeded = pointsNeeded;
    }

    @Override
    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.colorMessage("kits.Kit-Menu.Locked-Lores.Unlock-In-Store"));
        return itemStack;
    }
}
