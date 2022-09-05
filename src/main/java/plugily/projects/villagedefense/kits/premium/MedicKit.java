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

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.ParticleDisplay;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticle;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.kits.KitSpecifications;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {

  public MedicKit() {
    setName(new MessageBuilder("KIT_CONTENT_MEDIC_NAME").asKey().build());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_MEDIC_DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.medic");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(VersionUtils.getPotion(PotionType.REGEN, 1, true));

    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.GHAST_TEAR.parseMaterial()))
        .name(new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_AURA_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_MEDIC_GAME_ITEM_AURA_DESCRIPTION"))
        .build());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.GOLD_NUGGET.parseMaterial()))
        .name(new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_DESCRIPTION"))
        .build());

  }

  @Override
  public Material getMaterial() {
    return Material.GHAST_TEAR;
  }

  @Override
  public void reStock(Player player) {
    User user = getPlugin().getUserManager().getUser(player);
    for (Player arenaPlayer : user.getArena().getPlayersLeft()) {
      int heal;
      switch (KitSpecifications.getTimeState((Arena) user.getArena())) {
        case LATE:
          heal = 8;
          break;
        case MID:
          heal = 6;
          break;
        case EARLY:
        default:
          heal = 4;
          break;
      }
      double maxHealth = VersionUtils.getMaxHealth(arenaPlayer);
      if (arenaPlayer.getHealth() + heal > maxHealth) {
        arenaPlayer.setHealth(maxHealth);
        arenaPlayer.setFoodLevel(20);
      } else {
        arenaPlayer.setHealth(arenaPlayer.getHealth() + heal);
      }
    }
  }

  @EventHandler
  public void onCreatureHit(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof Creature) || !(e.getDamager() instanceof Player)) {
      return;
    }
    User user = getPlugin().getUserManager().getUser((Player) e.getDamager());
    if(!(user.getKit() instanceof MedicKit) || Math.random() > 0.1) {
      return;
    }
    healNearbyPlayers(e.getDamager());
  }

  private void healNearbyPlayers(Entity en) {
    for (Entity entity : en.getNearbyEntities(5, 5, 5)) {
      if (!(entity instanceof Player)) {
        continue;
      }

      Player player = (Player) entity;
      player.setHealth(Math.min(player.getHealth() + 1.0, VersionUtils.getMaxHealth(player)));
      VersionUtils.sendParticles("HEART", player, player.getLocation(), 3);
    }
  }

  @EventHandler
  public void onItemUse(PlugilyPlayerInteractEvent event) {
    if (getPlugin().getArenaRegistry().getArena(event.getPlayer()) == null) {
      return;
    }

    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if (user.isSpectator() || !(user.getKit() instanceof MedicKit)) {
      return;
    }

    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    if (!ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    Player player = event.getPlayer();
    if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_NAME").asKey().build())) {
      if (!user.checkCanCastCooldownAndMessage("medic_homecoming")) {
        return;
      }
      if (KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Medic.Homecoming", 60);
      user.setCooldown("medic_homecoming", cooldown);
      applyHomecoming(user);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    } else if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_AURA_NAME").asKey().build())) {
      if (!user.checkCanCastCooldownAndMessage("medic_aura")) {
        return;
      }
      if (KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      int cooldown;
      switch (KitSpecifications.getTimeState((Arena) user.getArena())) {
        case LATE:
          cooldown = getKitsConfig().getInt("Kit-Cooldown.Medic.Aura.II", 15);
          break;
        case MID:
          cooldown = getKitsConfig().getInt("Kit-Cooldown.Medic.Aura.I", 30);
          break;
        case EARLY:
        default:
          cooldown = 0;
          break;
      }
      user.setCooldown("medic_aura", cooldown);
      user.setCooldown("medic_aura_running", 10);
      applyAura(user);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    }
  }

  public void applyHomecoming(User user) {
    new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_ACTIVATE").asKey().send(user.getPlayer());
    List<Player> left = user.getArena().getPlayersLeft();
    for (Player arenaPlayer : user.getArena().getPlayers()) {
      if (left.contains(arenaPlayer)) {
        continue;
      }
      String title = new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_RESPAWNED_BY_TITLE").asKey().player(user.getPlayer()).build();
      String subTitle = new MessageBuilder("KIT_CONTENT_MEDIC_GAME_ITEM_HOMECOMING_RESPAWNED_BY_SUBTITLE").asKey().player(user.getPlayer()).build();
      VersionUtils.sendTitles(arenaPlayer, title, subTitle, 5, 40, 5);
      int amplifier;
      switch (KitSpecifications.getTimeState((Arena) user.getArena())) {
        case LATE:
          amplifier = 2;
          break;
        case MID:
          amplifier = 1;
          break;
        case EARLY:
        default:
          amplifier = 0;
          break;
      }
      arenaPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 30 * 20, amplifier));
      arenaPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, amplifier));
    }
    ArenaUtils.bringDeathPlayersBack((Arena) user.getArena());
  }

  public void applyAura(User user) {
    Player player = user.getPlayer();

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_MEDIC_GAME_ITEM_AURA_ACTIVE_ACTION_BAR");
    List<String> healingMessages = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_MEDIC_GAME_ITEM_AURA_HEALED_BY_ACTION_BAR");
    new BukkitRunnable() {
      int tick = 0;
      int messageIndex = 0;
      int healingMessageIndex = 0;

      @Override
      public void run() {
        //apply effects only once per second, particles every 5 ticks
        if (tick % 5 == 0 && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          XParticle.circle(3.5, 18, ParticleDisplay.simple(player.getLocation().add(0, 0.5, 0), XParticle.getParticle("HEART")));
        }
        if (tick % 10 == 0) {
          int heal;
          switch (KitSpecifications.getTimeState((Arena) user.getArena())) {
            case LATE:
              heal = 4;
              break;
            case MID:
              heal = 2;
              break;
            case EARLY:
            default:
              heal = 0;
              break;
          }
          for (Entity entity : player.getNearbyEntities(3.5, 3.5, 3.5)) {
            if (!(entity instanceof Player || entity instanceof Wolf || entity instanceof Golem)) {
              continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setHealth(Math.min(livingEntity.getHealth() + heal, VersionUtils.getMaxHealth(livingEntity)));
            VersionUtils.sendParticles("HEART", null, livingEntity.getLocation(), 5, 0, 0, 0);
            if (!entity.equals(player) && entity instanceof Player) {
              VersionUtils.sendActionBar((Player) entity, healingMessages.get(healingMessageIndex)
                  .replace("%player%", user.getPlayer().getName()));
            }
          }
          player.setHealth(Math.min(player.getHealth() + heal, VersionUtils.getMaxHealth(player)));
          VersionUtils.sendActionBar(player, messages.get(messageIndex)
              .replace("%number%", String.valueOf(user.getCooldown("medic_aura_running"))));
          messageIndex++;
          healingMessageIndex++;
          if (messageIndex > messages.size() - 1) {
            messageIndex = 0;
          }
          if (healingMessageIndex > healingMessages.size() - 1) {
            healingMessageIndex = 0;
          }
        }
        if (tick >= 20 * 10 || !getPlugin().getArenaRegistry().isInArena(user.getPlayer()) || user.isSpectator()) {
          //reset action bar
          VersionUtils.sendActionBar(player, "");
          cancel();
          return;
        }
        tick++;
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

}
