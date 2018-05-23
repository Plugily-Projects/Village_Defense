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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 30/12/2015.
 */
public class TornadoKit extends PremiumKit implements Listener {

    private int max_height = 5;
    private double max_radius = 4;
    private double radius_increment = max_radius / max_height;
    private Main plugin;


    public TornadoKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Tornado.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("villagedefense.kit.tornado") || PermissionsManager.isPremium(player);
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        ItemStack tornado = new ItemStack(Material.WEB, 5);
        List<String> tornadoLore = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Game-Item-Lore"), 40);
        this.setItemNameAndLore(tornado, ChatManager.colorMessage("Kits.Tornado.Game-Item-Name"), tornadoLore.toArray(new String[tornadoLore.size()]));
        player.getInventory().addItem(tornado);
    }

    @Override
    public Material getMaterial() {
        return Material.WEB;
    }

    @Override
    public void reStock(Player player) {
        ItemStack tornado = new ItemStack(Material.WEB, 5);
        List<String> tornadoLore = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Game-Item-Lore"), 40);
        this.setItemNameAndLore(tornado, ChatManager.colorMessage("Kits.Tornado.Game-Item-Name"), tornadoLore.toArray(new String[tornadoLore.size()]));
        player.getInventory().addItem(tornado);
    }

    @EventHandler
    public void onTornadoSpawn(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        if(player.getItemInHand() == null || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName() ||
                !player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Tornado.Game-Item-Name")) || !ArenaRegistry.isInArena(player))
            return;
        if(player.getItemInHand().getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
        event.setCancelled(true);
        Tornado tornado = new Tornado(player.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                tornado.update();
                if(tornado.getTimes() > 75) this.cancel();
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private class Tornado {
        private Location location;
        private Vector vector;
        private int angle;
        private int times;

        Tornado(Location location) {
            this.location = location;
            this.vector = location.getDirection();
            times = 0;
        }

        int getTimes() {
            return times;
        }

        Vector getVector() {
            return vector;
        }

        Location getLocation() {
            return location;
        }

        void setLocation(Location location) {
            this.location = location;
        }

        void update() {
            times++;
            int lines = 3;
            for(int l = 0; l < lines; l++) {
                double height_increase = 0.5;
                for(double y = 0; y < max_height; y += height_increase) {
                    double radius = y * radius_increment;
                    double x = Math.cos(Math.toRadians(360 / lines * l + y * 25 - angle)) * radius;
                    double z = Math.sin(Math.toRadians(360 / lines * l + y * 25 - angle)) * radius;
                    getLocation().getWorld().spigot().playEffect(getLocation().clone().add(x, y, z), Effect.CLOUD, 0, 0, 0, 0, 0, 0, 1, 255);

                }
            }

            for(Entity entity : getLocation().getWorld().getNearbyEntities(getLocation(), 2, 2, 2)) {
                if(entity.getType() == EntityType.ZOMBIE) {
                    entity.setVelocity(getVector().multiply(2).setY(0).add(new Vector(0, 1, 0)));
                }
            }
            setLocation(getLocation().add(getVector().getX() / (3 + Math.random() / 2), 0, getVector().getZ() / (3 + Math.random() / 2)));

            angle += 50;

        }
    }
}
