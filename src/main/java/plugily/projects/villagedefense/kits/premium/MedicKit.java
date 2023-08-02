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

import org.bukkit.Color;
import org.bukkit.EntityEffect;
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
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
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
import plugily.projects.villagedefense.kits.KitHelper;
import plugily.projects.villagedefense.kits.KitSpecifications;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {

  private static final String LANGUAGE_ACCESSOR = "KIT_CONTENT_MEDIC_";

  public MedicKit() {
    registerMessages();
    setName(new MessageBuilder(LANGUAGE_ACCESSOR + "NAME").asKey().build());
    setKey("Medic");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  private void registerMessages() {
    MessageManager manager = getPlugin().getMessageManager();
    manager.registerMessage(LANGUAGE_ACCESSOR + "NAME", new Message("Kit.Content.Medic.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "DESCRIPTION", new Message("Kit.Content.Medic.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME", new Message("Kit.Content.Medic.Game-Item.Aura.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_DESCRIPTION", new Message("Kit.Content.Medic.Game-Item.Aura.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_ACTIVE_ACTION_BAR", new Message("Kit.Content.Medic.Game-Item.Aura.Active-Action-Bar", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_HEALED_BY_ACTION_BAR", new Message("Kit.Content.Medic.Game-Item.Aura.Healed-By-Action-Bar", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME", new Message("Kit.Content.Medic.Game-Item.Homecoming.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_DESCRIPTION", new Message("Kit.Content.Medic.Game-Item.Homecoming.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_ACTIVATE", new Message("Kit.Content.Medic.Game-Item.Homecoming.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_RESPAWNED_BY_TITLE", new Message("Kit.Content.Medic.Game-Item.Homecoming.Respawned-By-Title", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_RESPAWNED_BY_SUBTITLE", new Message("Kit.Content.Medic.Game-Item.Homecoming.Respawned-By-Subtitle", ""));
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
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_DESCRIPTION"))
      .build());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.GOLD_NUGGET.parseMaterial()))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_DESCRIPTION"))
      .build());
  }

  @Override
  public Material getMaterial() {
    return Material.GHAST_TEAR;
  }

  @Override
  public void reStock(Player player) {
    User user = getPlugin().getUserManager().getUser(player);
    for(Player arenaPlayer : user.getArena().getPlayersLeft()) {
      int heal = (int) Settings.HEAL_AURA_POWER.getForArenaState((Arena) user.getArena());
      double maxHealth = VersionUtils.getMaxHealth(arenaPlayer);
      if(arenaPlayer.getHealth() + heal > maxHealth) {
        arenaPlayer.setHealth(maxHealth);
        arenaPlayer.setFoodLevel(20);
      } else {
        arenaPlayer.setHealth(arenaPlayer.getHealth() + heal);
      }
    }
    Arena arena = (Arena) user.getArena();
    if(arena.getWave() == KitSpecifications.GameTimeState.MID.getStartWave()) {
      new MessageBuilder("KIT_ABILITY_UNLOCKED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_ABILITY_UNLOCKED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME").asKey().build()).send(player);
    } else if(arena.getWave() == KitSpecifications.GameTimeState.LATE.getStartWave()) {
      new MessageBuilder("KIT_ABILITY_POWER_INCREASED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_ABILITY_POWER_INCREASED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME").asKey().build()).send(player);
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
    for(Entity entity : en.getNearbyEntities(5, 5, 5)) {
      if(!(entity instanceof Player)) {
        continue;
      }

      Player player = (Player) entity;
      player.setHealth(Math.min(player.getHealth() + 1.0, VersionUtils.getMaxHealth(player)));
      VersionUtils.sendParticles("HEART", player, player.getLocation(), 3);
    }
  }

  @EventHandler
  public void onItemUse(PlugilyPlayerInteractEvent event) {
    if(!KitHelper.isInGameWithKitAndItemInHand(event.getPlayer(), MedicKit.class)) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    Player player = event.getPlayer();
    if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("medic_homecoming")) {
        return;
      }
      if(KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      int cooldown = getKitsConfig().getInt("Kit-Cooldown.Medic.Homecoming", 60);
      user.setCooldown("medic_homecoming", cooldown);
      applyHomecoming(user);

      VersionUtils.setMaterialCooldown(player, stack.getType(), cooldown * 20);
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("medic_aura")) {
        return;
      }
      if(KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      int cooldown = getAuraCooldown((Arena) user.getArena());
      user.setCooldown("medic_aura", cooldown);
      int castTime = 10;
      user.setCooldown("medic_aura_running", castTime);
      KitHelper.scheduleAbilityCooldown(stack, player, castTime, cooldown);
      applyAura(user);
    }
  }

  private int getAuraCooldown(Arena arena) {
    switch(KitSpecifications.getTimeState(arena)) {
      case LATE:
        return getKitsConfig().getInt("Kit-Cooldown.Medic.Aura.II", 15);
      case MID:
        return getKitsConfig().getInt("Kit-Cooldown.Medic.Aura.I", 30);
      case EARLY:
      default:
        return 0;
    }
  }

  public void applyHomecoming(User user) {
    new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_ACTIVATE").asKey().send(user.getPlayer());
    List<Player> left = user.getArena().getPlayersLeft();
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_11_R1)) {
      user.getPlayer().playEffect(EntityEffect.valueOf("TOTEM_RESURRECT"));
    }

    String title = new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_RESPAWNED_BY_TITLE").asKey().player(user.getPlayer()).build();
    String subTitle = new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_RESPAWNED_BY_SUBTITLE").asKey().player(user.getPlayer()).build();
    for(Player arenaPlayer : user.getArena().getPlayers()) {
      if(left.contains(arenaPlayer)) {
        continue;
      }
      VersionUtils.sendTitles(arenaPlayer, title, subTitle, 5, 40, 5);
      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_11_R1)) {
        arenaPlayer.playEffect(EntityEffect.valueOf("TOTEM_RESURRECT"));
      }
      int amplifier = (int) Settings.HEAL_AURA_POWER.getForArenaState((Arena) user.getArena());
      arenaPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 30 * 20, amplifier));
      arenaPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, amplifier));
    }
    ArenaUtils.bringDeathPlayersBack((Arena) user.getArena());
  }

  public void applyAura(User user) {
    Player player = user.getPlayer();

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_ACTIVE_ACTION_BAR");
    List<String> healingMessages = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_HEALED_BY_ACTION_BAR");
    new BukkitRunnable() {
      int tick = 0;
      int messageIndex = 0;
      int healingMessageIndex = 0;

      @Override
      public void run() {
        //apply effects only once per second, particles every 5 ticks
        if(tick % 5 == 0 && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          XParticle.circle(3.5, 18, ParticleDisplay.simple(player.getLocation().add(0, 0.5, 0), XParticle.getParticle("HEART")));
        }
        if(tick % 10 == 0) {
          int heal = (int) Settings.HEAL_AURA_POWER.getForArenaState((Arena) user.getArena());
          for(LivingEntity entity : getNearbyAllies(player)) {
            entity.setHealth(Math.min(entity.getHealth() + heal, VersionUtils.getMaxHealth(entity)));
            VersionUtils.sendParticles("HEART", null, entity.getLocation(), 5, 0, 0, 0);
            if(!entity.equals(player) && entity instanceof Player) {
              VersionUtils.sendActionBar((Player) entity, healingMessages.get(healingMessageIndex)
                .replace("%player%", user.getPlayer().getName()));
            }
          }
          player.setHealth(Math.min(player.getHealth() + heal, VersionUtils.getMaxHealth(player)));
          VersionUtils.sendActionBar(player, messages.get(messageIndex)
            .replace("%number%", String.valueOf(user.getCooldown("medic_aura_running"))));
          messageIndex++;
          healingMessageIndex++;
          if(messageIndex > messages.size() - 1) {
            messageIndex = 0;
          }
          if(healingMessageIndex > healingMessages.size() - 1) {
            healingMessageIndex = 0;
          }
        }
        if(tick >= 20 * 10 || !getPlugin().getArenaRegistry().isInArena(user.getPlayer()) || user.isSpectator()) {
          //reset action bar
          VersionUtils.sendActionBar(player, "");
          cancel();
          return;
        }
        tick++;
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

  private List<LivingEntity> getNearbyAllies(Player player) {
    List<Entity> entities = player.getNearbyEntities(3.5, 3.5, 3.5);
    return entities.stream()
      .filter(e -> e instanceof Player || e instanceof Wolf || e instanceof Golem)
      .map(e -> (LivingEntity) e)
      .collect(Collectors.toList());
  }

  private enum Settings {
    HEAL_AURA_POWER(0, 2, 4), PASSIVE_HEAL_POWER(4, 6, 8), AMPLIFIER_POWER(0, 1, 2);

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
          return lateValue;
        case MID:
          return midValue;
        case EARLY:
        default:
          return earlyValue;
      }
    }
  }

}
