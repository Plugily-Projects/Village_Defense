/*
 * Village Defense - Protect villagers from hordes of zombies
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

package pl.plajer.villagedefense.kits.level;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;

/**
 * Created by Tom on 14/08/2014.
 */
public class ArcherKit extends LevelKit {

  public ArcherKit() {
    this.setLevel(getKitsConfig().getInt("Required-Level.Archer"));
    this.setName(ChatManager.colorMessage("Kits.Archer.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Archer.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagefense.kit.archer");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.GREEN, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(WeaponHelper.getEnchantedBow(Enchantment.DURABILITY, 10));
    player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
  }

  @Override
  public Material getMaterial() {
    return Material.BOW;
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemStack(Material.ARROW, 15));
  }
}
