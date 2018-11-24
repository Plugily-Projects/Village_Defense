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

package pl.plajer.villagedefense.handlers.setup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.ShopManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryEvents implements Listener {

  private Main plugin;

  public SetupInventoryEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    try {
      if (event.getWhoClicked().getType() != EntityType.PLAYER) {
        return;
      }
      Player player = (Player) event.getWhoClicked();
      if (!player.hasPermission("villagedefense.admin.create") || !event.getInventory().getName().contains("Arena VD:")
          || event.getInventory().getHolder() != null || event.getCurrentItem() == null
          || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
        return;
      }

      Arena arena = ArenaRegistry.getArena(event.getInventory().getName().replace("Arena VD: ", ""));
      ClickType clickType = event.getClick();

      //do not close inventory nor cancel event when setting arena name via name tag
      if (event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor() == null || event.getCursor().getType() != Material.NAME_TAG) {
        return;
      }
      if (event.getCursor() == null || event.getCursor().getType() != Material.NAME_TAG) {
        player.closeInventory();
        event.setCancelled(true);
      }

      switch (SetupInventory.ClickPosition.getByPosition(event.getRawSlot())) {
        case SET_ENDING:
          player.performCommand("vd " + arena.getID() + " set ENDLOC");
          break;
        case SET_LOBBY:
          player.performCommand("vd " + arena.getID() + " set LOBBYLOC");
          break;
        case SET_STARTING:
          player.performCommand("vd " + arena.getID() + " set STARTLOC");
          break;
        case SET_MINIMUM_PLAYERS:
          if (clickType.isRightClick()) {
            event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
            player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
          }
          if (clickType.isLeftClick()) {
            event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
            player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
          }
          player.openInventory(new SetupInventory(arena).getInventory());
          break;
        case SET_MAXIMUM_PLAYERS:
          if (clickType.isRightClick()) {
            event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
            player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
          }
          if (clickType.isLeftClick()) {
            event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
            player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
          }
          player.openInventory(new SetupInventory(arena).getInventory());
          break;
        case ADD_SIGN:
          Location location = player.getTargetBlock(null, 10).getLocation();
          if (location.getBlock().getState() instanceof Sign) {
            plugin.getSignManager().getLoadedSigns().put((Sign) location.getBlock().getState(), arena);
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
            String loc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
            FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
            List<String> locs = config.getStringList("instances." + arena + ".signs");
            locs.add(loc);
            config.set("instances." + arena + ".signs", locs);
            ConfigUtils.saveConfig(plugin, config, "arenas");
          } else {
            player.sendMessage(ChatManager.colorMessage("Commands.Look-Sign"));
          }
          break;
        case SET_MAP_NAME:
          if (event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor().getType() == Material.NAME_TAG) {
            if (!event.getCursor().hasItemMeta()) {
              player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
              return;
            }
            if (!event.getCursor().getItemMeta().hasDisplayName()) {
              player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
              return;
            }
            player.performCommand("vd " + arena.getID() + " set MAPNAME " + event.getCursor().getItemMeta().getDisplayName());
            event.getCurrentItem().getItemMeta().setDisplayName(ChatColor.GOLD + "Set a mapname (currently: " + event.getCursor().getItemMeta().getDisplayName());
          }
          break;
        case ADD_VILLAGER_SPAWN:
          player.performCommand("vd " + arena.getID() + " addspawn villager");
          break;
        case ADD_ZOMBIE_SPAWN:
          player.performCommand("vd " + arena.getID() + " addspawn zombie");
          break;
        case ADD_DOORS:
          player.performCommand("vd " + arena.getID() + " addspawn doors");
          break;
        case SET_CHEST_SHOP:
          Block targetBlock;
          targetBlock = player.getTargetBlock(null, 100);
          if (targetBlock == null || targetBlock.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "Look at the chest! You are targeting something else!");
            return;
          }
          boolean found = false;
          for (ItemStack stack : ((Chest) targetBlock.getState()).getBlockInventory()) {
            if (stack == null) {
              continue;
            }
            if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
              if (stack.getItemMeta().getLore().get(stack.getItemMeta().getLore().size() - 1).contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
                found = true;
                break;
              }
            }
          }
          if (!found) {
            player.sendMessage(ChatColor.RED + "No items in shop have price set! Set their prices using /vda setprice! You can ignore this warning");
          }
          LocationUtils.saveLoc(plugin, ConfigUtils.getConfig(plugin, "arenas"), "arenas", "instances." + arena.getID() + ".shop", targetBlock.getLocation());
          ShopManager.registerShop(arena);
          player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
          break;
        case REGISTER_ARENA:
          if (ArenaRegistry.getArena(arena.getID()).isReady()) {
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
            return;
          }
          String[] locations = new String[] {"lobbylocation", "Startlocation", "Endlocation"};
          String[] spawns = new String[] {"zombiespawns", "villagerspawns"};
          for (String s : locations) {
            if (!ConfigUtils.getConfig(plugin, "arenas").isSet("instances." + arena.getID() + "." + s) || ConfigUtils.getConfig(plugin, "arenas")
                .getString("instances." + arena.getID() + "." + s).equals(LocationUtils.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
              event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following spawn properly: " + s + " (cannot be world spawn location)");
              return;
            }
          }
          for (String s : spawns) {
            if (!ConfigUtils.getConfig(plugin, "arenas").isSet("instances." + arena.getID() + "." + s) || ConfigUtils.getConfig(plugin, "arenas")
                .getConfigurationSection("instances." + arena.getID() + "." + s).getKeys(false).size() < 2) {
              event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following mob spawns properly: " + s + " (must be minimum 2 spawns)");
              return;
            }
          }
          if (ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances." + arena.getID() + ".doors") == null) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure doors properly");
            return;
          }
          event.getWhoClicked().sendMessage(ChatColor.GREEN + "Validation succeeded! Registering new arena instance: " + arena.getID());
          FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
          config.set("instances." + arena.getID() + ".isdone", true);
          ConfigUtils.saveConfig(plugin, config, "arenas");
          List<Sign> signsToUpdate = new ArrayList<>();
          ArenaRegistry.unregisterArena(arena);
          if (plugin.getSignManager().getLoadedSigns().containsValue(arena)) {
            for (Sign s : plugin.getSignManager().getLoadedSigns().keySet()) {
              if (plugin.getSignManager().getLoadedSigns().get(s).equals(arena)) {
                signsToUpdate.add(s);
              }
            }
          }
          arena = ArenaUtils.initializeArena(arena.getID());
          arena.setReady(true);
          arena.setMinimumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt("instances." + arena.getID() + ".minimumplayers"));
          arena.setMaximumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt("instances." + arena.getID() + ".maximumplayers"));
          arena.setMapName(ConfigUtils.getConfig(plugin, "arenas").getString("instances." + arena.getID() + ".mapname"));
          arena.setLobbyLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString("instances." + arena.getID() + ".lobbylocation")));
          arena.setStartLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString("instances." + arena.getID() + ".Startlocation")));
          arena.setEndLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString("instances." + arena.getID() + ".Endlocation")));
          for (String string : ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances." + arena.getID() + ".zombiespawns").getKeys(false)) {
            String path = "instances." + arena.getID() + ".zombiespawns." + string;
            arena.addZombieSpawn(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path)));
          }
          for (String string : ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances." + arena.getID() + ".villagerspawns").getKeys(false)) {
            String path = "instances." + arena.getID() + ".villagerspawns." + string;
            arena.addVillagerSpawn(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path)));
          }
          for (String string : ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances." + arena.getID() + ".doors").getKeys(false)) {
            String path = "instances." + arena.getID() + ".doors." + string + ".";
            arena.addDoor(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "location")),
                (byte) ConfigUtils.getConfig(plugin, "arenas").getInt(path + "byte"));
          }
          ArenaRegistry.registerArena(arena);
          arena.start();
          for (Sign s : signsToUpdate) {
            plugin.getSignManager().getLoadedSigns().put(s, arena);
          }
          break;
        case VIEW_SETUP_VIDEO:
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorRawMessage("&6Check out this video: " + SetupInventory.VIDEO_LINK));
          break;
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}
