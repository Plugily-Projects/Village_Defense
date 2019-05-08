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

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.utils.CompatMaterialConstants;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plajer.xyz";
  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private static Random random = new Random();
  private Inventory inventory;

  public SetupInventory(Arena arena) {
    this.inventory = Bukkit.createInventory(null, 9 * 2, "Arena VD: " + arena.getId());

    inventory.setItem(ClickPosition.SET_ENDING.getPosition(), new ItemBuilder(Material.REDSTONE_BLOCK)
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " ending " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be teleported")
        .lore(ChatColor.DARK_GRAY + "after the game)")
        .lore(isOptionDoneBool("instances." + arena.getId() + ".Endlocation"))
        .build());
    inventory.setItem(ClickPosition.SET_LOBBY.getPosition(), new ItemBuilder(Material.LAPIS_BLOCK)
        .name(ChatColor.GOLD + "► Set" + ChatColor.WHITE + " lobby " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore(ChatColor.DARK_GRAY + "(location where players will wait")
        .lore(ChatColor.DARK_GRAY + "before the game starts)")
        .lore(isOptionDoneBool("instances." + arena.getId() + ".lobbylocation"))
        .build());
    inventory.setItem(ClickPosition.SET_STARTING.getPosition(), new ItemBuilder(Material.EMERALD_BLOCK)
        .name(ChatColor.GOLD + "► Set" + ChatColor.YELLOW + " starting " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the starting location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be teleported")
        .lore(ChatColor.DARK_GRAY + "when game starts)")
        .lore(isOptionDoneBool("instances." + arena.getId() + ".Startlocation"))
        .build());

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    int min = config.getInt("instances." + arena.getId() + ".minimumplayers");
    if (min == 0) {
      min = 1;
    }
    inventory.setItem(ClickPosition.SET_MINIMUM_PLAYERS.getPosition(), new ItemBuilder(Material.COAL)
        .amount(min)
        .name(ChatColor.GOLD + "► Set" + ChatColor.DARK_GREEN + " minimum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players are needed")
        .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
        .lore(isOptionDone("instances." + arena.getId() + ".minimumplayers"))
        .build());
    inventory.setItem(ClickPosition.SET_MAXIMUM_PLAYERS.getPosition(), new ItemBuilder(Material.REDSTONE)
        .amount(config.getInt("instances." + arena.getId() + ".maximumplayers"))
        .name(ChatColor.GOLD + "► Set" + ChatColor.GREEN + " maximum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore(isOptionDone("instances." + arena.getId() + ".maximumplayers"))
        .build());

    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      inventory.setItem(ClickPosition.ADD_SIGN.getPosition(), new ItemBuilder(Material.SIGN)
          .name(ChatColor.GOLD + "► Add game" + ChatColor.AQUA + " sign")
          .lore(ChatColor.GRAY + "Target a sign and click this.")
          .lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign")
          .lore(ChatColor.DARK_GRAY + "which you can use to join the game)")
          .build());
    }

    inventory.setItem(ClickPosition.SET_MAP_NAME.getPosition(), new ItemBuilder(Material.NAME_TAG)
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " map name " + ChatColor.GOLD + "(currently: " + arena.getMapName() + ")")
        .lore(ChatColor.GRAY + "Replace this name tag with named name tag.")
        .lore(ChatColor.GRAY + "It will be set as arena name.")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "Drop name tag here don't move")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "it and replace with new!!!")
        .build());
    inventory.setItem(ClickPosition.ADD_VILLAGER_SPAWN.getPosition(), new ItemBuilder(Material.EMERALD)
        .name(ChatColor.GOLD + "► Add" + ChatColor.GREEN + " villager " + ChatColor.GOLD + "spawn")
        .lore(ChatColor.GRAY + "Add new villager spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore(ChatColor.DARK_GRAY + "(villagers will spawn at this location)")
        .lore(isOptionDoneList("instances." + arena.getId() + ".villagerspawns"))
        .build());
    inventory.setItem(ClickPosition.ADD_ZOMBIE_SPAWN.getPosition(), new ItemBuilder(Material.ROTTEN_FLESH)
        .name(ChatColor.GOLD + "► Add" + ChatColor.BLUE + " zombie " + ChatColor.GOLD + "spawn")
        .lore(ChatColor.GRAY + "Add new zombie spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore(ChatColor.DARK_GRAY + "(zombies will spawn at this location)")
        .lore(isOptionDoneList("instances." + arena.getId() + ".zombiespawns"))
        .build());
    inventory.setItem(ClickPosition.ADD_DOORS.getPosition(), new ItemBuilder(CompatMaterialConstants.OAK_DOOR_ITEM)
        .name(ChatColor.GOLD + "► Add doors")
        .lore(ChatColor.GRAY + "Target arena door and click this.")
        .lore(ChatColor.DARK_GRAY + "(doors are required and will be")
        .lore(ChatColor.DARK_GRAY + "regenerated each game, villagers will hide")
        .lore(ChatColor.DARK_GRAY + "in houses so you can put doors there)")
        .lore(isOptionDoneList("instances." + arena.getId() + ".doors"))
        .build());
    inventory.setItem(ClickPosition.SET_CHEST_SHOP.getPosition(), new ItemBuilder(Material.CHEST)
        .name(ChatColor.GOLD + "► Set" + ChatColor.LIGHT_PURPLE + " chest " + ChatColor.GOLD + "shop")
        .lore(ChatColor.GRAY + "Target chest with configured game items")
        .lore(ChatColor.GRAY + "and click this.")
        .lore(ChatColor.DARK_GRAY + "(it allows to click villagers to buy game items)")
        .lore(ChatColor.RED + "Remember to set item prices for the game")
        .lore(ChatColor.RED + "using /vda setprice command!")
        .build());
    inventory.setItem(ClickPosition.REGISTER_ARENA.getPosition(), new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
        .name(ChatColor.GOLD + "► " + ChatColor.GREEN + "Register arena")
        .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
        .lore(ChatColor.GRAY + "It will validate and register arena.")
        .build());

    inventory.setItem(16, new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(ChatColor.GOLD + "► Enhancements Addon (AD) ◄")
        .lore(ChatColor.GRAY + "Enhance Village Defense gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Features of this addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build());
    inventory.setItem(17, new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(ChatColor.GOLD + "► View setup video")
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build());
  }

  private static String isOptionDone(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: " + config.getString(path) + ")";
  }

  public static void sendProTip(Player p) {
    int rand = random.nextInt(8 + 1);
    switch (rand) {
      case 0:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Console can execute /vda addorbs [amount] (player) command! Add game orbs via console!"));
        break;
      case 1:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Build Secret Well for your arena! Check how: https://bit.ly/2DTYxZc"));
        break;
      case 2:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plajer.xyz"));
        break;
      case 3:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7LeaderHeads leaderboard plugin is supported with our plugin! Check here: https://bit.ly/2Riu5L0"));
        break;
      case 4:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame!"));
        break;
      case 5:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plajer-Lair/Village_Defense"));
        break;
      case 6:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plajer.xyz/minecraft/villagedefense &7or discord https://discord.gg/UXzUdTP"));
        break;
      case 7:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Install HolographicDisplays plugin to access power-ups in game! (configure them in config.yml)"));
        break;
      case 8:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://uservoice.plajer.xyz/index.php?id=VillageDefense"));
        break;
      default:
        break;
    }
  }

  private String isOptionDoneList(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    if (!path.contains(".doors")) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: "
          + config.getConfigurationSection(path).getKeys(false).size() + ")";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: "
        + config.getConfigurationSection(path).getKeys(false).size() / 2 + ")";
  }

  private String isOptionDoneBool(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    if (Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(config.getString(path)))) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes";
  }

  public void addItem(ItemStack itemStack) {
    inventory.addItem(itemStack);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void openInventory(Player player) {
    player.openInventory(inventory);
  }

  public enum ClickPosition {
    SET_ENDING(0), SET_LOBBY(1), SET_STARTING(2), SET_MINIMUM_PLAYERS(3), SET_MAXIMUM_PLAYERS(4), ADD_SIGN(5), SET_MAP_NAME(6),
    ADD_VILLAGER_SPAWN(7), ADD_ZOMBIE_SPAWN(8), ADD_DOORS(9), SET_CHEST_SHOP(10), REGISTER_ARENA(11), ENHANCEMENTS_AD(16), VIEW_SETUP_VIDEO(17);

    private int position;

    ClickPosition(int position) {
      this.position = position;
    }

    public static ClickPosition getByPosition(int pos) {
      for (ClickPosition position : ClickPosition.values()) {
        if (position.getPosition() == pos) {
          return position;
        }
      }
      //couldn't find position, return tutorial
      return ClickPosition.VIEW_SETUP_VIDEO;
    }

    /**
     * @return gets position of item in inventory
     */
    public int getPosition() {
      return position;
    }
  }

}
