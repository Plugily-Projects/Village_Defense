package me.tomthedeveloper.kits;

import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.PremiumKit;
import me.tomthedeveloper.permissions.PermissionsManager;
import me.tomthedeveloper.utils.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

    public NakedKit() {
        setDescription(new String[]{
                "You are the ultimate master!",
                "You start off with a" + ChatManager.HIGHLIGHTED + " diamond",
                ChatManager.HIGHLIGHTED + "Sharpness VI sword!",
                ChatColor.DARK_PURPLE + "However you can't wear any",
                "armor during the game!",
                "Think good about your tactic!"
        });
        setName(ChatManager.HIGHLIGHTED + "Wild Naked");
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("villagedefense.kit.naked") || player.hasPermission(PermissionsManager.getVIP()) || player.hasPermission(PermissionsManager.getMVP()) || player.hasPermission(PermissionsManager.getELITE());
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        player.getInventory().addItem(itemStack);
    }

    @Override
    public Material getMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public void reStock(Player player) {
        player.getInventory().addItem(Items.getPotion(PotionType.INSTANT_HEAL, 1, true, 1));
    }

    @EventHandler
    public void onArmor(InventoryClickEvent event) {
        if (!(UserManager.getUser(event.getWhoClicked().getUniqueId()).getKit() instanceof NakedKit))
            return;
        if (event.getInventory().getType() != InventoryType.PLAYER)
            return;
        PlayerInventory inventory = (PlayerInventory) event.getInventory();
        boolean b = false;
        for (ItemStack itemStack : inventory.getArmorContents()) {
            if (itemStack != null) {
                itemStack.setType(Material.AIR);
                b = true;
            }
        }
        if (b = true) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "You can't equip armor with the " + ChatManager.HIGHLIGHTED + "Wild Naked kit" + ChatColor.RED + "!");
        }
        if (!getAllArmorTypes().contains(event.getCurrentItem()))
            return;
        if ((event.getClick() == ClickType.SHIFT_RIGHT)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RED + "You can't wear armor with the Wild Naked kit!");
            return;
        }
        if (Arrays.asList(100, 101, 102, 103).contains(event.getSlot()) || Arrays.asList(100, 101, 102, 103).contains(event.getRawSlot())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RED + "You can't wear armor with the Wild Naked kit!");

        }

    }

    @EventHandler
    public void onArmorClick(PlayerInteractEvent event) {
        if (!(UserManager.getUser(event.getPlayer().getUniqueId()).getKit() instanceof NakedKit))
            return;
        if (!event.hasItem())
            return;
        if (getAllArmorTypes().contains(event.getItem().getType())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't wear armor with the Wild Naked kit!");
        }

    }


    public List<Material> getAllArmorTypes() {
        List<Material> list = new ArrayList<>();
        list.add(Material.LEATHER_BOOTS);
        list.add(Material.LEATHER_CHESTPLATE);
        list.add(Material.LEATHER_LEGGINGS);
        list.add(Material.LEATHER_HELMET);
        list.add(Material.GOLD_BOOTS);
        list.add(Material.GOLD_CHESTPLATE);
        list.add(Material.GOLD_LEGGINGS);
        list.add(Material.GOLD_HELMET);
        list.add(Material.DIAMOND_BOOTS);
        list.add(Material.DIAMOND_LEGGINGS);
        list.add(Material.DIAMOND_CHESTPLATE);
        list.add(Material.DIAMOND_HELMET);
        list.add(Material.IRON_CHESTPLATE);
        list.add(Material.IRON_BOOTS);
        list.add(Material.IRON_HELMET);
        list.add(Material.IRON_LEGGINGS);
        list.add(Material.CHAINMAIL_BOOTS);
        list.add(Material.CHAINMAIL_LEGGINGS);
        list.add(Material.CHAINMAIL_CHESTPLATE);
        list.add(Material.CHAINMAIL_HELMET);
        return list;
    }
}
