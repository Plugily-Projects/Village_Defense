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

package pl.plajer.villagedefense.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.arena.ArenaState;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.ShopManager;
import pl.plajer.villagedefense.handlers.items.SpecialItem;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 16/08/2014.
 */
public class Events implements Listener {

  private Main plugin;

  public Events(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSpawn(CreatureSpawnEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (!event.getEntity().getWorld().equals(arena.getStartLocation().getWorld()) || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
          continue;
        }
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onItemPickup(PlayerExpChangeEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) == null) {
        return;
      }
      int amount = (int) Math.ceil(event.getAmount() * 1.6);
      User user = UserManager.getUser(event.getPlayer().getUniqueId());
      event.setAmount(amount);
      if (user.isSpectator()) {
        event.setAmount(0);
        return;
      }
      //bonus orbs with custom permissions
      for (String perm : plugin.getCustomPermissions().keySet()) {
        if (event.getPlayer().hasPermission(perm)) {
          amount = +(int) Math.ceil(event.getAmount() * (plugin.getCustomPermissions().get(perm) / 100));
          user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * (plugin.getCustomPermissions().get(perm) / 100)));
        }
      }

      if (event.getPlayer().hasPermission(PermissionsManager.getElite())) {
        amount += (int) Math.ceil(event.getAmount() * 1.5);
        user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 1.5));
      } else if (event.getPlayer().hasPermission(PermissionsManager.getMvp())) {
        amount += (int) Math.ceil(event.getAmount() * 1.0);
        user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 1.0));
      } else if (event.getPlayer().hasPermission(PermissionsManager.getVip())) {
        amount += (int) Math.ceil(event.getAmount() * 0.5);
        user.addStat(StatsStorage.StatisticType.ORBS, (int) Math.ceil(event.getAmount() * 0.5));
      } else {
        amount += event.getAmount();
        user.addStat(StatsStorage.StatisticType.ORBS, event.getAmount());
      }
      event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Orbs-Pickup").replace("%number%", String.valueOf(amount)));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onPickUp(PlayerPickupItemEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onKitMenuItemClick(InventoryClickEvent event) {
    try {
      ItemStack stack = event.getCurrentItem();
      Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
      if (arena == null) {
        return;
      }
      if (!Utils.isNamed(stack) || stack.getType() != plugin.getKitManager().getMaterial()
          || !stack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName())) {
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onKitMenuItemClickWithCursor(InventoryClickEvent event) {
    try {
      ItemStack stack = event.getCursor();
      Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
      if (arena == null) {
        return;
      }
      if (!Utils.isNamed(stack) || stack.getType() != plugin.getKitManager().getMaterial()
          || !stack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getKitManager().getItemName())) {
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
        event.setCancelled(true);
        return;
      }
      if (event.getItemDrop().getItemStack().getType() == Material.SADDLE) {
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onExplosionCancel(EntityExplodeEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (arena.getStartLocation().getWorld().getName().equals(event.getLocation().getWorld().getName())
            && arena.getStartLocation().distance(event.getLocation()) < 300) {
          event.blockList().clear();
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      User user = UserManager.getUser(event.getPlayer().getUniqueId());
      if (user.isSpectator()) {
        event.setCancelled(true);
        return;
      }
      if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SADDLE) {
        if (event.getRightClicked().getType() == EntityType.IRON_GOLEM || event.getRightClicked().getType() == EntityType.VILLAGER || event.getRightClicked().getType() == EntityType.WOLF) {
          event.getRightClicked().setPassenger(event.getPlayer());
          event.setCancelled(true);
          return;
        }
      }
      if (event.getRightClicked().getType() == EntityType.VILLAGER) {
        event.setCancelled(true);
        ShopManager.openShop(event.getPlayer());
      } else if (event.getRightClicked().getType() == EntityType.IRON_GOLEM) {
        IronGolem ironGolem = (IronGolem) event.getRightClicked();
        if (ironGolem.getCustomName() != null && ironGolem.getCustomName().contains(event.getPlayer().getName())) {
          event.getRightClicked().setPassenger(event.getPlayer());
        } else {
          event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Cant-Ride-Others-Golem"));
        }
      } else if (event.getRightClicked().getType() == EntityType.WOLF) {
        Wolf wolf = (Wolf) event.getRightClicked();
        if (wolf.getCustomName() != null && wolf.getCustomName().contains(event.getPlayer().getName())) {
          event.getRightClicked().setPassenger(event.getPlayer());
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      if (!plugin.getConfig().getBoolean("Block-Commands-In-Game", true)) {
        return;
      }
      for (String msg : plugin.getConfig().getStringList("Whitelisted-Commands")) {
        if (event.getMessage().contains(msg)) {
          return;
        }
      }
      if (event.getMessage().startsWith("/vd") || event.getMessage().contains("leave") || event.getMessage().contains("stats") || event.getMessage().startsWith("/vda")) {
        return;
      }
      if (event.getPlayer().isOp() || event.getPlayer().hasPermission("villagedefense.command.override")) {
        return;
      }
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDoorDrop(ItemSpawnEvent event) {
    try {
      //todo check
      if (event.getEntity().getItemStack().getType() == XMaterial.OAK_DOOR.parseMaterial()) {
        for (Entity entity : Utils.getNearbyEntities(event.getLocation(), 20)) {
          if (!(entity instanceof Player)) {
            continue;
          }
          if (ArenaRegistry.getArena((Player) entity) != null) {
            event.getEntity().remove();
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerInteractEvent event) {
    try {
      if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
      if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
        return;
      }
      SpecialItem key = SpecialItemManager.getSpecialItem("Leave");
      if (key == null) {
        return;
      }
      if (key.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
        event.setCancelled(true);
        if (plugin.isBungeeActivated()) {
          plugin.getBungeeManager().connectToHub(event.getPlayer());
        } else {
          ArenaManager.leaveAttempt(event.getPlayer(), arena);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onEntityCombust(EntityCombustByEntityEvent e) {
    try {
      if (!(e.getCombuster() instanceof Arrow)) {
        return;
      }
      Arrow arrow = (Arrow) e.getCombuster();
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }
      if (e.getEntity() instanceof Player) {
        if (ArenaRegistry.getArena((Player) arrow.getShooter()) != null && ArenaRegistry.getArena((Player) arrow.getShooter()).equals(ArenaRegistry.getArena((Player) e.getEntity()))) {
          e.setCancelled(true);
        }
      } else if (e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager || e.getEntity() instanceof Wolf) {
        for (Arena a : ArenaRegistry.getArenas()) {
          if (e.getEntity() instanceof IronGolem) {
            if (a.getIronGolems().contains(e.getEntity())) {
              e.setCancelled(true);
            }
          } else if (e.getEntity() instanceof Villager) {
            if (a.getVillagers().contains(e.getEntity())) {
              e.setCancelled(true);
            }
          } else {
            if (a.getWolfs().contains(e.getEntity())) {
              e.setCancelled(true);
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFriendHurt(EntityDamageByEntityEvent e) {
    try {
      if (!(e.getDamager() instanceof Player)) {
        return;
      }
      Arena arena = ArenaRegistry.getArena((Player) e.getDamager());
      if (arena == null) {
        return;
      }
      if (UserManager.getUser(e.getDamager().getUniqueId()).isSpectator()) {
        e.setCancelled(true);
        return;
      }
      if (!(e.getEntity() instanceof Player || e.getEntity() instanceof Wolf || e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager)) {
        return;
      }
      e.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onZombieHurt(EntityDamageEvent e) {
    try {
      if (plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled", true)) {
        if (!(e.getEntity() instanceof Zombie)) {
          return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (!arena.getZombies().contains(e.getEntity())) {
            continue;
          }
          e.getEntity().setCustomName(MinigameUtils.getProgressBar((int) ((Zombie) e.getEntity()).getHealth(),
              (int) ((Zombie) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(),
              50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSecond(EntityDamageByEntityEvent e) {
    try {
      User user = UserManager.getUser((e.getDamager().getUniqueId()));
      if (user.isSpectator()) {
        e.setCancelled(true);
        return;
      }
      if (!(e.getDamager() instanceof Arrow)) {
        return;
      }
      Arrow arrow = (Arrow) e.getDamager();
      if (arrow.getShooter() == null) {
        return;
      }
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }
      Arena arena = ArenaRegistry.getArena((Player) arrow.getShooter());
      if (arena == null) {
        return;
      }
      if (!(e.getEntity() instanceof Player || e.getEntity() instanceof Wolf || e.getEntity() instanceof IronGolem || e.getEntity() instanceof Villager)) {
        return;
      }
      e.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onEntityLeash(PlayerLeashEntityEvent event) {
    if (event.getEntity() instanceof Villager) {
      ((Villager) event.getEntity()).setLeashHolder(event.getPlayer());
    }
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    try {
      if (event.getEntity().getType() != EntityType.PLAYER) {
        return;
      }
      Arena arena = ArenaRegistry.getArena((Player) event.getEntity());
      if (arena == null) {
        return;
      }
      if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.ENDING) {
        event.setFoodLevel(20);
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onShop(InventoryClickEvent e) {
    try {
      if (!(e.getWhoClicked() instanceof Player)) {
        return;
      }
      Player player = (Player) e.getWhoClicked();
      User user = UserManager.getUser(player.getUniqueId());
      Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      ItemStack stack = e.getCurrentItem();
      if (arena == null || e.getInventory().getName() == null
          || !e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Shop-GUI-Name"))) {
        return;
      }
      e.setCancelled(true);
      if (stack == null || !stack.hasItemMeta() || !stack.getItemMeta().hasLore()) {
        return;
      }
      String string = ChatColor.stripColor(stack.getItemMeta().getLore().get(0));
      if (!(string.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || string.contains("orbs"))) {
        boolean b = false;
        for (String s : stack.getItemMeta().getLore()) {
          if (string.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop")) || string.contains("orbs")) {
            string = s;
            b = true;
          }
        }
        if (!b) {
          return;
        }
      }
      int price = Integer.parseInt(string.split(" ")[0]);
      if (price > user.getStat(StatsStorage.StatisticType.ORBS)) {
        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
        return;
      }
      if (Utils.isNamed(stack)) {
        if (stack.getItemMeta().getDisplayName().contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Golem-Item-Name")) || stack.getItemMeta().getDisplayName().contains(ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "language").getString("In-Game.Messages.Shop-Messages.Golem-Item-Name")))) {
          int i = 0;
          for (IronGolem golem : arena.getIronGolems()) {
            if (golem.getCustomName().equals(ChatManager.colorMessage("In-Game.Spawned-Golem-Name").replace("%player%", player.getName()))) {
              i++;
            }
          }
          if (i >= plugin.getConfig().getInt("Golems-Spawn-Limit", 15)) {
            e.getWhoClicked().sendMessage(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Mob-Limit-Reached")
                .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Golems-Spawn-Limit", 15))));
            return;
          }
          arena.spawnGolem(arena.getStartLocation(), player);
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Golem-Spawned"));
          user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - price);
          return;
        } else if (stack.getItemMeta().getDisplayName().contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Wolf-Item-Name")) || stack.getItemMeta().getDisplayName().contains(ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "language").getString("In-Game.Messages.Shop-Messages.Wolf-Item-Name")))) {
          int i = 0;
          for (Wolf wolf : arena.getWolfs()) {
            if (wolf.getCustomName().equals(ChatManager.colorMessage("In-Game.Spawned-Wolf-Name").replace("%player%", player.getName()))) {
              i++;
            }
          }
          if (i >= plugin.getConfig().getInt("Wolves-Spawn-Limit", 20)) {
            e.getWhoClicked().sendMessage(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Mob-Limit-Reached")
                .replace("%amount%", String.valueOf(plugin.getConfig().getInt("Wolves-Spawn-Limit", 20))));
            return;
          }
          arena.spawnWolf(arena.getStartLocation(), player);
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Wolf-Spawned"));
          user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - price);
          return;
        }
      }

      ItemStack itemStack = stack.clone();
      ItemMeta itemMeta = itemStack.getItemMeta();
      Iterator<String> lore = itemMeta.getLore().iterator();
      while (lore.hasNext()) {
        String next = lore.next();
        if (next.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
          lore.remove();
        }
      }
      List<String> newLore = new ArrayList<>();
      lore.forEachRemaining(newLore::add);
      itemMeta.setLore(newLore);
      itemStack.setItemMeta(itemMeta);
      player.getInventory().addItem(itemStack);
      user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - price);
      arena.setTotalOrbsSpent(arena.getTotalOrbsSpent() + price);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protecc our game (i didn't set it because my test server was destroyed, n-no......)
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if (!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protecc our game (i didn't set it because my test server was destroyed, n-no......)
  public void onBuild(BlockPlaceEvent event) {
    if (!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onCraft(PlayerInteractEvent event) {
    if (!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    if (event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onRottenFleshDrop(InventoryPickupItemEvent e) {
    try {
      if (e.getInventory().getType() != InventoryType.HOPPER) {
        return;
      }
      if (e.getItem().getItemStack().getType() != Material.ROTTEN_FLESH) {
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (e.getItem().getWorld().equals(arena.getStartLocation().getWorld())) {
            e.getItem().remove();
            e.getInventory().clear();
            return;
          }
        }
        return;
      }
      for (Entity entity : Utils.getNearbyEntities(e.getItem().getLocation(), 20)) {
        if (!(entity instanceof Player)) {
          continue;
        }
        Arena arena = ArenaRegistry.getArena((Player) entity);
        if (arena == null) {
          continue;
        }
        arena.addRottenFlesh(e.getItem().getItemStack().getAmount());
        e.getItem().remove();
        e.setCancelled(true);
        e.getInventory().clear();
        e.getItem().getLocation().getWorld().spawnParticle(Particle.CLOUD, e.getItem().getLocation(), 50, 2, 2, 2);
        if (!arena.checkLevelUpRottenFlesh()) {
          return;
        }
        for (Player p : arena.getPlayers()) {
          p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2.0);
          p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Rotten-Flesh-Level-Up"));
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  /**
   * Triggers when something combusts in the world.
   * Thanks to @HomieDion for part of this class!
   */
  @EventHandler(ignoreCancelled = true)
  public void onCombust(final EntityCombustEvent e) {
    try {
      // Ignore if this is caused by an event lower down the chain.
      if (e instanceof EntityCombustByEntityEvent || e instanceof EntityCombustByBlockEvent
          || !(e.getEntity() instanceof Zombie)
          || e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL) {
        return;
      }

      for (Arena arena : ArenaRegistry.getArenas()) {
        if (arena.getZombies().contains(e.getEntity())) {
          e.setCancelled(true);
          return;
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
