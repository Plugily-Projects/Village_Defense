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

package pl.plajer.villagedefense3.kits.kitapi.basekits;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class Kit {

  private String name;
  private boolean unlockedOnDefault = false;
  private String[] description = {""};

  protected Kit() {

  }

  public Kit(String name) {
    this.name = name;
  }

  public abstract boolean isUnlockedByPlayer(Player p);

  public boolean isUnlockedOnDefault() {
    return unlockedOnDefault;
  }

  public void setUnlockedOnDefault(boolean unlockedOnDefault) {
    this.unlockedOnDefault = unlockedOnDefault;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getDescription() {
    return description;
  }

  public void setDescription(String[] description) {
    this.description = description;
  }

  public abstract void giveKitItems(Player player);

  public abstract Material getMaterial();

  protected ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
    ItemMeta im = item.getItemMeta();
    im.setDisplayName(name);
    im.setLore(Arrays.asList(lore));
    item.setItemMeta(im);
    return item;
  }

  public ItemStack getItemStack() {
    ItemStack itemStack = new ItemStack(getMaterial());
    setItemNameAndLore(itemStack, getName(), getDescription());
    return itemStack;
  }

  public abstract void reStock(Player player);


}
