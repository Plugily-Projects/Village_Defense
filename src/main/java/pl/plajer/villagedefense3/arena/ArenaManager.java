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

package pl.plajer.villagedefense3.arena;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense3.handlers.language.LanguageManager;
import pl.plajer.villagedefense3.handlers.language.Locale;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.level.GolemFriendKit;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.villagedefenseapi.StatsStorage;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameJoinAttemptEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameLeaveAttemptEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameStopEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageWaveEndEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageWaveStartEvent;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.InventoryUtils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class ArenaManager {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  /**
   * Attempts player to join arena.
   * Calls VillageGameJoinAttemptEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param p player to join
   * @see VillageGameJoinAttemptEvent
   */
  public static void joinAttempt(Player p, Arena arena) {
    try {
      Main.debug(Main.LogLevel.INFO, "Initial join attempt, " + p.getName());
      VillageGameJoinAttemptEvent villageGameJoinAttemptEvent = new VillageGameJoinAttemptEvent(p, arena);
      Bukkit.getPluginManager().callEvent(villageGameJoinAttemptEvent);
      if (!arena.isReady()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Arena-Not-Configured"));
        return;
      }
      if (villageGameJoinAttemptEvent.isCancelled()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
        return;
      }
      if (!plugin.isBungeeActivated()) {
        if (!(p.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", "*")) || p.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", arena.getID())))) {
          p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-No-Permission"));
          return;
        }
      }
      Main.debug(Main.LogLevel.INFO, "Final join attempt, " + p.getName());
      Main.debug(Main.LogLevel.INFO, "Join task, " + p.getName());
      arena.addPlayer(p);
      if ((arena.getArenaState() == ArenaState.IN_GAME || (arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || arena.getArenaState() == ArenaState.ENDING)) {
        if (plugin.isInventoryManagerEnabled()) {
          p.setLevel(0);
          InventoryUtils.saveInventoryToFile(plugin, p);
        }
        arena.teleportToStartLocation(p);
        p.sendMessage(ChatManager.colorMessage("In-Game.You-Are-Spectator"));
        p.getInventory().clear();

        ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
        ItemMeta spectatorMeta = spectatorItem.getItemMeta();
        spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
        spectatorItem.setItemMeta(spectatorMeta);
        p.getInventory().setItem(0, spectatorItem);

        p.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());

        for (PotionEffect potionEffect : p.getActivePotionEffects()) {
          p.removePotionEffect(potionEffect.getType());
        }

        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + arena.getRottenFleshLevel());
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(true);
        p.setFlying(true);
        User user = UserManager.getUser(p.getUniqueId());
        user.setSpectator(true);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
        ArenaUtils.hidePlayer(p, arena);

        for (Player spectator : arena.getPlayers()) {
          if (UserManager.getUser(spectator.getUniqueId()).isSpectator()) {
            p.hidePlayer(spectator);
          } else {
            p.showPlayer(spectator);
          }
        }
        ArenaUtils.hidePlayersOutsideTheGame(p, arena);
        return;
      }
      if (plugin.isInventoryManagerEnabled()) {
        p.setLevel(0);
        InventoryUtils.saveInventoryToFile(plugin, p);
      }
      arena.teleportToLobby(p);
      p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      p.setFoodLevel(20);
      p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
      p.setFlying(false);
      p.setAllowFlight(false);
      p.getInventory().clear();
      arena.showPlayers();
      arena.doBarAction(Arena.BarAction.ADD, p);
      if (!UserManager.getUser(p.getUniqueId()).isSpectator()) {
        ChatManager.broadcastAction(arena, p, ChatManager.ActionType.JOIN);
      }
      User user = UserManager.getUser(p.getUniqueId());
      user.setKit(KitRegistry.getDefaultKit());
      plugin.getKitManager().giveKitMenuItem(p);
      if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
        p.getInventory().setItem(SpecialItemManager.getSpecialItem("Leave").getSlot(), SpecialItemManager.getSpecialItem("Leave").getItemStack());
      }
      p.updateInventory();
      for (Player player : arena.getPlayers()) {
        ArenaUtils.showPlayer(player, arena);
      }
      arena.showPlayers();
      Main.debug(Main.LogLevel.INFO, "Join task end, " + p.getName());
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  /**
   * Attempts player to leave arena.
   * Calls VillageGameLeaveAttemptEvent event.
   *
   * @param p player to join
   * @see VillageGameLeaveAttemptEvent
   */
  public static void leaveAttempt(Player p, Arena arena) {
    try {
      Main.debug(Main.LogLevel.INFO, "Initial leave attempt, " + p.getName());
      VillageGameLeaveAttemptEvent villageGameLeaveAttemptEvent = new VillageGameLeaveAttemptEvent(p, arena);
      Bukkit.getPluginManager().callEvent(villageGameLeaveAttemptEvent);
      User user = UserManager.getUser(p.getUniqueId());
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
      p.getInventory().clear();
      p.getInventory().setArmorContents(null);
      arena.removePlayer(p);
      if (!user.isSpectator()) {
        ChatManager.broadcastAction(arena, p, ChatManager.ActionType.LEAVE);
      }
      p.setGlowing(false);
      user.setSpectator(false);
      user.removeScoreboard();
      if (user.getKit() instanceof GolemFriendKit) {
        for (IronGolem ironGolem : arena.getIronGolems()) {
          if (ironGolem.getCustomName().contains(user.toPlayer().getName())) {
            ironGolem.remove();
          }
        }
      }
      arena.doBarAction(Arena.BarAction.REMOVE, p);
      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
      p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      p.setFoodLevel(20);
      p.setFlying(false);
      p.setAllowFlight(false);
      for (PotionEffect effect : p.getActivePotionEffects()) {
        p.removePotionEffect(effect.getType());
      }
      p.setFireTicks(0);
      if (arena.getPlayers().size() == 0) {
        arena.setArenaState(ArenaState.ENDING);
        arena.setTimer(0);
      }

      p.setGameMode(GameMode.SURVIVAL);
      for (Player players : plugin.getServer().getOnlinePlayers()) {
        if (ArenaRegistry.getArena(players) != null) {
          players.showPlayer(p);
        }
        p.showPlayer(players);
      }
      arena.teleportToEndLocation(p);
      if (!plugin.isBungeeActivated() && plugin.isInventoryManagerEnabled()) {
        InventoryUtils.loadInventory(plugin, p);
      }
      Main.debug(Main.LogLevel.INFO, "Final leave attempt, " + p.getName());
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  /**
   * Stops current arena. Calls VillageGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @see VillageGameStopEvent
   */
  public static void stopGame(boolean quickStop, Arena arena) {
    try {
      Main.debug(Main.LogLevel.INFO, "Game stop event initiate, arena " + arena.getID());
      VillageGameStopEvent villageGameStopEvent = new VillageGameStopEvent(arena);
      Bukkit.getPluginManager().callEvent(villageGameStopEvent);
      String summaryEnding;
      if (arena.getPlayersLeft().size() > 0) {
        summaryEnding = ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Summary-Villagers-Died");
      } else {
        summaryEnding = ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Summary-Players-Died");
      }
      List<String> summaryMessages;
      if (LanguageManager.getPluginLocale() == Locale.ENGLISH) {
        summaryMessages = LanguageManager.getLanguageFile().getStringList("In-Game.Messages.Game-End-Messages.Summary-Message");
      } else {
        summaryMessages = Arrays.asList(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Summary-Message").split(";"));
      }
      for (final Player p : arena.getPlayers()) {
        User user = UserManager.getUser(p.getUniqueId());
        if (user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE) <= arena.getWave()) {
          user.setStat(StatsStorage.StatisticType.HIGHEST_WAVE, arena.getWave());
        }
        for (String msg : summaryMessages) {
          MessageUtils.sendCenteredMessage(p, formatSummaryPlaceholders(msg, arena, user, summaryEnding));
        }
        arena.addExperience(p, arena.getWave());

        UserManager.getUser(p.getUniqueId()).removeScoreboard();
        if (!quickStop) {
          if (plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
            new BukkitRunnable() {
              int i = 0;

              public void run() {
                if (i == 4 || !arena.getPlayers().contains(p)) {
                  this.cancel();
                }
                MinigameUtils.spawnRandomFirework(p.getLocation());
                i++;
              }
            }.runTaskTimer(plugin, 30, 30);
          }
        }
      }
      arena.setRottenFleshAmount(0);
      arena.setRottenFleshLevel(0);
      arena.restoreDoors();
      for (Zombie zombie : arena.getZombies()) {
        zombie.remove();
      }
      arena.getZombies().clear();
      for (IronGolem ironGolem : arena.getIronGolems()) {
        ironGolem.remove();
      }
      arena.getIronGolems().clear();
      for (Villager villager : arena.getVillagers()) {
        villager.remove();
      }
      arena.getVillagers().clear();
      for (Wolf wolf : arena.getWolfs()) {
        wolf.remove();
      }
      arena.getWolfs().clear();

      if (arena.getVillagers().size() <= 0) {
        arena.showPlayers();
        arena.setTimer(10);
      } else {
        arena.setTimer(5);
      }
      arena.setArenaState(ArenaState.ENDING);
      Main.debug(Main.LogLevel.INFO, "Game stop event finish, arena " + arena.getID());
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  private static String formatSummaryPlaceholders(String msg, Arena a, User user, String summary) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%summary%", summary);
    formatted = StringUtils.replace(formatted, "%wave%", String.valueOf(a.getWave()));
    formatted = StringUtils.replace(formatted, "%player_best_wave%", String.valueOf(user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE)));
    formatted = StringUtils.replace(formatted, "%zombies%", String.valueOf(a.getTotalKilledZombies()));
    formatted = StringUtils.replace(formatted, "%orbs_spent%", String.valueOf(a.getTotalOrbsSpent()));
    return formatted;
  }

  /**
   * End wave in game.
   * Calls VillageWaveEndEvent event
   *
   * @see VillageWaveEndEvent
   */
  public static void endWave(Arena arena) {
    try {
      plugin.getRewardsHandler().performEndWaveRewards(arena, arena.getWave());
      arena.setTimer(plugin.getConfig().getInt("Cooldown-Before-Next-Wave", 25));
      arena.getZombieCheckerLocations().clear();
      arena.setWave(arena.getWave() + 1);
      VillageWaveEndEvent villageWaveEndEvent = new VillageWaveEndEvent(arena, arena.getWave());
      Bukkit.getPluginManager().callEvent(villageWaveEndEvent);
      for (Player player : arena.getPlayers()) {
        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Next-Wave-In"), arena.getTimer()));
        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.You-Feel-Refreshed"));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        User user = UserManager.getUser(player.getUniqueId());
        user.addStat(StatsStorage.StatisticType.ORBS, arena.getWave() * 10);
      }
      if (plugin.getConfig().getBoolean("Respawn-After-Wave", true)) {
        ArenaUtils.bringDeathPlayersBack(arena);
      }
      for (Player player : arena.getPlayersLeft()) {
        arena.addExperience(player, 5);
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

  /**
   * Starts wave in game.
   * Calls VillageWaveStartEvent event
   *
   * @see VillageWaveStartEvent
   */
  public static void startWave(Arena arena) {
    try {
      VillageWaveStartEvent villageWaveStartEvent = new VillageWaveStartEvent(arena, arena.getWave());
      Bukkit.getPluginManager().callEvent(villageWaveStartEvent);
      arena.setZombieAmount();
      if (plugin.getConfig().getBoolean("Respawn-After-Wave", true)) {
        ArenaUtils.bringDeathPlayersBack(arena);
      }
      for (User user : UserManager.getUsers(arena)) {
        user.getKit().reStock(user.toPlayer());
      }
      String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Wave-Started"), arena.getWave());
      for (Player p : arena.getPlayers()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
      }
    } catch (Exception e) {
      new ReportedException(plugin, e);
    }
  }

}
