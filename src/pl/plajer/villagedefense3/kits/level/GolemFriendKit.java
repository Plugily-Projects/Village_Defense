/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.kits.level;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_10_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R2;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Utils;
import pl.plajer.villagedefense3.utils.WeaponHelper;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 21/07/2015.
 */
public class GolemFriendKit extends LevelKit {

  private Main plugin;

  public GolemFriendKit(Main plugin) {
    this.plugin = plugin;
    setName(ChatManager.colorMessage("Kits.Golem-Friend.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Golem-Friend.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    setLevel(ConfigUtils.getConfig(plugin, "kits").getInt("Required-Level.GolemFriend"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.hasPermission("villagefense.kit.golemfriend");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
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
    if (plugin.is1_9_R1()) {
      ArenaInitializer1_9_R1 initializer = (ArenaInitializer1_9_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (plugin.is1_10_R1()) {
      ArenaInitializer1_10_R1 initializer = (ArenaInitializer1_10_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (plugin.is1_11_R1()) {
      ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (plugin.is1_12_R1()) {
      ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (plugin.is1_13_R1()) {
      ArenaInitializer1_13_R1 initializer = (ArenaInitializer1_13_R1) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    } else if (plugin.is1_13_R2()){
      ArenaInitializer1_13_R2 initializer = (ArenaInitializer1_13_R2) arena;
      initializer.spawnGolem(initializer.getStartLocation(), player);
    }
  }
}
