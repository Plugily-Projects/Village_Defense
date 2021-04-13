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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEntityEvent;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.upgrade.EntityUpgradeMenu;
import plugily.projects.villagedefense.utils.Utils;

import java.util.ArrayList;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class EntityUpgradeListener implements Listener {

  private final EntityUpgradeMenu upgradeMenu;

  public EntityUpgradeListener(EntityUpgradeMenu upgradeMenu) {
    this.upgradeMenu = upgradeMenu;
    upgradeMenu.getPlugin().getServer().getPluginManager().registerEvents(this, upgradeMenu.getPlugin());
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof LivingEntity) || !(e.getDamager() instanceof IronGolem)) {
      return;
    }
    switch(e.getDamager().getType()) {
      case IRON_GOLEM:
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(!arena.getIronGolems().contains(e.getDamager())) {
            continue;
          }
          e.setDamage(e.getDamage() + upgradeMenu.getTier(e.getDamager(), upgradeMenu.getUpgrade("Damage")) * 2);
        }
        break;
      case WOLF:
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(!arena.getWolves().contains(e.getDamager())) {
            continue;
          }
          int tier = upgradeMenu.getTier(e.getDamager(), upgradeMenu.getUpgrade("Swarm-Awareness"));
          if(tier == 0) {
            return;
          }
          double multiplier = 1;
          for(Entity en : Utils.getNearbyEntities(e.getDamager().getLocation(), 3)) {
            if(en instanceof Wolf) {
              multiplier += tier * 0.2;
            }
          }
          e.setDamage(e.getDamage() * multiplier);
        }
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onFinalDefense(EntityDeathEvent e) {
    LivingEntity entity = e.getEntity();
    if(!(entity instanceof IronGolem)) {
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!arena.getIronGolems().contains(entity)) {
        continue;
      }
      int tier = upgradeMenu.getTier(entity, upgradeMenu.getUpgrade("Final-Defense"));
      if(tier == 0) {
        return;
      }
      VersionUtils.sendParticles("EXPLOSION_HUGE", arena.getPlayers(), entity.getLocation(), 5);
      for(Entity en : Utils.getNearbyEntities(entity.getLocation(), tier * 5)) {
        if(en instanceof Zombie) {
          ((Zombie) en).damage(10000.0, entity);
        }
      }
      for(Zombie zombie : new ArrayList<>(arena.getZombies())) {
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 0));
        zombie.damage(0.5, entity);
      }
    }
  }

  @EventHandler
  public void onEntityClick(CBPlayerInteractEntityEvent e) {
    if(ArenaRegistry.getArena(e.getPlayer()) == null || upgradeMenu.getPlugin().getUserManager().getUser(e.getPlayer()).isSpectator()
        || (e.getRightClicked().getType() != EntityType.IRON_GOLEM && e.getRightClicked().getType() != EntityType.WOLF) || e.getRightClicked().getCustomName() == null) {
      return;
    }
    if(VersionUtils.checkOffHand(e.getHand()) || !e.getPlayer().isSneaking()) {
      return;
    }
    upgradeMenu.openUpgradeMenu((LivingEntity) e.getRightClicked(), e.getPlayer());
  }

}
