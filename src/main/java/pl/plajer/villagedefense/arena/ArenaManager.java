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

package pl.plajer.villagedefense.arena;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.api.event.game.VillageGameJoinAttemptEvent;
import pl.plajer.villagedefense.api.event.game.VillageGameLeaveAttemptEvent;
import pl.plajer.villagedefense.api.event.game.VillageGameStopEvent;
import pl.plajer.villagedefense.api.event.wave.VillageWaveEndEvent;
import pl.plajer.villagedefense.api.event.wave.VillageWaveStartEvent;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.handlers.reward.Reward;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.level.GolemFriendKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

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
    Debugger.debug(Debugger.Level.INFO, "Initial join attempt, " + p.getName());
    if (!canJoinArenaAndMessage(p, arena)) {
      return;
    }
    Debugger.debug(Debugger.Level.INFO, "Final join attempt, " + p.getName());
    Debugger.debug(Debugger.Level.INFO, "Join task, " + p.getName());
    arena.getPlayers().add(p);
    User user = plugin.getUserManager().getUser(p);
    arena.getScoreboardManager().createScoreboard(user);
    if ((arena.getArenaState() == ArenaState.IN_GAME || (arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || arena.getArenaState() == ArenaState.ENDING)) {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
        InventorySerializer.saveInventoryToFile(plugin, p);
      }
      p.teleport(arena.getStartLocation());
      p.sendMessage(plugin.getChatManager().colorMessage(Messages.YOU_ARE_SPECTATOR));
      p.getInventory().clear();

      p.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(plugin.getChatManager().colorMessage(Messages.SPECTATOR_ITEM_NAME)).build());
      p.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(plugin.getChatManager().colorMessage(Messages.SPECTATOR_SETTINGS_MENU_ITEM_NAME)).build());
      p.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());

      for (PotionEffect potionEffect : p.getActivePotionEffects()) {
        p.removePotionEffect(potionEffect.getType());
      }

      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + arena.getOption(ArenaOption.ROTTEN_FLESH_LEVEL));
      p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      p.setFoodLevel(20);
      p.setGameMode(GameMode.SURVIVAL);
      p.setAllowFlight(true);
      p.setFlying(true);
      user.setSpectator(true);
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
      p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
      ArenaUtils.hidePlayer(p, arena);

      for (Player spectator : arena.getPlayers()) {
        if (plugin.getUserManager().getUser(spectator).isSpectator()) {
          p.hidePlayer(spectator);
        } else {
          p.showPlayer(spectator);
        }
      }
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, p);
    }
    p.teleport(arena.getLobbyLocation());
    p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    p.setFoodLevel(20);
    p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
    p.setFlying(false);
    p.setAllowFlight(false);
    p.getInventory().clear();
    arena.doBarAction(Arena.BarAction.ADD, p);
    if (!plugin.getUserManager().getUser(p).isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, p, ChatManager.ActionType.JOIN);
    }
    user.setKit(KitRegistry.getDefaultKit());
    plugin.getKitManager().giveKitMenuItem(p);
    if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      p.getInventory().setItem(SpecialItemManager.getSpecialItem("Leave").getSlot(), SpecialItemManager.getSpecialItem("Leave").getItemStack());
    }
    p.updateInventory();
    for (Player player : arena.getPlayers()) {
      ArenaUtils.showPlayer(player, arena);
      player.setExp(1);
      player.setLevel(0);
    }
    Debugger.debug(Debugger.Level.INFO, "Join task end, " + p.getName());
  }

  private static boolean canJoinArenaAndMessage(Player p, Arena arena) {
    if (!arena.isReady()) {
      p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ARENA_NOT_CONFIGURED));
      return false;
    }

    VillageGameJoinAttemptEvent event = new VillageGameJoinAttemptEvent(p, arena);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.JOIN_CANCELLED_VIA_API));
      return false;
    }

    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      if (!(p.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", "*")) || p.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", arena.getId())))) {
        p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.JOIN_NO_PERMISSION));
        return false;
      }
    }
    return true;
  }

  /**
   * Attempts player to leave arena.
   * Calls VillageGameLeaveAttemptEvent event.
   *
   * @param player player to join
   * @see VillageGameLeaveAttemptEvent
   */
  public static void leaveAttempt(Player player, Arena arena) {
    player.setExp(0);
    player.setLevel(0);
    Debugger.debug(Debugger.Level.INFO, "Initial leave attempt, " + player.getName());
    VillageGameLeaveAttemptEvent villageGameLeaveAttemptEvent = new VillageGameLeaveAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(villageGameLeaveAttemptEvent);
    User user = plugin.getUserManager().getUser(player);
    user.setStat(StatsStorage.StatisticType.ORBS, 0);
    arena.getScoreboardManager().removeScoreboard(user);
    arena.getPlayers().remove(player);
    if (!user.isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.LEAVE);
    }
    user.setSpectator(false);
    if (user.getKit() instanceof GolemFriendKit) {
      for (IronGolem ironGolem : arena.getIronGolems()) {
        if (ironGolem.getCustomName().contains(user.getPlayer().getName())) {
          ironGolem.remove();
        }
      }
    }
    arena.doBarAction(Arena.BarAction.REMOVE, player);
    if (arena.getPlayers().isEmpty() && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
      arena.setArenaState(ArenaState.ENDING);
      arena.setTimer(0);
    }
    ArenaUtils.resetPlayerAfterGame(player);
    arena.teleportToEndLocation(player);
    Debugger.debug(Debugger.Level.INFO, "Final leave attempt, " + player.getName());
  }

  /**
   * Stops current arena. Calls VillageGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @see VillageGameStopEvent
   */
  public static void stopGame(boolean quickStop, Arena arena) {
    Debugger.debug(Debugger.Level.INFO, "Game stop event initiate, arena " + arena.getId());
    VillageGameStopEvent villageGameStopEvent = new VillageGameStopEvent(arena);
    Bukkit.getPluginManager().callEvent(villageGameStopEvent);
    String summaryEnding;
    if (plugin.getConfig().getBoolean("Wave-Limit.Enabled", false) && arena.getWave() >= plugin.getConfig().getInt("Wave-Limit.Limit", 25)) {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_WIN_GAME);
    } else if (!arena.getPlayersLeft().isEmpty()) {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_VILLAGERS_DIED);
    } else {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_PLAYERS_DIED);
    }
    List<String> summaryMessages = LanguageManager.getLanguageList("In-Game.Messages.Game-End-Messages.Summary-Message");
    for (Player player : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(player);
      if (user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE) <= arena.getWave()) {
        user.setStat(StatsStorage.StatisticType.HIGHEST_WAVE, arena.getWave());
      }
      for (String msg : summaryMessages) {
        MiscUtils.sendCenteredMessage(player, formatSummaryPlaceholders(msg, arena, user, summaryEnding));
      }
      ArenaUtils.addExperience(player, arena.getWave());

      if (!quickStop) {
        spawnFireworks(arena, player);
      }
    }
    arena.getScoreboardManager().stopAllScoreboards();
    arena.setOptionValue(ArenaOption.ROTTEN_FLESH_AMOUNT, 0);
    arena.setOptionValue(ArenaOption.ROTTEN_FLESH_LEVEL, 0);
    arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, 0);
    arena.setTimer(arena.getVillagers().isEmpty() ? 10 : 5);
    arena.getMapRestorerManager().fullyRestoreArena();
    arena.setArenaState(ArenaState.ENDING);
    Debugger.debug(Debugger.Level.INFO, "Game stop event finish, arena " + arena.getId());
  }

  private static String formatSummaryPlaceholders(String msg, Arena a, User user, String summary) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%summary%", summary);
    formatted = StringUtils.replace(formatted, "%wave%", String.valueOf(a.getWave()));
    formatted = StringUtils.replace(formatted, "%player_best_wave%", String.valueOf(user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE)));
    formatted = StringUtils.replace(formatted, "%zombies%", String.valueOf(a.getOption(ArenaOption.TOTAL_KILLED_ZOMBIES)));
    formatted = StringUtils.replace(formatted, "%orbs_spent%", String.valueOf(a.getOption(ArenaOption.TOTAL_ORBS_SPENT)));
    return formatted;
  }

  private static void spawnFireworks(Arena arena, Player p) {
    if (!plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      public void run() {
        if (i == 4 || !arena.getPlayers().contains(p)) {
          this.cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(p.getLocation());
        i++;
      }
    }.runTaskTimer(plugin, 30, 30);
  }

  /**
   * End wave in game.
   * Calls VillageWaveEndEvent event
   *
   * @see VillageWaveEndEvent
   */
  public static void endWave(Arena arena) {
    if (plugin.getConfig().getBoolean("Wave-Limit.Enabled", false) && arena.getWave() >= plugin.getConfig().getInt("Wave-Limit.Limit", 25)) {
      stopGame(false, arena);
      return;
    }
    plugin.getRewardsHandler().performReward(arena, Reward.RewardType.END_WAVE);
    arena.setTimer(plugin.getConfig().getInt("Cooldown-Before-Next-Wave", 25));
    arena.getZombieSpawnManager().getZombieCheckerLocations().clear();
    arena.setWave(arena.getWave() + 1);
    Bukkit.getPluginManager().callEvent(new VillageWaveEndEvent(arena, arena.getWave()));
    refreshAllPlayers(arena);
    if (plugin.getConfig().getBoolean("Respawn-After-Wave", true)) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }
    for (Player player : arena.getPlayersLeft()) {
      ArenaUtils.addExperience(player, 5);
    }
  }

  private static void refreshAllPlayers(Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.NEXT_WAVE_IN), arena.getTimer()));
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.YOU_FEEL_REFRESHED));
      player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      User user = plugin.getUserManager().getUser(player);
      user.addStat(StatsStorage.StatisticType.ORBS, arena.getWave() * 10);
    }
  }

  /**
   * Starts wave in game.
   * Calls VillageWaveStartEvent event
   *
   * @see VillageWaveStartEvent
   */
  public static void startWave(Arena arena) {
    VillageWaveStartEvent event = new VillageWaveStartEvent(arena, arena.getWave());
    Bukkit.getPluginManager().callEvent(event);
    int zombiesAmount = (int) Math.ceil((arena.getPlayers().size() * 0.5) * (arena.getOption(ArenaOption.WAVE) * arena.getOption(ArenaOption.WAVE)) / 2);
    if (zombiesAmount > 750) {
      arena.setOptionValue(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER, (int) Math.ceil((zombiesAmount - 750.0) / 15));
      Debugger.debug(Debugger.Level.INFO, "Detected abnormal wave (" + arena.getWave() + ") in arena " + arena.getId() + "! Applying zombie limit and difficulty multiplier to "
          + arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
      zombiesAmount = 750;
    }
    arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesAmount);
    arena.setOptionValue(ArenaOption.ZOMBIE_IDLE_PROCESS, (int) Math.floor((double) arena.getWave() / 15));
    if (arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS) > 0) {
      Debugger.debug(Debugger.Level.INFO, "Spawn idle process initiated for arena " + arena.getId() + " to prevent server overload! Value: " + arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS));
    }
    if (plugin.getConfig().getBoolean("Respawn-After-Wave", true)) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }
    for (User user : plugin.getUserManager().getUsers(arena)) {
      user.getKit().reStock(user.getPlayer());
    }
    plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.WAVE_STARTED), arena.getWave()));
  }

}
