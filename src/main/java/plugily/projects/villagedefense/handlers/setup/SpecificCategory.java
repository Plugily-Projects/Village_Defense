/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.handlers.setup;

import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSpecificCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MaterialLocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MaterialMultiLocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MultiLocationItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.07.2022
 */
public class SpecificCategory extends PluginSpecificCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);

    MultiLocationItem zombieSpawn = new MultiLocationItem(getSetupInventory(), new ItemBuilder(XMaterial.ROTTEN_FLESH.parseMaterial()), "Zombie Spawn", "Click add new zombie spawn\non the place you're standing at.", "zombiespawns", 2);
    gui.setItem((getInventoryLine() * 9) + 1, zombieSpawn);
    getItemList().add(zombieSpawn);

    MultiLocationItem villagerSpawns = new MultiLocationItem(getSetupInventory(), new ItemBuilder(XMaterial.ROTTEN_FLESH.parseMaterial()), "Villager Spawn", "Click add new villager spawn\non the place you're standing at.", "villagerspawns", 2);
    gui.setItem((getInventoryLine() * 9) + 2, villagerSpawns);
    getItemList().add(villagerSpawns);

    MaterialMultiLocationItem door = new MaterialMultiLocationItem(getSetupInventory(), new ItemBuilder(XMaterial.OAK_DOOR.parseMaterial()), "Game Door", "Doors will be regenerated\n each game, villagers will\n hide in houses so you can put\n doors to keep zombies away!", "doors", MaterialUtils.DOORS, false, 0);
    gui.setItem((getInventoryLine() * 9) + 3, door);
    getItemList().add(door);

    MaterialLocationItem chest = new MaterialLocationItem(getSetupInventory(), new ItemBuilder(XMaterial.CHEST.parseMaterial()), "Game Shop", "Look at (double-) chest with items\nand click it to set it as game shop.\n(it allows to click villagers to buy game items)\nRemember to set item prices for the game\nusing /vda setprice command!", "shop", XMaterial.CHEST.parseMaterial());
    gui.setItem((getInventoryLine() * 9) + 4, chest);
    getItemList().add(chest);
  }

}