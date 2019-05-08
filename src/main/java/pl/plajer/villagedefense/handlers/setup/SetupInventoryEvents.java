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

package pl.plajer.villagedefense.handlers.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.material.Door;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

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
    if (event.getWhoClicked().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    if (!(player.hasPermission("villagedefense.admin.create") && event.getInventory().getName().contains("Arena VD:")
        && Utils.isNamed(event.getCurrentItem()))) {
      return;
    }

    SetupInventory.ClickPosition slot = SetupInventory.ClickPosition.getByPosition(event.getRawSlot());
    //do not close inventory nor cancel event when setting arena name via name tag
    if (event.getCurrentItem().getType() != Material.NAME_TAG) {
      if (!(slot == SetupInventory.ClickPosition.SET_MINIMUM_PLAYERS || slot == SetupInventory.ClickPosition.SET_MAXIMUM_PLAYERS)) {
        player.closeInventory();
      }
      event.setCancelled(true);
    }

    Arena arena = ArenaRegistry.getArena(event.getInventory().getName().replace("Arena VD: ", ""));
    ClickType clickType = event.getClick();
    String locationString = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
        + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    switch (slot) {
      case SET_ENDING:
        config.set("instances." + arena.getId() + ".Endlocation", locationString);
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
        break;
      case SET_LOBBY:
        config.set("instances." + arena.getId() + ".lobbylocation", locationString);
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
        break;
      case SET_STARTING:
        config.set("instances." + arena.getId() + ".Startlocation", locationString);
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aStarting location for arena " + arena.getId() + " set at your location!"));
        break;
      case SET_MINIMUM_PLAYERS:
        if (clickType.isRightClick()) {
          event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
        }
        if (clickType.isLeftClick()) {
          event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
        }
        config.set("instances." + arena.getId() + ".minimumplayers", event.getCurrentItem().getAmount());
        player.updateInventory();
        break;
      case SET_MAXIMUM_PLAYERS:
        if (clickType.isRightClick()) {
          event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
        }
        if (clickType.isLeftClick()) {
          event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
        }
        config.set("instances." + arena.getId() + ".maximumplayers", event.getCurrentItem().getAmount());
        player.updateInventory();
        break;
      case ADD_SIGN:
        Location location = player.getTargetBlock(null, 10).getLocation();
        if (!(location.getBlock().getState() instanceof Sign)) {
          player.sendMessage(plugin.getChatManager().colorMessage("Commands.Look-Sign"));
          break;
        }
        plugin.getSignManager().getLoadedSigns().put((Sign) location.getBlock().getState(), arena);
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Created"));
        String loc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
        List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
        locs.add(loc);
        config.set("instances." + arena.getId() + ".signs", locs);
        break;
      case SET_MAP_NAME:
        if (event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor().getType() == Material.NAME_TAG) {
          if (!Utils.isNamed(event.getCursor())) {
            player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
            return;
          }
          String newName = event.getCursor().getItemMeta().getDisplayName();
          config.set("instances." + arena.getId() + ".mapname", newName);
          player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + arena.getId() + " set to " + newName));
          event.getCurrentItem().getItemMeta().setDisplayName(ChatColor.GOLD + "Set a mapname (currently: " + newName);
        }
        break;
      case ADD_VILLAGER_SPAWN:
        int villagers = (config.isSet("instances." + arena.getId() + ".villagerspawns")
            ? config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns").getKeys(false).size() : 0) + 1;
        LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".villagerspawns." + villagers, player.getLocation());
        String villagerProgress = villagers >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
        player.sendMessage(plugin.getChatManager().colorRawMessage(villagerProgress + "&aVillager spawn added! &8(&7" + villagers + "/2&8)"));
        break;
      case ADD_ZOMBIE_SPAWN:
        int zombies = (config.isSet("instances." + arena.getId() + ".zombiespawns")
            ? config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns").getKeys(false).size() : 0) + 1;
        LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".zombiespawns." + zombies, player.getLocation());
        String zombieProgress = zombies >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
        player.sendMessage(plugin.getChatManager().colorRawMessage(zombieProgress + "&aZombie spawn added! &8(&7" + zombies + "/2&8)"));
        break;
      case ADD_DOORS:
        Block block = player.getTargetBlock(null, 10);
        Material door;
        if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
          door = Material.WOODEN_DOOR;
        } else {
          door = XMaterial.OAK_DOOR.parseMaterial();
        }
        if (block.getType() != door) {
          player.sendMessage(ChatColor.RED + "Target block is not oak door!");
          return;
        }
        int doors = (config.isSet("instances." + arena.getId() + ".doors")
            ? config.getConfigurationSection("instances." + arena.getId() + ".doors").getKeys(false).size() : 0) + 1;

        Block relativeBlock = null;
        if (block.getRelative(BlockFace.DOWN).getType() == door) {
          relativeBlock = block;
          block = block.getRelative(BlockFace.DOWN);
        } else if (block.getRelative(BlockFace.UP).getType() == door) {
          relativeBlock = block.getRelative(BlockFace.UP);
        }
        if (relativeBlock == null) {
          player.sendMessage("This door doesn't have 2 blocks? Maybe it's bugged? Try placing it again.");
          return;
        }
        String relativeLocation = relativeBlock.getWorld().getName() + "," + relativeBlock.getX() + "," + relativeBlock.getY() + "," + relativeBlock.getZ() + ",0.0" + ",0.0";
        config.set("instances." + arena.getId() + ".doors." + doors + ".location", relativeLocation);
        config.set("instances." + arena.getId() + ".doors." + doors + ".byte", 8);
        doors++;

        String doorLocation = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0" + ",0.0";
        config.set("instances." + arena.getId() + ".doors." + doors + ".location", doorLocation);
        if (plugin.is1_13_R1() || plugin.is1_13_R2()) {
          config.set("instances." + arena.getId() + ".doors." + doors + ".byte", Utils.getDoorByte(((Door) block.getState().getData()).getFacing()));
        } else {
          config.set("instances." + arena.getId() + ".doors." + doors + ".byte", block.getData());
        }
        player.sendMessage(ChatColor.GREEN + "Door successfully added!");
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
            if (stack.getItemMeta().getLore().get(stack.getItemMeta().getLore().size() - 1)
                .contains(plugin.getChatManager().colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
              found = true;
              break;
            }
          }
        }
        if (!found) {
          player.sendMessage(ChatColor.RED + "No items in shop have price set! Set their prices using /vda setprice! You can ignore this warning");
        }
        LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".shop", targetBlock.getLocation());
        player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7You can use special items in shops! Check out https://bit.ly/2T2GhA9"));
        break;
      case REGISTER_ARENA:
        if (ArenaRegistry.getArena(arena.getId()).isReady()) {
          event.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
          return;
        }
        String[] locations = new String[] {"lobbylocation", "Startlocation", "Endlocation"};
        String[] spawns = new String[] {"zombiespawns", "villagerspawns"};
        for (String s : locations) {
          if (!config.isSet("instances." + arena.getId() + "." + s) || config.getString("instances." + arena.getId() + "." + s)
              .equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following spawn properly: " + s + " (cannot be world spawn location)");
            return;
          }
        }
        for (String s : spawns) {
          if (!config.isSet("instances." + arena.getId() + "." + s)
              || config.getConfigurationSection("instances." + arena.getId() + "." + s).getKeys(false).size() < 2) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following mob spawns properly: " + s + " (must be minimum 2 spawns)");
            return;
          }
        }
        if (config.getConfigurationSection("instances." + arena.getId() + ".doors") == null) {
          event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure doors properly");
          return;
        }
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Validation succeeded! Registering new arena instance: " + arena.getId());
        config.set("instances." + arena.getId() + ".isdone", true);
        List<Sign> signsToUpdate = new ArrayList<>();
        ArenaRegistry.unregisterArena(arena);
        if (plugin.getSignManager().getLoadedSigns().containsValue(arena)) {
          for (Map.Entry<Sign, Arena> entry : plugin.getSignManager().getLoadedSigns().entrySet()) {
            if (entry.getValue().equals(arena)) {
              signsToUpdate.add(entry.getKey());
            }
          }
        }
        arena = ArenaUtils.initializeArena(arena.getId());
        arena.setReady(true);
        arena.setMinimumPlayers(config.getInt("instances." + arena.getId() + ".minimumplayers"));
        arena.setMaximumPlayers(config.getInt("instances." + arena.getId() + ".maximumplayers"));
        arena.setMapName(config.getString("instances." + arena.getId() + ".mapname"));
        arena.setLobbyLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".lobbylocation")));
        arena.setStartLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".Startlocation")));
        arena.setEndLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".Endlocation")));
        ArenaUtils.setWorld(arena);
        for (String string : config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns").getKeys(false)) {
          String path = "instances." + arena.getId() + ".zombiespawns." + string;
          arena.addZombieSpawn(LocationSerializer.getLocation(config.getString(path)));
        }
        for (String string : config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns").getKeys(false)) {
          String path = "instances." + arena.getId() + ".villagerspawns." + string;
          arena.addVillagerSpawn(LocationSerializer.getLocation(config.getString(path)));
        }
        for (String string : config.getConfigurationSection("instances." + arena.getId() + ".doors").getKeys(false)) {
          String path = "instances." + arena.getId() + ".doors." + string + ".";
          arena.getMapRestorerManager().addDoor(LocationSerializer.getLocation(config.getString(path + "location")),
              (byte) config.getInt(path + "byte"));
        }
        ArenaRegistry.registerArena(arena);
        arena.start();
        for (Sign s : signsToUpdate) {
          plugin.getSignManager().getLoadedSigns().put(s, arena);
        }
        break;
      case ENHANCEMENTS_AD:
        player.sendMessage(plugin.getChatManager().getPrefix()
            + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://download.plajer.xyz/preview/minecraft.php"));
        break;
      case VIEW_SETUP_VIDEO:
        player.sendMessage(plugin.getChatManager().getPrefix()
            + plugin.getChatManager().colorRawMessage("&6Check out this video: " + SetupInventory.VIDEO_LINK));
        break;
      default:
        break;
    }
    ConfigUtils.saveConfig(plugin, config, "arenas");
  }
}
