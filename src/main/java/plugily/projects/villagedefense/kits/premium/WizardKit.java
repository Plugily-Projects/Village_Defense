/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.creatures.CreatureUtils;

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
    setName(new MessageBuilder("KIT_CONTENT_WIZARD_NAME").asKey().build());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_DESCRIPTION");
    setDescription(description);
    getPlugin().getKitRegistry().registerKit(this);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.wizard");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(new ItemBuilder(getMaterial())
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_DESCRIPTION"))
        .build());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.INK_SAC.parseMaterial(), 4))
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_DESCRIPTION"))
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
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_DESCRIPTION"))
        .build());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    wizardsOnDuty.remove(e.getPlayer());
  }

  @EventHandler
  public void onWizardDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Creature && event.getEntity() instanceof Player)) {
      return;
    }
    if(!wizardsOnDuty.contains(event.getEntity()) || getPlugin().getArenaRegistry().getArena((Player) event.getEntity()) == null) {
      return;
    }
    ((Creature) event.getDamager()).damage(2.0, event.getEntity());
  }

  @EventHandler
  public void onStaffUse(PlugilyPlayerInteractEvent event) {
    if(getPlugin().getArenaRegistry().getArena(event.getPlayer()) == null) {
      return;
    }

    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if(user.isSpectator() || !(user.getKit() instanceof WizardKit)) {
      return;
    }

    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    if(!ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    Player player = event.getPlayer();
    if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_ESSENCE_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("essence")) {
        return;
      }
      wizardsOnDuty.add(player);
      if(VersionUtils.getMaxHealth(player) > (player.getHealth() + 3)) {
        player.setHealth(player.getHealth() + 3);
      } else {
        player.setHealth(VersionUtils.getMaxHealth(player));
      }
      getPlugin().getBukkitHelper().takeOneItem(player, stack);
      VersionUtils.setGlowing(player, true);
      applyRageParticles(player);
      for(Entity entity : player.getNearbyEntities(2, 2, 2)) {
        if(CreatureUtils.isEnemy(entity)) {
          ((Creature) entity).damage(9.0, player);
        }
      }
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
        VersionUtils.setGlowing(player, false);
        wizardsOnDuty.remove(player);
      }, 20L * 15);
      user.setCooldown("essence", getKitsConfig().getInt("Kit-Cooldown.Wizard.Essence", 15));
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("wizard_staff")) {
        return;
      }
      applyMagicAttack(player);
      user.setCooldown("wizard_staff", getKitsConfig().getInt("Kit-Cooldown.Wizard.Staff", 1));
    }
  }

  private void applyRageParticles(Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        Location loc = player.getLocation();
        loc.add(0, 0.8, 0);
        VersionUtils.sendParticles("VILLAGER_ANGRY", null, loc, 5, 0, 0, 0);
        if(!wizardsOnDuty.contains(player) || !getPlugin().getArenaRegistry().isInArena(player)) {
          cancel();
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
          if(!(CreatureUtils.isEnemy(en)) || en.getLocation().distance(loc) >= 1.5 || en.equals(player)) {
            continue;
          }
          ((LivingEntity) en).damage(6.0, player);
          VersionUtils.sendParticles("FIREWORKS_SPARK", null, en.getLocation(), 2, 0.5, 0.5, 0.5);
        }
        loc.subtract(x, y, z);
        if(positionModifier > 40) {
          cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

}
