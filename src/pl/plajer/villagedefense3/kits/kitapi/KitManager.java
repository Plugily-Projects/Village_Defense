package pl.plajer.villagedefense3.kits.kitapi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.villagedefenseapi.VillagePlayerChooseKitEvent;

import java.util.Arrays;

/**
 * Class for setting Kit values.
 * Need to use before registering new kit!
 *
 * @author TomTheDeveloper
 */
public class KitManager implements Listener {

    private Main plugin;
    private Inventory invMenu;
    private String itemName;
    private Material material;
    private String[] description;
    private String menuName;

    private String UNLOCKED;
    private String LOCKED;

    public KitManager(Main plugin) {
        itemName = ChatManager.colorMessage("Kits.Kit-Menu-Item-Name");
        this.plugin = plugin;
        UNLOCKED = ChatManager.colorMessage("Kits.Kit-Menu.Unlocked-Kit-Lore");
        LOCKED = ChatManager.colorMessage("Kits.Kit-Menu.Locked-Lores.Locked-Lore");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Returns name of kit
     *
     * @return name of kit
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets name of kit
     *
     * @param name kit name
     */
    public void setItemName(String name) {
        this.itemName = name;
    }

    private void createKitMenu(Player player) {
        invMenu = Bukkit.createInventory(null, Util.serializeInt(plugin.getKitRegistry().getKits().size()), getMenuName());
        for(Kit kit : plugin.getKitRegistry().getKits()) {
            ItemStack itemStack = kit.getItemStack();
            if(kit.isUnlockedByPlayer(player))
                Util.addLore(itemStack, UNLOCKED);
            else
                Util.addLore(itemStack, LOCKED);

            invMenu.addItem(itemStack);
        }
    }

    /**
     * Returns material represented by kit
     *
     * @return material represented by kit
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets material that kit will represents
     *
     * @param material material that kit will represents
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Returns description of kit
     *
     * @return description of kit
     */
    public String[] getDescription() {
        return description;
    }

    /**
     * Sets description of kit
     *
     * @param description description of kit
     */
    public void setDescription(String[] description) {
        this.description = description;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void openKitMenu(Player player) {
        createKitMenu(player);
        player.openInventory(invMenu);
    }

    public void giveKitMenuItem(Player player) {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getItemName());
        itemMeta.setLore(Arrays.asList(getDescription()));
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
    }


    @EventHandler
    private void onKitMenuItemClick(PlayerInteractEvent event) {
        if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        if(event.getPlayer().getItemInHand().getType() != getMaterial())
            return;
        if(!event.getPlayer().getItemInHand().hasItemMeta())
            return;
        if(!event.getPlayer().getItemInHand().getItemMeta().hasLore())
            return;
        if(!event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getItemName())) return;
        openKitMenu(event.getPlayer());
    }

    @EventHandler
    public void onKitChoose(InventoryClickEvent event) {
        if(!event.getInventory().getName().equalsIgnoreCase(getMenuName()))
            return;
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if(event.getCurrentItem() == null)
            return;
        if(!(event.isLeftClick() || event.isRightClick()))
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        VillagePlayerChooseKitEvent villagePlayerChooseKitEvent = new VillagePlayerChooseKitEvent(player, plugin.getKitRegistry().getKit(event.getCurrentItem()));
        Bukkit.getPluginManager().callEvent(villagePlayerChooseKitEvent);
    }

    @EventHandler
    public void checkIfIsUnlocked(VillagePlayerChooseKitEvent event) {
        if(event.getKit().isUnlockedByPlayer(event.getPlayer())) {
            User user = UserManager.getUser(event.getPlayer().getUniqueId());
            user.setKit(event.getKit());
            String chosenkitmessage = ChatManager.colorMessage("Kits.Choose-Message");
            chosenkitmessage = ChatManager.formatMessage(chosenkitmessage, event.getKit());
            event.getPlayer().sendMessage(chosenkitmessage);
        } else {
            String chosenKitMessageButNotUnlocked = ChatManager.colorMessage("Kits.Not-Unlocked-Message");
            event.getPlayer().sendMessage(ChatManager.formatMessage(chosenKitMessageButNotUnlocked, event.getKit()));
        }

    }
}
