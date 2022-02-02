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

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class HealerKit extends LevelKit {

  public HealerKit() {
    setName(getPlugin().getChatManager().colorMessage("KIT_CONTENT_HEALER_NAME"));
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_HEALER_DESCRIPTION");
    setDescription(description);
    setLevel(getKitsConfig().getInt("Required-Level.Healer"));
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat("LEVEL") >= getLevel() || player.hasPermission("villagedefense.kit.healer");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(VersionUtils.getPotion(PotionType.INSTANT_HEAL, 2, true));
    player.getInventory().addItem(VersionUtils.getPotion(PotionType.REGEN, 1, true));
  }

  @Override
  public Material getMaterial() {
    return XMaterial.POPPY.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    for(int i = 0; i < 2; i++) {
      player.getInventory().addItem(VersionUtils.getPotion(PotionType.INSTANT_HEAL, 2, true));
    }
    for(int i = 0; i < 2; i++) {
      player.getInventory().addItem(VersionUtils.getPotion(PotionType.REGEN, 1, true));
    }
  }
}
