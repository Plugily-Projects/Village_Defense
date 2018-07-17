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

import java.util.ArrayList;
import java.util.List;

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
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Utils;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

  private List<Material> armorTypes = new ArrayList<>();

  public NakedKit(Main plugin) {
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Wild-Naked.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    setName(ChatManager.colorMessage("Kits.Wild-Naked.Kit-Name"));
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    KitRegistry.registerKit(this);
    armorTypes.add(Material.LEATHER_BOOTS);
    armorTypes.add(Material.LEATHER_CHESTPLATE);
    armorTypes.add(Material.LEATHER_LEGGINGS);
    armorTypes.add(Material.LEATHER_HELMET);
    armorTypes.add(Material.GOLD_BOOTS);
    armorTypes.add(Material.GOLD_CHESTPLATE);
    armorTypes.add(Material.GOLD_LEGGINGS);
    armorTypes.add(Material.GOLD_HELMET);
    armorTypes.add(Material.DIAMOND_BOOTS);
    armorTypes.add(Material.DIAMOND_LEGGINGS);
    armorTypes.add(Material.DIAMOND_CHESTPLATE);
    armorTypes.add(Material.DIAMOND_HELMET);
    armorTypes.add(Material.IRON_CHESTPLATE);
    armorTypes.add(Material.IRON_BOOTS);
    armorTypes.add(Material.IRON_HELMET);
    armorTypes.add(Material.IRON_LEGGINGS);
    armorTypes.add(Material.CHAINMAIL_BOOTS);
    armorTypes.add(Material.CHAINMAIL_LEGGINGS);
    armorTypes.add(Material.CHAINMAIL_CHESTPLATE);
    armorTypes.add(Material.CHAINMAIL_HELMET);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return player.hasPermission("villagedefense.kit.naked") || PermissionsManager.isPremium(player);
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
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 1, true, 1));
  }

  @EventHandler
  public void onArmor(InventoryClickEvent event) {
    if (UserManager.getUser(event.getWhoClicked().getUniqueId()) == null) {
      return;
    }
    if (!ArenaRegistry.isInArena((Player) event.getWhoClicked())) {
      return;
    }
    if (!(UserManager.getUser(event.getWhoClicked().getUniqueId()).getKit() instanceof NakedKit)) {
      return;
    }
    if (!(event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
      for (ItemStack is : event.getWhoClicked().getInventory().getArmorContents()) {
        if (is != null) {
          if (armorTypes.contains(is.getType())) {
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
    if (!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    if (UserManager.getUser(event.getPlayer().getUniqueId()) == null) {
      return;
    }
    if (!(UserManager.getUser(event.getPlayer().getUniqueId()).getKit() instanceof NakedKit)) {
      return;
    }
    if (!event.hasItem()) {
      return;
    }
    if (armorTypes.contains(event.getItem().getType())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
    }
  }
}
