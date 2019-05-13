/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

/**
 * Created by Tom on 7/08/2014.
 */
public class ArmorHelper {

  private ArmorHelper() {
  }

  public static void setArmor(Player player, ArmorType type) {
    switch (type) {
      case LEATHER:
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        break;
      case IRON:
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        break;
      case GOLD:
        player.getInventory().setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
        player.getInventory().setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
        player.getInventory().setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
        player.getInventory().setHelmet(XMaterial.GOLDEN_HELMET.parseItem());
        break;
      case DIAMOND:
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        break;
      default:
        break; //o.o
    }
  }

  public static void setColouredArmor(Color color, Player player) {
    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
    LeatherArmorMeta helmMeta = (LeatherArmorMeta) helmet.getItemMeta();
    helmMeta.setColor(color);
    LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
    armorMeta.setColor(color);
    LeatherArmorMeta legsMeta = (LeatherArmorMeta) leggings.getItemMeta();
    legsMeta.setColor(color);
    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
    bootsMeta.setColor(color);
    boots.setItemMeta(bootsMeta);
    helmet.setItemMeta(helmMeta);
    chestplate.setItemMeta(armorMeta);
    leggings.setItemMeta(legsMeta);
    boots.setItemMeta(bootsMeta);
    player.getInventory().setHelmet(helmet);
    player.getInventory().setChestplate(chestplate);
    player.getInventory().setLeggings(leggings);
    player.getInventory().setBoots(boots);
  }

  public enum ArmorType {
    LEATHER, IRON, DIAMOND, GOLD
  }

}


