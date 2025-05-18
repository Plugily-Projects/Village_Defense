/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2025 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.commands.arguments.admin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.commands.completion.CompletableArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class TestArgument {

  private Map<Location, Byte> blockLocations = new HashMap<>();

  public TestArgument(ArgumentsRegistry registry) throws ClassNotFoundException, NoSuchMethodException {
    registry.getTabCompletion().registerCompletion(new CompletableArgument("villagedefenseadmin", "test", Arrays.asList("save", "revert")));
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("test", "villagedefense.admin.clear", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda test save revert", "/vda test save",
            "Test")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder(ChatColor.RED + "Please type valid mob type to clear: save revert").prefix().send(sender);
          return;
        }
        switch(args[1].toLowerCase()) {
          case "save":
            for(Block block : registry.getPlugin().getBukkitHelper().getNearbyBlocks((Player) sender, 1)) {
              Material door = Utils.getCachedDoor(block);

              if(block.getType() != door) {
                continue;
              }
              Location doorBottum = getLowerLocationOfDoor(block);
              blockLocations.put(doorBottum, doorBottum.getBlock().getState().getRawData());
              new MessageBuilder("Data bottom = " + doorBottum.getBlock().getState().getRawData()).send(sender);
              new MessageBuilder("Data up = " + doorBottum.getWorld().getBlockAt(doorBottum.getBlockX(), doorBottum.getBlockY() + 1, doorBottum.getBlockZ()).getState().getRawData()).send(sender);
              Block b = block.getRelative(BlockFace.UP);

              if(b.getType() == door) {
                b.setType(Material.AIR);
              } else if((b = block.getRelative(BlockFace.DOWN)).getType() == door) {
                b.setType(Material.AIR);
              }

              block.setType(Material.AIR);
              VersionUtils.playSound(doorBottum, "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
              return;
            }
            break;
          case "revert":
            for(Location location : blockLocations.keySet()) {
              Block bottomDoor = location.getBlock();
              Block topDoor = location.getBlock().getWorld( ).getBlockAt( new Location( location.getBlock().getWorld( ), location.getBlock().getLocation( ).getX( ), location.getBlock().getLocation( ).getY( ) + 1, location.getBlock().getLocation( ).getZ( )));
              //bottomDoor.setType(XMaterial.OAK_DOOR.get(), true);
              //topDoor.setType(XMaterial.OAK_DOOR.get(), true);
              try {
                setBlockInNativeWorld(bottomDoor.getWorld(), bottomDoor.getX(), bottomDoor.getY(), bottomDoor.getZ(), 64, blockLocations.get(location), true);
                setBlockInNativeWorld(topDoor.getWorld(), topDoor.getX(), topDoor.getY(), topDoor.getZ(), 64, (byte) 0x8, false);
              } catch(Exception e) {
                throw new RuntimeException(e);
              }
            }


            new MessageBuilder("Data = DONE");
            break;
          default:
            new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().send(sender);
        }
      }
    });
  }


  /**
   * Sets a block in the Minecraft world.  This method uses reflection to access
   * the Minecraft server's internal methods, so it is version-dependent.
   *
   * @param world         The Bukkit world in which to set the block.
   * @param x             The x-coordinate of the block.
   * @param y             The y-coordinate of the block.
   * @param z             The z-coordinate of the block.
   * @param blockId       The ID of the block to set.
   * @param data          The data value of the block to set.
   * @param applyPhysics Whether to apply physics to the block change.
   * @throws Exception if any error occurs during reflection.  It is CRITICAL
   * that you handle these exceptions in your calling code.
   */
  public static void setBlockInNativeWorld(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) throws Exception {

    // Use ReflectionUtilities to get NMS classes.
    Class<?> craftWorldClass = ReflectionUtilities.getOBCClass("CraftWorld");
    Class<?> worldServerClass = ReflectionUtilities.getNMSClass("WorldServer");
    Class<?> blockPositionClass = ReflectionUtilities.getNMSClass("BlockPosition");
    Class<?> iBlockDataClass = ReflectionUtilities.getNMSClass("IBlockData");
    Class<?> blockClass = ReflectionUtilities.getNMSClass("Block");


    // Get the NMS world object.
    //net.minecraft.server.v1_14_R1.WorldServer nmsWorld = ((CraftWorld) world).getHandle();
    Object nmsWorld = ReflectionUtilities.getHandle(world); //Gets the NMS World

    // Create a BlockPosition object.
    //net.minecraft.server.v1_14_R1.BlockPosition bp = new net.minecraft.server.v1_14_R1.BlockPosition(x, y, z);
    Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(x, y, z);


    // Get the IBlockData for the given block ID and data.
    //net.minecraft.server.v1_14_R1.IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(blockId + (data << 12));
    Method getByCombinedIdMethod = blockClass.getMethod("getByCombinedId", int.class);
    Object blockData = getByCombinedIdMethod.invoke(null, blockId + (data << 12));

    // Set the block using the NMS method.  The 'flags' parameter controls
    // whether physics is applied.  3 = apply physics, 2 = don't apply physics.
    //nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    Method setTypeAndDataMethod = worldServerClass.getMethod("setTypeAndData", blockPositionClass, iBlockDataClass, int.class);
    setTypeAndDataMethod.invoke(nmsWorld, blockPosition, blockData, applyPhysics ? 3 : 2);
  }



  public static class ReflectionUtilities {

    public static void setValue(Object instance, String fieldName, Object value) throws Exception {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
    }

    public static Object getValue(Object instance, String fieldName) throws Exception {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(instance);
    }

    public static String getVersion() {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      String version = name.substring(name.lastIndexOf('.') + 1) + ".";
      return version;
    }

    public static Class<?> getNMSClass(String className) {
      String fullName = "net.minecraft.server." + getVersion() + className;
      Class<?> clazz = null;
      try {
        clazz = Class.forName(fullName);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return clazz;
    }

    public static Class<?> getOBCClass(String className) {
      String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
      Class<?> clazz = null;
      try {
        clazz = Class.forName(fullName);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return clazz;
    }

    public static Object getHandle(Object obj) {
      try {
        return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    public static Field getField(Class<?> clazz, String name) {
      try {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
      for (Method m : clazz.getMethods()) {
        if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
          m.setAccessible(true);
          return m;
        }
      }
      return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
      boolean equal = true;
      if (l1.length != l2.length) { return false; }
      for (int i = 0; i < l1.length; i++) {
        if (l1[i] != l2[i]) {
          equal = false;
          break;
        }
      }
      return equal;
    }
  }




  public Location getLowerLocationOfDoor(Block block) {
    BlockState blockState = block.getState();
    Door door = ((Door) blockState.getData());
    Location lower;
    if(door.isTopHalf()) {
      lower = block.getLocation().subtract(0, 1, 0);
    } else {
      if(!door.isOpen()) {
        lower = block.getLocation().subtract(0, 1, 0);
        if(MaterialUtils.isDoor(lower.getBlock().getType()))
          return lower;
        else return block.getLocation();
      }
      lower = block.getLocation();
    }
    VersionUtils.sendParticles("FIREWORK", null, lower, 5, 0.1, 0.1, 0.1);
    return lower;
  }


}
