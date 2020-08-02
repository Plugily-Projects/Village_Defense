/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.utils.constants;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import plugily.projects.villagedefense.utils.ServerVersion.Version;

/**
 * Bridge for compatibility between 1.12 and 1.13
 * for some materials.
 *
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class CompatMaterialConstants {

  private static Material playerHead;
  private static ItemStack playerHeadItem;
  private static Material oakDoorBlock;
  private static Material oakDoorItem;

  private CompatMaterialConstants() {
  }

  @SuppressWarnings("deprecation")
  public static void init() {
    if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
      playerHead = Material.getMaterial("SKULL_ITEM");
      playerHeadItem = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
      oakDoorBlock = Material.getMaterial("WOODEN_DOOR");
      oakDoorItem = Material.getMaterial("WOOD_DOOR");
    } else {
      playerHead = XMaterial.PLAYER_HEAD.parseMaterial();
      playerHeadItem = XMaterial.PLAYER_HEAD.parseItem();
      oakDoorBlock = XMaterial.OAK_DOOR.parseMaterial();
      oakDoorItem = XMaterial.OAK_DOOR.parseMaterial();
    }
  }

  public static Material getPlayerHead() {
    return playerHead;
  }

  public static ItemStack getPlayerHeadItem() {
    return playerHeadItem.clone();
  }

  public static Material getOakDoorBlock() {
    return oakDoorBlock;
  }

  public static Material getOakDoorItem() {
    return oakDoorItem;
  }
}
