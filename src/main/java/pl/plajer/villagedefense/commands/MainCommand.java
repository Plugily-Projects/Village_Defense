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

package pl.plajer.villagedefense.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.setup.SetupInventory;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 7/08/2014.
 */
//todo we should get rid of this
public class MainCommand {

  private Main plugin;

  public MainCommand(Main plugin, boolean register) {
    this.plugin = plugin;
    if (register) {
      ArgumentsRegistry argumentsRegistry = new ArgumentsRegistry(plugin);
      TabCompletion completion = new TabCompletion(plugin);
      plugin.getCommand("villagedefense").setExecutor(argumentsRegistry);
      plugin.getCommand("villagedefense").setTabCompleter(completion);
      plugin.getCommand("villagedefenseadmin").setExecutor(argumentsRegistry);
      plugin.getCommand("villagedefenseadmin").setTabCompleter(completion);
    }
  }

  protected boolean checkSenderPlayer(CommandSender sender) {
    if (sender instanceof Player) {
      return true;
    }
    sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
    return false;
  }

  public boolean checkIsInGameInstance(Player player) {
    if (ArenaRegistry.getArena(player) == null) {
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Not-Playing"));
      return false;
    }
    return true;
  }

  public boolean hasPermission(CommandSender sender, String perm) {
    if (sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
    return false;
  }

  public void performSetup(Player player, String[] args) {
    if (args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
      if (ArenaRegistry.getArena(args[0]) == null) {
        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
        return;
      }

      sendProTip(player);
      new SetupInventory(ArenaRegistry.getArena(args[0])).openInventory(player);
      return;
    }
    if (!(args.length > 2)) {
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.contains("instances." + args[0])) {
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      return;
    }

    if (args[1].equalsIgnoreCase("addspawn")) {
      if (args[2].equalsIgnoreCase("zombie")) {
        int i = (config.isSet("instances." + args[0] + ".zombiespawns") ? config.getConfigurationSection("instances." + args[0] + ".zombiespawns").getKeys(false).size() : 0) + 1;
        LocationUtils.saveLoc(plugin, config, "arenas", "instances." + args[0] + ".zombiespawns." + i, player.getLocation());
        String progress = i >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
        player.sendMessage(ChatManager.colorRawMessage(progress + "&aZombie spawn added! &8(&7" + i + "/2&8)"));
        return;
      }
      if (args[2].equalsIgnoreCase("villager")) {
        int i = (config.isSet("instances." + args[0] + ".villagerspawns") ? config.getConfigurationSection("instances." + args[0] + ".villagerspawns").getKeys(false).size() : 0) + 1;
        LocationUtils.saveLoc(plugin, config, "arenas", "instances." + args[0] + ".villagerspawns." + i, player.getLocation());
        String progress = i >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
        player.sendMessage(ChatManager.colorRawMessage(progress + "&aVillager spawn added! &8(&7" + i + "/2&8)"));
        return;
      }
      if (args[2].equalsIgnoreCase("doors")) {
        Block block = player.getTargetBlock(null, 10);
        if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
          if (block.getType() != Material.WOODEN_DOOR) {
            player.sendMessage(ChatColor.RED + "Target block is not oak door!");
            return;
          }
        } else {
          if (block.getType() != XMaterial.OAK_DOOR.parseMaterial()) {
            player.sendMessage(ChatColor.RED + "Target block is not oak door!");
            return;
          }
        }
        String ID = args[0];
        int i = (config.isSet("instances." + args[0] + ".doors") ? config.getConfigurationSection("instances." + args[0] + ".doors").getKeys(false).size() : 0) + 1;

        Block relativeBlock = null;
        if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
          if (block.getRelative(BlockFace.DOWN).getType() == Material.WOODEN_DOOR) {
            relativeBlock = block;
            block = block.getRelative(BlockFace.DOWN);
          } else if (block.getRelative(BlockFace.UP).getType() == Material.WOODEN_DOOR) {
            relativeBlock = block.getRelative(BlockFace.UP);
          }
        } else {
          if (block.getRelative(BlockFace.DOWN).getType() == XMaterial.OAK_DOOR.parseMaterial()) {
            relativeBlock = block;
            block = block.getRelative(BlockFace.DOWN);
          } else if (block.getRelative(BlockFace.UP).getType() == XMaterial.OAK_DOOR.parseMaterial()) {
            relativeBlock = block.getRelative(BlockFace.UP);
          }
        }
        if (relativeBlock == null) {
          player.sendMessage("This door doesn't have 2 blocks? Maybe it's bugged? Try placing it again.");
          return;
        }
        String location = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0" + ",0.0";
        String relativeLocation = relativeBlock.getWorld().getName() + "," + relativeBlock.getX() + "," + relativeBlock.getY() + "," + relativeBlock.getZ() + ",0.0" + ",0.0";
        config.set("instances." + ID + ".doors." + i + ".location", relativeLocation);
        config.set("instances." + ID + ".doors." + i + ".byte", 8);
        i++;
        config.set("instances." + ID + ".doors." + i + ".location", location);
        if (plugin.is1_13_R1() || plugin.is1_13_R2()) {
          config.set("instances." + ID + ".doors." + i + ".byte", Utils.getDoorByte(((Door) block.getState().getData()).getFacing()));
        } else {
          config.set("instances." + ID + ".doors." + i + ".byte", block.getData());
        }
        player.sendMessage(ChatColor.GREEN + "Door successfully added!");
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }
    }
    if (!(args[1].equalsIgnoreCase("set"))) {
      return;
    }
    if (args.length == 3) {
      String location = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ()
          + "," + player.getLocation().getYaw() + ",0.0";
      switch (args[2].toLowerCase()) {
        case "lobbylocation":
        case "lobbyloc":
          config.set("instances." + args[0] + ".lobbylocation", location);
          player.sendMessage(ChatManager.colorRawMessage("&e✔ Completed | &aLobby location for arena " + args[0] + " set at your location!"));
          break;
        case "startlocation":
        case "startloc":
          config.set("instances." + args[0] + ".Startlocation", location);
          player.sendMessage(ChatManager.colorRawMessage("&e✔ Completed | &aStarting location for arena " + args[0] + " set at your location!"));
          break;
        case "endlocation":
        case "endloc":
          config.set("instances." + args[0] + ".Endlocation", location);
          player.sendMessage(ChatManager.colorRawMessage("&e✔ Completed | &aEnding location for arena " + args[0] + " set at your location!"));
          break;
        default:
          player.sendMessage(ChatManager.colorRawMessage("&cInvalid argument! Use: /vd <arena> set <lobbyloc | startloc | endloc>"));
          break;
      }
    } else if (args.length == 4) {
      switch (args[2].toLowerCase()) {
        case "maximumplayers":
        case "maxplayers":
          config.set("instances." + args[0] + ".maximumplayers", Integer.parseInt(args[3]));
          break;
        case "minimumplayers":
        case "minplayers":
          config.set("instances." + args[0] + ".minimumplayers", Integer.parseInt(args[3]));
          break;
        case "mapname":
        case "name":
          config.set("instances." + args[0] + ".mapname", args[3]);
          player.sendMessage(ChatManager.colorRawMessage("&e✔ Completed | &aName of arena " + args[0] + " set to " + args[3]));
          break;
        default:
          player.sendMessage(ChatManager.colorRawMessage("&cInvalid argument! Use /vd <arena> set <minplayers | maxplayers | name> <value>"));
          break;
      }
    }
    ConfigUtils.saveConfig(plugin, config, "arenas");
  }

  public void createArenaCommand(Player player, String[] args) {
    for (Arena arena : ArenaRegistry.getArenas()) {
      if (arena.getID().equalsIgnoreCase(args[1])) {
        player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
        player.sendMessage(ChatColor.DARK_RED + "Usage: /vd create <ID>");
        return;
      }
    }
    if (ConfigUtils.getConfig(plugin, "arenas").contains("instances." + args[1])) {
      player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
    } else {
      createInstanceInConfig(args[1], player.getWorld().getName());
      player.sendMessage(ChatColor.BOLD + "------------------------------------------");
      player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
      player.sendMessage("");
      player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/vd " + args[1] + " edit" + ChatColor.GREEN + "!");
      player.sendMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
      player.sendMessage(ChatColor.GOLD + SetupInventory.VIDEO_LINK);
      player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
      sendProTip(player);
    }
  }

  private void createInstanceInConfig(String ID, String worldName) {
    String path = "instances." + ID + ".";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    LocationUtils.saveLoc(plugin, config, "arenas", path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationUtils.saveLoc(plugin, config, "arenas", path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationUtils.saveLoc(plugin, config, "arenas", path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    config.set(path + "minimumplayers", 1);
    config.set(path + "maximumplayers", 10);
    config.set(path + "mapname", ID);
    config.set(path + "signs", new ArrayList<>());
    config.set(path + "isdone", false);
    config.set(path + "world", worldName);
    ConfigUtils.saveConfig(plugin, config, "arenas");

    Arena arena = ArenaUtils.initializeArena(ID);

    arena.setMinimumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt(path + "minimumplayers"));
    arena.setMaximumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt(path + "maximumplayers"));
    arena.setMapName(ConfigUtils.getConfig(plugin, "arenas").getString(path + "mapname"));
    arena.setLobbyLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "lobbylocation")));
    arena.setStartLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "Startlocation")));
    arena.setEndLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "Endlocation")));
    arena.setReady(false);

    ArenaRegistry.registerArena(arena);
  }

  private void sendProTip(Player p) {
    int rand = new Random().nextInt(7 + 1);
    switch (rand) {
      case 0:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Console can execute /vd addorbs [amount] (player) command! Add game orbs via console!"));
        break;
      case 1:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Build Secret Well for your arena! Check how: https://bit.ly/2DTYxZc"));
        break;
      case 2:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plajer.xyz"));
        break;
      case 3:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7LeaderHeads leaderboard plugin is supported with our plugin! Check here: https://bit.ly/2Riu5L0"));
        break;
      case 4:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame!"));
        break;
      case 5:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plajer-Lair/Village_Defense"));
        break;
      case 6:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plajer.xyz/minecraft/villagedefense &7or discord https://discord.gg/UXzUdTP"));
        break;
      case 7:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Install HolographicDisplays plugin to access power-ups in game! (configure them in config.yml)"));
        break;
    }
  }

}
