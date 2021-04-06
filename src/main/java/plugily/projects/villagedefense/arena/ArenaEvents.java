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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBEntityPickupItemEvent;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.ChatManager;
import plugily.projects.villagedefense.handlers.items.SpecialItem;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.reward.Reward;
import plugily.projects.villagedefense.user.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaEvents implements Listener {

  private final Main plugin;

  public ArenaEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  //override WorldGuard build deny flag where villagers cannot be damaged
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onVillagerDamage(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof Villager && e.getDamager() instanceof Zombie)) {
      return;
    }
    for(Arena a : ArenaRegistry.getArenas()) {
      if(a.getVillagers().contains(e.getEntity()) && a.getZombies().contains(e.getDamager())) {
        Zombie zombie = (Zombie) e.getDamager();
        //check villagerbuster
        if(zombie.getEquipment().getHelmet().getType().isBlock() && zombie.getEquipment().getChestplate().getType() == Material.LEATHER_CHESTPLATE) {
          zombie.damage(zombie.getHealth() * 2);
          Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent(zombie, new ArrayList<>(Arrays.asList(new ItemStack(Material.ROTTEN_FLESH))), 6));
          zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.PRIMED_TNT);
          e.setCancelled(true);
        } else {
          e.setCancelled(false);
        }
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Wolf && e.getEntity() instanceof Zombie)) {
      return;
    }
    //trick to get non player killer of zombie
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!arena.getZombies().contains(e.getEntity())) {
        continue;
      }
      if(e.getDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
        //prevent offline player cast error
        if(!(((Wolf) e.getDamager()).getOwner() instanceof Player)) {
          return;
        }
        Player player = (Player) ((Wolf) e.getDamager()).getOwner();
        if(ArenaRegistry.getArena(player) != null) {
          plugin.getUserManager().addStat(player, StatsStorage.StatisticType.KILLS);
          plugin.getUserManager().addExperience(player, 2 * arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
        }
        return;
      }
    }
  }

  @EventHandler
  public void onItemDrop(ItemSpawnEvent e) {
    org.bukkit.entity.Item item = e.getEntity();
    if(item.getItemStack().getType() != Material.ROTTEN_FLESH) {
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      org.bukkit.Location start = arena.getStartLocation();
      if(!item.getWorld().equals(start.getWorld()) || item.getLocation().distance(start) > 150) {
        continue;
      }
      arena.addDroppedFlesh(item);
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if(!(event.getEntity() instanceof IronGolem || event.getEntity() instanceof Wolf)) {
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      switch(event.getEntityType()) {
        case IRON_GOLEM:
          if(!arena.getIronGolems().contains(event.getEntity())) {
            continue;
          }
          if(((org.bukkit.entity.Creature) event.getEntity()).getHealth() <= event.getDamage()) {
            event.setCancelled(true);
            event.setDamage(0);
            arena.removeIronGolem((IronGolem) event.getEntity());
          }
          return;
        case WOLF:
          if(!arena.getWolves().contains(event.getEntity())) {
            continue;
          }
          if(((org.bukkit.entity.Creature) event.getEntity()).getHealth() <= event.getDamage()) {
            event.setCancelled(true);
            event.setDamage(0);
            Wolf wolf = ((Wolf) event.getEntity());
            java.util.UUID ownerUUID = VersionUtils.isPaper() ? wolf.getOwnerUniqueId()
                : (wolf.getOwner() != null) ? wolf.getOwner().getUniqueId() : null;
            Player playerOwner = ownerUUID != null ? Bukkit.getPlayer(ownerUUID) : null;
            if(playerOwner != null) {
              playerOwner.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.WOLF_DIED));
            }
            arena.removeWolf(wolf);
          }
          return;
        default:
          return;
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDeathEvent e) {
    LivingEntity entity = e.getEntity();
    if(!(entity instanceof Zombie || entity instanceof Villager)) {
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      switch(e.getEntityType()) {
        case ZOMBIE:
          if(!arena.getZombies().contains(entity)) {
            continue;
          }
          arena.removeZombie((Zombie) entity);
          arena.addOptionValue(ArenaOption.TOTAL_KILLED_ZOMBIES, 1);
          Arena killerArena = ArenaRegistry.getArena(entity.getKiller());
          if(killerArena != null) {
            plugin.getUserManager().addStat(entity.getKiller(), StatsStorage.StatisticType.KILLS);
            plugin.getUserManager().addExperience(entity.getKiller(), 2 * arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
            plugin.getRewardsHandler().performReward(entity.getKiller(), Reward.RewardType.ZOMBIE_KILL);
            plugin.getPowerupRegistry().spawnPowerup(entity.getLocation(), killerArena);
          }
          return;
        case VILLAGER:
          if(!arena.getVillagers().contains(entity)) {
            continue;
          }
          arena.getStartLocation().getWorld().strikeLightningEffect(entity.getLocation());
          arena.removeVillager((Villager) entity);
          plugin.getRewardsHandler().performReward(null, arena, Reward.RewardType.VILLAGER_DEATH);
          plugin.getHolidayManager().applyHolidayDeathEffects(entity);
          plugin.getChatManager().broadcast(arena, Messages.VILLAGER_DIED);
          return;
        default:
          break;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getEntity());
    if(arena == null) {
      return;
    }

    final Player player = e.getEntity();

    if(player.isDead()) {
      player.setHealth(VersionUtils.getMaxHealth(player));
    }
    plugin.getRewardsHandler().performReward(player, arena, Reward.RewardType.PLAYER_DEATH);
    ComplementAccessor.getComplement().setDeathMessage(e, "");
    e.getDrops().clear();
    e.setDroppedExp(0);
    plugin.getHolidayManager().applyHolidayDeathEffects(player);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      player.spigot().respawn();
      User user = plugin.getUserManager().getUser(player);
      if(arena.getArenaState() == ArenaState.STARTING) {
        player.teleport(arena.getStartLocation());
        return;
      } else if(arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        user.setStat(StatsStorage.StatisticType.ORBS, 0);
        player.teleport(arena.getEndLocation());
        return;
      }
      plugin.getUserManager().addStat(player, StatsStorage.StatisticType.DEATHS);
      player.teleport(arena.getStartLocation());
      user.setSpectator(true);
      player.setGameMode(GameMode.SURVIVAL);
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
      ArenaUtils.hidePlayer(player, arena);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().clear();
      VersionUtils.sendTitle(player, plugin.getChatManager().colorMessage(Messages.DEATH_SCREEN), 0, 5 * 20, 0);
      sendSpectatorActionBar(user, arena);
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.DEATH);

      //running in a scheduler of 1 tick due to respawn bug
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
          if(item.getDisplayStage() == SpecialItem.DisplayStage.SPECTATOR) {
            player.getInventory().setItem(item.getSlot(), item.getItemStack());
          }
        }
      }, 1);

      untargetPlayerFromZombies(player, arena);
    }, 10);
  }

  private void sendSpectatorActionBar(User user, Arena arena) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if(ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1) || arena.getArenaState() == ArenaState.ENDING) {
          cancel();
          return;
        }
        if(user.isSpectator()) {
          VersionUtils.sendActionBar(user.getPlayer(), plugin.getChatManager().colorMessage(Messages.DIED_RESPAWN_IN_NEXT_WAVE));
        } else {
          cancel();
        }
      }
    }.runTaskTimer(plugin, 30, 30);
  }

  private void untargetPlayerFromZombies(Player player, Arena arena) {
    for(Zombie zombie : arena.getZombies()) {
      if(zombie.getTarget() == null || !zombie.getTarget().equals(player)) {
        continue;
      }
      //set new target as villager so zombies won't stay still waiting for nothing
      for(Villager villager : arena.getVillagers()) {
        zombie.setTarget(villager);
      }
    }
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    Player player = e.getPlayer();
    player.setAllowFlight(true);
    player.setFlying(true);
    User user = plugin.getUserManager().getUser(player);
    if(!user.isSpectator()) {
      user.setSpectator(true);
      player.setGameMode(GameMode.SURVIVAL);
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.removePotionEffect(PotionEffectType.SPEED);
      user.setStat(StatsStorage.StatisticType.ORBS, 0);
    }
    e.setRespawnLocation(arena.getStartLocation());
  }

  @EventHandler
  public void playerCommandExecution(PlayerCommandPreprocessEvent e) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.ENABLE_SHORT_COMMANDS)) {
      if(e.getMessage().equalsIgnoreCase("/start")) {
        e.getPlayer().performCommand("vda forcestart");
        e.setCancelled(true);
        return;
      }
      if(e.getMessage().equalsIgnoreCase("/leave")) {
        e.getPlayer().performCommand("vd leave");
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPickup(CBEntityPickupItemEvent e) {
    if(e.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Arena arena = ArenaRegistry.getArena((Player) e.getEntity());
    if(arena == null) {
      return;
    }
    if(plugin.getUserManager().getUser((Player) e.getEntity()).isSpectator()) {
      e.setCancelled(true);
    }
    arena.removeDroppedFlesh(e.getItem());
  }

  @EventHandler
  public void onEntityDamageEvent(EntityDamageEvent e) {
    if(!(e.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) e.getEntity();
    Arena arena = ArenaRegistry.getArena(victim);
    if(arena == null) {
      return;
    }
    if(e.getCause() == EntityDamageEvent.DamageCause.DROWNING && plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_DROWNING_DAMAGE)) {
      e.setCancelled(true);
    }
    if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
      if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_FALL_DAMAGE)) {
        if(e.getDamage() >= 20.0) {
          //kill the player for suicidal death, else do not
          victim.damage(1000.0);
        }
      }
      e.setCancelled(true);
    }
    //kill the player on void
    if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
      if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
        victim.damage(0);
        victim.teleport(arena.getLobbyLocation());
      } else {
        victim.damage(1000.0);
        victim.teleport(arena.getStartLocation());
      }
    }
  }
}
