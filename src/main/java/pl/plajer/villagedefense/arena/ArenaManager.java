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
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
import pl.plajer.villagedefense.handlers.items.SpecialItem;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.handlers.party.GameParty;
import pl.plajer.villagedefense.handlers.reward.Reward;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.level.GolemFriendKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class ArenaManager {

  private static Main plugin;

  private ArenaManager() {
  }

  public static void init(Main plugin) {
    ArenaManager.plugin = plugin;
  }

  /**
   * Attempts player to join arena.
   * Calls VillageGameJoinAttemptEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param player player to join
   * @see VillageGameJoinAttemptEvent
   */
  public static void joinAttempt(@NotNull Player player, @NotNull Arena arena) {
    Debugger.debug(Level.INFO, "[{0}] Initial join attempt for {1}", arena.getId(), player.getName());
    if (!canJoinArenaAndMessage(player, arena)) {
      return;
    }
    Debugger.debug(Level.INFO, "[{0}] Checked join attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();
    if (ArenaRegistry.isInArena(player)) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ALREADY_PLAYING));
      return;
    }
    //check if player is in party and send party members to the game
    if (plugin.getPartyHandler().isPlayerInParty(player)) {
      GameParty party = plugin.getPartyHandler().getParty(player);
      if (party.getLeader().equals(player)) {
        if (arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
          for (Player partyPlayer : party.getPlayers()) {
            if (partyPlayer == player) {
              continue;
            }
            if (ArenaRegistry.isInArena(partyPlayer)) {
              if (ArenaRegistry.getArena(partyPlayer).getArenaState() == ArenaState.IN_GAME) {
                continue;
              }
              leaveAttempt(partyPlayer, ArenaRegistry.getArena(partyPlayer));
            }
            partyPlayer.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.JOIN_AS_PARTY_MEMBER), partyPlayer));
            joinAttempt(partyPlayer, arena);
          }
        } else {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.NOT_ENOUGH_SPACE_FOR_PARTY), player));
          return;
        }
      }
    }
    arena.getPlayers().add(player);
    User user = plugin.getUserManager().getUser(player);
    arena.getScoreboardManager().createScoreboard(user);
    if ((arena.getArenaState() == ArenaState.IN_GAME || (arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || arena.getArenaState() == ArenaState.ENDING)) {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
        InventorySerializer.saveInventoryToFile(plugin, player);
      }
      player.teleport(arena.getStartLocation());
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.YOU_ARE_SPECTATOR));
      player.getInventory().clear();

      for (SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
        if (item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }

      for (PotionEffect potionEffect : player.getActivePotionEffects()) {
        player.removePotionEffect(potionEffect.getType());
      }

      player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + arena.getOption(ArenaOption.ROTTEN_FLESH_LEVEL));
      player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      player.setFoodLevel(20);
      player.setGameMode(GameMode.SURVIVAL);
      player.setAllowFlight(true);
      player.setFlying(true);
      user.setSpectator(true);
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
      ArenaUtils.hidePlayer(player, arena);

      for (Player spectator : arena.getPlayers()) {
        if (plugin.getUserManager().getUser(spectator).isSpectator()) {
          player.hidePlayer(spectator);
        } else {
          player.showPlayer(spectator);
        }
      }
      Debugger.debug(Level.INFO, "[{0}] Final join attempt as spectator for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, player);
    }
    player.teleport(arena.getLobbyLocation());
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    player.setFoodLevel(20);
    player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    arena.doBarAction(Arena.BarAction.ADD, player);
    if (!plugin.getUserManager().getUser(player).isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.JOIN);
    }
    user.setKit(KitRegistry.getDefaultKit());
    for (SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
      if (item.getDisplayStage() != SpecialItem.DisplayStage.LOBBY) {
        continue;
      }
      player.getInventory().setItem(item.getSlot(), item.getItemStack());
    }
    player.updateInventory();
    for (Player arenaPlayer : arena.getPlayers()) {
      ArenaUtils.showPlayer(arenaPlayer, arena);
      arenaPlayer.setExp(1);
      arenaPlayer.setLevel(0);
    }
    Debugger.debug(Level.INFO, "[{0}] Final join attempt as player for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  private static boolean canJoinArenaAndMessage(Player player, Arena arena) {
    if (!arena.isReady()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ARENA_NOT_CONFIGURED));
      return false;
    }

    VillageGameJoinAttemptEvent event = new VillageGameJoinAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.JOIN_CANCELLED_VIA_API));
      return false;
    }

    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      String perm = PermissionsManager.getJoinPerm();
      if (!(player.hasPermission(perm.replace("<arena>", "*")) || player.hasPermission(perm.replace("<arena>", arena.getId())))) {
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.JOIN_NO_PERMISSION)
            .replace("%permission%", perm.replace("<arena>", arena.getId())));
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
  public static void leaveAttempt(@NotNull Player player, @NotNull Arena arena) {
    Debugger.debug(Level.INFO, "[{0}] Initial leave attempt of {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();

    player.setExp(0);
    player.setLevel(0);
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
    if (arena.getPlayers().isEmpty() && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != ArenaState.STARTING) {
      arena.setArenaState(ArenaState.ENDING);
      arena.setTimer(0);
    }
    ArenaUtils.resetPlayerAfterGame(player);
    arena.teleportToEndLocation(player);
    Debugger.debug(Level.INFO, "[{0}] Final leave attempt for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  /**
   * Stops current arena. Calls VillageGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @see VillageGameStopEvent
   */
  public static void stopGame(boolean quickStop, @NotNull Arena arena) {
    Debugger.debug(Level.INFO, "[{0}] Game stop event start", arena.getId());
    long start = System.currentTimeMillis();

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
    Debugger.debug(Level.INFO, "[{0}] Game stop event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

  private static String formatSummaryPlaceholders(String msg, Arena arena, User user, String summary) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%summary%", summary);
    formatted = StringUtils.replace(formatted, "%wave%", String.valueOf(arena.getWave()));
    formatted = StringUtils.replace(formatted, "%player_best_wave%", String.valueOf(user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE)));
    formatted = StringUtils.replace(formatted, "%zombies%", String.valueOf(arena.getOption(ArenaOption.TOTAL_KILLED_ZOMBIES)));
    formatted = StringUtils.replace(formatted, "%orbs_spent%", String.valueOf(arena.getOption(ArenaOption.TOTAL_ORBS_SPENT)));
    return formatted;
  }

  private static void spawnFireworks(Arena arena, Player player) {
    if (!plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      public void run() {
        if (i == 4 || !arena.getPlayers().contains(player)) {
          this.cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(player.getLocation());
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
  public static void endWave(@NotNull Arena arena) {
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
  public static void startWave(@NotNull Arena arena) {
    Debugger.debug(Level.INFO, "[{0}] Wave start event called", arena.getId());
    long start = System.currentTimeMillis();

    VillageWaveStartEvent event = new VillageWaveStartEvent(arena, arena.getWave());
    Bukkit.getPluginManager().callEvent(event);
    int zombiesAmount = (int) Math.ceil((arena.getPlayers().size() * 0.5) * (arena.getOption(ArenaOption.WAVE) * arena.getOption(ArenaOption.WAVE)) / 2);
    if (zombiesAmount > 750) {
      arena.setOptionValue(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER, (int) Math.ceil((zombiesAmount - 750.0) / 15));
      Debugger.debug(Level.WARNING, "[{0}] Detected abnormal wave ({1})! Applying zombie limit and difficulty multiplier to {2}",
          arena.getId(), arena.getWave(), arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
      zombiesAmount = 750;
    }
    arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesAmount);
    arena.setOptionValue(ArenaOption.ZOMBIE_IDLE_PROCESS, (int) Math.floor((double) arena.getWave() / 15));
    if (arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS) > 0) {
      Debugger.debug(Level.INFO, "[{0}] Spawn idle process initiated to prevent server overload! Value: {1}", arena.getId(), arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS));
    }
    if (plugin.getConfig().getBoolean("Respawn-After-Wave", true)) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }
    for (User user : plugin.getUserManager().getUsers(arena)) {
      user.getKit().reStock(user.getPlayer());
    }
    plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.WAVE_STARTED), arena.getWave()));
    Debugger.debug(Level.INFO, "[{0}] Wave start event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

}
