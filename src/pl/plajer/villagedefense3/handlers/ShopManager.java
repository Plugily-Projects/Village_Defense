/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.handlers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  @Getter
  private static Map<Arena, Inventory> arenaShop = new HashMap<>();

  public ShopManager() {
    for (Arena a : ArenaRegistry.getArenas()) {
      if (ConfigurationManager.getConfig("arenas").isSet("instances." + a.getID() + ".shop")) {
        registerShop(a);
      }
    }
  }

  public static void registerShop(Arena a) {
    Location location = Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString("instances." + a.getID() + ".shop"));
    if (!(location.getBlock().getState() instanceof Chest)) {
      Main.debug("Shop failed to load, invalid location for loc " + location, System.currentTimeMillis());
      return;
    }
    int i = ((Chest) location.getBlock().getState()).getInventory().getContents().length;
    Inventory inventory = Bukkit.createInventory(null, Utils.serializeInt(i), ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"));
    i = 0;
    for (ItemStack itemStack : ((Chest) location.getBlock().getState()).getInventory().getContents()) {
      if (itemStack != null && itemStack.getType() != Material.REDSTONE_BLOCK) {
        inventory.setItem(i, itemStack);
      }
      i++;
    }
    arenaShop.put(a, inventory);
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
