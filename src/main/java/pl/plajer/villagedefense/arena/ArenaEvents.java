/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.options.ArenaOption;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.reward.GameReward;
import pl.plajer.villagedefense.user.User;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaEvents implements Listener {

  private Main plugin;

  public ArenaEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  //override WorldGuard build deny flag where villagers cannot be damaged
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onVillagerDamage(EntityDamageByEntityEvent e) {
    try {
      if (!(e.getEntity() instanceof Villager && e.getDamager() instanceof Zombie)) {
        return;
      }
      for (Arena a : ArenaRegistry.getArenas()) {
        if (a.getVillagers().contains(e.getEntity()) && a.getZombies().contains(e.getDamager())) {
          e.setCancelled(false);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
    try {
      if (!(e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Wolf && e.getEntity() instanceof Zombie)) {
        return;
      }
      //trick to get non player killer of zombie
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (!arena.getZombies().contains(e.getEntity())) {
          continue;
        }
        if (e.getDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
          //prevent offline player cast error
          if (((Wolf) e.getDamager()).getOwner() == null || !(((Wolf) e.getDamager()).getOwner() instanceof Player)) {
            return;
          }
          Player player = (Player) ((Wolf) e.getDamager()).getOwner();
          if (ArenaRegistry.getArena(player) != null) {
            ArenaUtils.addStat(player, StatsStorage.StatisticType.KILLS);
            ArenaUtils.addExperience(player, 2);
          }
          return;
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDieEntity(EntityDeathEvent e) {
    try {
      if (!(e.getEntity() instanceof Zombie || e.getEntity() instanceof Villager)) {
        return;
      }
      for (Arena arena : ArenaRegistry.getArenas()) {
        switch (e.getEntityType()) {
          case ZOMBIE:
            if (!arena.getZombies().contains(e.getEntity())) {
              continue;
            }
            arena.removeZombie((Zombie) e.getEntity());
            arena.addOptionValue(ArenaOption.TOTAL_KILLED_ZOMBIES, 1);
            if (ArenaRegistry.getArena(e.getEntity().getKiller()) != null) {
              ArenaUtils.addStat(e.getEntity().getKiller(), StatsStorage.StatisticType.KILLS);
              ArenaUtils.addExperience(e.getEntity().getKiller(), 2);
              plugin.getRewardsHandler().performReward(e.getEntity().getKiller(), GameReward.RewardType.ZOMBIE_KILL);
              plugin.getPowerupManager().spawnPowerup(e.getEntity().getLocation(), ArenaRegistry.getArena(e.getEntity().getKiller()));
            }
            return;
          case VILLAGER:
            if (!arena.getVillagers().contains(e.getEntity())) {
              return;
            }
            arena.getStartLocation().getWorld().strikeLightningEffect(e.getEntity().getLocation());
            arena.removeVillager((Villager) e.getEntity());
            plugin.getHolidayManager().applyHolidayDeathEffects(e.getEntity());
            plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("In-Game.Messages.Villager-Died"));
            return;
          default:
            break;
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    try {
      Arena arena = ArenaRegistry.getArena(e.getEntity());
      if (arena == null) {
        return;
      }
      if (e.getEntity().isDead()) {
        e.getEntity().setHealth(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      }
      e.setDeathMessage("");
      e.getDrops().clear();
      e.setDroppedExp(0);
      plugin.getHolidayManager().applyHolidayDeathEffects(e.getEntity());
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        e.getEntity().spigot().respawn();
        Player player = e.getEntity();
        if (arena.getArenaState() == ArenaState.STARTING) {
          player.teleport(arena.getStartLocation());
          return;
        } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
          player.getInventory().clear();
          player.setFlying(false);
          player.setAllowFlight(false);
          User user = plugin.getUserManager().getUser(player.getUniqueId());
          user.setStat(StatsStorage.StatisticType.ORBS, 0);
          player.teleport(arena.getEndLocation());
          return;
        }
        User user = plugin.getUserManager().getUser(player.getUniqueId());
        ArenaUtils.addStat(player, StatsStorage.StatisticType.DEATHS);
        arena.teleportToStartLocation(player);
        user.setSpectator(true);
        player.setGameMode(GameMode.SURVIVAL);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
        ArenaUtils.hidePlayer(player, arena);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        player.sendTitle(plugin.getChatManager().colorMessage("In-Game.Death-Screen"), null, 0, 5 * 20, 0);
        new BukkitRunnable() {
          @Override
          public void run() {
            if (arena.getArenaState() == ArenaState.ENDING) {
              this.cancel();
              return;
            }
            if (user.isSpectator()) {
              player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getChatManager().colorMessage("In-Game.Died-Respawn-In-Next-Wave")));
            } else {
              this.cancel();
            }
          }
        }.runTaskTimer(plugin, 30, 30);
        plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.DEATH);

        //running in a scheduler of 1 tick due to 1.13 bug
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
          player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Item-Name")).build());
          player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(plugin.getChatManager().colorMessage("In-Game.Spectator.Settings-Menu.Item-Name")).build());
          player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());
        }, 1);

        //tryin to untarget dead player bcuz they will still target him
        for (Zombie zombie : arena.getZombies()) {
          if (zombie.getTarget() == null || !zombie.getTarget().equals(player)) {
            continue;
          }
          //set new target as villager so zombies won't stay still waiting for nothing
          for (Villager villager : arena.getVillagers()) {
            zombie.setTarget(villager);
          }
        }
      }, 2);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    try {
      Arena arena = ArenaRegistry.getArena(e.getPlayer());
      if (arena == null || !arena.getPlayers().contains(e.getPlayer())) {
        return;
      }
      Player player = e.getPlayer();
      player.setAllowFlight(true);
      player.setFlying(true);
      arena.teleportToStartLocation(player);
      User user = plugin.getUserManager().getUser(player.getUniqueId());
      if (!user.isSpectator()) {
        user.setSpectator(true);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.SPEED);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
      }
      e.setRespawnLocation(arena.getStartLocation());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
