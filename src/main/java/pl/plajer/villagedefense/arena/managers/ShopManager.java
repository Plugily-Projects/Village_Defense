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

package pl.plajer.villagedefense.arena.managers;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  /**
   * Default name of golem spawn item from language.yml
   */
  public static final String DEFAULT_GOLEM_ITEM_NAME;
  /**
   * Default name of wolf spawn item from language.yml
   */
  public static final String DEFAULT_WOLF_ITEM_NAME;

  static {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "language");
    DEFAULT_GOLEM_ITEM_NAME = config.getString("In-Game.Messages.Shop-Messages.Golem-Item-Name");
    DEFAULT_WOLF_ITEM_NAME = config.getString("In-Game.Messages.Shop-Messages.Wolf-Item-Name");
  }

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private FileConfiguration config;
  private Gui gui;
  private Arena arena;

  public ShopManager(Arena arena) {
    this.config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "arenas");
    this.arena = arena;
    if (config.isSet("instances." + arena.getId() + ".shop")) {
      registerShop();
    }
  }

  public Gui getShop() {
    return gui;
  }

  public void openShop(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    if (gui == null) {
      player.sendMessage(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.No-Shop-Defined"));
      return;
    }
    gui.show(player);
  }

  private void registerShop() {
    if (!validateShop()) {
      return;
    }
    ItemStack[] contents = ((Chest) LocationUtils.getLocation(config.getString("instances." + arena.getId() + ".shop"))
        .getBlock().getState()).getInventory().getContents();
    int i = contents.length;
    Gui gui = new Gui(plugin, MinigameUtils.serializeInt(i) / 9, plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"));
    StaticPane pane = new StaticPane(9, MinigameUtils.serializeInt(i) / 9);
    int x = 0;
    int y = 0;
    for (ItemStack itemStack : contents) {
      if (itemStack == null || itemStack.getType() == Material.REDSTONE_BLOCK) {
        x++;
        if (x == 9) {
          x = 0;
          y++;
        }
        continue;
      }

      String costString = null;
      boolean found = false;
      //seek for item price
      if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
        for (String s : itemStack.getItemMeta().getLore()) {
          if (s.contains(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || s.contains("orbs")) {
            costString = ChatColor.stripColor(s).replaceAll("[^0-9]", "");
            found = true;
            break;
          }
        }
      }
      if (!found) {
        Debugger.debug(LogLevel.WARN, "No price set for shop item in arena " + arena.getId() + " skipping!");
        continue;
      }
      final int cost = Integer.parseInt(costString);

      pane.addItem(new GuiItem(itemStack, e -> {
        Player player = (Player) e.getWhoClicked();
        if (!arena.getPlayers().contains(player)) {
          return;
        }
        e.setCancelled(true);
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
          return;
        }
        User user = plugin.getUserManager().getUser(player);
        if (cost > user.getStat(StatsStorage.StatisticType.ORBS)) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
          return;
        }
        if (Utils.isNamed(itemStack)) {
          String name = itemStack.getItemMeta().getDisplayName();
          int spawnedAmount = 0;
          if (name.contains(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Golem-Item-Name"))
              || name.contains(DEFAULT_GOLEM_ITEM_NAME)) {
            for (IronGolem golem : arena.getIronGolems()) {
              if (golem.getCustomName().equals(plugin.getChatManager().colorMessage("In-Game.Spawned-Golem-Name").replace("%player%", player.getName()))) {
                spawnedAmount++;
              }
            }
            if (spawnedAmount >= plugin.getConfig().getInt("Golems-Spawn-Limit", 15)) {
              player.sendMessage(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Mob-Limit-Reached")
                  .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Golems-Spawn-Limit", 15))));
              return;
            }
            arena.spawnGolem(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Golem-Spawned"));
            user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
            return;
          } else if (name.contains(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Wolf-Item-Name"))
              || name.contains(DEFAULT_WOLF_ITEM_NAME)) {
            for (Wolf wolf : arena.getWolfs()) {
              if (wolf.getCustomName().equals(plugin.getChatManager().colorMessage("In-Game.Spawned-Wolf-Name").replace("%player%", player.getName()))) {
                spawnedAmount++;
              }
            }
            if (spawnedAmount >= plugin.getConfig().getInt("Wolves-Spawn-Limit", 20)) {
              player.sendMessage(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Mob-Limit-Reached")
                  .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Wolves-Spawn-Limit", 20))));
              return;
            }
            arena.spawnWolf(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Wolf-Spawned"));
            user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
            return;
          }
        }

        ItemStack stack = itemStack.clone();
        ItemMeta itemMeta = stack.getItemMeta();
        Iterator<String> lore = itemMeta.getLore().iterator();
        while (lore.hasNext()) {
          String next = lore.next();
          if (next.contains(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
            lore.remove();
          }
        }
        List<String> newLore = new ArrayList<>();
        lore.forEachRemaining(newLore::add);
        itemMeta.setLore(newLore);
        stack.setItemMeta(itemMeta);
        player.getInventory().addItem(stack);
        user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
        arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
      }), x, y);
      x++;
      if (x == 9) {
        x = 0;
        y++;
      }
    }
    gui.addPane(pane);
    this.gui = gui;
  }

  private boolean validateShop() {
    if (config.getString("instances." + arena.getId() + ".shop", "").equals("") || config.getString("instances." + arena.getId() + ".shop", "").split(",").length == 0) {
      Debugger.debug(LogLevel.WARN, "There is no shop for arena " + arena.getId() + "! Aborting registering shop!");
      return false;
    }
    Location location = LocationUtils.getLocation(config.getString("instances." + arena.getId() + ".shop"));
    if (location.getWorld() == null || location.getBlock() == null || !(location.getBlock().getState() instanceof Chest)) {
      Debugger.debug(LogLevel.WARN, "Shop failed to load, invalid location for loc " + location);
      return false;
    }
    return true;
  }


}
