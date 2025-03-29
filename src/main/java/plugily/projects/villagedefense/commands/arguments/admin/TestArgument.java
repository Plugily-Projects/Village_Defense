/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.commands.completion.CompletableArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticle;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class TestArgument {

  private ArrayList<Location> blockLocations = new ArrayList<>();
  private byte data;

  public TestArgument(ArgumentsRegistry registry) {
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
              blockLocations.add(doorBottum);
              data = block.getData();
              new MessageBuilder("Data = " + block.getState().getData()).send(sender);
              org.bukkit.Location blockLoc = block.getLocation();


              Block b = block.getRelative(BlockFace.UP);

              if(b.getType() == door) {
                b.setType(Material.AIR);
              } else if((b = block.getRelative(BlockFace.DOWN)).getType() == door) {
                b.setType(Material.AIR);
              }

              block.setType(Material.AIR);
              VersionUtils.playSound(blockLoc, "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR");
              return;
            }
            break;
          case "revert":
            for(Location doorBootom : blockLocations) {
              doorBootom.getBlock().setType(XMaterial.OAK_DOOR.get(), false);
              Block doorUper = doorBootom.getBlock().getRelative(BlockFace.UP);
              doorUper.setType(XMaterial.OAK_DOOR.get(), true);
            }
            blockLocations.clear();
            break;
          default:
            new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().send(sender);
        }
      }
    });
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
