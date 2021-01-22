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

package plugily.projects.villagedefense.utils;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import plugily.projects.villagedefense.MockUtils;

/**
 * @author Plajer
 * <p>
 * Created at 26.05.2019
 */
public class ArmorHelperTest {

  @Test
  public void checkIfWearProperArmor() {
    PlayerMock player = MockUtils.getDefaultPlayer();
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.IRON);
    Assert.assertEquals(new ItemStack(Material.IRON_HELMET), player.getInventory().getHelmet());
    Assert.assertEquals(new ItemStack(Material.IRON_CHESTPLATE), player.getInventory().getChestplate());
    Assert.assertEquals(new ItemStack(Material.IRON_LEGGINGS), player.getInventory().getLeggings());
    Assert.assertEquals(new ItemStack(Material.IRON_BOOTS), player.getInventory().getBoots());

    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.DIAMOND);
    Assert.assertEquals(new ItemStack(Material.DIAMOND_HELMET), player.getInventory().getHelmet());
    Assert.assertEquals(new ItemStack(Material.DIAMOND_CHESTPLATE), player.getInventory().getChestplate());
    Assert.assertEquals(new ItemStack(Material.DIAMOND_LEGGINGS), player.getInventory().getLeggings());
    Assert.assertEquals(new ItemStack(Material.DIAMOND_BOOTS), player.getInventory().getBoots());

    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
    Assert.assertEquals(new ItemStack(XMaterial.GOLDEN_HELMET.parseMaterial()), player.getInventory().getHelmet());
    Assert.assertEquals(new ItemStack(XMaterial.GOLDEN_CHESTPLATE.parseMaterial()), player.getInventory().getChestplate());
    Assert.assertEquals(new ItemStack(XMaterial.GOLDEN_LEGGINGS.parseMaterial()), player.getInventory().getLeggings());
    Assert.assertEquals(new ItemStack(XMaterial.GOLDEN_BOOTS.parseMaterial()), player.getInventory().getBoots());
    System.out.println("> ArmorHelper#setArmor equal armor sets | PASSED");
  }

}