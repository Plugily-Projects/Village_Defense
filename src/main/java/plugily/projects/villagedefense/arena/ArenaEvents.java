/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.ChatManager;
import plugily.projects.villagedefense.handlers.items.SpecialItem;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.reward.Reward;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.ServerVersion.Version;
import plugily.projects.villagedefense.utils.Utils;

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
    if (!(e.getEntity() instanceof Villager && e.getDamager() instanceof Zombie)) {
      return;
    }
    for (Arena a : ArenaRegistry.getArenas()) {
      if (a.getVillagers().contains(e.getEntity()) && a.getZombies().contains(e.getDamager())) {
        //check villagerbuster
        if (((Zombie) e.getDamager()).getEquipment().getHelmet().getType().isBlock() && ((Zombie) e.getDamager()).getEquipment().getChestplate().getType() == Material.LEATHER_CHESTPLATE) {
          ((Zombie) e.getDamager()).damage(((Zombie) e.getDamager()).getHealth() * 2);
          ItemStack[] itemStack = new ItemStack[] {new ItemStack(Material.ROTTEN_FLESH)};
          Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) e.getDamager(), new ArrayList<>(Arrays.asList(itemStack)), 6));
          (e.getDamager()).getWorld().spawnEntity((e.getDamager()).getLocation(), EntityType.PRIMED_TNT);
          e.setCancelled(true);
        } else {
          e.setCancelled(false);
        }
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
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
        if (!(((Wolf) e.getDamager()).getOwner() instanceof Player)) {
          return;
        }
        Player player = (Player) ((Wolf) e.getDamager()).getOwner();
        if (ArenaRegistry.getArena(player) != null) {
          plugin.getUserManager().addStat(player, StatsStorage.StatisticType.KILLS);
          plugin.getUserManager().addExperience(player, 2 * arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
        }
        return;
      }
    }
  }

  @EventHandler
  public void onItemDrop(ItemSpawnEvent e) {
    if (e.getEntity().getItemStack().getType() != Material.ROTTEN_FLESH) {
      return;
    }
    for (Arena arena : ArenaRegistry.getArenas()) {
      if (!e.getEntity().getWorld().equals(arena.getStartLocation().getWorld())) {
        continue;
      }
      if (e.getEntity().getLocation().distance(arena.getStartLocation()) > 150) {
        continue;
      }
      arena.addDroppedFlesh(e.getEntity());
    }
  }

  @Deprecated //should use EntityPickupItemEvent
  @EventHandler
  public void onDropPickup(PlayerPickupItemEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    arena.removeDroppedFlesh(e.getItem());
  }

  @EventHandler
  public void onDieEntity(EntityDeathEvent e) {
    if (!(e.getEntity() instanceof Zombie || e.getEntity() instanceof Villager || e.getEntity() instanceof IronGolem || e.getEntity() instanceof Wolf)) {
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
            plugin.getUserManager().addStat(e.getEntity().getKiller(), StatsStorage.StatisticType.KILLS);
            plugin.getUserManager().addExperience(e.getEntity().getKiller(), 2 * arena.getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER));
            plugin.getRewardsHandler().performReward(e.getEntity().getKiller(), Reward.RewardType.ZOMBIE_KILL);
            plugin.getPowerupRegistry().spawnPowerup(e.getEntity().getLocation(), ArenaRegistry.getArena(e.getEntity().getKiller()));
          }
          return;
        case VILLAGER:
          if (!arena.getVillagers().contains(e.getEntity())) {
            continue;
          }
          arena.getStartLocation().getWorld().strikeLightningEffect(e.getEntity().getLocation());
          arena.removeVillager((Villager) e.getEntity());
          plugin.getHolidayManager().applyHolidayDeathEffects(e.getEntity());
          plugin.getChatManager().broadcast(arena, Messages.VILLAGER_DIED);
          return;
        case IRON_GOLEM:
          if (!arena.getIronGolems().contains(e.getEntity())) {
            continue;
          }
          arena.removeIronGolem((IronGolem) e.getEntity());
          e.getDrops().clear();
          return;
        case WOLF:
          if (!arena.getWolves().contains(e.getEntity())) {
            continue;
          }
          if (Bukkit.getServer().getVersion().contains("Paper")) {
            //only on paper
            e.setCancelled(true);
          }
          //todo remove odd wolf death message on spigot
          arena.removeWolf((Wolf) e.getEntity());
          arena.getPlayers().forEach(player -> player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.WOLF_DIED), (Player) ((Wolf) e.getEntity()).getOwner())));
        default:
          break;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getEntity());
    if (arena == null) {
      return;
    }
    if (e.getEntity().isDead()) {
      e.getEntity().setHealth(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }
    e.setDeathMessage("");
    e.getDrops().clear();
    e.setDroppedExp(0);
    plugin.getHolidayManager().applyHolidayDeathEffects(e.getEntity());
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> e.getEntity().spigot().respawn(), 5);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      Player player = e.getEntity();
      player.spigot().respawn();
      User user = plugin.getUserManager().getUser(player);
      if (arena.getArenaState() == ArenaState.STARTING) {
        player.teleport(arena.getStartLocation());
        return;
      } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
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
      Utils.sendTitle(player, 0, 5 * 20, 0, plugin.getChatManager().colorMessage(Messages.DEATH_SCREEN), null);
      sendSpectatorActionBar(user, arena);
      plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.DEATH);

      //running in a scheduler of 1 tick due to respawn bug
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        for (SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
          if (item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
            continue;
          }
          player.getInventory().setItem(item.getSlot(), item.getItemStack());
        }
      }, 1);

      untargetPlayerFromZombies(player, arena);
    }, 10);
  }

  private void sendSpectatorActionBar(User user, Arena arena) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (Version.isCurrentEqual(Version.v1_11_R1) || arena.getArenaState() == ArenaState.ENDING) {
          this.cancel();
          return;
        }
        if (user.isSpectator()) {
          if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
            user.getPlayer().sendActionBar(TextComponent.fromLegacyText(plugin.getChatManager().colorMessage(Messages.DIED_RESPAWN_IN_NEXT_WAVE)));
          } else {
            user.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getChatManager().colorMessage(Messages.DIED_RESPAWN_IN_NEXT_WAVE)));
          }
        } else {
          this.cancel();
        }
      }
    }.runTaskTimer(plugin, 30, 30);
  }

  private void untargetPlayerFromZombies(Player player, Arena arena) {
    for (Zombie zombie : arena.getZombies()) {
      if (zombie.getTarget() == null || !zombie.getTarget().equals(player)) {
        continue;
      }
      //set new target as villager so zombies won't stay still waiting for nothing
      arena.getVillagers().forEach(zombie::setTarget);
    }
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null || !arena.getPlayers().contains(e.getPlayer())) {
      return;
    }
    Player player = e.getPlayer();
    player.setAllowFlight(true);
    player.setFlying(true);
    User user = plugin.getUserManager().getUser(player);
    if (!user.isSpectator()) {
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
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.ENABLE_SHORT_COMMANDS)) {
      Player player = e.getPlayer();
      if (e.getMessage().equalsIgnoreCase("/start")) {
        player.performCommand("vda forcestart");
        e.setCancelled(true);
        return;
      }
      if (e.getMessage().equalsIgnoreCase("/leave")) {
        player.performCommand("vd leave");
        e.setCancelled(true);
      }
    }
  }

}
