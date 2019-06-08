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

package pl.plajer.villagedefense.kits.level;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.LevelKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;

/**
 * Created by Tom on 28/07/2015.
 */
public class HardcoreKit extends LevelKit {

  public HardcoreKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_HARDCORE_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_HARDCORE_DESCRIPTION), 40);
    this.setDescription(description.toArray(new String[0]));
    setLevel(getKitsConfig().getInt("Required-Level.Hardcore"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagedefense.kit.hardcore");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 2, true));
    player.getInventory().addItem(new ItemStack(Material.COOKIE, 10));
    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10.0);

  }

  @Override
  public Material getMaterial() {
    return CompatMaterialConstants.getPlayerHead();
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 2, true));
  }
}
