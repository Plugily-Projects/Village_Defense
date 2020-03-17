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

package pl.plajer.villagedefense.arena.states;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.api.event.game.VillageGameStartEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.arena.ArenaUtils;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 03.06.2019
 */
public class StartingState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_STARTING_IN).replace("%time%", String.valueOf(arena.getTimer())));
    arena.getGameBar().setProgress(arena.getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60));
    for (Player player : arena.getPlayers()) {
      player.setExp((float) (arena.getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60)));
      player.setLevel(arena.getTimer());
    }
    if (arena.getPlayers().size() < arena.getMinimumPlayers() && !arena.isForceStart()) {
      arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_WAITING_FOR_PLAYERS));
      arena.getGameBar().setProgress(1.0);
      plugin.getChatManager().broadcastMessage(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_WAITING_FOR_PLAYERS), arena.getMinimumPlayers()));
      arena.setArenaState(ArenaState.WAITING_FOR_PLAYERS);
      Bukkit.getPluginManager().callEvent(new VillageGameStartEvent(arena));
      arena.setTimer(15);
      for (Player player : arena.getPlayers()) {
        player.setExp(1);
        player.setLevel(0);
      }
      return;
    }
    if (arena.getTimer() == 0 || arena.isForceStart()) {
      arena.spawnVillagers();
      Bukkit.getPluginManager().callEvent(new VillageGameStartEvent(arena));
      arena.setArenaState(ArenaState.IN_GAME);
      arena.getGameBar().setProgress(1.0);
      arena.setTimer(5);
      for (Player player : arena.getPlayers()) {
        player.teleport(arena.getStartLocation());
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        User user = plugin.getUserManager().getUser(player);
        user.setStat(StatsStorage.StatisticType.ORBS, plugin.getConfig().getInt("Orbs-Starting-Amount", 20));
        plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
        player.updateInventory();
        ArenaUtils.addExperience(player, 10);
        arena.setTimer(plugin.getConfig().getInt("Cooldown-Before-Next-Wave", 25));
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.LOBBY_MESSAGES_GAME_STARTED));
      }
      arena.setFighting(false);
    }
    if (arena.isForceStart()) {
      arena.setForceStart(false);
    }
    arena.setTimer(arena.getTimer() - 1);
  }

}
