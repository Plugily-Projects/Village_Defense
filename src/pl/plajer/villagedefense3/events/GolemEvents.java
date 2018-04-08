package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.user.UserManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GolemEvents implements Listener {

    private Map<Player, IronGolem> clickedGolem = new HashMap<>();
    private Main plugin;

    public GolemEvents(Main plugin) {
        this.plugin = plugin;
        if(plugin.getConfig().getBoolean("Golem-Upgrades-Enabled")) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            if(Main.isDebugged()) {
                System.out.println("[Village Debugger] Golem upgrades successfully registered!");
            }
        }
    }

    @EventHandler
    public void onGolemClick(PlayerInteractEntityEvent e) {
        if(!ArenaRegistry.isInGameInstance(e.getPlayer()) || !(e.getRightClicked() instanceof IronGolem)) {
            return;
        }
        if(UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) return;
        if(e.getRightClicked().getCustomName() == null || !(e.getRightClicked().getCustomName().contains(e.getPlayer().getName()))) {
            e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Cant-Upgrade-Others"));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 3 * 9, ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"));
        for(int i = 1; i <= 3; i++) {
            ItemStack golemHealthUpgrade = new ItemStack(Material.IRON_INGOT, i);
            ItemMeta meta = golemHealthUpgrade.getItemMeta();
            meta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i));
            meta.setLore(Arrays.asList(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i + "-Lore").split(";")));
            golemHealthUpgrade.setItemMeta(meta);
            inv.setItem((i * 3) + 7, golemHealthUpgrade);
        }

        ItemStack golemHeal = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta healMeta = golemHeal.getItemMeta();
        healMeta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal"));
        healMeta.setLore(Arrays.asList(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal-Lore").split(";")));
        golemHeal.setItemMeta(healMeta);

        ItemStack golemHealth = new ItemStack(Material.BOOK, 1);
        ItemMeta healthMeta = golemHealth.getItemMeta();
        healthMeta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Health").replaceAll("%health%", String.valueOf(((IronGolem) e.getRightClicked()).getHealth())));
        golemHealth.setItemMeta(healthMeta);
        inv.setItem(4, golemHealth);
        inv.setItem(22, golemHeal);
        e.getPlayer().openInventory(inv);
        clickedGolem.put(e.getPlayer(), (IronGolem) e.getRightClicked());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory() == null || e.getCurrentItem() == null || clickedGolem.get(e.getWhoClicked()) == null)
            return;
        if(e.getInventory().getName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"))) {
            if(!e.getCurrentItem().hasItemMeta()) return;
            double golemHealth = clickedGolem.get(e.getWhoClicked()).getMaxHealth();
            Integer orbs = UserManager.getUser(e.getWhoClicked().getUniqueId()).getInt("orbs");
            e.setCancelled(true);
            //checking for health upgrades
            for(int i = 1; i <= 3; i++) {
                if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i))) {
                    if(golemHealth == 160.0) {
                        e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Already-Purchased"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    Integer price = plugin.getConfig().getInt("Golem-Upgrade-Tier" + i + "-Cost");
                    if(orbs >= price) {
                        if(golemHealth >= 100.0 + (20 * i)) {
                            e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Already-Purchased"));
                            e.getWhoClicked().closeInventory();
                            return;
                        }
                        clickedGolem.get(e.getWhoClicked()).setMaxHealth(100.0 + (20.0 * i));
                        clickedGolem.get(e.getWhoClicked()).setHealth(clickedGolem.get(e.getWhoClicked()).getMaxHealth());
                        e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Applied"));
                        UserManager.getUser(e.getWhoClicked().getUniqueId()).setInt("orbs", orbs - price);
                        clickedGolem.get(e.getWhoClicked()).getWorld().playEffect(e.getWhoClicked().getLocation(), Effect.LAVA_POP, 20);
                        clickedGolem.remove(e.getWhoClicked());
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                }
            }
            //checking for heal upgrade
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal"))) {
                if(clickedGolem.get(e.getWhoClicked()).getHealth() == clickedGolem.get(e.getWhoClicked()).getMaxHealth()) {
                    e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal-Full"));
                    e.getWhoClicked().closeInventory();
                    return;
                }
                Integer price = plugin.getConfig().getInt("Golem-Upgrade-Heal-Cost");
                if(orbs >= price) {
                    clickedGolem.get(e.getWhoClicked()).setHealth(clickedGolem.get(e.getWhoClicked()).getMaxHealth());
                    e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Applied"));
                    UserManager.getUser(e.getWhoClicked().getUniqueId()).setInt("orbs", orbs - price);
                    clickedGolem.get(e.getWhoClicked()).getWorld().playEffect(e.getWhoClicked().getLocation(), Effect.LAVA_POP, 20);
                    clickedGolem.remove(e.getWhoClicked());
                    e.getWhoClicked().closeInventory();
                } else {
                    e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
                    e.getWhoClicked().closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(e.getInventory() == null || clickedGolem.get(e.getPlayer()) == null) return;
        if(e.getInventory().getName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"))) {
            clickedGolem.remove(e.getPlayer());
        }
    }

}
