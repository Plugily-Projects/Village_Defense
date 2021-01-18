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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.initializers.*;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.LevelKit;
import plugily.projects.villagedefense.utils.Utils;

import java.util.List;

/**
 * Created by Tom on 21/07/2015.
 */
public class GolemFriendKit extends LevelKit {

  public GolemFriendKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_GOLEM_FRIEND_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_GOLEM_FRIEND_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
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
    if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1)) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_12_R1)) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_13_R1)) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_13_R2)) {
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_14_R1)) {
      ArenaInitializer1_14_R1 initializer = (ArenaInitializer1_14_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_15_R1)) {
      ArenaInitializer1_15_R1 initializer = (ArenaInitializer1_15_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_16_R1)) {
      ArenaInitializer1_16_R1 initializer = (ArenaInitializer1_16_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_16_R2)) {
      ArenaInitializer1_16_R2 initializer = (ArenaInitializer1_16_R2) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else {
      ArenaInitializer1_16_R3 initializer = (ArenaInitializer1_16_R3) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    }
  }
}
