/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.handlers.upgrade.EntityUpgradeMenu;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class EntityUpgradeListener implements Listener {

  private final EntityUpgradeMenu upgradeMenu;
  private final Main plugin;

  public EntityUpgradeListener(EntityUpgradeMenu upgradeMenu) {
    this.upgradeMenu = upgradeMenu;
    this.plugin = upgradeMenu.getPlugin();
    upgradeMenu.getPlugin().getServer().getPluginManager().registerEvents(this, upgradeMenu.getPlugin());
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof LivingEntity) || !(event.getDamager() instanceof IronGolem)) {
      return;
    }
    switch(event.getDamager().getType()) {
      case IRON_GOLEM:
        for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
          if(!arena.getIronGolems().contains(event.getDamager())) {
            continue;
          }
          event.setDamage(event.getDamage() + upgradeMenu.getTier(event.getDamager(), upgradeMenu.getUpgrade("Damage")) * 2);
        }
        break;
      case WOLF:
        for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
          if(!arena.getWolves().contains(event.getDamager())) {
            continue;
          }
          int tier = upgradeMenu.getTier(event.getDamager(), upgradeMenu.getUpgrade("Swarm-Awareness"));
          if(tier == 0) {
            return;
          }
          double multiplier = 1;
          for(Entity entity : plugin.getBukkitHelper().getNearbyEntities(event.getDamager().getLocation(), 3)) {
            if(entity instanceof Wolf) {
              multiplier += tier * 0.2;
            }
          }
          event.setDamage(event.getDamage() * multiplier);
        }
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onFinalDefense(EntityDeathEvent event) {
    if(event.getEntityType() != EntityType.IRON_GOLEM) {
      return;
    }

    LivingEntity livingEntity = event.getEntity();

    for(Arena arena : plugin.getArenaRegistry().getPluginArenas()) {
      if(!arena.getIronGolems().contains(livingEntity)) {
        continue;
      }
      int tier = upgradeMenu.getTier(livingEntity, upgradeMenu.getUpgrade("Final-Defense"));
      if(tier == 0) {
        return;
      }
      VersionUtils.sendParticles("EXPLOSION_HUGE", arena.getPlayers(), livingEntity.getLocation(), 5);
      for(Entity en : plugin.getBukkitHelper().getNearbyEntities(livingEntity.getLocation(), tier * 5)) {
        if(CreatureUtils.isEnemy(en)) {
          ((Creature) en).damage(10000.0, livingEntity);
        }
      }
      for(Creature zombie : new ArrayList<>(arena.getEnemies())) {
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 0));
        zombie.damage(0.5, livingEntity);
      }
    }
  }

  @EventHandler
  public void onEntityClick(PlugilyPlayerInteractEntityEvent event) {
    if((event.getRightClicked().getType() != EntityType.IRON_GOLEM && event.getRightClicked().getType() != EntityType.WOLF)
      || VersionUtils.checkOffHand(event.getHand())
      || !event.getPlayer().isSneaking()
      || !event.getRightClicked().hasMetadata("VD_OWNER_UUID")
      || plugin.getArenaRegistry().getArena(event.getPlayer()) == null) {
      return;
    }
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      return;
    }
    UUID uuid = UUID.fromString(event.getRightClicked().getMetadata("VD_OWNER_UUID").get(0).asString());
    if(!event.getPlayer().getUniqueId().equals(uuid)) {
      new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_ENTITIES_CANT_UPGRADE_OTHER").asKey().player(event.getPlayer()).sendPlayer();
      return;
    }
    upgradeMenu.openUpgradeMenu((LivingEntity) event.getRightClicked(), event.getPlayer());
  }

}
