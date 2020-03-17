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

package pl.plajer.villagedefense.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.language.Messages;

/**
 * Created by Tom on 29/07/2014.
 */
public class Utils {

  private static Main plugin;

  private Utils() {
  }

  public static void init(Main plugin) {
    Utils.plugin = plugin;
  }

  public static void takeOneItem(Player player, ItemStack stack) {
    if (stack.getAmount() <= 1) {
      player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    } else {
      player.getInventory().getItemInMainHand().setAmount(stack.getAmount() - 1);
    }
  }

  /**
   * Serialize int to use it in Inventories size
   * ex. you have 38 kits and it will serialize it to 45 (9*5)
   * because it is valid inventory size
   * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public static int serializeInt(Integer i) {
    if ((i % 9) == 0) {
      return i;
    } else {
      return (int) ((Math.ceil(i / 9) * 9) + 9);
    }
  }

  public static List<Block> getNearbyBlocks(LivingEntity entity, int distance) {
    List<Block> blocks = new LinkedList<>();
    Iterator<Block> itr = new BlockIterator(entity, distance);
    while (itr.hasNext()) {
      Block block = itr.next();
      if (block.getType().isTransparent()) {
        continue;
      }
      blocks.add(block);
    }
    return blocks;
  }

  public static Entity[] getNearbyEntities(Location loc, int radius) {
    int chunkRadius = radius < 16 ? 1 : radius / 16;
    Set<Entity> radiusEntities = new HashSet<>();
    for (int chunkX = 0 - chunkRadius; chunkX <= chunkRadius; chunkX++) {
      for (int chunkZ = 0 - chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        for (Entity e : new Location(loc.getWorld(), x + chunkX * 16, y, z + chunkZ * 16).getChunk().getEntities()) {
          if (!(loc.getWorld().getName().equalsIgnoreCase(e.getWorld().getName()))) {
            continue;
          }
          if (e.getLocation().distanceSquared(loc) <= radius * radius && e.getLocation().getBlock() != loc.getBlock()) {
            radiusEntities.add(e);
          }
        }
      }
    }
    return radiusEntities.toArray(new Entity[0]);
  }

  public static List<String> splitString(String string, int max) {
    List<String> matchList = new ArrayList<>();
    Pattern regex = Pattern.compile(".{1," + max + "}(?:\\s|$)", Pattern.DOTALL);
    Matcher regexMatcher = regex.matcher(string);
    while (regexMatcher.find()) {
      matchList.add(plugin.getChatManager().colorRawMessage("&7") + regexMatcher.group());
    }
    return matchList;
  }

  public static ItemStack getPotion(PotionType type, int tier, boolean splash) {
    ItemStack potion;
    if (!splash) {
      potion = new ItemStack(Material.POTION, 1);
    } else {
      potion = new ItemStack(Material.SPLASH_POTION, 1);
    }

    PotionMeta meta = (PotionMeta) potion.getItemMeta();
    if (tier >= 2 && !splash) {
      meta.setBasePotionData(new PotionData(type, false, true));
    } else {
      meta.setBasePotionData(new PotionData(type, false, false));
    }
    potion.setItemMeta(meta);
    return potion;
  }

  public static byte getDoorByte(BlockFace face) {
    switch (face) {
      case NORTH:
        return 3;
      case SOUTH:
        return 1;
      case WEST:
        return 2;
      case EAST:
      default:
        return 0;
    }
  }

  public static BlockFace getFacingByByte(byte bt) {
    switch (bt) {
      case 2:
        return BlockFace.WEST;
      case 3:
        return BlockFace.EAST;
      case 4:
        return BlockFace.NORTH;
      case 1:
      default:
        return BlockFace.SOUTH;
    }
  }

  public static void playSound(Location loc, String before1_13, String after1_13) {
    if (plugin.is1_13_R1() || plugin.is1_13_R2() || plugin.is1_14_R1() || plugin.is1_15_R1()) {
      loc.getWorld().playSound(loc, Sound.valueOf(after1_13), 1, 1);
    } else {
      loc.getWorld().playSound(loc, before1_13, 1, 1);
    }
  }

  /**
   * @param s string to check whether is integer number
   * @return true if it is, false otherwise, like 12a, 12.03 33333333333333 etc.
   */
  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean checkIsInGameInstance(Player player) {
    if (ArenaRegistry.getArena(player) == null) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.COMMANDS_NOT_PLAYING));
      return false;
    }
    return true;
  }

  public static boolean hasPermission(CommandSender sender, String perm) {
    if (sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.COMMANDS_NO_PERMISSION));
    return false;
  }

}
