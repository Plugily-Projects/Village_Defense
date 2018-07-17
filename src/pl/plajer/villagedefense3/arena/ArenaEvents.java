/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.arena;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;

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
    if (e.getEntity() instanceof Villager && e.getDamager() instanceof Zombie) {
      for (Arena a : ArenaRegistry.getArenas()) {
        if (a.getVillagers().contains(e.getEntity()) && a.getZombies().contains(e.getDamager())) {
          e.setCancelled(false);
        }
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Wolf && e.getEntity() instanceof Zombie) {
      //trick to get non player killer of zombie
      for (Arena a : ArenaRegistry.getArenas()) {
        if (a.getZombies().contains(e.getEntity())) {
          if (e.getDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
            //prevent offline player cast error
            if (((Wolf) e.getDamager()).getOwner() == null || !(((Wolf) e.getDamager()).getOwner() instanceof Player))
              return;
            Player player = (Player) ((Wolf) e.getDamager()).getOwner();
            if (ArenaRegistry.getArena(player) != null) {
              a.addStat(player, "kills");
              a.addExperience(player, 2);
            }
            return;
          }
        }
      }
    }
  }

  @EventHandler
  public void onDieEntity(EntityDeathEvent event) {
    if (event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.VILLAGER) {
      for (Arena a : ArenaRegistry.getArenas()) {
        switch (event.getEntityType()) {
          case ZOMBIE:
            if (a.getZombies().contains(event.getEntity())) {
              a.removeZombie((Zombie) event.getEntity());
              if (ArenaRegistry.getArena(event.getEntity().getKiller()) != null) {
                a.addStat(event.getEntity().getKiller(), "kills");
                a.addExperience(event.getEntity().getKiller(), 2);
                plugin.getRewardsHandler().performZombieKillReward(event.getEntity().getKiller());
                plugin.getPowerupManager().spawnPowerup(event.getEntity().getLocation(), ArenaRegistry.getArena(event.getEntity().getKiller()));
              }
              return;
            }
            break;
          case VILLAGER:
            if (a.getVillagers().contains(event.getEntity())) {
              a.getStartLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
              a.removeVillager((Villager) event.getEntity());
              for (Player p : a.getPlayers()) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Villager-Died"));
              }
              return;
            }
            break;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getEntity());
    if (arena == null) return;
    if (e.getEntity().isDead())
      e.getEntity().setHealth(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    e.setDeathMessage("");
    e.getDrops().clear();
    e.setDroppedExp(0);
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
      user.setInt("orbs", 0);
      player.teleport(arena.getEndLocation());
      return;
    }
    User user = UserManager.getUser(player.getUniqueId());
    arena.addStat(player, "deaths");
    arena.teleportToStartLocation(player);
    user.setSpectator(true);
    player.setGameMode(GameMode.SURVIVAL);
    user.setFakeDead(true);
    user.setInt("orbs", 0);
    ArenaUtils.hidePlayer(player, arena);
    player.setAllowFlight(true);
    player.setFlying(true);
    player.getInventory().clear();
    MessageUtils.sendTitle(player, ChatColor.stripColor(ChatManager.colorMessage("In-Game.Death-Screen")), 0, 5 * 20, 0, ChatColor.RED);
    new BukkitRunnable() {
      @Override
      public void run() {
        if (user.isSpectator())
          player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatManager.colorMessage("In-Game.Died-Respawn-In-Next-Wave")));
        else this.cancel();
      }
    }.runTaskTimer(plugin, 20, 20);
    ChatManager.broadcastAction(arena, player, ChatManager.ActionType.DEATH);

    ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
    ItemMeta spectatorMeta = spectatorItem.getItemMeta();
    spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
    spectatorItem.setItemMeta(spectatorMeta);
    player.getInventory().setItem(0, spectatorItem);

    player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());
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
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) return;
    if (arena.getPlayers().contains(e.getPlayer())) {
      Player player = e.getPlayer();
      User user = UserManager.getUser(player.getUniqueId());
      player.setAllowFlight(true);
      player.setFlying(true);
      if (user.isFakeDead()) {
        arena.teleportToStartLocation(player);
      } else {
        arena.teleportToStartLocation(player);
        user.setSpectator(true);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        user.setFakeDead(true);
        user.setInt("orbs", 0);
      }
      e.setRespawnLocation(arena.getStartLocation());
    }
  }

}
