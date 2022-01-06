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

package plugily.projects.villagedefense.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.event.game.VillageGameSecretWellEvent;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.utils.Utils;

/**
 * Created by Tom on 16/08/2014.
 */
public class Events implements Listener {

  private final Main plugin;

  public Events(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }


  @EventHandler
  public void onItemPickup(PlayerExpChangeEvent event) {
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }

    User user = plugin.getUserManager().getUser(player);
    if(user.isSpectator()) {
      event.setAmount(0);
      return;
    }

    int amount = (int) Math.ceil(event.getAmount() * 1.6 * arena.getArenaOption("ZOMBIE_DIFFICULTY_MULTIPLIER"));

    event.setAmount(amount);

    amount += (amount * (plugin.getPermissionsManager().getPermissionCategoryValue("ORBS_BOOSTER", player) / 100));

    amount += event.getAmount();
    user.addStat(plugin.getStatsStorage().getStatisticType("ORBS"), event.getAmount());

    event.getPlayer().sendMessage(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ORBS_PICKUP").replace("%number%", Integer.toString(amount)));
  }


  @EventHandler
  public void onEntityInteractEntity(PlugilyPlayerInteractEntityEvent event) {
    if(VersionUtils.checkOffHand(event.getHand())) {
      return;
    }

    Arena arena = (Arena) plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }

    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
      return;
    }
    if(VersionUtils.getItemInHand(event.getPlayer()).getType() == Material.SADDLE) {
      if(event.getRightClicked().getType() == EntityType.IRON_GOLEM || event.getRightClicked().getType() == EntityType.VILLAGER || event.getRightClicked().getType() == EntityType.WOLF) {
        VersionUtils.setPassenger(event.getRightClicked(), event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }
    if(event.getRightClicked().getType() == EntityType.VILLAGER) {
      event.setCancelled(true);
      arena.getShopManager().openShop(event.getPlayer());
    } else if(event.getRightClicked().getType() == EntityType.IRON_GOLEM) {
      if(event.getPlayer().isSneaking()) {
        return;
      }
      IronGolem ironGolem = (IronGolem) event.getRightClicked();
      if(ironGolem.getCustomName() != null && ironGolem.getCustomName().contains(event.getPlayer().getName())) {
        VersionUtils.setPassenger(event.getRightClicked(), event.getPlayer());
      } else {
        event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_GOLEM_CANT_RIDE_OTHER"));
      }
    } else if(event.getRightClicked().getType() == EntityType.WOLF) {
      Wolf wolf = (Wolf) event.getRightClicked();
      //to prevent wolves sitting
      if(wolf.getCustomName() != null && wolf.getCustomName().contains(event.getPlayer().getName())) {
        VersionUtils.setPassenger(event.getRightClicked(), event.getPlayer());
      }
      wolf.setSitting(false);
    }
  }

  @EventHandler
  public void onDoorDrop(ItemSpawnEvent event) {
    if(event.getEntity().getItemStack().getType() == Utils.getCachedDoor(event.getLocation().getBlock())) {
      for(Entity entity : plugin.getBukkitHelper().getNearbyEntities(event.getLocation(), 20)) {
        if(entity instanceof Player && plugin.getArenaRegistry().getArena((Player) entity) != null) {
          event.getEntity().remove();
        }
      }
    }
  }


  @EventHandler
  public void onItemMove(InventoryClickEvent e) {
    if(e.getWhoClicked() instanceof Player && plugin.getArenaRegistry().isInArena((Player) e.getWhoClicked())) {
      if(plugin.getArenaRegistry().getArena(((Player) e.getWhoClicked())).getArenaState() != ArenaState.IN_GAME) {
        if(e.getClickedInventory() == e.getWhoClicked().getInventory()) {
          if(e.getView().getType() == InventoryType.CRAFTING || e.getView().getType() == InventoryType.PLAYER) {
            e.setResult(Event.Result.DENY);
          }
        }
      }
    }
  }


  @EventHandler
  public void onEntityCombust(EntityCombustByEntityEvent e) {
    if(!(e.getCombuster() instanceof Projectile)) {
      return;
    }
    Projectile projectile = (Projectile) e.getCombuster();
    if(!(projectile.getShooter() instanceof Player)) {
      return;
    }
    if(e.getEntity() instanceof Player) {
      Arena arena = plugin.getArenaRegistry().getArena((Player) projectile.getShooter());
      if(arena != null && arena.equals(plugin.getArenaRegistry().getArena((Player) e.getEntity()))) {
        e.setCancelled(true);
      }
    } else if(e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager || e.getEntity() instanceof Wolf) {
      for(Arena a : plugin.getArenaRegistry().getPluginArenas()) {
        if(a.getWolves().contains(e.getEntity()) || a.getVillagers().contains(e.getEntity()) || a.getIronGolems().contains(e.getEntity())) {
          e.setCancelled(true);
          return;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFriendHurt(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof Player) || plugin.getArenaRegistry().getArena((Player) e.getDamager()) == null) {
      return;
    }
    if(plugin.getUserManager().getUser((Player) e.getDamager()).isSpectator()) {
      e.setCancelled(true);
      return;
    }
    if(!(e.getEntity() instanceof Player || e.getEntity() instanceof Wolf || e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager)) {
      return;
    }
    e.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onCreatureHurt(EntityDamageEvent e) {
    if(!(e.getEntity() instanceof Creature) || !plugin.getConfig().getBoolean("Zombies.Health-Bar", true)) {
      return;
    }
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(!arena.getEnemies().contains(e.getEntity())) {
        continue;
      }
      Creature creature = (Creature) e.getEntity();
      creature.setCustomName(StringFormatUtils.getProgressBar((int) creature.getHealth(), (int) VersionUtils.getMaxHealth(creature),
          50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSecond(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof Projectile)) {
      return;
    }
    Projectile projectile = (Projectile) e.getDamager();
    if(!(projectile.getShooter() instanceof Player)) {
      return;
    }
    if(plugin.getArenaRegistry().getArena((Player) projectile.getShooter()) == null || !(e.getEntity() instanceof Player || e.getEntity() instanceof Wolf
        || e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager)) {
      return;
    }
    e.setCancelled(true);
  }

  @EventHandler
  public void onEntityLeash(PlayerLeashEntityEvent event) {
    if(event.getEntity() instanceof Villager) {
      ((Villager) event.getEntity()).setLeashHolder(event.getPlayer());
    }
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBuild(BlockPlaceEvent event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer()) && event.getBlock().getType() != Utils.getCachedDoor(event.getBlock())) {
      event.setCancelled(true);
    }
  }


  @EventHandler
  public void onSecretWellDrop(InventoryPickupItemEvent e) {
    if(e.getInventory().getType() != InventoryType.HOPPER) {
      return;
    }

    Item item = e.getItem();
    Location location = item.getLocation();

    Arena currentArena = null;
    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(location.getWorld() == arena.getStartLocation().getWorld()) {
        currentArena = arena;
        item.remove();
        e.setCancelled(true);
        e.getInventory().clear();
        break;
      }
    }

    if(currentArena == null) {
      return;
    }

    ItemStack itemStack = item.getItemStack();

    VillageGameSecretWellEvent villageGameSecretWellEvent = new VillageGameSecretWellEvent(currentArena, itemStack, location);
    Bukkit.getPluginManager().callEvent(villageGameSecretWellEvent);
    if(villageGameSecretWellEvent.isCancelled()) {
      return;
    }

    if(itemStack.getType() == Material.ROTTEN_FLESH) {
      for(Entity entity : plugin.getBukkitHelper().getNearbyEntities(location, 20)) {
        if(!(entity instanceof Player)) {
          continue;
        }
        Arena arena = plugin.getArenaRegistry().getArena((Player) entity);
        if(arena == null) {
          continue;
        }
        arena.changeArenaOptionBy("ROTTEN_FLESH_AMOUNT", itemStack.getAmount());
        VersionUtils.sendParticles("CLOUD", arena.getPlayers(), location, 50, 2, 2, 2);
        if(!arena.checkLevelUpRottenFlesh() || arena.getArenaOption("ROTTEN_FLESH_LEVEL") >= 30) {
          return;
        }
        for(Player p : arena.getPlayers()) {
          VersionUtils.setMaxHealth(p, VersionUtils.getMaxHealth(p) + 2.0);
          p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_ROTTEN_FLESH_LEVEL_UP"));
        }
      }
    }
  }

  /**
   * Triggers when something combusts in the world.
   * Thanks to @HomieDion for part of this class!
   */
  @EventHandler(ignoreCancelled = true)
  public void onCombust(EntityCombustEvent e) {
    // Ignore if this is caused by an event lower down the chain.
    if(e instanceof EntityCombustByEntityEvent || e instanceof EntityCombustByBlockEvent
        || !(e.getEntity() instanceof Creature)
        || e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL) {
      return;
    }

    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(arena.getEnemies().contains(e.getEntity())) {
        e.setCancelled(true);
        break;
      }
    }
  }


}
