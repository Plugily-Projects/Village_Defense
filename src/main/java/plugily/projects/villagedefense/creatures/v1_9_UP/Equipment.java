
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.inventory.ItemStack;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.01.2022
 */
public class Equipment {

  private final ItemStack itemStack;
  private final int dropChance;
  private final EquipmentType equipmentType;

  public Equipment(ItemStack itemStack, int dropChance, EquipmentType equipmentType) {
    this.itemStack = itemStack;
    this.dropChance = dropChance;
    this.equipmentType = equipmentType;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getDropChance() {
    return dropChance;
  }

  public EquipmentType getEquipmentType() {
    return equipmentType;
  }

  public enum EquipmentType {
    HELMET, CHESTPLATE, LEGGINGS, BOOTS, HAND
  }

}
