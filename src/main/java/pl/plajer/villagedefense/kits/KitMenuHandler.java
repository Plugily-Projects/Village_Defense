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

package pl.plajer.villagedefense.kits;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.event.player.VillagePlayerChooseKitEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.items.SpecialItem;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.basekits.Kit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;

/**
 * @author Plajer
 * <p>
 * Created at 12.07.2019
 */
public class KitMenuHandler implements Listener {

  private Main plugin;
  private String unlockedString;
  private String lockedString;
  private SpecialItem kitItem;

  public KitMenuHandler(Main plugin) {
    this.plugin = plugin;
    this.kitItem = plugin.getSpecialItemManager().getSpecialItem(SpecialItemManager.SpecialItems.KIT_SELECTOR.getName());
    unlockedString = plugin.getChatManager().colorMessage(Messages.KITS_MENU_UNLOCKED_LORE);
    lockedString = plugin.getChatManager().colorMessage(Messages.KITS_MENU_LOCKED_LORE);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void createMenu(Player player) {
    Gui gui = new Gui(plugin, Utils.serializeInt(KitRegistry.getKits().size()) / 9, plugin.getChatManager().colorMessage(Messages.KITS_OPEN_KIT_MENU));
    StaticPane pane = new StaticPane(9, gui.getRows());
    gui.addPane(pane);
    int x = 0;
    int y = 0;
    for (Kit kit : KitRegistry.getKits()) {
      ItemStack itemStack = kit.getItemStack();
      if (kit.isUnlockedByPlayer(player)) {
        itemStack = new ItemBuilder(itemStack).lore(unlockedString).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(lockedString).build();
      }

      pane.addItem(new GuiItem(itemStack, e -> {
        if (!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick())) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);
        e.setCancelled(true);
        if (!ItemUtils.isItemName(e.getCurrentItem()) || arena == null) {
          return;
        }
        VillagePlayerChooseKitEvent event = new VillagePlayerChooseKitEvent(player, KitRegistry.getKit(e.getCurrentItem()), arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
          return;
        }
        if (!kit.isUnlockedByPlayer(player)) {
          player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_NOT_UNLOCKED_MESSAGE).replace("%KIT%", kit.getName()));
          return;
        }
        User user = plugin.getUserManager().getUser(player);
        user.setKit(kit);
        player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_CHOOSE_MESSAGE).replace("%KIT%", kit.getName()));
      }), x, y);
      x++;
      if (x == 9) {
        x = 0;
        y++;
      }
    }
    gui.show(player);
  }

  @EventHandler
  public void onKitMenuItemClick(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (!stack.equals(kitItem.getItemStack())) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer());
  }

}
