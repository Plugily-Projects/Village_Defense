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

package plugily.projects.villagedefense.arena.managers;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
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
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.villagedefense.ConfigPreferences.Option;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;
import plugily.projects.villagedefense.utils.constants.Constants;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  private final String defaultGolemItemName;
  private final String defaultWolfItemName;

  private final Main plugin;
  private final FileConfiguration config;
  private Gui gui;
  private final Arena arena;

  public ShopManager(Arena arena) {
    this.config = ConfigUtils.getConfig(arena.getPlugin(), Constants.Files.ARENAS.getName());
    this.plugin = arena.getPlugin();
    this.arena = arena;
    FileConfiguration languageConfig = ConfigUtils.getConfig(arena.getPlugin(), Constants.Files.LANGUAGE.getName());
    defaultGolemItemName = languageConfig.getString("In-Game.Messages.Shop-Messages.Golem-Item-Name");
    defaultWolfItemName = languageConfig.getString("In-Game.Messages.Shop-Messages.Wolf-Item-Name");
    if(config.isSet("instances." + arena.getId() + ".shop")) {
      registerShop();
    }
  }

  public Gui getShop() {
    return gui;
  }

  /**
   * Default name of golem spawn item from language.yml
   *
   * @return the default golem item name
   */
  public String getDefaultGolemItemName() {
    return defaultGolemItemName;
  }

  /**
   * Default name of wolf spawn item from language.yml
   *
   * @return the default wolf item name
   */
  public String getDefaultWolfItemName() {
    return defaultWolfItemName;
  }

  public void openShop(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      return;
    }
    if(gui == null) {
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_NO_SHOP_DEFINED));
      return;
    }
    gui.show(player);
  }

  private void registerShop() {
    if(!validateShop()) {
      return;
    }
    ItemStack[] contents = ((Chest) LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".shop"))
        .getBlock().getState()).getInventory().getContents();
    int i = contents.length;
    Gui gui = new Gui(plugin, Utils.serializeInt(i) / 9, plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_SHOP_GUI_NAME));
    StaticPane pane = new StaticPane(9, Utils.serializeInt(i) / 9);
    int x = 0;
    int y = 0;
    for(ItemStack itemStack : contents) {
      if(itemStack == null || itemStack.getType() == Material.REDSTONE_BLOCK) {
        x++;
        if(x == 9) {
          x = 0;
          y++;
        }
        continue;
      }

      String costString = "";
      //seek for item price
      if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
        for(String s : ComplementAccessor.getComplement().getLore(itemStack.getItemMeta())) {
          if(s.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_CURRENCY_IN_SHOP)) || s.contains("orbs")) {
            costString = ChatColor.stripColor(s).replaceAll("[^0-9]", "");
            break;
          }
        }
      }
      if(costString.isEmpty()) {
        Debugger.debug(Level.WARNING, "No price set for shop item in arena {0} skipping item!", arena.getId());
        continue;
      }
      final int cost = Integer.parseInt(costString);

      pane.addItem(new GuiItem(itemStack, e -> {
        Player player = (Player) e.getWhoClicked();
        if(!arena.getPlayers().contains(player)) {
          return;
        }
        e.setCancelled(true);
        User user = plugin.getUserManager().getUser(player);
        if(cost > user.getStat(StatsStorage.StatisticType.ORBS)) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_NOT_ENOUGH_ORBS));
          return;
        }
        if(ItemUtils.isItemStackNamed(itemStack)) {
          String name = ComplementAccessor.getComplement().getDisplayName(itemStack.getItemMeta());
          int spawnedAmount = 0;
          if(name.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_GOLEM_ITEM_NAME))
              || name.contains(defaultGolemItemName)) {
            List<IronGolem> golems = arena.getIronGolems();
            if(plugin.getConfigPreferences().getOption(Option.CAN_BUY_GOLEMSWOLVES_IF_THEY_DIED)) {
              golems = golems.stream().filter(IronGolem::isDead).collect(Collectors.toList());
            }
            for(IronGolem golem : golems) {
              if(plugin.getChatManager().colorMessage(Messages.SPAWNED_GOLEM_NAME).replace("%player%", player.getName()).equals(golem.getCustomName())) {
                spawnedAmount++;
              }
            }
            if(spawnedAmount >= plugin.getConfig().getInt("Golems-Spawn-Limit", 15)) {
              player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_MOB_LIMIT_REACHED)
                  .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Golems-Spawn-Limit", 15))));
              return;
            }
            arena.spawnGolem(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.GOLEM_SPAWNED));
            user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
            arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
            return;
          } else if(name.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_WOLF_ITEM_NAME))
              || name.contains(defaultWolfItemName)) {
            List<Wolf> wolves = arena.getWolves();
            if(plugin.getConfigPreferences().getOption(Option.CAN_BUY_GOLEMSWOLVES_IF_THEY_DIED)) {
              wolves = wolves.stream().filter(Wolf::isDead).collect(Collectors.toList());
            }
            for(Wolf wolf : wolves) {
              if(plugin.getChatManager().colorMessage(Messages.SPAWNED_WOLF_NAME).replace("%player%", player.getName()).equals(wolf.getCustomName())) {
                spawnedAmount++;
              }
            }
            if(spawnedAmount >= plugin.getConfig().getInt("Wolves-Spawn-Limit", 20)) {
              player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_MOB_LIMIT_REACHED)
                  .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Wolves-Spawn-Limit", 20))));
              return;
            }
            arena.spawnWolf(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.WOLF_SPAWNED));
            user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
            arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
            return;
          }
        }

        ItemStack stack = itemStack.clone();
        ItemMeta itemMeta = stack.getItemMeta();
        if(itemMeta != null) {
          if(itemMeta.hasLore()) {
            ComplementAccessor.getComplement().setLore(itemMeta, ComplementAccessor.getComplement().getLore(itemMeta).stream().filter(lore ->
                !lore.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_CURRENCY_IN_SHOP)))
                .collect(Collectors.toList()));
          }
          stack.setItemMeta(itemMeta);
        }
        player.getInventory().addItem(stack);
        user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
        arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
      }), x, y);
      x++;
      if(x == 9) {
        x = 0;
        y++;
      }
    }
    gui.addPane(pane);
    this.gui = gui;
  }

  private boolean validateShop() {
    String shop = config.getString("instances." + arena.getId() + ".shop", "");
    if(shop.isEmpty() || shop.split(",").length == 0) {
      Debugger.debug(Level.WARNING, "There is no shop for arena {0}! Aborting registering shop!", arena.getId());
      return false;
    }
    Location location = LocationSerializer.getLocation(shop);
    //todo are these still revelant checks
    if(location.getWorld() == null || !(location.getBlock().getState() instanceof Chest)) {
      Debugger.debug(Level.WARNING, "Shop failed to load, invalid location for location {0}", LocationSerializer.locationToString(location));
      return false;
    }
    return true;
  }

}
