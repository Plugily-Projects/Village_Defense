package pl.plajer.villagedefense3.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ItemUtils;
import pl.plajer.villagedefense3.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

    private Main plugin;

    public NakedKit(Main plugin) {
        this.plugin = plugin;
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Wild-Naked.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        setName(ChatManager.colorMessage("Kits.Wild-Naked.Kit-Name"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getKitRegistry().registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("villagedefense.kit.naked") || player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite());
    }

    @Override
    public void giveKitItems(Player player) {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        player.getInventory().addItem(itemStack);
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public void reStock(Player player) {
        player.getInventory().addItem(ItemUtils.getPotion(PotionType.INSTANT_HEAL, 1, true, 1));
    }

    @EventHandler
    public void onArmor(InventoryClickEvent event) {
        if(UserManager.getUser(event.getWhoClicked().getUniqueId()) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getWhoClicked());
        if(gameInstance == null)
            return;
        if(!(UserManager.getUser(event.getWhoClicked().getUniqueId()).getKit() instanceof NakedKit))
            return;
        if(!(event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            for(ItemStack is : event.getWhoClicked().getInventory().getArmorContents()) {
                if(is != null) {
                    if(getAllArmorTypes().contains(is.getType())) {
                        //we cannot cancel event using scheduler, we must remove all armor contents from inventory manually
                        event.getWhoClicked().sendMessage(ChatManager.colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
                        event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
                        event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
                        event.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
                        event.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
                        return;
                    }
                }
            }
        }, 1);
    }

    @EventHandler
    public void onArmorClick(PlayerInteractEvent event) {
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()) == null)
            return;
        if(!(UserManager.getUser(event.getPlayer().getUniqueId()).getKit() instanceof NakedKit))
            return;
        if(!event.hasItem())
            return;
        if(getAllArmorTypes().contains(event.getItem().getType())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
        }

    }


    private List<Material> getAllArmorTypes() {
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
