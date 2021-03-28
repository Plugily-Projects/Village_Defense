/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits.level;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.LevelKit;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

import java.util.List;

/**
 * Created by Tom on 19/07/2015.
 */
public class WorkerKit extends LevelKit implements Listener {

  public WorkerKit() {
    setLevel(getKitsConfig().getInt("Required-Level.Worker"));
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_WORKER_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_WORKER_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }


  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LEVEL) >= getLevel() || player.hasPermission("villagedefense.kit.worker");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.PURPLE, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(WeaponHelper.getEnchantedBow(Enchantment.DURABILITY, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.ARROW.parseMaterial(), 64));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 10));
    player.getInventory().addItem(new ItemStack(getMaterial(), 2));
  }

  @Override
  public Material getMaterial() {
    return Utils.getCachedDoor(null);
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemStack(getMaterial()));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDoorPlace(BlockPlaceEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(e.getPlayer());
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(user.isSpectator() || !arena.getMapRestorerManager().getGameDoorLocations()
        .containsKey(e.getBlock().getLocation())) {
      e.setCancelled(true);
      return;
    }
    if(stack.getType() != Utils.getCachedDoor(e.getBlock())) {
      e.setCancelled(true);
      return;
    }
    //to override world guard protection
    e.setCancelled(false);
    e.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage(Messages.KITS_WORKER_GAME_ITEM_PLACE_MESSAGE));
  }

}
