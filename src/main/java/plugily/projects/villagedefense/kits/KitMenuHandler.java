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

package plugily.projects.villagedefense.kits;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.event.player.VillagePlayerChooseKitEvent;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.items.SpecialItem;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.basekits.Kit;
import plugily.projects.villagedefense.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 12.07.2019
 */
public class KitMenuHandler implements Listener {

  private final Main plugin;
  private final String unlockedString;
  private final String lockedString;
  private final SpecialItem kitItem;

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
    for(Kit kit : KitRegistry.getKits()) {
      ItemStack itemStack = kit.getItemStack();
      if(kit.isUnlockedByPlayer(player)) {
        itemStack = new ItemBuilder(itemStack).lore(unlockedString).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(lockedString).build();
      }

      pane.addItem(new GuiItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick()) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) {
          return;
        }
        VillagePlayerChooseKitEvent event = new VillagePlayerChooseKitEvent(player, KitRegistry.getKit(e.getCurrentItem()), arena);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
          return;
        }
        if(!kit.isUnlockedByPlayer(player)) {
          player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_NOT_UNLOCKED_MESSAGE).replace("%KIT%", kit.getName()));
          return;
        }
        plugin.getUserManager().getUser(player).setKit(kit);
        player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_CHOOSE_MESSAGE).replace("%KIT%", kit.getName()));
      }), x, y);
      x++;
      if(x == 9) {
        x = 0;
        y++;
      }
    }
    gui.show(player);
  }

  @EventHandler
  public void onKitMenuItemClick(CBPlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(!stack.equals(kitItem.getItemStack())) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer());
  }

}
