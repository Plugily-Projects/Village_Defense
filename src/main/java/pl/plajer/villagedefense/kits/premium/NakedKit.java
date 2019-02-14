/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.kits.premium;

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

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

  private List<Material> armorTypes = new ArrayList<>();

  public NakedKit() {
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    setName(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Kit-Name"));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
    armorTypes.add(Material.LEATHER_BOOTS);
    armorTypes.add(Material.LEATHER_CHESTPLATE);
    armorTypes.add(Material.LEATHER_LEGGINGS);
    armorTypes.add(Material.LEATHER_HELMET);
    armorTypes.add(XMaterial.GOLDEN_BOOTS.parseMaterial());
    armorTypes.add(XMaterial.GOLDEN_CHESTPLATE.parseMaterial());
    armorTypes.add(XMaterial.GOLDEN_LEGGINGS.parseMaterial());
    armorTypes.add(XMaterial.GOLDEN_HELMET.parseMaterial());
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
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 1, true));
  }

  @EventHandler
  public void onArmor(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    User user = getPlugin().getUserManager().getUser((Player) event.getWhoClicked());
    if (user == null || !ArenaRegistry.isInArena((Player) event.getWhoClicked())) {
      return;
    }
    if (!(user.getKit() instanceof NakedKit)) {
      return;
    }
    if (!(event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
      for (ItemStack stack : event.getWhoClicked().getInventory().getArmorContents()) {
        if (stack == null || !armorTypes.contains(stack.getType())) {
          continue;
        }
        //we cannot cancel event using scheduler, we must remove all armor contents from inventory manually
        event.getWhoClicked().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
        event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
        return;
      }
    }, 1);
  }

  @EventHandler
  public void onArmorClick(PlayerInteractEvent event) {
    if (!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if (user == null || !(user.getKit() instanceof NakedKit) || !event.hasItem()) {
      return;
    }
    if (armorTypes.contains(event.getItem().getType())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
    }
  }
}
