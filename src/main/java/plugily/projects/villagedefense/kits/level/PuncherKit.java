/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits.level;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.LevelKit;
import plugily.projects.villagedefense.utils.Utils;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class PuncherKit extends LevelKit {

  public PuncherKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_PUNCHER_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_PUNCHER_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    setLevel(getKitsConfig().getInt("Required-Level.Puncher"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagedefense.kit.puncher");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_SHOVEL.parseItem(), new Enchantment[]{
        Enchantment.DURABILITY, Enchantment.KNOCKBACK, Enchantment.DAMAGE_ALL}, new int[]{10, 5, 2}));
    ArmorHelper.setColouredArmor(Color.BLACK, player);
    player.getInventory().addItem(WeaponHelper.getEnchantedBow(Enchantment.DURABILITY, 5));
    player.getInventory().addItem(new ItemStack(Material.ARROW, 25));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
  }

  @Override
  public Material getMaterial() {
    return XMaterial.DIAMOND_SHOVEL.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }
}
