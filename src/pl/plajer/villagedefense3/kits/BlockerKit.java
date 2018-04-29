package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tom on 17/12/2015.
 */
public class BlockerKit extends PremiumKit implements Listener {

    private Main plugin;

    public BlockerKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Blocker.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Blocker.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite()) || player.hasPermission("villagedefense.kit.blockerkit") || player.hasPermission("villagedefense.kits.blockerkit");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setColouredArmor(Color.RED, player);
        player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STONE_SWORD), new org.bukkit.enchantments.Enchantment[]{org.bukkit.enchantments.Enchantment.DURABILITY}, new int[]{10}));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        ItemStack is = new ItemStack(Material.FENCE, 3);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatManager.colorMessage("Kits.Blocker.Game-Item-Name"));
        im.setLore(Collections.singletonList(ChatManager.colorMessage("Kits.Blocker.Game-Item-Lore")));
        is.setItemMeta(im);
        player.getInventory().addItem(is);
        player.getInventory().addItem(new ItemStack(Material.SADDLE));

    }

    @Override
    public Material getMaterial() {
        return Material.BARRIER;
    }

    @Override
    public void reStock(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack is = new ItemStack(Material.FENCE, 3);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatManager.colorMessage("Kits.Blocker.Game-Item-Name"));
        im.setLore(Collections.singletonList(ChatManager.colorMessage("Kits.Blocker.Game-Item-Lore")));
        is.setItemMeta(im);
        inventory.addItem(is);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBarrierPlace(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        if(!ArenaRegistry.isInArena(player) || player.getItemInHand() == null || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName() ||
                !player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Blocker.Game-Item-Name")))
            return;
        Block block = null;
        for(Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
            if(blocks.getType() == Material.AIR)
                block = blocks;
        }
        if(block == null) {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Blocker.Game-Item-Place-Fail"));
            return;
        }
        if(player.getItemInHand().getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());

        user.toPlayer().sendMessage(ChatManager.colorMessage("Kits.Blocker.Game-Item-Place-Message"));
        ZombieBarrier zombieBarrier = new ZombieBarrier();
        zombieBarrier.setLocation(block.getLocation());
        if(plugin.is1_8_R3()) {
            zombieBarrier.getLocation().getWorld().spigot().playEffect(zombieBarrier.getLocation(), Effect.FIREWORKS_SPARK, 0, 0, 0, 0, 0, 1, 20, 50);
        } else {
            zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                zombieBarrier.decrementSeconds();
                if(zombieBarrier.getSeconds() <= 0) {
                    zombieBarrier.getLocation().getBlock().setType(Material.AIR);
                    if(plugin.is1_8_R3()) {
                        zombieBarrier.getLocation().getWorld().spigot().playEffect(zombieBarrier.getLocation(), Effect.FIREWORKS_SPARK, 0, 0, 0, 0, 0, 1, 20, 50);
                    } else {
                        zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20, 20);
        block.setType(Material.FENCE);
    }


    private class ZombieBarrier {
        private Location location;
        private int seconds;

        ZombieBarrier() {
            seconds = 10;
        }

        Location getLocation() {
            return location;
        }

        void setLocation(Location location) {
            this.location = location;
        }

        int getSeconds() {
            return seconds;
        }

        void decrementSeconds() {
            this.seconds = seconds - 1;
        }
    }
}
