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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
import plugily.projects.minigamesbox.classic.utils.version.xseries.ParticleDisplay;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticle;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.kits.KitSpecifications;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2018
 * <p>
 */
public class WizardKit extends PremiumKit implements Listener {

  private final List<Player> corruptedWizards = new ArrayList<>();

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
    player.getInventory().setItem(3, new ItemBuilder(getMaterial())
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_DESCRIPTION"))
        .build());
    player.getInventory().setItem(4, new ItemBuilder(new ItemStack(XMaterial.POPPY.parseMaterial(), 1))
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_DESCRIPTION"))
        .build());
    player.getInventory().setItem(5, new ItemBuilder(new ItemStack(XMaterial.SPIDER_EYE.parseMaterial(), 1))
        .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_BLOODLUST_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_BLOODLUST_DESCRIPTION"))
        .build());

    ArmorHelper.setColouredArmor(Color.fromRGB(100, 149, 237), player);
    player.getInventory().setItem(8, new ItemStack(Material.SADDLE));
  }

  @Override
  public Material getMaterial() {
    return Material.BLAZE_ROD;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    boolean giveItem;
    switch (KitSpecifications.getTimeState(arena)) {
      case LATE:
        giveItem = arena.getWave() % 2 == 0;
        break;
      case MID:
      case EARLY:
      default:
        giveItem = arena.getWave() % 3 == 0;
        break;
    }
    if (giveItem) {
      player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.POPPY.parseMaterial()))
          .name(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_NAME").asKey().build())
          .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_DESCRIPTION"))
          .build());
    }
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }
    Player damager = (Player) event.getDamager();
    if (getPlugin().getArenaRegistry().getArena(damager) == null) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(damager);
    if (user.isSpectator() || !(user.getKit() instanceof WizardKit)
        || !corruptedWizards.contains(damager) || !CreatureUtils.isEnemy(event.getEntity())) {
      return;
    }
    LivingEntity entity = (LivingEntity) event.getEntity();
    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, 3, false, true));
    entity.setFireTicks(20 * 3);
  }

  @EventHandler
  public void onItemUse(PlugilyPlayerInteractEvent event) {
    if (getPlugin().getArenaRegistry().getArena(event.getPlayer()) == null) {
      return;
    }

    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if (user.isSpectator() || !(user.getKit() instanceof WizardKit)) {
      return;
    }

    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    if (!ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    Player player = event.getPlayer();
    if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_BLOODLUST_NAME").asKey().build())) {
      if (!user.checkCanCastCooldownAndMessage("wizard_bloodlust")) {
        return;
      }
      if (KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      applyBloodlust(user);
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Wizard.Bloodlust", 70);
      user.setCooldown("wizard_bloodlust", cooldown);
      user.setCooldown("wizard_bloodlust_running", 15);
      new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_BLOODLUST_ACTIVATE").asKey().send(player);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    } else if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_NAME").asKey().build())) {
      if (!user.checkCanCastCooldownAndMessage("wizard_flower")) {
        return;
      }
      getPlugin().getBukkitHelper().takeOneItem(player, stack);
      corruptedWizards.add(player);
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Wizard.Flower", 15);
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> corruptedWizards.remove(player), cooldown * 20L);
      user.setCooldown("wizard_flower", cooldown);
      new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_FLOWER_ACTIVATE").asKey().send(player);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    } else if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_WIZARD_GAME_ITEM_WAND_NAME").asKey().build())) {
      //no cooldown message, this ability is spammy no need for such message
      if (user.getCooldown("wizard_staff") > 0) {
        return;
      }
      applyMagicAttack(user);
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Wizard.Staff", 1);
      user.setCooldown("wizard_staff", cooldown);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    }
  }

  private void applyBloodlust(User user) {
    Player player = user.getPlayer();
    player.getWorld().strikeLightningEffect(player.getLocation());

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WIZARD_GAME_ITEM_BLOODLUST_ACTIVE_ACTION_BAR");
    new BukkitRunnable() {
      int damageTick = 0;
      int messageIndex = 0;

      @Override
      public void run() {
        //apply effects only once per second, particles every tick
        XParticle.circle(3.5, 28, ParticleDisplay.simple(player.getLocation().add(0, 0.5, 0), XParticle.getParticle("SMOKE_NORMAL")));
        if (damageTick % 20 == 0) {
          double totalDamage = 0;
          for (Entity en : player.getNearbyEntities(3.5, 3.5, 3.5)) {
            if (!CreatureUtils.isEnemy(en) || en.equals(player)) {
              continue;
            }
            LivingEntity entity = (LivingEntity) en;
            //10% of entity's max health, we use direct health set to override armors etc.
            entity.damage(0, user.getPlayer());
            double damage = (VersionUtils.getMaxHealth(entity) / 100.0) * 10.0;
            entity.setHealth(Math.max(0, entity.getHealth() - damage));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3, false, true));
            VersionUtils.sendParticles("SUSPENDED", null, en.getLocation(), 1, 0, 0, 0);
            totalDamage += damage;
          }
          player.setHealth(Math.min(player.getHealth() + (totalDamage * 0.08), VersionUtils.getMaxHealth(player)));
        }
        if (damageTick % 10 == 0) {
          VersionUtils.sendActionBar(player, messages.get(messageIndex)
              .replace("%number%", String.valueOf(user.getCooldown("wizard_bloodlust_running"))));
          messageIndex++;
          if (messageIndex > messages.size() - 1) {
            messageIndex = 0;
          }
        }
        if (damageTick >= 20 * 15 || !getPlugin().getArenaRegistry().isInArena(user.getPlayer()) || user.isSpectator()) {
          //reset action bar
          VersionUtils.sendActionBar(player, "");
          cancel();
          return;
        }
        damageTick++;
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

  private void applyMagicAttack(User user) {
    XParticle.drawLine(user.getPlayer(), 40, 1, ParticleDisplay.of(XParticle.getParticle("FLAME")));
    Arena arena = (Arena) user.getArena();
    final int finalPierce = getDefaultPierce(arena);
    new BukkitRunnable() {
      final Location loc = user.getPlayer().getLocation();
      final Vector direction = loc.getDirection().normalize();
      double positionModifier = 0;
      int pierce = finalPierce;

      @Override
      public void run() {
        positionModifier += 3.5;
        double x = direction.getX() * positionModifier,
            y = direction.getY() * positionModifier + 1.5,
            z = direction.getZ() * positionModifier;
        loc.add(x, y, z);
        for (Entity en : loc.getChunk().getEntities()) {
          if (!CreatureUtils.isEnemy(en) || en.getLocation().distance(loc) >= 1.8 || en.equals(user.getPlayer()) || pierce <= 0) {
            continue;
          }
          LivingEntity entity = (LivingEntity) en;
          //wand damage: 30/32/35 scaling % of entity's max health, we use direct health set to override armors etc.
          entity.damage(0, user.getPlayer());
          double maxHealthPercent = getWandPercentageDamage(arena);
          entity.setHealth(Math.max(0, entity.getHealth() - (entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 100.0) * maxHealthPercent));

          if (entity.isDead()) {
            XSound.ENTITY_ARROW_HIT_PLAYER.play(user.getPlayer());
          } else {
            XSound.BLOCK_NOTE_BLOCK_GUITAR.play(user.getPlayer());
          }
          VersionUtils.sendParticles("DAMAGE_INDICATOR", null, en.getLocation(), 1, 0, 0, 0);
          pierce--;
        }
        loc.subtract(x, y, z);
        if (positionModifier > 40 || pierce <= 0) {
          cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

  private int getDefaultPierce(Arena arena) {
    switch (KitSpecifications.getTimeState(arena)) {
      case LATE:
        return 7;
      case MID:
        return 6;
      case EARLY:
      default:
        return 5;
    }
  }

  private double getWandPercentageDamage(Arena arena) {
    switch (KitSpecifications.getTimeState(arena)) {
      case LATE:
        return 35.0;
      case MID:
        return 32.0;
      case EARLY:
      default:
        return 30.0;
    }
  }

}
