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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajer.villagedefense4.handlers.ChatManager;
import pl.plajer.villagedefense4.handlers.PermissionsManager;
import pl.plajer.villagedefense4.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense4.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense4.user.UserManager;
import pl.plajer.villagedefense4.utils.ArmorHelper;
import pl.plajer.villagedefense4.utils.Utils;
import pl.plajer.villagedefense4.utils.XMaterial;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2018
 */
public class WizardKit extends PremiumKit implements Listener {

  private List<Player> wizardsOnDuty = new ArrayList<>();
  private Main plugin;

  public WizardKit(Main plugin) {
    setName(ChatManager.colorMessage("Kits.Wizard.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Wizard.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.wizard");
  }

  @Override
  public void giveKitItems(Player player) {
    ItemStack wizardStaff = new ItemStack(Material.BLAZE_ROD, 1);
    List<String> staffLore = Utils.splitString(ChatManager.colorMessage("Kits.Wizard.Staff-Item-Lore"), 40);
    this.setItemNameAndLore(wizardStaff, ChatManager.colorMessage("Kits.Wizard.Staff-Item-Name"), staffLore.toArray(new String[0]));
    player.getInventory().addItem(wizardStaff);

    ItemStack essenceOfDarkness = new ItemStack(XMaterial.INK_SAC.parseMaterial(), 4);
    List<String> essenceLore = Utils.splitString(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Lore"), 40);
    this.setItemNameAndLore(essenceOfDarkness, ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"), essenceLore.toArray(new String[0]));
    player.getInventory().addItem(essenceOfDarkness);

    ArmorHelper.setColouredArmor(Color.GRAY, player);
    player.getInventory().addItem(new ItemStack(Material.SADDLE));

  }

  @Override
  public Material getMaterial() {
    return Material.BLAZE_ROD;
  }

  @Override
  public void reStock(Player player) {
    ItemStack essenceOfDarkness = new ItemStack(XMaterial.INK_SAC.parseMaterial(), 1);
    List<String> essenceLore = Utils.splitString(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Lore"), 40);
    this.setItemNameAndLore(essenceOfDarkness, ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"), essenceLore.toArray(new String[0]));
    player.getInventory().addItem(essenceOfDarkness);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    wizardsOnDuty.remove(e.getPlayer());
  }

  @EventHandler
  public void onWizardDamage(EntityDamageByEntityEvent e) {
    try {
      if (e.getDamager() instanceof Zombie && e.getEntity() instanceof Player) {
        if (!wizardsOnDuty.contains(e.getEntity())) {
          return;
        }
        if (ArenaRegistry.getArena((Player) e.getEntity()) == null) {
          return;
        }
        ((Zombie) e.getDamager()).damage(2.0, e.getEntity());
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onStaffUse(PlayerInteractEvent e) {
    try {
      if (UserManager.getUser(e.getPlayer().getUniqueId()) == null) {
        return;
      }
      if (ArenaRegistry.getArena(e.getPlayer()) == null) {
        return;
      }
      if (!(UserManager.getUser(e.getPlayer().getUniqueId()).getKit() instanceof WizardKit)) {
        return;
      }
      final Player p = e.getPlayer();
      ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
      if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
        if (is.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"))) {
          if (UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("essence") > 0 && !UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("essence")));
            e.getPlayer().sendMessage(msgstring);
            return;
          }
          wizardsOnDuty.add(p);
          if (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() > (p.getHealth() + 3)) {
            p.setHealth(p.getHealth() + 3);
          } else {
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
          }
          if (is.getAmount() <= 1) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
          } else {
            p.getInventory().getItemInMainHand().setAmount(is.getAmount() - 1);
          }
          p.setGlowing(true);
          new BukkitRunnable() {
            @Override
            public void run() {
              Location loc = p.getLocation();
              loc.add(0, 0.8, 0);
              p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, loc, 5, 0, 0, 0, 0);
              if (!wizardsOnDuty.contains(p) || !ArenaRegistry.isInArena(p)) {
                this.cancel();
              }
            }
          }.runTaskTimer(plugin, 0, 2);
          for (Entity en : p.getNearbyEntities(2, 2, 2)) {
            if (en instanceof Zombie) {
              ((Zombie) en).damage(9.0, p);
            }
          }
          Bukkit.getScheduler().runTaskLater(plugin, () -> {
            p.setGlowing(false);
            wizardsOnDuty.remove(p);
          }, 20 * 15);
          UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("essence", 15);
        } else if (is.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Kits.Wizard.Staff-Item-Name"))) {
          if (UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Spectator-Warning"));
            return;
          }
          if (UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("wizard_staff") > 0 && !UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("wizard_staff")));
            e.getPlayer().sendMessage(msgstring);
            return;
          }
          new BukkitRunnable() {
            double t = 0;
            Location loc = p.getLocation();
            Vector direction = loc.getDirection().normalize();

            @Override
            public void run() {
              t += 0.5;
              double x = direction.getX() * t;
              double y = direction.getY() * t + 1.5;
              double z = direction.getZ() * t;
              loc.add(x, y, z);
              p.getWorld().spawnParticle(Particle.TOWN_AURA, loc, 5);
              for (Entity en : loc.getChunk().getEntities()) {
                if (!(en instanceof LivingEntity && en instanceof Zombie)) {
                  continue;
                }
                if (en.getLocation().distance(loc) < 1.5) {
                  if (!en.equals(p)) {
                    ((LivingEntity) en).damage(6.0, p);
                    en.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, en.getLocation(), 2, 0.5, 0.5, 0.5, 0);
                  }
                }
              }
              loc.subtract(x, y, z);
              if (t > 40) {
                this.cancel();
              }
            }
          }.runTaskTimer(plugin, 0, 1);
          UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("wizard_staff", 1);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}
