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

package pl.plajer.villagedefense3.kits;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 27/08/2014.
 */
public class ShotBowKit extends PremiumKit implements Listener {

    private Main plugin;

    public ShotBowKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Shot-Bow.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Shot-Bow.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.shotbow");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getEnchantedBow(new Enchantment[]{Enchantment.DURABILITY, Enchantment.ARROW_KNOCKBACK}, new int[]{10, 1}));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        ArmorHelper.setColouredArmor(Color.YELLOW, player);
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));

    }

    @Override
    public Material getMaterial() {
        return Material.ARROW;
    }

    @Override
    public void reStock(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    @EventHandler
    public void onBowInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType() != Material.BOW || !e.getPlayer().getInventory().contains(Material.ARROW) ||
                    !(UserManager.getUser(e.getPlayer().getUniqueId()).getKit() instanceof ShotBowKit) || UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator())
                return;
            if(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("shotbow") == 0) {
                for(int i = 0; i < 4; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Projectile pr = e.getPlayer().launchProjectile(Arrow.class);
                        pr.setVelocity(e.getPlayer().getLocation().getDirection().multiply(3));
                        pr.setBounce(false);
                        pr.setShooter(e.getPlayer());
                        ((Arrow) pr).setCritical(true);

                        if(e.getPlayer().getInventory().contains(Material.ARROW))
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                    }, 2 * (2 * i));
                }
                e.setCancelled(true);
                UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("shotbow", 5);
            } else {
                String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
                msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("shotbow")));
                e.getPlayer().sendMessage(msgstring);
            }
        }
    }

}
