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

package pl.plajer.villagedefense.kits.premium;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 19/08/2014.
 */
public class HeavyTankKit extends PremiumKit {

  public HeavyTankKit() {
    setName(ChatManager.colorMessage("Kits.Heavy-Tank.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Heavy-Tank.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.heavytank");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.STICK), new Enchantment[] {Enchantment.DURABILITY, Enchantment.DAMAGE_ALL}, new int[] {10, 2}));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
    player.setHealth(40.0);
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.IRON);
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
  }

  @Override
  public Material getMaterial() {
    return Material.DIAMOND_CHESTPLATE;
  }

  @Override
  public void reStock(Player player) {
  }
}
