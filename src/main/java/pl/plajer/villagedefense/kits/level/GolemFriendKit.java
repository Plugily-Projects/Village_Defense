/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.initializers.*;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.LevelKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

import java.util.List;

/**
 * Created by Tom on 21/07/2015.
 */
public class GolemFriendKit extends LevelKit {

  public GolemFriendKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_GOLEM_FRIEND_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_GOLEM_FRIEND_DESCRIPTION), 40);
    this.setDescription(description.toArray(new String[0]));
    setLevel(getKitsConfig().getInt("Required-Level.GolemFriend"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("villagedefense.kit.golemfriend");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    spawnGolem(player, arena);
  }

  @Override
  public Material getMaterial() {
    return Material.IRON_INGOT;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena.getWave() % 5 == 0) {
      spawnGolem(player, arena);
    }
  }

  private void spawnGolem(Player player, Arena arena) {
    if (getPlugin().is1_11_R1()) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_12_R1()) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_13_R1()) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_13_R2()) {
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_14_R1()) {
      ArenaInitializer1_14_R1 initializer = (ArenaInitializer1_14_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_15_R1()) {
      ArenaInitializer1_15_R1 initializer = (ArenaInitializer1_15_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (getPlugin().is1_16_R1()) {
      ArenaInitializer1_16_R1 initializer = (ArenaInitializer1_16_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    }
  }
}
