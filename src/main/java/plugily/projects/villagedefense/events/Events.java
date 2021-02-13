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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.items.SpecialItem;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;

import java.util.Map;

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
  public void onSpawn(CreatureSpawnEvent event) {
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!event.getEntity().getWorld().equals(arena.getStartLocation().getWorld()) || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
        continue;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemPickup(PlayerExpChangeEvent event) {
    if(ArenaRegistry.getArena(event.getPlayer()) == null) {
      return;
    }
    int multiplier = ArenaRegistry.getArena(event.getPlayer()).getOption(ArenaOption.ZOMBIE_DIFFICULTY_MULTIPLIER);
    int amount = (int) Math.ceil(event.getAmount() * 1.6 * multiplier);
    User user = plugin.getUserManager().getUser(event.getPlayer());
    event.setAmount(amount);
    if(user.isSpectator()) {
      event.setAmount(0);
      return;
    }
    //bonus orbs with custom permissions
    for(Map.Entry<String, Integer> perm : plugin.getConfigPreferences().getCustomPermissions().entrySet()) {
      if(event.getPlayer().hasPermission(perm.getKey())) {
        int orbs = perm.getValue() / 100;
        amount = +(int) Math.ceil(event.getAmount() * (double) orbs);
        user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * orbs));
      }
    }

    if(event.getPlayer().hasPermission(PermissionsManager.getElite())) {
      amount += (int) Math.ceil(event.getAmount() * 1.5);
      user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 1.5));
    } else if(event.getPlayer().hasPermission(PermissionsManager.getMvp())) {
      amount += (int) Math.ceil(event.getAmount() * 1.0);
      user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 1.0));
    } else if(event.getPlayer().hasPermission(PermissionsManager.getVip())) {
      amount += (int) Math.ceil(event.getAmount() * 0.5);
      user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 0.5));
    } else {
      amount += event.getAmount();
      user.addStat(StatsStorage.StatisticType.ORBS, event.getAmount());
    }
    event.getPlayer().sendMessage(plugin.getChatManager().colorMessage(Messages.ORBS_PICKUP).replace("%number%", String.valueOf(amount)));
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena != null && (plugin.getUserManager().getUser(event.getPlayer()).isSpectator() || event.getItemDrop().getItemStack().getType() == Material.SADDLE)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onExplosionCancel(EntityExplodeEvent event) {
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(arena.getStartLocation().getWorld().getName().equals(event.getLocation().getWorld().getName())
          && arena.getStartLocation().distance(event.getLocation()) < 300) {
        event.blockList().clear();
      }
    }
  }

  @EventHandler
  public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(event.getHand() == EquipmentSlot.OFF_HAND || arena == null) {
      return;
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    if(user.isSpectator()) {
      event.setCancelled(true);
      return;
    }
    if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SADDLE) {
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
      IronGolem ironGolem = (IronGolem) event.getRightClicked();
      if(event.getPlayer().isSneaking()) {
        return;
      }
      if(ironGolem.getCustomName() != null && ironGolem.getCustomName().contains(event.getPlayer().getName())) {
        VersionUtils.setPassenger(event.getRightClicked(), event.getPlayer());
      } else {
        event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.CANT_RIDE_OTHERS_GOLEM));
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
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    if(!plugin.getConfig().getBoolean("Block-Commands-In-Game", true)) {
      return;
    }
    String command = event.getMessage().substring(1);
    command = (command.indexOf(' ') >= 0 ? command.substring(0, command.indexOf(' ')) : command);
    for(String msg : plugin.getConfig().getStringList("Whitelisted-Commands")) {
      if(command.equalsIgnoreCase(msg.toLowerCase())) {
        return;
      }
    }
    if(command.equalsIgnoreCase("vd") || event.getMessage().contains("leave") || event.getMessage().contains("stats") || command.equalsIgnoreCase("vda")) {
      return;
    }
    if(event.getPlayer().isOp() || event.getPlayer().hasPermission("villagedefense.command.override")) {
      return;
    }
    event.setCancelled(true);
    event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ONLY_COMMAND_IN_GAME_IS_LEAVE));
  }

  @EventHandler
  public void onDoorDrop(ItemSpawnEvent event) {
    if(event.getEntity().getItemStack().getType() == Utils.getCachedDoor(event.getLocation().getBlock())
        || event.getEntity().getItemStack().getType() == Utils.getCachedDoor(event.getLocation().getBlock())) {
      for(Entity entity : Utils.getNearbyEntities(event.getLocation(), 20)) {
        if(entity instanceof Player && ArenaRegistry.getArena((Player) entity) != null) {
          event.getEntity().remove();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
    if(!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
      return;
    }
    SpecialItem key = plugin.getSpecialItemManager().getSpecialItem(SpecialItemManager.SpecialItems.LOBBY_LEAVE_ITEM.getName());
    if(key == SpecialItem.INVALID_ITEM) {
      return;
    }
    if(key.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
      event.setCancelled(true);
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        plugin.getBungeeManager().connectToHub(event.getPlayer());
        Debugger.debug("{0} has left the arena {1}! Teleported to the Hub server.", event.getPlayer().getName(), arena.getId());
      } else {
        ArenaManager.leaveAttempt(event.getPlayer(), arena);
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
      Arena arena = ArenaRegistry.getArena((Player) projectile.getShooter());
      if(arena != null && arena.equals(ArenaRegistry.getArena((Player) e.getEntity()))) {
        e.setCancelled(true);
      }
    } else if(e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager || e.getEntity() instanceof Wolf) {
      for(Arena a : ArenaRegistry.getArenas()) {
        if(a.getWolves().contains(e.getEntity()) || a.getVillagers().contains(e.getEntity()) || a.getIronGolems().contains(e.getEntity())) {
          e.setCancelled(true);
          return;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFriendHurt(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof Player)) {
      return;
    }
    Arena arena = ArenaRegistry.getArena((Player) e.getDamager());
    if(arena == null) {
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
  public void onZombieHurt(EntityDamageEvent e) {
    if(!(e.getEntity() instanceof Zombie) || !plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled", true)) {
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!arena.getZombies().contains(e.getEntity())) {
        continue;
      }
      MiscUtils.getEntityAttribute((Zombie) e.getEntity(), Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> {
        e.getEntity().setCustomName(StringFormatUtils.getProgressBar((int) ((Zombie) e.getEntity()).getHealth(),
            (int) ai.getValue(),
            50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
      });
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
    Arena arena = ArenaRegistry.getArena((Player) projectile.getShooter());
    if(arena == null || !(e.getEntity() instanceof Player || e.getEntity() instanceof Wolf
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

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Arena arena = ArenaRegistry.getArena((Player) event.getEntity());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.ENDING) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
  public void onBuild(BlockPlaceEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer()) && event.getBlock().getType() != Utils.getCachedDoor(event.getBlock())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCraft(PlayerInteractEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer()) && event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onRottenFleshDrop(InventoryPickupItemEvent e) {
    if(e.getInventory().getType() != InventoryType.HOPPER) {
      return;
    }
    if(e.getItem().getItemStack().getType() != Material.ROTTEN_FLESH) {
      for(Arena arena : ArenaRegistry.getArenas()) {
        if(e.getItem().getWorld().equals(arena.getStartLocation().getWorld())) {
          e.getItem().remove();
          e.getInventory().clear();
          return;
        }
      }
      return;
    }
    for(Entity entity : Utils.getNearbyEntities(e.getItem().getLocation(), 20)) {
      if(!(entity instanceof Player)) {
        continue;
      }
      Arena arena = ArenaRegistry.getArena((Player) entity);
      if(arena == null) {
        continue;
      }
      arena.addOptionValue(ArenaOption.ROTTEN_FLESH_AMOUNT, e.getItem().getItemStack().getAmount());
      e.getItem().remove();
      e.setCancelled(true);
      e.getInventory().clear();
      e.getItem().getLocation().getWorld().spawnParticle(Particle.CLOUD, e.getItem().getLocation(), 50, 2, 2, 2);
      if(!arena.checkLevelUpRottenFlesh() || arena.getOption(ArenaOption.ROTTEN_FLESH_LEVEL) >= 30) {
        return;
      }
      for(Player p : arena.getPlayers()) {
        MiscUtils.getEntityAttribute(p, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(ai.getValue() + 2.0));
        p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ROTTEN_FLESH_LEVEL_UP));
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
        || !(e.getEntity() instanceof Zombie)
        || e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL) {
      return;
    }

    for(Arena arena : ArenaRegistry.getArenas()) {
      if(arena.getZombies().contains(e.getEntity())) {
        e.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
  public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
    if(event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting) {
      if(event.getRemover() instanceof Player && ArenaRegistry.isInArena((Player) event.getRemover())) {
        event.setCancelled(true);
        return;
      }
      if(!(event.getRemover() instanceof Projectile)) {
        return;
      }
      Projectile projectile = (Projectile) event.getRemover();
      if(projectile.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) projectile.getShooter())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onArmorStandDestroy(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof LivingEntity)) {
      return;
    }
    final LivingEntity livingEntity = (LivingEntity) e.getEntity();
    if(livingEntity.getType() != EntityType.ARMOR_STAND) {
      return;
    }
    if(e.getDamager() instanceof Player && ArenaRegistry.isInArena((Player) e.getDamager())) {
      e.setCancelled(true);
    } else if(e.getDamager() instanceof Projectile) {
      Projectile projectile = (Projectile) e.getDamager();
      if(projectile.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) projectile.getShooter())) {
        e.setCancelled(true);
        return;
      }
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteractWithArmorStand(PlayerArmorStandManipulateEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }


}
