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

package plugily.projects.villagedefense.kits.premium;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.ParticleDisplay;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticle;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.kits.KitHelper;
import plugily.projects.villagedefense.kits.KitSpecifications;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2018
 * <p>
 */
public class WizardKit extends PremiumKit implements Listener {

  private static final String LANGUAGE_ACCESSOR = "KIT_CONTENT_WIZARD_";
  private final List<Player> corruptedWizards = new ArrayList<>();

  public WizardKit() {
    registerMessages();
    setName(new MessageBuilder(LANGUAGE_ACCESSOR + "NAME").asKey().build());
    setKey("Wizard");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "DESCRIPTION");
    setDescription(description);
    getPlugin().getKitRegistry().registerKit(this);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }

  private void registerMessages() {
    MessageManager manager = getPlugin().getMessageManager();
    manager.registerMessage(LANGUAGE_ACCESSOR + "NAME", new Message("Kit.Content.Wizard.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "DESCRIPTION", new Message("Kit.Content.Wizard.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_WAND_NAME", new Message("Kit.Content.Wizard.Game-Item.Wand.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_WAND_DESCRIPTION", new Message("Kit.Content.Wizard.Game-Item.Wand.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_NAME", new Message("Kit.Content.Wizard.Game-Item.Flower.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_DESCRIPTION", new Message("Kit.Content.Wizard.Game-Item.Flower.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_ACTIVATE", new Message("Kit.Content.Wizard.Game-Item.Flower.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_NAME", new Message("Kit.Content.Wizard.Game-Item.Bloodlust.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_DESCRIPTION", new Message("Kit.Content.Wizard.Game-Item.Bloodlust.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_ACTIVATE", new Message("Kit.Content.Wizard.Game-Item.Bloodlust.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_ACTIVE_ACTION_BAR", new Message("Kit.Content.Wizard.Game-Item.Bloodlust.Active-Action-Bar", ""));
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.wizard");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().setItem(3, new ItemBuilder(getMaterial())
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_WAND_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_WAND_DESCRIPTION"))
        .build());
    player.getInventory().setItem(4, new ItemBuilder(new ItemStack(XMaterial.POPPY.parseMaterial(), 1))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_DESCRIPTION"))
        .build());
    player.getInventory().setItem(5, new ItemBuilder(new ItemStack(XMaterial.SPIDER_EYE.parseMaterial(), 1))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_DESCRIPTION"))
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
        .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_DESCRIPTION"))
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
    if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("wizard_bloodlust")) {
        return;
      }
      if(KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Wizard.Bloodlust", 70);
      user.setCooldown("wizard_bloodlust", cooldown);
      int castTime = 15;
      user.setCooldown("wizard_bloodlust_running", castTime);
      new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_ACTIVATE").asKey().send(player);

      KitHelper.scheduleAbilityCooldown(stack, user.getPlayer(), castTime, cooldown);
      applyBloodlust(user);
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("wizard_flower")) {
        return;
      }
      getPlugin().getBukkitHelper().takeOneItem(player, stack);
      corruptedWizards.add(player);
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Wizard.Flower", 15);
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> corruptedWizards.remove(player), cooldown * 20L);
      user.setCooldown("wizard_flower", cooldown);
      new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FLOWER_ACTIVATE").asKey().send(player);
      XSound.ENCHANT_THORNS_HIT.play(user.getPlayer(), 1f, 0f);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_WAND_NAME").asKey().build())) {
      //no cooldown message, this ability is spammy no need for such message
      if(user.getCooldown("wizard_staff") > 0) {
        return;
      }
      applyMagicAttack(user);
      double cooldown = Settings.WAND_COOLDOWN.getForArenaState((Arena) user.getArena());
      user.setCooldown("wizard_staff", cooldown);

      VersionUtils.setMaterialCooldown(player, stack.getType(), (int) (cooldown * 20));
    }
  }

  private void applyBloodlust(User user) {
    Player player = user.getPlayer();
    player.getWorld().strikeLightningEffect(player.getLocation());
    XSound.ENTITY_WITHER_SPAWN.playRepeatedly(getPlugin(), user.getPlayer(), 1f, 2f, 3, 25);

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_BLOODLUST_ACTIVE_ACTION_BAR");
    new BukkitRunnable() {
      int damageTick = 0;
      int messageIndex = 0;

      @Override
      public void run() {
        //apply effects only once per second, particles every tick
        if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          XParticle.circle(3.5, 28, ParticleDisplay.simple(player.getLocation().add(0, 0.5, 0), XParticle.getParticle("SMOKE_NORMAL")));
        }
        int soundLimit = 5;
        if (damageTick % 20 == 0) {
          double totalDamage = 0;
          for (Entity entity : player.getNearbyEntities(3.5, 3.5, 3.5)) {
            if (!CreatureUtils.isEnemy(entity) || entity.equals(player)) {
              continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            //10% of entity's max health, we use direct health set to override armors etc.
            livingEntity.damage(0, user.getPlayer());
            double damage = (VersionUtils.getMaxHealth(livingEntity) / 100.0) * 10.0;
            livingEntity.setHealth(Math.max(0, livingEntity.getHealth() - damage));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3, false, true));
            VersionUtils.sendParticles("SUSPENDED", null, entity.getLocation(), 1, 0, 0, 0);
            totalDamage += damage;
            if(soundLimit > 0) {
              XSound.ENTITY_WITHER_HURT.play(user.getPlayer(), 0.2f, 0f);
              soundLimit--;
            }
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
    if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      XParticle.drawLine(user.getPlayer(), 40, 1, ParticleDisplay.of(XParticle.getParticle("FLAME")));
    }
    final int finalPierce = (int) Settings.DEFAULT_PIERCE.getForArenaState((Arena) user.getArena());
    new BukkitRunnable() {
      final Location location = user.getPlayer().getLocation();
      final Vector direction = location.getDirection().normalize();
      double positionModifier = 0;
      int pierce = finalPierce;
      boolean soundPlayed = false;

      @Override
      public void run() {
        positionModifier += 3.5;
        double x = direction.getX() * positionModifier,
            y = direction.getY() * positionModifier + 1.5,
            z = direction.getZ() * positionModifier;
        location.add(x, y, z);
        for (Entity entity : location.getChunk().getEntities()) {
          if(!CreatureUtils.isEnemy(entity) || entity.getLocation().distance(location) >= 1.8 || entity.equals(user.getPlayer()) || pierce <= 0) {
            continue;
          }
          LivingEntity livingEntity = (LivingEntity) entity;
          //wand damage: 30/32/35 scaling % of entity's max health, we use direct health set to override armors etc.
          livingEntity.damage(0, user.getPlayer());
          double maxHealthPercent = Settings.WAND_PERCENT_DAMAGE.getForArenaState((Arena) user.getArena());
          livingEntity.setHealth(Math.max(0, livingEntity.getHealth() - (VersionUtils.getMaxHealth(livingEntity) / 100.0) * maxHealthPercent));

          if(!soundPlayed) {
            XSound.BLOCK_NOTE_BLOCK_HARP.play(user.getPlayer());
            soundPlayed = true;
          }
          VersionUtils.sendParticles("DAMAGE_INDICATOR", null, entity.getLocation(), 1, 0, 0, 0);
          pierce--;
        }
        location.subtract(x, y, z);
        if(positionModifier > 40 || pierce <= 0) {
          cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

  private enum Settings {
    DEFAULT_PIERCE(5, 6, 7), WAND_COOLDOWN(1, 0.75, 0.5), WAND_PERCENT_DAMAGE(30.0, 32.0, 35.0);

    private final double earlyValue;
    private final double midValue;
    private final double lateValue;

    Settings(double earlyValue, double midValue, double lateValue) {
      this.earlyValue = earlyValue;
      this.midValue = midValue;
      this.lateValue = lateValue;
    }

    public double getForArenaState(Arena arena) {
      switch(KitSpecifications.getTimeState(arena)) {
        case LATE:
          return earlyValue;
        case MID:
          return midValue;
        case EARLY:
        default:
          return lateValue;
      }
    }
  }

}
