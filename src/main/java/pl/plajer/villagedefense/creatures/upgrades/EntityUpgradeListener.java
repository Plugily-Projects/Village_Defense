/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class EntityUpgradeListener implements Listener {

  private Map<Player, Entity> clickedEntity = new HashMap<>();
  private EntityUpgradeMenu upgradeMenu;

  public EntityUpgradeListener(EntityUpgradeMenu upgradeMenu) {
    this.upgradeMenu = upgradeMenu;
    upgradeMenu.getPlugin().getServer().getPluginManager().registerEvents(this, upgradeMenu.getPlugin());
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    try {
      if (!(e.getDamager() instanceof LivingEntity) || !(e.getDamager() instanceof IronGolem)) {
        return;
      }
      switch (e.getDamager().getType()) {
        case IRON_GOLEM:
          for (Arena arena : ArenaRegistry.getArenas()) {
            if (!arena.getIronGolems().contains(e.getDamager())) {
              continue;
            }
            e.setDamage(e.getDamage() + upgradeMenu.getTier(e.getDamager(), upgradeMenu.getUpgrade("DAMAGE")) * 2);
          }
          break;
        case WOLF:
          for (Arena arena : ArenaRegistry.getArenas()) {
            if (!arena.getWolfs().contains(e.getDamager())) {
              continue;
            }
            int tier = upgradeMenu.getTier(e.getDamager(), upgradeMenu.getUpgrade("SWARM_AWARENESS"));
            if (tier == 0) {
              return;
            }
            double multiplier = 1;
            for (Entity en : Utils.getNearbyEntities(e.getDamager().getLocation(), 3)) {
              if (en instanceof Wolf) {
                multiplier += tier * 0.2;
              }
            }
            e.setDamage(e.getDamage() * multiplier);
          }
          break;
      }
    } catch (Exception ex) {
      new ReportedException(upgradeMenu.getPlugin(), ex);
    }
  }

  @EventHandler
  public void onFinalDefense(EntityDeathEvent e) {
    try {
      if (!(e.getEntity() instanceof IronGolem)) {
        return;
      }
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (!arena.getIronGolems().contains(e.getEntity())) {
          continue;
        }
        int tier = upgradeMenu.getTier(e.getEntity(), upgradeMenu.getUpgrade("FINAL_DEFENSE"));
        if (tier == 0) {
          return;
        }
        e.getEntity().getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getEntity().getLocation(), 5);
        for (Entity en : Utils.getNearbyEntities(e.getEntity().getLocation(), tier * 5)) {
          if (en instanceof Zombie) {
            ((Zombie) en).damage(10000.0, e.getEntity());
          }
        }
        for (Zombie zombie : arena.getZombies()) {
          zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 0));
          zombie.damage(0.5, e.getEntity());
        }
      }
    } catch (Exception ex) {
      new ReportedException(upgradeMenu.getPlugin(), ex);
    }
  }

  @EventHandler
  public void onEntityClick(PlayerInteractEntityEvent e) {
    try {
      if (ArenaRegistry.getArena(e.getPlayer()) == null || upgradeMenu.getPlugin().getUserManager().getUser(e.getPlayer()).isSpectator()
          || (e.getRightClicked().getType() != EntityType.IRON_GOLEM && e.getRightClicked().getType() != EntityType.WOLF) || e.getRightClicked().getCustomName() == null) {
        return;
      }
      if (!e.getPlayer().isSneaking()) {
        return;
      }
      //to cancel wolves sitting down
      e.setCancelled(true);
      upgradeMenu.openUpgradeMenu((LivingEntity) e.getRightClicked(), e.getPlayer());
      clickedEntity.put(e.getPlayer(), e.getRightClicked());
    } catch (Exception ex) {
      new ReportedException(upgradeMenu.getPlugin(), ex);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || !Utils.isNamed(e.getCurrentItem()) || !(e.getWhoClicked() instanceof Player) || clickedEntity.get(e.getWhoClicked()) == null) {
        return;
      }
      if (!e.getInventory().getName().equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Title"))) {
        return;
      }
      e.setCancelled(true);
      Player p = (Player) e.getWhoClicked();
      String upgrade = e.getCurrentItem().getItemMeta().getDisplayName();
      if (upgrade.equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgrades.Health.Name"))) {
        //todo add pricing
        upgradeMenu.applyUpgrade(clickedEntity.get(p), upgradeMenu.getUpgrade("HEALTH"));
        p.sendMessage(upgradeMenu.getPlugin().getChatManager().getPrefix() +
            upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%",
                String.valueOf(upgradeMenu.getTier(clickedEntity.get(p), upgradeMenu.getUpgrade("HEALTH")))));
        p.closeInventory();
      } else if (upgrade.equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgrades.Damage.Name"))) {
        //todo add pricing
        upgradeMenu.applyUpgrade(clickedEntity.get(p), upgradeMenu.getUpgrade("DAMAGE"));
        p.sendMessage(upgradeMenu.getPlugin().getChatManager().getPrefix() +
            upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%",
                String.valueOf(upgradeMenu.getTier(clickedEntity.get(p), upgradeMenu.getUpgrade("DAMAGE")))));
        p.closeInventory();
      } else if (upgrade.equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgrades.Speed.Name"))) {
        //todo add pricing
        upgradeMenu.applyUpgrade(clickedEntity.get(p), upgradeMenu.getUpgrade("SPEED"));
        p.sendMessage(upgradeMenu.getPlugin().getChatManager().getPrefix() +
            upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%",
                String.valueOf(upgradeMenu.getTier(clickedEntity.get(p), upgradeMenu.getUpgrade("SPEED")))));
        p.closeInventory();
      } else if (upgrade.equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgrades.Final-Defense.Name"))) {
        //todo add pricing
        upgradeMenu.applyUpgrade(clickedEntity.get(p), upgradeMenu.getUpgrade("FINAL_DEFENSE"));
        p.sendMessage(upgradeMenu.getPlugin().getChatManager().getPrefix() +
            upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%",
                String.valueOf(upgradeMenu.getTier(clickedEntity.get(p), upgradeMenu.getUpgrade("FINAL_DEFENSE")))));
        p.closeInventory();
      } else if (upgrade.equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgrades.Swarm-Awareness.Name"))) {
        //todo add pricing
        upgradeMenu.applyUpgrade(clickedEntity.get(p), upgradeMenu.getUpgrade("SWARM_AWARENESS"));
        p.sendMessage(upgradeMenu.getPlugin().getChatManager().getPrefix() +
            upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%",
                String.valueOf(upgradeMenu.getTier(clickedEntity.get(p), upgradeMenu.getUpgrade("SWARM_AWARENESS")))));
        p.closeInventory();
      }
    } catch (Exception ex) {
      new ReportedException(upgradeMenu.getPlugin(), ex);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    try {
      if (e.getInventory() == null || clickedEntity.get(e.getPlayer()) == null) {
        return;
      }
      if (e.getInventory().getName().equals(upgradeMenu.getPlugin().getChatManager().colorMessage("Upgrade-Menu.Title"))) {
        clickedEntity.remove(e.getPlayer());
      }
    } catch (Exception ex) {
      new ReportedException(upgradeMenu.getPlugin(), ex);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    clickedEntity.remove(e.getPlayer());
  }

}
