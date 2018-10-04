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

package pl.plajer.villagedefense4.kits.premium;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.handlers.PermissionsManager;
import pl.plajer.villagedefense4.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense4.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense4.user.User;
import pl.plajer.villagedefense4.user.UserManager;
import pl.plajer.villagedefense4.utils.ArmorHelper;
import pl.plajer.villagedefense4.utils.Utils;
import pl.plajer.villagedefense4.utils.WeaponHelper;
import pl.plajer.villagedefense4.utils.XMaterial;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {

  public MedicKit(Main plugin) {
    setName(ChatManager.colorMessage("Kits.Medic.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Medic.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.medic");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(Utils.getPotion(PotionType.REGEN, 1, true));
  }

  @Override
  public Material getMaterial() {
    return Material.GHAST_TEAR;
  }

  @Override
  public void reStock(Player player) {
  }

  @EventHandler
  public void onZombieHit(EntityDamageByEntityEvent e) {
    try {
      if (!(e.getEntity() instanceof Zombie && e.getDamager() instanceof Player)) {
        return;
      }
      User user = UserManager.getUser(e.getDamager().getUniqueId());
      if (!(user.getKit() instanceof MedicKit)) {
        return;
      }
      if (Math.random() <= 0.1) {
        for (Entity entity : user.toPlayer().getNearbyEntities(5, 5, 5)) {
          if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() > (player.getHealth() + 1)) {
              player.setHealth(player.getHealth() + 1);
            } else {
              player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }
            player.getEyeLocation().getWorld().spawnParticle(Particle.HEART, player.getLocation(), 20);
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }
}
