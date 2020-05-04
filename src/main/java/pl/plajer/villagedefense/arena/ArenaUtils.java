/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.arena;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_13_R2;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_14_R1;
import pl.plajer.villagedefense.arena.initializers.ArenaInitializer1_15_R1;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.user.User;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaUtils {

  private static Main plugin;

  private ArenaUtils() {
  }

  public static void init(Main plugin) {
    ArenaUtils.plugin = plugin;
  }

  public static void hidePlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.hidePlayer(p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.showPlayer(p);
    }
  }

  public static void resetPlayerAfterGame(Player player) {
    for (Player players : plugin.getServer().getOnlinePlayers()) {
      players.showPlayer(player);
      player.showPlayer(players);
    }
    player.setGlowing(false);
    player.setGameMode(GameMode.SURVIVAL);
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    player.setFireTicks(0);
    player.setFoodLevel(20);
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, player);
    }
  }

  public static void bringDeathPlayersBack(Arena arena) {
    for (Player player : arena.getPlayers()) {
      if (arena.getPlayersLeft().contains(player)) {
        continue;
      }
      User user = plugin.getUserManager().getUser(player);
      user.setSpectator(false);

      player.teleport(arena.getStartLocation());
      player.setFlying(false);
      player.setAllowFlight(false);
      //the default fly speed
      player.setFlySpeed(0.1f);
      player.setGameMode(GameMode.SURVIVAL);
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.removePotionEffect(PotionEffectType.SPEED);
      player.getInventory().clear();
      ArenaUtils.showPlayer(player, arena);
      user.getKit().giveKitItems(player);
      player.updateInventory();
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.BACK_IN_GAME));
    }
  }

  @Deprecated //move somewhere else
  public static void updateLevelStat(Player player, Arena arena) {
    User user = plugin.getUserManager().getUser(player);
    if (Math.pow(50.0 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5) < user.getStat(StatsStorage.StatisticType.XP)) {
      user.addStat(StatsStorage.StatisticType.LEVEL, 1);
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.YOU_LEVELED_UP), user.getStat(StatsStorage.StatisticType.LEVEL)));
    }
  }

  public static Arena initializeArena(String id) {
    Arena arena;
    if (plugin.is1_11_R1()) {
      arena = new ArenaInitializer1_11_R1(id, plugin);
    } else if (plugin.is1_12_R1()) {
      arena = new ArenaInitializer1_12_R1(id, plugin);
    } else if (plugin.is1_13_R1()) {
      arena = new ArenaInitializer1_13_R1(id, plugin);
    } else if (plugin.is1_13_R2()) {
      arena = new ArenaInitializer1_13_R2(id, plugin);
    } else if (plugin.is1_14_R1()) {
      arena = new ArenaInitializer1_14_R1(id, plugin);
    } else {
      arena = new ArenaInitializer1_15_R1(id, plugin);
    }
    return arena;
  }

  public static void setWorld(Arena arena) {
    if (plugin.is1_11_R1()) {
      ((ArenaInitializer1_11_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_12_R1()) {
      ((ArenaInitializer1_12_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_13_R1()) {
      ((ArenaInitializer1_13_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_13_R2()) {
      ((ArenaInitializer1_13_R2) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_14_R1()) {
      ((ArenaInitializer1_14_R1) arena).setWorld(arena.getStartLocation());
    } else {
      ((ArenaInitializer1_15_R1) arena).setWorld(arena.getStartLocation());
    }
  }

  @Deprecated //move somewhere else
  public static void addExperience(Player player, int i) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(StatsStorage.StatisticType.XP, i);
    if (player.hasPermission(PermissionsManager.getVip())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if (player.hasPermission(PermissionsManager.getMvp())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if (player.hasPermission(PermissionsManager.getElite())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    ArenaUtils.updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  @Deprecated //move somewhere else
  public static void addStat(Player player, StatsStorage.StatisticType stat) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(stat, 1);
    ArenaUtils.updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  public static void removeSpawnedZombies(Arena arena) {
    boolean eachThree = arena.getZombies().size() > 70;
    int i = 0;
    for (Zombie zombie : arena.getZombies()) {
      if (eachThree && (i % 3) == 0) {
        zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
      }
      zombie.remove();
      i++;
    }
  }

}
