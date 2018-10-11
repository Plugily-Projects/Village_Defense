/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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
import org.bukkit.entity.EntityType;
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
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.RewardsHandler;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
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
      if (e.getEntity() instanceof Villager && e.getDamager() instanceof Zombie) {
        for (Arena a : ArenaRegistry.getArenas()) {
          if (a.getVillagers().contains(e.getEntity()) && a.getZombies().contains(e.getDamager())) {
            e.setCancelled(false);
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
    try {
      if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Wolf && e.getEntity() instanceof Zombie) {
        //trick to get non player killer of zombie
        for (Arena a : ArenaRegistry.getArenas()) {
          if (a.getZombies().contains(e.getEntity())) {
            if (e.getDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
              //prevent offline player cast error
              if (((Wolf) e.getDamager()).getOwner() == null || !(((Wolf) e.getDamager()).getOwner() instanceof Player)) {
                return;
              }
              Player player = (Player) ((Wolf) e.getDamager()).getOwner();
              if (ArenaRegistry.getArena(player) != null) {
                a.addStat(player, StatsStorage.StatisticType.KILLS);
                a.addExperience(player, 2);
              }
              return;
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDieEntity(EntityDeathEvent e) {
    try {
      if (e.getEntity().getType() == EntityType.ZOMBIE || e.getEntity().getType() == EntityType.VILLAGER) {
        for (Arena a : ArenaRegistry.getArenas()) {
          switch (e.getEntityType()) {
            case ZOMBIE:
              if (a.getZombies().contains(e.getEntity())) {
                a.removeZombie((Zombie) e.getEntity());
                a.setTotalKilledZombies(a.getTotalKilledZombies() + 1);
                if (ArenaRegistry.getArena(e.getEntity().getKiller()) != null) {
                  a.addStat(e.getEntity().getKiller(), StatsStorage.StatisticType.KILLS);
                  a.addExperience(e.getEntity().getKiller(), 2);
                  plugin.getRewardsHandler().performReward(e.getEntity().getKiller(), RewardsHandler.RewardType.ZOMBIE_KILL);
                  plugin.getPowerupManager().spawnPowerup(e.getEntity().getLocation(), ArenaRegistry.getArena(e.getEntity().getKiller()));
                }
                return;
              }
              break;
            case VILLAGER:
              if (a.getVillagers().contains(e.getEntity())) {
                a.getStartLocation().getWorld().strikeLightningEffect(e.getEntity().getLocation());
                a.removeVillager((Villager) e.getEntity());
                for (Player p : a.getPlayers()) {
                  p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Villager-Died"));
                }
                return;
              }
              break;
          }
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
          User user = UserManager.getUser(player.getUniqueId());
          user.setStat(StatsStorage.StatisticType.ORBS, 0);
          player.teleport(arena.getEndLocation());
          return;
        }
        User user = UserManager.getUser(player.getUniqueId());
        arena.addStat(player, StatsStorage.StatisticType.DEATHS);
        arena.teleportToStartLocation(player);
        user.setSpectator(true);
        player.setGameMode(GameMode.SURVIVAL);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
        ArenaUtils.hidePlayer(player, arena);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        player.sendTitle(ChatManager.colorMessage("In-Game.Death-Screen"), null, 0, 5 * 20, 0);
        new BukkitRunnable() {
          @Override
          public void run() {
            if (arena.getArenaState() == ArenaState.ENDING) {
              this.cancel();
            }
            if (user.isSpectator()) {
              player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatManager.colorMessage("In-Game.Died-Respawn-In-Next-Wave")));
            } else {
              this.cancel();
            }
          }
        }.runTaskTimer(plugin, 20, 20);
        ChatManager.broadcastAction(arena, player, ChatManager.ActionType.DEATH);

        //running in a scheduler of 1 tick due to 1.13 bug
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
          player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name")).build());
          player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name")).build());
          player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());
        }, 1);

        //tryin to untarget dead player bcuz they will still target him
        for (Zombie zombie : arena.getZombies()) {
          if (zombie.getTarget() != null) {
            if (zombie.getTarget().equals(player)) {
              //set new target as villager so zombies won't stay still waiting for nothing
              for (Villager villager : arena.getVillagers()) {
                zombie.setTarget(villager);
              }
            }
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
      if (arena == null) {
        return;
      }
      if (arena.getPlayers().contains(e.getPlayer())) {
        Player player = e.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        player.setAllowFlight(true);
        player.setFlying(true);
        if (user.isSpectator()) {
          arena.teleportToStartLocation(player);
        } else {
          arena.teleportToStartLocation(player);
          user.setSpectator(true);
          player.setGameMode(GameMode.SURVIVAL);
          player.removePotionEffect(PotionEffectType.NIGHT_VISION);
          player.removePotionEffect(PotionEffectType.SPEED);
          user.setStat(StatsStorage.StatisticType.ORBS, 0);
        }
        e.setRespawnLocation(arena.getStartLocation());
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
