/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.kits.kitapi.basekits;

import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class PremiumKit extends Kit {

  protected PremiumKit() {
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack itemStack = new ItemStack(getMaterial());
    setItemNameAndLore(itemStack, getName(), getDescription());
    MinigameUtils.addLore(itemStack, ChatManager.colorMessage("Kits.Kit-Menu.Locked-Lores.Unlock-In-Store"));
    return itemStack;
  }
}
