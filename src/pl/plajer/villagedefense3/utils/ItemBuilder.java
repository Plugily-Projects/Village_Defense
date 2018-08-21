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

package pl.plajer.villagedefense3.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Tom on 9/04/2015.
 */
public class ItemBuilder implements Listener {

  private static final HashMap<String, PotionEffect> effects = new HashMap<>();

  private final ItemStack is;

  public ItemBuilder(final ItemStack is) {
    this.is = is;
  }

  public ItemBuilder name(final String name) {
    final ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(name);
    is.setItemMeta(meta);
    return this;
  }

  public ItemBuilder lore(final String name) {
    final ItemMeta meta = is.getItemMeta();
    List<String> lore = meta.getLore();
    if (lore == null) {
      lore = new ArrayList<>();
    }
    lore.add(name);
    meta.setLore(lore);
    is.setItemMeta(meta);
    return this;
  }

  public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
    is.addUnsafeEnchantment(enchantment, level);
    return this;
  }

  public ItemBuilder enchantment(final Enchantment enchantment) {
    is.addUnsafeEnchantment(enchantment, 1);
    return this;
  }

  public ItemBuilder color(Color color) {
    if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET
            || is.getType() == Material.LEATHER_LEGGINGS) {
      LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
      meta.setColor(color);
      is.setItemMeta(meta);
      return this;
    } else {
      throw new IllegalArgumentException("color() only applicable for leather armor!");
    }
  }

  public ItemStack build() {
    return is;
  }

  @EventHandler
  public void onItemConsume(PlayerItemConsumeEvent e) {
    if (e.getItem().hasItemMeta()) {
      @SuppressWarnings("unchecked") HashMap<String, PotionEffect> copy = (HashMap<String, PotionEffect>) effects.clone();
      String name = e.getItem().getItemMeta().getDisplayName();
      while (copy.containsKey(name)) {
        e.getPlayer().addPotionEffect(copy.get(name), true);
        copy.remove(name);
        name += "#";
      }
    }
  }

}