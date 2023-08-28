/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.block.Block;
import plugily.projects.villagedefense.Main;

/**
 * @author Plajer
 * <p>
 * Created at 28.08.2023
 */
public class ProtocolUtils {

  private static boolean enabled = false;

  private ProtocolUtils() {
  }

  public static void init(Main plugin) {
    ProtocolUtils.enabled = plugin.getServer().getPluginManager().getPlugin("ProtocolLib") != null;
  }

  public static void removeBlockBreakAnimation(Block block) {
    sendBlockBreakAnimation(block, 10);
  }

  //https://wiki.vg/Protocol#Set_Block_Destroy_Stage
  public static void sendBlockBreakAnimation(Block block, int stage) {
    if(!enabled) {
      return;
    }
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    PacketContainer packet = manager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
    packet.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
    packet.getIntegers().write(0, block.hashCode());
    packet.getIntegers().write(1, stage);
    manager.broadcastServerPacket(packet);
  }

}
