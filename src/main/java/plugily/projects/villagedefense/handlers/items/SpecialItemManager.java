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

package plugily.projects.villagedefense.handlers.items;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItemManager {

  private final List<SpecialItem> specialItems = new ArrayList<>();
  private final FileConfiguration config;
  private final Main plugin;

  public SpecialItemManager(Main plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, Constants.Files.SPECIAL_ITEMS.getName());
  }

  public void addItem(SpecialItem item) {
    specialItems.add(item);
  }

  @NotNull
  public SpecialItem getSpecialItem(String name) {
    for(SpecialItem item : specialItems) {
      if(item.getName().equals(name)) {
        return item;
      }
    }
    return SpecialItem.INVALID_ITEM;
  }

  @NotNull
  public SpecialItem getRelatedSpecialItem(ItemStack itemStack) {
    for(SpecialItem item : specialItems) {
      if(item.getItemStack().isSimilar(itemStack)) {
        return item;
      }
    }
    return SpecialItem.INVALID_ITEM;
  }

  public void registerItems() {
    for(String key : config.getKeys(false)) {
      if("Version".equals(key)) {
        continue;
      }
      XMaterial mat;
      String name;
      List<String> lore;
      int slot;
      try {
        mat = XMaterial.matchXMaterial(config.getString(key + ".material-name", "bedrock").toUpperCase()).orElse(XMaterial.BEDROCK);
        name = plugin.getChatManager().colorRawMessage(config.getString(key + ".displayname"));
        lore = config.getStringList(key + ".lore").stream()
            .map(itemLore -> itemLore = plugin.getChatManager().colorRawMessage(itemLore))
            .collect(Collectors.toList());
        slot = config.getInt(key + ".slot");
      } catch(Exception ex) {
        plugin.getLogger().log(Level.WARNING, "Configuration of " + key + "is missing a value. (material-name, displayname, lore or slot)");
        continue;
      }
      SpecialItem.DisplayStage stage;
      try {
        stage = SpecialItem.DisplayStage.valueOf(config.getString(key + ".stage").toUpperCase());
      } catch(Exception ex) {
        Debugger.debug(Level.WARNING, "Invalid display stage of special item " + key + " in special_items.yml! Please use lobby or spectator!");
        stage = SpecialItem.DisplayStage.LOBBY;
      }
      SpecialItem item = new SpecialItem(key, new ItemBuilder(mat.parseItem()).name(name).lore(lore).build(), slot, stage);
      addItem(item);
    }
  }

  public List<SpecialItem> getSpecialItems() {
    return specialItems;
  }

  public enum SpecialItems {
    KIT_SELECTOR("Kit-Menu"), LOBBY_LEAVE_ITEM("Leave-Lobby"), PLAYERS_LIST("Player-List"),
    SPECTATOR_OPTIONS("Spectator-Options"), SPECTATOR_LEAVE_ITEM("Leave-Spectator");

    private final String name;

    SpecialItems(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

}
