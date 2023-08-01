/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.arena;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
import plugily.projects.villagedefense.Main;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaEvents extends PluginArenaEvents {

  private final Main plugin;

  public ArenaEvents(Main plugin) {
    super(plugin);
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  //override WorldGuard build deny flag where villagers cannot be damaged
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onVillagerDamage(EntityDamageByEntityEvent e) {
    if(e.getEntityType() != EntityType.VILLAGER || !(e.getDamager() instanceof Creature)) {
      return;
    }

    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(arena.getVillagers().contains(e.getEntity()) && arena.getEnemies().contains(e.getDamager())) {
        e.setCancelled(false);
        break;
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof Wolf && e.getEntity() instanceof Creature)) {
      return;
    }

    if(e.getDamage() >= ((Creature) e.getEntity()).getHealth()) {

      //trick to get non player killer of zombie
      for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
        if(arena.getEnemies().contains(e.getEntity())) {
          org.bukkit.entity.AnimalTamer owner = ((Wolf) e.getDamager()).getOwner();

          if(owner instanceof Player) { //prevent offline player cast error
            Player player = (Player) owner;

            if(plugin.getArenaRegistry().getArena(player) != null) {
              plugin.getUserManager().addStat(player, plugin.getStatsStorage().getStatisticType("KILLS"));
              plugin.getUserManager().addExperience(player, 2 * arena.getArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER"));
            }
          }

          break;
        }
      }
    }
  }

  @EventHandler
  public void onItemDrop(ItemSpawnEvent e) {
    org.bukkit.entity.Item item = e.getEntity();

    if(item.getItemStack().getType() != Material.ROTTEN_FLESH) {
      return;
    }

    Location itemLoc = item.getLocation();

    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      Location start = arena.getStartLocation();

      if(itemLoc.getWorld() != start.getWorld() || itemLoc.distance(start) > 150) {
        continue;
      }

      arena.addDroppedFlesh(item);
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if(event.getEntityType() != EntityType.IRON_GOLEM && event.getEntityType() != EntityType.WOLF)
      return;

    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      switch(event.getEntityType()) {
        case IRON_GOLEM:
          if(!arena.getIronGolems().contains(event.getEntity())) {
            continue;
          }

          IronGolem ironGolem = (IronGolem) event.getEntity();

          if(ironGolem.getHealth() <= event.getDamage()) {
            event.setCancelled(true);
            event.setDamage(0);
            arena.removeIronGolem(ironGolem);
          }
          return;
        case WOLF:
          if(!arena.getWolves().contains(event.getEntity())) {
            continue;
          }

          Wolf wolf = (Wolf) event.getEntity();

          if(wolf.getHealth() <= event.getDamage()) {
            event.setCancelled(true);
            event.setDamage(0);

            java.util.UUID ownerUUID = (wolf.getOwner() != null) ? wolf.getOwner().getUniqueId() : null;

            if(ownerUUID != null) {
              Player playerOwner = plugin.getServer().getPlayer(ownerUUID);

              if(playerOwner != null)
                new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_WOLF_DEATH").asKey().player(playerOwner).sendPlayer();
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
  public void onDieEntity(EntityDeathEvent event) {
    LivingEntity entity = event.getEntity();
    if(!(entity instanceof Creature)) {
      return;
    }
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(event.getEntityType() == EntityType.VILLAGER) {
        if(!arena.getVillagers().contains(entity)) {
          continue;
        }
        arena.getStartLocation().getWorld().strikeLightningEffect(entity.getLocation());
        arena.removeVillager((Villager) entity);
        plugin.getRewardsHandler().performReward(null, arena, plugin.getRewardsHandler().getRewardType("VILLAGER_DEATH"));
        plugin.getHolidayManager().applyHolidayDeathEffects(entity);
        new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_VILLAGER_DIED").asKey().arena(arena).sendArena();
      } else {
        if(!arena.getEnemies().contains(entity)) {
          continue;
        }
        arena.removeEnemy((Creature) entity);
        arena.changeArenaOptionBy("TOTAL_KILLED_ZOMBIES", 1);

        Player killer = entity.getKiller();
        Arena killerArena = plugin.getArenaRegistry().getArena(killer);

        if(killerArena != null) {
          plugin.getUserManager().addStat(killer, plugin.getStatsStorage().getStatisticType("KILLS"));
          plugin.getUserManager().addExperience(killer, 2 * arena.getArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER"));
          plugin.getRewardsHandler().performReward(killer, plugin.getRewardsHandler().getRewardType("ZOMBIE_KILL"));
          plugin.getPowerupRegistry().spawnPowerup(entity.getLocation(), killerArena);
        }
      }
      break;
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    Arena arena = plugin.getArenaRegistry().getArena(e.getEntity());
    if(arena == null) {
      return;
    }

    final Player player = e.getEntity();

    if(player.isDead()) {
      player.setHealth(VersionUtils.getMaxHealth(player));
    }
    plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("PLAYER_DEATH"));
    ComplementAccessor.getComplement().setDeathMessage(e, "");
    e.getDrops().clear();
    e.setDroppedExp(0);
    plugin.getHolidayManager().applyHolidayDeathEffects(player);
    player.spigot().respawn();
    plugin.getServer().getScheduler().runTask(plugin, () -> {
      if(arena.getArenaState() == ArenaState.STARTING) {
        VersionUtils.teleport(player, arena.getStartLocation());
        return;
      }

      if(arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        plugin.getUserManager().getUser(player).setStatistic("ORBS", 0);
        VersionUtils.teleport(player, arena.getEndLocation());
        return;
      }

      User user = plugin.getUserManager().getUser(player);

      plugin.getUserManager().addStat(user, plugin.getStatsStorage().getStatisticType("DEATHS"));
      VersionUtils.teleport(player, arena.getStartLocation());
      user.setSpectator(true);
      player.setGameMode(GameMode.SURVIVAL);

      modifyUserOrbs(user);

      ArenaUtils.hidePlayer(player, arena);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().clear();
      VersionUtils.sendTitle(player, new MessageBuilder("IN_GAME_DEATH_SCREEN").asKey().build(), 0, 5 * 20, 0);
      sendSpectatorActionBar(user, arena);
      new MessageBuilder(MessageBuilder.ActionType.DEATH).arena(arena).player(player).sendArena();

      plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.SPECTATOR);

      arena.getCreatureTargetManager().unTargetPlayerFromZombies(player, arena);
    });
  }

  private void sendSpectatorActionBar(User user, Arena arena) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if(arena.getArenaState() == ArenaState.ENDING || !user.isSpectator()) {
          cancel();
          return;
        }
        Player player = user.getPlayer();
        if(player == null) {
          cancel();
        } else {
          VersionUtils.sendActionBar(player, new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_RESPAWN_ON_NEXT").asKey().player(player).arena(arena).build());
        }
      }
    }.runTaskTimer(plugin, 30, 30);
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRespawn(PlayerRespawnEvent e) {
    Arena arena = plugin.getArenaRegistry().getArena(e.getPlayer());
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

      modifyUserOrbs(user);
    }
    e.setRespawnLocation(arena.getStartLocation());
  }

  private void modifyUserOrbs(User user) {
    int deathValue = plugin.getConfig().getInt("Orbs.Death.Value", 50);
    int current = user.getStatistic("ORBS");
    switch(getOrbDeathType()) {
      case KEEP:
        return;
      case AMOUNT:
        user.setStatistic("ORBS", (Math.max(current + deathValue, 0)));
        break;
      case SET:
        user.setStatistic("ORBS", deathValue);
        break;
      case PERCENTAGE:
        user.setStatistic("ORBS", current * (deathValue / 100));
        break;
      default:
        break;
    }
  }

  private OrbDeathType getOrbDeathType() {
    return OrbDeathType.valueOf(plugin.getConfig().getString("Orbs.Death.Type", "KEEP"));
  }

  private enum OrbDeathType {
    PERCENTAGE, AMOUNT, SET, KEEP
  }

  @EventHandler
  public void onPickup(PlugilyEntityPickupItemEvent e) {
    if(e.getEntity().getType() != EntityType.PLAYER || e.getItem().getItemStack().getType() != Material.ROTTEN_FLESH) {
      return;
    }
    Player player = (Player) e.getEntity();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    if(plugin.getUserManager().getUser(player).isSpectator()) {
      e.setCancelled(true);
    }
    arena.removeDroppedFlesh(e.getItem());
  }


}
