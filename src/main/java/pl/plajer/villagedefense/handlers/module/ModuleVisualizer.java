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

package pl.plajer.villagedefense.handlers.module;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class ModuleVisualizer {

  private static Main plugin;
  private Gui gui;

  public ModuleVisualizer() {
    int rows = Utils.serializeInt(plugin.getModuleLoader().getModulesInfo().size()) / 9;
    if (rows == 0) {
      rows = 1;
    }
    int totalModules = plugin.getModuleLoader().getModulesInfo().size();
    int loadedModules = totalModules - plugin.getModuleLoader().getNotLoadedModulesAmount();
    this.gui = new Gui(plugin, rows, "List of Modules (" + loadedModules + "/" + totalModules + " active)");
    loadModulesInformation();
  }

  public static void init(Main plugin) {
    ModuleVisualizer.plugin = plugin;
  }

  private void loadModulesInformation() {
    OutlinePane pane = new OutlinePane(9, gui.getRows());
    this.gui.addPane(pane);
    for (ModuleWrapper moduleInfo : plugin.getModuleLoader().getModulesInfo()) {
      pane.addItem(new GuiItem(new ItemBuilder(getItemStack(moduleInfo))
          .name(getItemName(moduleInfo))
          .lore(getItemLore(moduleInfo)).build(), e -> e.setCancelled(true)));
    }
  }

  private ItemStack getItemStack(ModuleWrapper moduleInfo) {
    switch (moduleInfo.getLoadStatus()) {
      case LOADED:
        if (moduleInfo.hasLoggedExceptions()) {
          return XMaterial.YELLOW_WOOL.parseItem();
        }
        return XMaterial.LIME_WOOL.parseItem();
      case INCOMPATIBLE:
        return XMaterial.YELLOW_WOOL.parseItem();
      case FAILED_TO_LOAD:
        return XMaterial.RED_WOOL.parseItem();
      case AMBIGUOUS:
        return XMaterial.GRAY_WOOL.parseItem();
      default:
        return new ItemStack(Material.DIRT);
    }
  }

  private String getItemName(ModuleWrapper moduleInfo) {
    switch (moduleInfo.getLoadStatus()) {
      case LOADED:
        if (moduleInfo.hasLoggedExceptions()) {
          return color("&e&l" + moduleInfo.getModule().getModuleName() + " - EXCEPTIONS OCCURRED");
        }
        return color("&a&l" + moduleInfo.getModule().getModuleName() + " - LOADED");
      case INCOMPATIBLE:
        return color("&6&l" + moduleInfo.getModule().getModuleName() + " - INCOMPATIBLE");
      case FAILED_TO_LOAD:
        return color("&4&l" + moduleInfo.getModule().getModuleName() + " - FAILED TO LOAD");
      case AMBIGUOUS:
        return color("&8&l" + moduleInfo.getModule().getModuleName() + " - AMBIGUOUS");
      default:
        return moduleInfo.getModule().getModuleName();
    }
  }

  private List<String> getItemLore(ModuleWrapper moduleInfo) {
    List<String> lore = new ArrayList<>(Utils.splitString(moduleInfo.getModule().getDescription(), 40));
    lore.add("");
    lore.add(color("&6Version: &7" + moduleInfo.getModule().getVersion()));
    lore.add(color("&6Author: &7" + moduleInfo.getModule().getAuthor()));
    lore.add(color("&6Compatible With: &7" + Arrays.toString(moduleInfo.getModule().getCompatibleVersions().toArray())
        + " &8(Yours " + ModuleLoader.CURRENT_COMPATIBILITY_VERSION + ")"));
    switch (moduleInfo.getLoadStatus()) {
      case LOADED:
        lore.add(color("&6Load Time: &7" + moduleInfo.getInfo(ModuleWrapper.LogInfoKey.LOAD_TIME.getKey())));
        lore.add("");
        lore.add(color("&aModule successfully loaded"));
        if (moduleInfo.hasLoggedExceptions()) {
          lore.add(color("&eModule has encountered exceptions in runtime!"));
        }
        break;
      case INCOMPATIBLE:
        lore.add(color("&6Incompatible With: &c" + moduleInfo.getInfo(ModuleWrapper.LogInfoKey.INCOMPATIBILITY.getKey())));
        lore.add("");
        lore.add(color("&cModule not loaded - Incompatible with other modules"));
        break;
      case FAILED_TO_LOAD:
        lore.add(color("&6Load Error: &c" + moduleInfo.getInfo(ModuleWrapper.LogInfoKey.LOAD_EXCEPTION.getKey())));
        lore.add("");
        lore.add(color("&cModule not loaded - Encountered exception while loading"));
        break;
      case AMBIGUOUS:
        lore.add(color("&6Overrides Module: &c" + moduleInfo.getInfo(ModuleWrapper.LogInfoKey.AMBIGUOUS_NAME.getKey())));
        lore.add("");
        lore.add(color("&cModule not loaded - Overrides module"));
        lore.add(color("&c" + moduleInfo.getInfo(ModuleWrapper.LogInfoKey.AMBIGUOUS_NAME.getKey()) + " with the same name"));
        break;
      default:
        break;
    }
    return lore;
  }

  private String color(String message) {
    return plugin.getChatManager().colorRawMessage(message);
  }

  public void openInventory(Player player) {
    gui.show(player);
  }

}
