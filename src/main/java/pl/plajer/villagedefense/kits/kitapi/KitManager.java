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

package pl.plajer.villagedefense.kits.kitapi;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.event.player.VillagePlayerChooseKitEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.Kit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Class for setting Kit values.
 * Need to use before registering new kit!
 *
 * @author TomTheDeveloper
 */
public class KitManager implements Listener {

  private String[] description;
  private Main plugin;
  private String itemName;
  private Material material;
  private String menuName;

  private String unlockedString;
  private String lockedString;

  public KitManager(Main plugin) {
    this.plugin = plugin;
    itemName = plugin.getChatManager().colorMessage("Kits.Kit-Menu-Item-Name");
    unlockedString = plugin.getChatManager().colorMessage("Kits.Kit-Menu.Unlocked-Kit-Lore");
    lockedString = plugin.getChatManager().colorMessage("Kits.Kit-Menu.Locked-Lores.Locked-Lore");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Returns name of kit
   *
   * @return name of kit
   */
  public String getItemName() {
    return itemName;
  }

  /**
   * Sets name of kit
   *
   * @param name kit name
   */
  public void setItemName(String name) {
    this.itemName = name;
  }

  public void createKitMenu(Player player) {
    Gui guiMenu = new Gui(plugin, MinigameUtils.serializeInt(KitRegistry.getKits().size()) / 9, getMenuName());
    StaticPane pane = new StaticPane(9, guiMenu.getRows());
    int x = 0;
    int y = 0;
    for (Kit kit : KitRegistry.getKits()) {
      ItemStack itemStack = kit.getItemStack();
      if (kit.isUnlockedByPlayer(player)) {
        MinigameUtils.addLore(itemStack, unlockedString);
      } else {
        MinigameUtils.addLore(itemStack, lockedString);
      }

      pane.addItem(new GuiItem(itemStack, e -> {
        if (!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick())) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);
        e.setCancelled(true);
        if (!Utils.isNamed(e.getCurrentItem()) || arena == null) {
          return;
        }
        VillagePlayerChooseKitEvent event = new VillagePlayerChooseKitEvent(player, KitRegistry.getKit(e.getCurrentItem()), arena);
        Bukkit.getPluginManager().callEvent(event);
      }), x, y);
      x++;
      if (x == 9) {
        x = 0;
        y++;
      }
    }
    guiMenu.addPane(pane);
    guiMenu.show(player);
  }

  /**
   * Returns material represented by kit
   *
   * @return material represented by kit
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Sets material that kit will represents
   *
   * @param material material that kit will represents
   */
  public void setMaterial(Material material) {
    this.material = material;
  }

  /**
   * Returns description of kit
   *
   * @return description of kit
   */
  public String[] getDescription() {
    return description.clone();
  }

  /**
   * Sets description of kit
   *
   * @param description description of kit
   */
  public void setDescription(String[] description) {
    this.description = description.clone();
  }

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public void giveKitMenuItem(Player player) {
    ItemStack itemStack = new ItemStack(getMaterial());
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(getItemName());
    itemMeta.setLore(Arrays.asList(getDescription()));
    itemStack.setItemMeta(itemMeta);
    player.getInventory().addItem(itemStack);
  }


  @EventHandler
  public void onKitMenuItemClick(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (stack.getType() != getMaterial() || !stack.hasItemMeta() || !stack.getItemMeta().hasLore()) {
      return;
    }
    if (!stack.getItemMeta().getDisplayName().equalsIgnoreCase(getItemName())) {
      return;
    }
    createKitMenu(e.getPlayer());
  }

  @EventHandler
  public void onKitChoose(VillagePlayerChooseKitEvent e) {
    if (e.getKit().isUnlockedByPlayer(e.getPlayer())) {
      User user = plugin.getUserManager().getUser(e.getPlayer());
      user.setKit(e.getKit());
      e.getPlayer().sendMessage(plugin.getChatManager().colorMessage("Kits.Choose-Message").replace("%KIT%", e.getKit().getName()));
    } else {
      e.getPlayer().sendMessage(plugin.getChatManager().colorMessage("Kits.Not-Unlocked-Message").replace("%KIT%", e.getKit().getName()));
    }

  }
}
