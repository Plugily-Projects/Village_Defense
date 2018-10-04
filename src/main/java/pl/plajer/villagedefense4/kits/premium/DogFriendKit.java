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

package pl.plajer.villagedefense4.kits.premium;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.arena.Arena;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajer.villagedefense4.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense4.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense4.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense4.arena.initializers.ArenaInitializer1_13_R2;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.handlers.PermissionsManager;
import pl.plajer.villagedefense4.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense4.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense4.utils.ArmorHelper;
import pl.plajer.villagedefense4.utils.Utils;
import pl.plajer.villagedefense4.utils.WeaponHelper;
import pl.plajer.villagedefense4.utils.XMaterial;

/**
 * Created by Tom on 18/07/2015.
 */
public class DogFriendKit extends PremiumKit {

  private Main plugin;

  public DogFriendKit(Main plugin) {
    this.plugin = plugin;
    this.setName(ChatManager.colorMessage("Kits.Dog-Friend.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Dog-Friend.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
  }


  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.dogfriend");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.LEATHER);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    if (plugin.is1_11_R1()) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      for (int i = 0; i < 3; i++) {
        initializer.spawnWolf(initializer.getStartLocation(), player);
      }
    } else if (plugin.is1_12_R1()) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      for (int i = 0; i < 3; i++) {
        initializer.spawnWolf(initializer.getStartLocation(), player);
      }
    } else if (plugin.is1_13_R1()) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      for (int i = 0; i < 3; i++) {
        initializer.spawnWolf(initializer.getStartLocation(), player);
      }
    } else if (plugin.is1_13_R2()) {
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      for (int i = 0; i < 3; i++) {
        initializer.spawnWolf(initializer.getStartLocation(), player);
      }
    }
  }

  @Override
  public Material getMaterial() {
    return Material.BONE;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    if (plugin.is1_11_R1()) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (plugin.is1_12_R1()) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (plugin.is1_13_R1()) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (plugin.is1_13_R2()) {
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    }
  }
}
