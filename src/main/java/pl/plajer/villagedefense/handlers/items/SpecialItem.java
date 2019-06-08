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

package pl.plajer.villagedefense.handlers.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItem {

  public static final SpecialItem INVALID_ITEM = new SpecialItem("INVALID", new ItemStack(Material.BEDROCK), -1);
  private final String name;
  private ItemStack itemStack;
  private int slot;

  public SpecialItem(String name, ItemStack itemStack, int slot) {
    this.name = name;
    this.itemStack = itemStack;
    this.slot = slot;
  }

  public String getName() {
    return name;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }


}
