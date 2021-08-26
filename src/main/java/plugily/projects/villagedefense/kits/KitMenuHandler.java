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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.minigamesbox.inventory.normal.FastInv;
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
    FastInv gui = new FastInv(Utils.serializeInt(KitRegistry.getKits().size()), plugin.getChatManager().colorMessage(Messages.KITS_OPEN_KIT_MENU));
    for(Kit kit : KitRegistry.getKits()) {
      ItemStack itemStack = kit.getItemStack();
      itemStack = new ItemBuilder(itemStack)
          .lore(kit.isUnlockedByPlayer(player) ? unlockedString : lockedString)
          .build();

      gui.addItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.isLeftClick() || e.isRightClick()) || !(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) {
          return;
        }
        VillagePlayerChooseKitEvent event = new VillagePlayerChooseKitEvent(player, kit, arena);
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
      });
    }
    gui.open(player);
  }

  @EventHandler
  public void onKitMenuItemClick(CBPlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }

    if(!VersionUtils.getItemInHand(e.getPlayer()).equals(kitItem.getItemStack())) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer());
  }

}
