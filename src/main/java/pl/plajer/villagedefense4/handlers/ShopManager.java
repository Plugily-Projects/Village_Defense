/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense4.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.arena.Arena;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  private static Map<Arena, Inventory> arenaShop = new HashMap<>();

  public ShopManager() {
    for (Arena a : ArenaRegistry.getArenas()) {
      if (ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "arenas").isSet("instances." + a.getID() + ".shop")) {
        registerShop(a);
      }
    }
  }

  public static Map<Arena, Inventory> getArenaShop() {
    return arenaShop;
  }

  public static void registerShop(Arena a) {
    try {
      FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "arenas");
      if (config.getString("instances." + a.getID() + ".shop", "").equals("") || config.getString("instances." + a.getID() + ".shop", "").split(",").length == 0) {
        Main.debug(Main.LogLevel.WARN, "There is no shop for arena " + a.getID() + "! Aborting registering shop!");
        return;
      }
      Location location = LocationUtils.getLocation(config.getString("instances." + a.getID() + ".shop"));
      if (location.getBlock() == null || !(location.getBlock().getState() instanceof Chest)) {
        Main.debug(Main.LogLevel.WARN, "Shop failed to load, invalid location for loc " + location);
        return;
      }
      int i = ((Chest) location.getBlock().getState()).getInventory().getContents().length;
      Inventory inventory = Bukkit.createInventory(null, MinigameUtils.serializeInt(i), ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"));
      i = 0;
      for (ItemStack itemStack : ((Chest) location.getBlock().getState()).getInventory().getContents()) {
        if (itemStack != null && itemStack.getType() != Material.REDSTONE_BLOCK) {
          inventory.setItem(i, itemStack);
        }
        i++;
      }
      arenaShop.put(a, inventory);
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public static void openShop(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    if (arenaShop.get(arena) == null) {
      player.sendMessage(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.No-Shop-Defined"));
      return;
    }
    player.openInventory(arenaShop.get(arena));
  }


}
