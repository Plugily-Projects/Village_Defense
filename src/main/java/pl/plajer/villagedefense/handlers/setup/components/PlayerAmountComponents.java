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

package pl.plajer.villagedefense.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.handlers.setup.SetupInventory;
import pl.plajer.villagedefense.utils.constants.Constants;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

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
    FileConfiguration config = setupInventory.getConfig();
    Arena arena = setupInventory.getArena();
    Main plugin = setupInventory.getPlugin();
    pane.addItem(new GuiItem(new ItemBuilder(Material.COAL).amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("minimumplayers"))
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Minimum Players Amount"))
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players are needed")
        .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
        .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".minimumplayers"))
        .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getInventory().getItem(e.getSlot()).setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        e.getInventory().getItem(e.getSlot()).setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        e.getInventory().getItem(e.getSlot()).setAmount(1);
      }
      config.set("instances." + arena.getId() + ".minimumplayers", e.getCurrentItem().getAmount());
      arena.setMinimumPlayers(e.getCurrentItem().getAmount());
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
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        e.getInventory().getItem(e.getSlot()).setAmount(1);
      }
      config.set("instances." + arena.getId() + ".maximumplayers", e.getCurrentItem().getAmount());
      arena.setMaximumPlayers(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 4, 0);
  }

}
