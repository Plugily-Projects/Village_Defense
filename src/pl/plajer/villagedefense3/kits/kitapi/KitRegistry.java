package pl.plajer.villagedefense3.kits.kitapi;

import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.kits.kitapi.basekits.FreeKit;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;

import java.util.ArrayList;
import java.util.List;

/**
 * Kit registry class for registering new kits.
 *
 * @author TomTheDeveloper
 */
public class KitRegistry {

    private static List<Kit> kits = new ArrayList<>();
    private static Kit defaultKit = null;

    /**
     * Method for registering new kit
     *
     * @param kit Kit to register
     */
    public static void registerKit(Kit kit) {
        kits.add(kit);
    }

    /**
     * Return default game kit
     *
     * @return default game kit
     */
    public static Kit getDefaultKit() {
        return defaultKit;
    }

    /**
     * Sets default game kit
     *
     * @param defaultKit default kit to set, must be FreeKit
     */
    public static void setDefaultKit(FreeKit defaultKit) {
        KitRegistry.defaultKit = defaultKit;
    }

    /**
     * Returns all available kits
     *
     * @return list of all registered kits
     */
    public static List<Kit> getKits() {
        return kits;
    }

    /**
     * Get registered kit by it's represented item stack
     *
     * @param itemStack itemstack that kit represents
     * @return Registered kit or default if not found
     */
    public static Kit getKit(ItemStack itemStack) {
        Kit returnKit = getDefaultKit();
        for(Kit kit : kits) {
            if(itemStack.getType() == kit.getMaterial()) {
                returnKit = kit;
                break;
            }
        }
        return returnKit;
    }


}
