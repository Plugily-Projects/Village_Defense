/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Plajer
 * <p>
 * Created at 01.08.2023
 */
public class KitHelper {

  private static JavaPlugin plugin;

  private KitHelper() {
  }

  public static void init(JavaPlugin plugin) {
    KitHelper.plugin = plugin;
  }

  public static void scheduleAbilityCooldown(ItemStack item, Player player, int castTime, int cooldown) {
    ItemMeta meta = item.getItemMeta();
    meta.addEnchant(Enchantment.DURABILITY, 1, true);
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTaskLater(KitHelper.plugin, () -> {
      ItemMeta newMeta = item.getItemMeta();
      newMeta.removeEnchant(Enchantment.DURABILITY);
      item.setItemMeta(newMeta);

      VersionUtils.setMaterialCooldown(player, item.getType(), (cooldown - castTime) * 20);
    }, castTime * 20L);
  }

}
