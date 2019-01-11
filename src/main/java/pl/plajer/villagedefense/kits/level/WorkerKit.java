/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.kits.level;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 19/07/2015.
 */
public class WorkerKit extends LevelKit implements Listener {

  public WorkerKit() {
    this.setLevel(getKitsConfig().getInt("Required-Level.Worker"));
    this.setName(getPlugin().getChatManager().colorMessage("Kits.Worker.Kit-Name"));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Worker.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }


  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagefense.kit.worker");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.PURPLE, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(WeaponHelper.getEnchantedBow(Enchantment.DURABILITY, 10));
    player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.OAK_DOOR.parseMaterial(), 2));
  }

  @Override
  public Material getMaterial() {
    return XMaterial.OAK_DOOR.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(XMaterial.OAK_DOOR.parseItem());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDoorPlace(BlockPlaceEvent e) {
    try {
      Arena arena = ArenaRegistry.getArena(e.getPlayer());
      if (arena == null) {
        return;
      }
      User user = getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId());
      ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
      if (stack == null || user.isSpectator() || !arena.getDoorLocations().containsKey(e.getBlock().getLocation())) {
        e.setCancelled(true);
        return;
      }
      if (getPlugin().is1_12_R1() || getPlugin().is1_11_R1()) {
        if (stack.getType() != Material.WOOD_DOOR || stack.getType() != Material.WOODEN_DOOR) {
          e.setCancelled(true);
          return;
        }
      } else {
        if (stack.getType() != XMaterial.OAK_DOOR.parseMaterial()) {
          e.setCancelled(true);
          return;
        }
      }
      e.setCancelled(false);
      e.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Worker.Game-Item-Place-Message"));
    } catch (Exception ex) {
      new ReportedException(getPlugin(), ex);
    }
  }

}
