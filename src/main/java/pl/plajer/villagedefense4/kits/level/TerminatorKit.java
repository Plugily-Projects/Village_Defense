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

package pl.plajer.villagedefense4.kits.level;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.api.StatsStorage;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense4.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense4.user.UserManager;
import pl.plajer.villagedefense4.utils.ArmorHelper;
import pl.plajer.villagedefense4.utils.Utils;
import pl.plajer.villagedefense4.utils.WeaponHelper;
import pl.plajer.villagedefense4.utils.XMaterial;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 18/07/2015.
 */
public class TerminatorKit extends LevelKit {

  public TerminatorKit(Main plugin) {
    setName(ChatManager.colorMessage("Kits.Terminator.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Terminator.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    setLevel(ConfigUtils.getConfig(plugin, "kits").getInt("Required-Level.Terminator"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return UserManager.getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagefense.kit.terminator");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.BONE), new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK}, new int[]{3, 7}));
    ArmorHelper.setColouredArmor(Color.BLACK, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(Utils.getPotion(PotionType.STRENGTH, 2, true));
    player.getInventory().addItem(Utils.getPotion(PotionType.REGEN, 1, true));

  }

  @Override
  public Material getMaterial() {
    return Material.ANVIL;
  }

  @Override
  public void reStock(Player player) {
    for (int i = 0; i < 2; i++) {
      player.getInventory().addItem(Utils.getPotion(PotionType.STRENGTH, 2, true));
    }

  }
}
