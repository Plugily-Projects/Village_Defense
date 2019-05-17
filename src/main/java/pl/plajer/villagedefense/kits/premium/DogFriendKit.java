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

package pl.plajer.villagedefense.kits.premium;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_13_R2;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_14_R1;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.PremiumKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

/**
 * Created by Tom on 18/07/2015.
 */
public class DogFriendKit extends PremiumKit {

  public DogFriendKit() {
    this.setName(getPlugin().getChatManager().colorMessage(Messages.KITS_DOG_FRIEND_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_DOG_FRIEND_DESCRIPTION), 40);
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
    for (int i = 0; i < 3; i++) {
      spawnWolf(arena, player);
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
    spawnWolf(arena, player);
  }

  private void spawnWolf(Arena arena, Player player) {
    if (getPlugin().is1_11_R1()) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_12_R1()) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_13_R1()) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_13_R2()) {
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_14_R1()) {
      ArenaInitializer1_14_R1 initializer = (ArenaInitializer1_14_R1) arena;
      initializer.spawnWolf(initializer.getStartLocation(), player);
    }
  }

}
