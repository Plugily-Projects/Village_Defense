/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.event.wave.VillageWaveEndEvent;
import plugily.projects.villagedefense.api.event.wave.VillageWaveStartEvent;
import plugily.projects.villagedefense.arena.assist.AssistHandler;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class ArenaManager extends PluginArenaManager {

  private final Main plugin;

  public ArenaManager(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  public void additionalSpectatorSettings(Player player, PluginArena arena) {
    super.additionalSpectatorSettings(player, arena);
    if(!plugin.getConfigPreferences().getOption("RESPAWN_IN_GAME_JOIN")) {
      plugin.getUserManager().getUser(player).setPermanentSpectator(true);
    }
  }

  @Override
  public void leaveAttempt(@NotNull Player player, @NotNull PluginArena arena) {
    player.removeMetadata(AssistHandler.ASSIST_CONTAINER_METADATA, plugin);
    Arena gameArena = (Arena) arena;
    List<Creature> pets = new ArrayList<>();
    pets.addAll(gameArena.getIronGolems());
    pets.addAll(gameArena.getWolves());
    pets
      .stream()
      .filter(Objects::nonNull)
      .filter(pet -> pet.hasMetadata("VD_OWNER_UUID"))
      .filter(pet -> UUID.fromString(pet.getMetadata("VD_OWNER_UUID").get(0).asString()).equals(player.getUniqueId()))
      .forEach(pet -> {
        if(pet instanceof IronGolem) {
          gameArena.removeIronGolem((IronGolem) pet);
        } else {
          gameArena.removeWolf((Wolf) pet);
        }
      });
    super.leaveAttempt(player, arena);
  }

  @Override
  public void stopGame(boolean quickStop, @NotNull PluginArena arena) {
    int wave = ((Arena) arena).getWave();
    for(Player player : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(player);
      if(!quickStop) {
        if(user.getStatistic("HIGHEST_WAVE") <= wave) {
          if(user.isSpectator() && !plugin.getConfigPreferences().getOption("RESPAWN_AFTER_WAVE")) {
            continue;
          }
          user.setStatistic("HIGHEST_WAVE", wave);
        }
        if(plugin.getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= plugin.getConfig().getInt("Limit.Wave.Game-End", 25)) {
          plugin.getUserManager().addStat(user, plugin.getStatsStorage().getStatisticType("WINS"));
          XSound.ENTITY_VILLAGER_YES.play(player);
        } else {
          plugin.getUserManager().addStat(user, plugin.getStatsStorage().getStatisticType("LOSES"));
          XSound.ENTITY_VILLAGER_NO.play(player);
        }
        plugin.getUserManager().addExperience(player, wave);
      }
    }
    super.stopGame(quickStop, arena);
  }

  /**
   * End wave in game.
   * Calls VillageWaveEndEvent event
   *
   * @param arena End wave on which arena
   * @see VillageWaveEndEvent
   */
  public void endWave(@NotNull Arena arena) {
    int wave = arena.getWave();

    if(plugin.getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= plugin.getConfig().getInt("Limit.Wave.Game-End", 25)) {
      stopGame(false, arena);
      return;
    }

    new TitleBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_TITLE_END").asKey().arena(arena).integer(wave).sendArena();

    for(User user : plugin.getUserManager().getUsers(arena)) {
      if(!user.isSpectator() && !user.isPermanentSpectator()) {
        Player player = user.getPlayer();
        plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("END_WAVE"));
      }
      XSound.ENTITY_VILLAGER_YES.play(user.getPlayer());
    }

    arena.setTimer(plugin.getConfig().getInt("Time-Manager.Cooldown-Before-Next-Wave", 25));
    arena.getEnemySpawnManager().getEnemyCheckerLocations().clear();
    arena.setWave(wave + 1);

    Bukkit.getPluginManager().callEvent(new VillageWaveEndEvent(arena, arena.getWave()));

    refreshAllPlayers(arena);
    refreshVillagers(arena);
    removeAllAssists(arena);

    if(plugin.getConfigPreferences().getOption("RESPAWN_AFTER_WAVE")) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }

    for(Player player : arena.getPlayersLeft()) {
      plugin.getUserManager().addExperience(player, 5);
    }
  }

  private void refreshAllPlayers(Arena arena) {
    int waveStat = arena.getWave() * 10;

    String nextWave = new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_NEXT_IN").asKey().arena(arena).integer(arena.getTimer()).build();
    for(Player player : arena.getPlayers()) {
      int healPower = (int) Math.ceil(VersionUtils.getMaxHealth(player) * 0.25); //25% of max health rounded up
      player.sendMessage(nextWave);
      player.setHealth(Math.min(player.getHealth() + healPower, VersionUtils.getMaxHealth(player)));

      plugin.getUserManager().getUser(player).adjustStatistic(plugin.getStatsStorage().getStatisticType("ORBS"), waveStat);
    }
  }

  private void refreshVillagers(Arena arena) {
    int healPower = 2; //1 heart
    for(Villager villager : arena.getVillagers()) {
      villager.setHealth(Math.min(villager.getHealth() + healPower, VersionUtils.getMaxHealth(villager)));
      villager.setCustomName(CreatureUtils.getHealthNameTag(villager));
    }
  }

  private void removeAllAssists(Arena arena) {
    List<LivingEntity> allEntities = new ArrayList<>();
    allEntities.addAll(arena.getPlayers());
    allEntities.addAll(arena.getWolves());
    allEntities.addAll(arena.getIronGolems());
    for(LivingEntity livingEntity : allEntities) {
      livingEntity.removeMetadata(AssistHandler.ASSIST_CONTAINER_METADATA, plugin);
    }
  }

  /**
   * Starts wave in game.
   * Calls VillageWaveStartEvent event
   *
   * @param arena start wave on this arena
   * @see VillageWaveStartEvent
   */
  public void startWave(@NotNull Arena arena) {
    plugin.getDebugger().debug("[{0}] Wave start event called", arena.getId());
    long start = System.currentTimeMillis();

    int wave = arena.getWave();

    Bukkit.getPluginManager().callEvent(new VillageWaveStartEvent(arena, wave));

    int zombiesAmount = (int) Math.ceil((arena.getPlayers().size() * 0.5) * (wave * wave) / 2);
    int maxzombies = plugin.getConfig().getInt("Limit.Spawn.Creatures", 75);

    if(zombiesAmount > maxzombies) {
      int multiplier = (int) Math.ceil((zombiesAmount - (double) maxzombies) / plugin.getConfig().getInt("Creatures.Multiplier-Divider", 18));

      if(multiplier < 2) multiplier = 2;

      arena.setArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER", multiplier);

      plugin.getDebugger().debug("[{0}] Detected abnormal wave ({1})! Applying zombie limit and difficulty multiplier to {2} | ZombiesAmount: {3} | MaxZombies: {4}",
        arena.getId(), wave, arena.getArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER"), zombiesAmount, maxzombies);

      zombiesAmount = maxzombies;
    }

    int zombieIdle = (int) Math.floor((double) wave / 15);

    arena.setArenaOption("ZOMBIES_TO_SPAWN", zombiesAmount);
    arena.setArenaOption("ZOMBIE_IDLE_PROCESS", zombieIdle);

    if(zombieIdle > 0) {
      plugin.getDebugger().debug("[{0}] Spawn idle process initiated to prevent server overload! Value: {1}", arena.getId(), zombieIdle);
    }

    if(plugin.getConfigPreferences().getOption("RESPAWN_AFTER_WAVE")) {
      ArenaUtils.bringDeathPlayersBack(arena);
    }

    new TitleBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_TITLE_START").asKey().arena(arena).integer(wave).sendArena();

    for(User user : plugin.getUserManager().getUsers(arena)) {
      Player player = user.getPlayer();
      if(!user.isSpectator()) {
        user.getKit().reStock(player);
      }
      plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("START_WAVE"));

      new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_STARTED").asKey().arena(arena).integer(wave).player(player).sendPlayer();
    }

    plugin.getDebugger().debug("[{0}] Wave start event finished took {1}ms", arena.getId(), System.currentTimeMillis() - start);
  }

}
