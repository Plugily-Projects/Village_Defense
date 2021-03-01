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

package plugily.projects.villagedefense.kits.premium;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.PremiumKit;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2018
 */
public class WizardKit extends PremiumKit implements Listener {

  private final List<Player> wizardsOnDuty = new ArrayList<>();

  public WizardKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.wizard");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(new ItemBuilder(getMaterial())
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_STAFF_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_STAFF_ITEM_LORE), 40))
        .build());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.INK_SAC.parseMaterial(), 4))
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_ESSENCE_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_ESSENCE_ITEM_LORE), 40))
        .build());

    ArmorHelper.setColouredArmor(Color.GRAY, player);
    player.getInventory().addItem(new ItemStack(Material.SADDLE));

  }

  @Override
  public Material getMaterial() {
    return Material.BLAZE_ROD;
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.INK_SAC.parseMaterial()))
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_ESSENCE_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_ESSENCE_ITEM_LORE), 40))
        .build());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    wizardsOnDuty.remove(e.getPlayer());
  }

  @EventHandler
  public void onWizardDamage(EntityDamageByEntityEvent e) {
    if(!(e.getDamager() instanceof Zombie && e.getEntity() instanceof Player)) {
      return;
    }
    if(!wizardsOnDuty.contains(e.getEntity()) || ArenaRegistry.getArena((Player) e.getEntity()) == null) {
      return;
    }
    ((Zombie) e.getDamager()).damage(2.0, e.getEntity());
  }

  @EventHandler
  public void onStaffUse(CBPlayerInteractEvent e) {
    User user = getPlugin().getUserManager().getUser(e.getPlayer());
    if(ArenaRegistry.getArena(e.getPlayer()) == null) {
      return;
    }
    if(!(user.getKit() instanceof WizardKit) || user.isSpectator()) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(!ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    Player player = e.getPlayer();
    if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_ESSENCE_ITEM_NAME))) {
      if(!user.checkCanCastCooldownAndMessage("essence")) {
        return;
      }
      wizardsOnDuty.add(player);
      if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > (player.getHealth() + 3)) {
        player.setHealth(player.getHealth() + 3);
      } else {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
      }
      Utils.takeOneItem(player, stack);
      player.setGlowing(true);
      applyRageParticles(player);
      for(Entity en : player.getNearbyEntities(2, 2, 2)) {
        if(en instanceof Zombie) {
          ((Zombie) en).damage(9.0, player);
        }
      }
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
        player.setGlowing(false);
        wizardsOnDuty.remove(player);
      }, 20L * 15);
      user.setCooldown("essence", 15);
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(getPlugin().getChatManager().colorMessage(Messages.KITS_WIZARD_STAFF_ITEM_NAME))) {
      if(!user.checkCanCastCooldownAndMessage("wizard_staff")) {
        return;
      }
      applyMagicAttack(player);
      user.setCooldown("wizard_staff", 1);
    }
  }

  private void applyRageParticles(Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        Location loc = player.getLocation();
        loc.add(0, 0.8, 0);
        VersionUtils.sendParticles("VILLAGER_ANGRY", null, loc, 5, 0, 0, 0);
        if(!wizardsOnDuty.contains(player) || !ArenaRegistry.isInArena(player)) {
          this.cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 0, 2);
  }

  private void applyMagicAttack(Player player) {
    new BukkitRunnable() {
      double positionModifier = 0;
      final Location loc = player.getLocation();
      final Vector direction = loc.getDirection().normalize();

      @Override
      public void run() {
        positionModifier += 0.5;
        double x = direction.getX() * positionModifier,
            y = direction.getY() * positionModifier + 1.5,
            z = direction.getZ() * positionModifier;
        loc.add(x, y, z);
        VersionUtils.sendParticles("TOWN_AURA", null, loc, 5, 0, 0, 0);
        for(Entity en : loc.getChunk().getEntities()) {
          if(!(en instanceof Zombie) || en.getLocation().distance(loc) >= 1.5 || en.equals(player)) {
            continue;
          }
          ((LivingEntity) en).damage(6.0, player);
          VersionUtils.sendParticles("FIREWORKS_SPARK", null, en.getLocation(), 2, 0.5, 0.5, 0.5);
        }
        loc.subtract(x, y, z);
        if(positionModifier > 40) {
          this.cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

}
