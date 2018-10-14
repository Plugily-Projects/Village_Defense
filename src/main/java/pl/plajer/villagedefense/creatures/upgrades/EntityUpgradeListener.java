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

package pl.plajer.villagedefense.creatures.upgrades;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class EntityUpgradeListener implements Listener {

  private Map<Player, Entity> clickedEntity = new HashMap<>();
  private Main plugin;

  public EntityUpgradeListener(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onEntityClick(PlayerInteractEntityEvent e) {
    try {
      if (ArenaRegistry.getArena(e.getPlayer()) == null || UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator() ||
          (e.getRightClicked().getType() != EntityType.IRON_GOLEM && e.getRightClicked().getType() != EntityType.WOLF) || e.getRightClicked().getCustomName() == null) {
        return;
      }
      EntityUpgradeMenu.openUpgradeMenu(e.getRightClicked(), e.getPlayer());
      clickedEntity.put(e.getPlayer(), e.getRightClicked());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName() ||
          !(e.getWhoClicked() instanceof Player) || clickedEntity.get(e.getWhoClicked()) == null) {
        return;
      }
      if (!e.getInventory().getName().equals(ChatManager.colorMessage("Upgrade-Menu.Title"))) {
        return;
      }
      //todo check position
      /*if (!e.getCurrentItem().getItemMeta().getDisplayName().equals()) {
        e.setCancelled(true);
        return;
      }*/
      Player p = (Player) e.getWhoClicked();
      String upgrade = e.getCurrentItem().getItemMeta().getDisplayName();
      if (upgrade.equals(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Health.Name"))) {
        //todo add pricing
        EntityUpgradeMenu.applyUpgrade(clickedEntity.get(p), EntityUpgrade.HEALTH);
        p.closeInventory();
      } else if (upgrade.equals(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Damage.Name"))) {
        //todo add pricing
        EntityUpgradeMenu.applyUpgrade(clickedEntity.get(p), EntityUpgrade.DAMAGE);
        p.closeInventory();
      } else if (upgrade.equals(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Speed.Name"))) {
        //todo add pricing
        EntityUpgradeMenu.applyUpgrade(clickedEntity.get(p), EntityUpgrade.SPEED);
        p.closeInventory();
      } else {
        p.sendMessage("Not supported yet");
        p.closeInventory();
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    try {
      if (e.getInventory() == null || clickedEntity.get(e.getPlayer()) == null) {
        return;
      }
      if (e.getInventory().getName().equals(ChatManager.colorMessage("Upgrade-Menu.Title"))) {
        clickedEntity.remove(e.getPlayer());
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    clickedEntity.remove(e.getPlayer());
  }

}
