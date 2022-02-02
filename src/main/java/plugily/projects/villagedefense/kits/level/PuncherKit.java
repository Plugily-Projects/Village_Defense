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

package plugily.projects.villagedefense.kits.level;

import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

/**
 * Created by Tom on 18/08/2014.
 */
public class PuncherKit extends LevelKit {

  public PuncherKit() {
    setName(getPlugin().getChatManager().colorMessage("KIT_CONTENT_PUNCHER_NAME"));
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_PUNCHER_DESCRIPTION");
    setDescription(description);
    setLevel(getKitsConfig().getInt("Required-Level.Puncher"));
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat("LEVEL") >= getLevel() || player.hasPermission("villagedefense.kit.puncher");
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
