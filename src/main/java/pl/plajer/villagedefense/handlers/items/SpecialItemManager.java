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

package pl.plajer.villagedefense.handlers.items;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.utils.Constants;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItemManager {

  private List<SpecialItem> specialItems = new ArrayList<>();
  private FileConfiguration config;
  private Main plugin;

  public SpecialItemManager(Main plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, Constants.Files.LOBBY_ITEMS.getName());
  }

  public void addItem(SpecialItem item) {
    specialItems.add(item);
  }

  @NotNull
  public SpecialItem getSpecialItem(String name) {
    for (SpecialItem item : specialItems) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return SpecialItem.INVALID_ITEM;
  }

  @NotNull
  public SpecialItem getRelatedSpecialItem(ItemStack itemStack) {
    for (SpecialItem item : specialItems) {
      if (item.getItemStack().isSimilar(itemStack)) {
        return item;
      }
    }
    return SpecialItem.INVALID_ITEM;
  }

  public void registerItems() {
    for (String key : config.getKeys(false)) {
      XMaterial mat = XMaterial.fromString(config.getString(key + ".material-name"));
      String name = plugin.getChatManager().colorRawMessage(config.getString(key + ".displayname"));
      List<String> lore = config.getStringList(key + ".lore").stream().map(itemLore -> itemLore = plugin.getChatManager().colorRawMessage(itemLore))
          .collect(Collectors.toList());
      int slot = config.getInt(key + ".slot");

      validateItem(key, name, lore, mat.parseMaterial(), slot);
      SpecialItem item = new SpecialItem(key, new ItemBuilder(mat.parseItem()).name(name).lore(lore).build(), slot);
      addItem(item);
    }
  }

  private void validateItem(String path, String name, List<String> lore, Material material, int slot) {
    if (!config.contains(path)) {
      config.set(path + ".data", 0);
      config.set(path + ".displayname", name);
      config.set(path + ".lore", lore);
      config.set(path + ".material-name", material.toString());
      config.set(path + ".slot", slot);
    } else {
      if (!config.isSet(path + ".material-name")) {
        config.set(path + ".material-name", material.toString());
        Debugger.debug(Level.WARNING, "Found outdated item {0} in lobbyitems.yml! We've converted it to the newest version!", path);
      }
    }
    ConfigUtils.saveConfig(plugin, config, Constants.Files.LOBBY_ITEMS.getName());
  }

  public List<SpecialItem> getSpecialItems() {
    return specialItems;
  }

}
