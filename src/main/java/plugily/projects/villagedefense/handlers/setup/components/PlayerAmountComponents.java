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

package plugily.projects.villagedefense.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.utils.constants.Constants;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class PlayerAmountComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Arena arena = setupInventory.getArena();
    if (arena == null) {
      return;
    }
    FileConfiguration config = setupInventory.getConfig();
    if (config == null) {
      return;
    }
    Main plugin = setupInventory.getPlugin();
    pane.addItem(new GuiItem(new ItemBuilder(Material.COAL).amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("minimumplayers"))
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Minimum Players Amount"))
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players are needed")
        .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
        .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".minimumplayers"))
        .build(), e -> {
      ItemStack itemStack = e.getInventory().getItem(e.getSlot()), currentItem = e.getCurrentItem();
      if (itemStack == null || currentItem == null) {
        return;
      }
      if (e.getClick().isRightClick()) {
        itemStack.setAmount(currentItem.getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        itemStack.setAmount(currentItem.getAmount() - 1);
      }
      if (itemStack.getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        itemStack.setAmount(1);
      }
      config.set("instances." + arena.getId() + ".minimumplayers", currentItem.getAmount());
      arena.setMinimumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 3, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE)
        .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("maximumplayers"))
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Maximum Players Amount"))
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".maximumplayers"))
        .build(), e -> {
      ItemStack itemStack = e.getInventory().getItem(e.getSlot()), currentItem = e.getCurrentItem();
      if (itemStack == null || currentItem == null) {
        return;
      }
      if (e.getClick().isRightClick()) {
        currentItem.setAmount(currentItem.getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        currentItem.setAmount(currentItem.getAmount() - 1);
      }
      if (currentItem.getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        currentItem.setAmount(1);
      }
      config.set("instances." + arena.getId() + ".maximumplayers", currentItem.getAmount());
      arena.setMaximumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 4, 0);
  }

}
