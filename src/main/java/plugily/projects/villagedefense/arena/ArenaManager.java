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

package plugily.projects.villagedefense.arena;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.api.event.game.VillageGameJoinAttemptEvent;
import plugily.projects.villagedefense.api.event.game.VillageGameLeaveAttemptEvent;
import plugily.projects.villagedefense.api.event.game.VillageGameStopEvent;
import plugily.projects.villagedefense.api.event.wave.VillageWaveEndEvent;
import plugily.projects.villagedefense.api.event.wave.VillageWaveStartEvent;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.ChatManager;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.items.SpecialItem;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.party.GameParty;
import plugily.projects.villagedefense.handlers.reward.Reward;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.level.GolemFriendKit;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Debugger;

import java.util.List;

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
   * @param arena  arena to join
   * @see VillageGameJoinAttemptEvent
   */
  public static void joinAttempt(@NotNull Player player, @NotNull Arena arena) {
    Debugger.debug("[{0}] Initial join attempt for {1}", arena.getId(), player.getName());
    if(!canJoinArenaAndMessage(player, arena) || !checkFullGamePermission(player, arena)) {
      return;
    }
    Debugger.debug("[{0}] Checked join attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();
    if(ArenaRegistry.isInArena(player)) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ALREADY_PLAYING));
      return;
    }
    //check if player is in party and send party members to the game
    if(plugin.getPartyHandler().isPlayerInParty(player)) {
      GameParty party = plugin.getPartyHandler().getParty(player);
      if(party.getLeader() != null && player.getUniqueId().equals(party.getLeader().getUniqueId())) {
        if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
          for(Player partyPlayer : party.getPlayers()) {
            if(player.getUniqueId().equals(partyPlayer.getUniqueId())) {
              continue;
            }
            if(ArenaRegistry.isInArena(partyPlayer)) {
              if(ArenaRegistry.getArena(partyPlayer).getArenaState() == ArenaState.IN_GAME) {
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
    if((arena.getArenaState() == ArenaState.IN_GAME || (arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || arena.getArenaState() == ArenaState.ENDING)) {
      if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INGAME_JOIN_RESPAWN)) {
        user.setPermanentSpectator(true);
      }
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
        InventorySerializer.saveInventoryToFile(plugin, player);
      }
      player.teleport(arena.getStartLocation());
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.YOU_ARE_SPECTATOR));
      player.getInventory().clear();

      for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
        if(item.getDisplayStage() == SpecialItem.DisplayStage.SPECTATOR) {
          player.getInventory().setItem(item.getSlot(), item.getItemStack());
        }
      }

      player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
      VersionUtils.setMaxHealth(player, VersionUtils.getMaxHealth(player) + arena.getOption(ArenaOption.ROTTEN_FLESH_LEVEL));
      player.setHealth(VersionUtils.getMaxHealth(player));
      player.setFoodLevel(20);
      player.setGameMode(GameMode.SURVIVAL);
      player.setAllowFlight(true);
      player.setFlying(true);
      user.setSpectator(true);
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
      ArenaUtils.hidePlayer(player, arena);

      for(Player spectator : arena.getPlayers()) {
        if(plugin.getUserManager().getUser(spectator).isSpectator()) {
          VersionUtils.hidePlayer(plugin, player, spectator);
        } else {
          VersionUtils.showPlayer(plugin, player, spectator);
        }
      }
      Debugger.debug("[{0}] Final join attempt as spectator for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
      return;
    }
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, player);
    }
    player.teleport(arena.getLobbyLocation());
    player.setHealth(VersionUtils.getMaxHealth(player));
    player.setFoodLevel(20);
    player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    arena.doBarAction(Arena.BarAction.ADD, player);
    if(!user.isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.JOIN);
    }
    user.setKit(KitRegistry.getDefaultKit());
    for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
      if(item.getDisplayStage() == SpecialItem.DisplayStage.LOBBY) {
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }
    }
    player.updateInventory();
    for(Player arenaPlayer : arena.getPlayers()) {
      ArenaUtils.showPlayer(arenaPlayer, arena);
      arenaPlayer.setExp(1);
      arenaPlayer.setLevel(0);
    }
    plugin.getSignManager().updateSigns();
    Debugger.debug("[{0}] Final join attempt as player for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  private static boolean checkFullGamePermission(Player player, Arena arena) {
    if(arena.getPlayers().size() + 1 <= arena.getMaximumPlayers()) {
      return true;
    }
    if(!PermissionsManager.isPremium(player) || !player.hasPermission(PermissionsManager.getJoinFullGames())) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.FULL_GAME_NO_PERMISSION));
      return false;
    }
    for(Player players : arena.getPlayers()) {
      if(PermissionsManager.isPremium(players) || players.hasPermission(PermissionsManager.getJoinFullGames())) {
        continue;
      }
      if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
        leaveAttempt(players, arena);
        players.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_YOU_WERE_KICKED_FOR_PREMIUM_SLOT));
        plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_KICKED_FOR_PREMIUM_SLOT), players));
        return true;
      }
      return true;
    }
    player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.NO_SLOTS_FOR_PREMIUM));
    return false;
  }

  private static boolean canJoinArenaAndMessage(Player player, Arena arena) {
    if(!arena.isReady()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ARENA_NOT_CONFIGURED));
      return false;
    }

    VillageGameJoinAttemptEvent event = new VillageGameJoinAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(event);
    if(event.isCancelled()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.JOIN_CANCELLED_VIA_API));
      return false;
    }
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      String perm = PermissionsManager.getJoinPerm();
      if(!(player.hasPermission(perm.replace("<arena>", "*")) || player.hasPermission(perm.replace("<arena>", arena.getId())))) {
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
   * @param player player to leave
   * @param arena  arena to leave
   * @see VillageGameLeaveAttemptEvent
   */
  public static void leaveAttempt(@NotNull Player player, @NotNull Arena arena) {
    Debugger.debug("[{0}] Initial leave attempt of {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();

    //the default fly speed
    player.setFlySpeed(0.1f);
    player.setExp(0);
    player.setLevel(0);

    Bukkit.getPluginManager().callEvent(new VillageGameLeaveAttemptEvent(player, arena));

    User user = plugin.getUserManager().getUser(player);
    user.setStat(StatsStorage.StatisticType.ORBS, 0);
    arena.getScoreboardManager().removeScoreboard(user);
    arena.getPlayers().remove(player);
    if(!user.isSpectator()) {
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.LEAVE);
    }
    user.setSpectator(false);
    user.setPermanentSpectator(false);

    if(user.getKit() instanceof GolemFriendKit) {
      arena.getIronGolems().stream().filter(ironGolem -> ironGolem.getCustomName().contains(user.getPlayer().getName()))
          .forEach(IronGolem::remove);
    }
    arena.doBarAction(Arena.BarAction.REMOVE, player);
    if(arena.getPlayers().isEmpty() && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != ArenaState.STARTING) {
      arena.setArenaState(ArenaState.ENDING);
      arena.setTimer(0);
      //needed as no players online and else it is auto canceled
      arena.getMapRestorerManager().fullyRestoreArena();
    }
    ArenaUtils.resetPlayerAfterGame(player);
    arena.teleportToEndLocation(player);
    plugin.getSignManager().updateSigns();
    Debugger.debug("[{0}] Final leave attempt for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  /**
   * Stops current arena. Calls VillageGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @param arena     which arena should stop
   * @see VillageGameStopEvent
   */
  public static void stopGame(boolean quickStop, @NotNull Arena arena) {
    Debugger.debug("[{0}] Game stop event start", arena.getId());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new VillageGameStopEvent(arena));

    String summaryEnding;
    if(plugin.getConfig().getBoolean("Wave-Limit.Enabled", false) && arena.getWave() >= plugin.getConfig().getInt("Wave-Limit.Limit", 25)) {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_WIN_GAME);
    } else if(!arena.getPlayersLeft().isEmpty()) {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_VILLAGERS_DIED);
    } else {
      summaryEnding = plugin.getChatManager().colorMessage(Messages.END_MESSAGES_SUMMARY_PLAYERS_DIED);
    }
    List<String> summaryMessages = LanguageManager.getLanguageList("In-Game.Messages.Game-End-Messages.Summary-Message");
    for(Player player : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(player);
      if(user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE) <= arena.getWave()) {
        if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.RESPAWN_AFTER_WAVE) && user.isSpectator()) {
          continue;
        }
        user.setStat(StatsStorage.StatisticType.HIGHEST_WAVE, arena.getWave());
      }
      for(String msg : summaryMessages) {
        MiscUtils.sendCenteredMessage(player, formatSummaryPlaceholders(msg, arena, user, summaryEnding));
      }
      plugin.getUserManager().addExperience(player, arena.getWave());
      if(!quickStop) {
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
    Debugger.debug("[{0}] Game stop event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
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
    if(!plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      @Override
      public void run() {
        if(i == 4 || !arena.getPlayers().contains(player) || arena.getArenaState() == ArenaState.RESTARTING) {
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
   * @param arena End wave on which arena
   * @see VillageWaveEndEvent
   */
  public static void endWave(@NotNull Arena arena) {
    if(plugin.getConfig().getBoolean("Wave-Limit.Enabled", false) && arena.getWave() >= plugin.getConfig().getInt("Wave-Limit.Limit", 25)) {
      stopGame(false, arena);
      return;
    }

    String titleTimes = plugin.getConfig().getString("Wave-Title-Messages.EndWave.Times", "20, 30, 20");
    String[] split = titleTimes.split(", ");

    int fadeIn = split.length > 1 ? Integer.parseInt(split[0]) : 20,
        stay = split.length > 2 ? Integer.parseInt(split[1]) : 30,
        fadeOut = split.length > 3 ? Integer.parseInt(split[2]) : 20;

    String title = plugin.getConfig().getString("Wave-Title-Messages.EndWave.Title", "");
    String subTitle = plugin.getConfig().getString("Wave-Title-Messages.EndWave.SubTitle", "");

    title = title.replace("%wave%", Integer.toString(arena.getWave()));
    subTitle = subTitle.replace("%wave%", Integer.toString(arena.getWave()));
    title = plugin.getChatManager().colorRawMessage(title);
    subTitle = plugin.getChatManager().colorRawMessage(subTitle);

    for(User user : plugin.getUserManager().getUsers(arena)) {
      if (!user.isSpectator() && !user.isPermanentSpectator()) {
        VersionUtils.sendTitles(user.getPlayer(), title, subTitle, fadeIn, stay, fadeOut);
        plugin.getRewardsHandler().performReward(user.getPlayer(), arena, Reward.RewardType.END_WAVE);
      }
    }

    arena.setTimer(plugin.getConfig().getInt("Cooldown-Before-Next-Wave", 25));
    arena.getZombieSpawnManager().getZombieCheckerLocations().clear();
    arena.setWave(arena.getWave() + 1);

    Bukkit.getPluginManager().callEvent(new VillageWaveEndEvent(arena, arena.getWave()));

    refreshAllPlayers(arena);

    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.RESPAWN_AFTER_WAVE)) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }

    for(Player player : arena.getPlayersLeft()) {
      plugin.getUserManager().addExperience(player, 5);
    }
  }

  private static void refreshAllPlayers(Arena arena) {
    for(Player player : arena.getPlayers()) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.NEXT_WAVE_IN), arena.getTimer()));
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.YOU_FEEL_REFRESHED));
      player.setHealth(VersionUtils.getMaxHealth(player));
      plugin.getUserManager().getUser(player).addStat(StatsStorage.StatisticType.ORBS, arena.getWave() * 10);
    }
  }

  /**
   * Starts wave in game.
   * Calls VillageWaveStartEvent event
   *
   * @param arena start wave on this arena
   * @see VillageWaveStartEvent
   */
  public static void startWave(@NotNull Arena arena) {
    Debugger.debug("[{0}] Wave start event called", arena.getId());
    long start = System.currentTimeMillis();

    Bukkit.getPluginManager().callEvent(new VillageWaveStartEvent(arena, arena.getWave()));

    int zombiesAmount = (int) Math.ceil((arena.getPlayers().size() * 0.5) * (arena.getOption(ArenaOption.WAVE) * arena.getOption(ArenaOption.WAVE)) / 2);
    int maxzombies = plugin.getConfig().getInt("Zombies-Limit", 75);
    if(zombiesAmount > maxzombies) {
      int multiplier = (int) Math.ceil((zombiesAmount - (double) maxzombies) / plugin.getConfig().getInt("Zombie-Multiplier-Divider", 18));
      if(multiplier < 2) multiplier = 2;
      arena.setOptionValue(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER, multiplier);
      Debugger.debug("[{0}] Detected abnormal wave ({1})! Applying zombie limit and difficulty multiplier to {2} | ZombiesAmount: {3} | MaxZombies: {4}",
          arena.getId(), arena.getWave(), arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER), zombiesAmount, maxzombies);
      zombiesAmount = maxzombies;
    }

    arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesAmount);
    arena.setOptionValue(ArenaOption.ZOMBIE_IDLE_PROCESS, (int) Math.floor((double) arena.getWave() / 15));
    if(arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS) > 0) {
      Debugger.debug("[{0}] Spawn idle process initiated to prevent server overload! Value: {1}", arena.getId(), arena.getOption(ArenaOption.ZOMBIE_IDLE_PROCESS));
    }

    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.RESPAWN_AFTER_WAVE)) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }

    String titleTimes = plugin.getConfig().getString("Wave-Title-Messages.StartWave.Times", "20, 30, 20");
    String[] split = titleTimes.split(", ");

    int fadeIn = split.length > 1 ? Integer.parseInt(split[0]) : 20,
        stay = split.length > 2 ? Integer.parseInt(split[1]) : 30,
        fadeOut = split.length > 3 ? Integer.parseInt(split[2]) : 20;

    String title = plugin.getConfig().getString("Wave-Title-Messages.StartWave.Title", "");
    String subTitle = plugin.getConfig().getString("Wave-Title-Messages.StartWave.SubTitle", "");

    title = title.replace("%wave%", Integer.toString(arena.getWave()));
    subTitle = subTitle.replace("%wave%", Integer.toString(arena.getWave()));
    title = plugin.getChatManager().colorRawMessage(title);
    subTitle = plugin.getChatManager().colorRawMessage(subTitle);

    for(User user : plugin.getUserManager().getUsers(arena)) {
      user.getKit().reStock(user.getPlayer());
      VersionUtils.sendTitles(user.getPlayer(), title, subTitle, fadeIn, stay, fadeOut);
    }

    plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.WAVE_STARTED), arena.getWave()));
    Debugger.debug("[{0}] Wave start event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

}
