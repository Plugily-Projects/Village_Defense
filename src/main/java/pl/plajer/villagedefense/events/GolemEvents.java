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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.api.event.player.VillagePlayerGolemUpgradeEvent;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;

public class GolemEvents implements Listener {

  private Map<Player, IronGolem> clickedGolem = new HashMap<>();
  private Main plugin;

  public GolemEvents(Main plugin) {
    this.plugin = plugin;
    if (plugin.getConfig().getBoolean("Golem-Upgrades-Enabled", true)) {
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
      Main.debug(Main.LogLevel.INFO, "Golem upgrades successfully registered!");
    }
  }

  @EventHandler
  public void onGolemClick(PlayerInteractEntityEvent e) {
    try {
      if (!ArenaRegistry.isInArena(e.getPlayer()) || !(e.getRightClicked() instanceof IronGolem)) {
        return;
      }
      if (UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
        return;
      }
      if (e.getRightClicked().getCustomName() == null || !(e.getRightClicked().getCustomName().contains(e.getPlayer().getName()))) {
        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Cant-Upgrade-Others"));
        return;
      }
      Inventory inv = Bukkit.createInventory(null, 3 * 9, ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"));
      for (int i = 1; i <= 3; i++) {
        ItemStack golemHealthUpgrade = new ItemStack(Material.IRON_INGOT, i);
        ItemMeta meta = golemHealthUpgrade.getItemMeta();
        meta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i));
        meta.setLore(Arrays.asList(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i + "-Lore")
                .replace("%cost%", plugin.getConfig().getString("Golem-Upgrade-Tier" + i + "-Cost")).split(";")));
        golemHealthUpgrade.setItemMeta(meta);
        inv.setItem((i * 3) + 7, golemHealthUpgrade);
      }

      ItemStack golemHeal = new ItemStack(Material.GOLD_BLOCK, 1);
      ItemMeta healMeta = golemHeal.getItemMeta();
      healMeta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal"));
      healMeta.setLore(Arrays.asList(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal-Lore")
              .replace("%cost%", plugin.getConfig().getString("Golem-Upgrade-Heal-Cost")).split(";")));
      golemHeal.setItemMeta(healMeta);

      ItemStack golemHealth = new ItemStack(Material.BOOK, 1);
      ItemMeta healthMeta = golemHealth.getItemMeta();
      healthMeta.setDisplayName(ChatManager.colorMessage("In-Game.Golem-Upgrades.Health").replace("%health%", String.valueOf(((IronGolem) e.getRightClicked()).getHealth())));
      golemHealth.setItemMeta(healthMeta);
      inv.setItem(4, golemHealth);
      inv.setItem(22, golemHeal);
      e.getPlayer().openInventory(inv);
      clickedGolem.put(e.getPlayer(), (IronGolem) e.getRightClicked());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || e.getCurrentItem() == null || clickedGolem.get(e.getWhoClicked()) == null
              || !(e.getWhoClicked() instanceof Player)) {
        return;
      }
      if (e.getInventory().getName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"))) {
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) {
          return;
        }
        Player p = (Player) e.getWhoClicked();
        double golemHealth = clickedGolem.get(p).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        Integer orbs = UserManager.getUser(p.getUniqueId()).getStat(StatsStorage.StatisticType.ORBS);
        e.setCancelled(true);
        //checking for health upgrades
        for (int i = 1; i <= 3; i++) {
          if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Tier" + i))) {
            if (golemHealth == 160.0) {
              p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Already-Purchased"));
              p.closeInventory();
              return;
            }
            Integer price = plugin.getConfig().getInt("Golem-Upgrade-Tier" + i + "-Cost");
            if (orbs >= price) {
              if (golemHealth >= 100.0 + (20 * i)) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Already-Purchased"));
                p.closeInventory();
                return;
              }
              clickedGolem.get(p).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0 + (20.0 * i));
              clickedGolem.get(p).setHealth(clickedGolem.get(p).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

              VillagePlayerGolemUpgradeEvent event = new VillagePlayerGolemUpgradeEvent(ArenaRegistry.getArena(p), clickedGolem.get(p), p, golemHealth);
              Bukkit.getPluginManager().callEvent(event);

              p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Applied"));
              UserManager.getUser(p.getUniqueId()).setStat(StatsStorage.StatisticType.ORBS, orbs - price);
              clickedGolem.get(p).getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 20);
              clickedGolem.remove(p);
              p.closeInventory();
              return;
            } else {
              p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
              p.closeInventory();
              return;
            }
          }
        }
        //checking for heal upgrade
        if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal"))) {
          if (clickedGolem.get(p).getHealth() == clickedGolem.get(p).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Heal-Full"));
            p.closeInventory();
            return;
          }
          Integer price = plugin.getConfig().getInt("Golem-Upgrade-Heal-Cost", 150);
          if (orbs >= price) {
            clickedGolem.get(p).setHealth(clickedGolem.get(p).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

            VillagePlayerGolemUpgradeEvent event = new VillagePlayerGolemUpgradeEvent(ArenaRegistry.getArena(p), clickedGolem.get(p), p, golemHealth);
            Bukkit.getPluginManager().callEvent(event);

            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Applied"));
            UserManager.getUser(p.getUniqueId()).setStat(StatsStorage.StatisticType.ORBS, orbs - price);
            clickedGolem.get(p).getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 20);
            clickedGolem.remove(p);
            p.closeInventory();
          } else {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Not-Enough-Orbs"));
            p.closeInventory();
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    try {
      if (e.getInventory() == null || clickedGolem.get(e.getPlayer()) == null) {
        return;
      }
      if (e.getInventory().getName().equals(ChatManager.colorMessage("In-Game.Golem-Upgrades.Upgrade-Inventory"))) {
        clickedGolem.remove(e.getPlayer());
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
