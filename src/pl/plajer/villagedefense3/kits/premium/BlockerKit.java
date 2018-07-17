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

package pl.plajer.villagedefense3.kits.premium;

import org.bukkit.Color;
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
import pl.plajer.villagedefense3.utils.Utils;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 17/12/2015.
 */
public class BlockerKit extends PremiumKit implements Listener {

  private Main plugin;

  public BlockerKit(Main plugin) {
    this.plugin = plugin;
    setName(ChatManager.colorMessage("Kits.Blocker.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Blocker.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.blocker");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.RED, player);
    player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STONE_SWORD), new org.bukkit.enchantments.Enchantment[]{org.bukkit.enchantments.Enchantment.DURABILITY}, new int[]{10}));
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    ItemStack is = new ItemStack(Material.FENCE, 3);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatManager.colorMessage("Kits.Blocker.Game-Item-Name"));
    im.setLore(Arrays.asList(ChatManager.colorMessage("Kits.Blocker.Game-Item-Lore").split("\n")));
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
    im.setLore(Utils.splitString(ChatManager.colorMessage("Kits.Blocker.Game-Item-Lore"), 40));
    is.setItemMeta(im);
    inventory.addItem(is);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBarrierPlace(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
      return;
    Player player = event.getPlayer();
    ItemStack stack = player.getInventory().getItemInMainHand();
    if (!ArenaRegistry.isInArena(player) || stack == null || !stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName() ||
            !stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Blocker.Game-Item-Name")))
      return;
    Block block = null;
    for (Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
      if (blocks.getType() == Material.AIR)
        block = blocks;
    }
    if (block == null) {
      event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Blocker.Game-Item-Place-Fail"));
      return;
    }
    if (stack.getAmount() <= 1) {
      player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    } else {
      player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
    }
    event.setCancelled(false);
    User user = UserManager.getUser(event.getPlayer().getUniqueId());

    user.toPlayer().sendMessage(ChatManager.colorMessage("Kits.Blocker.Game-Item-Place-Message"));
    ZombieBarrier zombieBarrier = new ZombieBarrier();
    zombieBarrier.setLocation(block.getLocation());
    zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
    new BukkitRunnable() {
      @Override
      public void run() {
        zombieBarrier.decrementSeconds();
        if (zombieBarrier.getSeconds() <= 0) {
          zombieBarrier.getLocation().getBlock().setType(Material.AIR);
          zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
          this.cancel();
        }
      }
    }.runTaskTimer(plugin, 20, 20);
    block.setType(Material.FENCE);
  }


  private class ZombieBarrier {
    private Location location;
    private int seconds = 10;

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
