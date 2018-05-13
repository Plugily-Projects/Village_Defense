/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.Kit;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
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

    private String unlockedString;
    private String lockedString;

    public KitManager(Main plugin) {
        itemName = ChatManager.colorMessage("Kits.Kit-Menu-Item-Name");
        this.plugin = plugin;
        unlockedString = ChatManager.colorMessage("Kits.Kit-Menu.Unlocked-Kit-Lore");
        lockedString = ChatManager.colorMessage("Kits.Kit-Menu.Locked-Lores.Locked-Lore");
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
        invMenu = Bukkit.createInventory(null, Util.serializeInt(KitRegistry.getKits().size()), getMenuName());
        for(Kit kit : KitRegistry.getKits()) {
            ItemStack itemStack = kit.getItemStack();
            if(kit.isUnlockedByPlayer(player))
                Util.addLore(itemStack, unlockedString);
            else
                Util.addLore(itemStack, lockedString);

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
    private void onKitMenuItemClick(PlayerInteractEvent e) {
        if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        if(e.getPlayer().getItemInHand().getType() != getMaterial())
            return;
        if(!e.getPlayer().getItemInHand().hasItemMeta())
            return;
        if(!e.getPlayer().getItemInHand().getItemMeta().hasLore())
            return;
        if(!e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getItemName())) return;
        openKitMenu(e.getPlayer());
    }

    @EventHandler
    public void onKitChoose(InventoryClickEvent e) {
        if(!e.getInventory().getName().equalsIgnoreCase(getMenuName()))
            return;
        if(!(e.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(e.getCurrentItem() == null)
            return;
        if(!(e.isLeftClick() || e.isRightClick()))
            return;
        if(!e.getCurrentItem().hasItemMeta())
            return;
        if(!ArenaRegistry.isInArena(player))
            return;
        VillagePlayerChooseKitEvent villagePlayerChooseKitEvent = new VillagePlayerChooseKitEvent(player, KitRegistry.getKit(e.getCurrentItem()), ArenaRegistry.getArena(player));
        Bukkit.getPluginManager().callEvent(villagePlayerChooseKitEvent);
    }

    @EventHandler
    public void checkIfIsUnlocked(VillagePlayerChooseKitEvent e) {
        if(e.getKit().isUnlockedByPlayer(e.getPlayer())) {
            User user = UserManager.getUser(e.getPlayer().getUniqueId());
            user.setKit(e.getKit());
            e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Choose-Message").replaceAll("%KIT%", e.getKit().getName()));
        } else {
            e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Not-Unlocked-Message").replaceAll("%KIT%", e.getKit().getName()));
        }

    }
}
