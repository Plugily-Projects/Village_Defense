/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2022  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.ParticleDisplay;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XParticle;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.kits.KitSpecifications;

/**
 * Created by Tom on 30/12/2015.
 */
//todo final flight ability
public class TornadoKit extends PremiumKit implements Listener {

  private static final int TORNADO_MAX_HEIGHT = 5;
  private static final double TORNADO_MAX_RADIUS = 4;
  private static final double TORNADO_RADIUS_INCREMENT = TORNADO_MAX_RADIUS / TORNADO_MAX_HEIGHT;

  public TornadoKit() {
    setName(new MessageBuilder("KIT_CONTENT_TORNADO_NAME").asKey().build());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return player.hasPermission("villagedefense.kit.tornado") || getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player);
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
    player.getInventory().addItem(new ItemBuilder(new ItemStack(getMaterial(), 5))
        .name(new MessageBuilder("KIT_CONTENT_TORNADO_GAME_ITEM_TORNADO_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_GAME_ITEM_TORNADO_DESCRIPTION"))
        .build());
  }

  @Override
  public Material getMaterial() {
    return XMaterial.COBWEB.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    int amount = 1;
    switch (KitSpecifications.getTimeState((Arena) getPlugin().getArenaRegistry().getArena(player))) {
      case LATE:
        amount = 5;
        break;
      case MID:
        amount = 3;
        break;
      case EARLY:
        amount = 2;
        break;
    }
    player.getInventory().addItem(new ItemBuilder(new ItemStack(getMaterial(), amount))
        .name(new MessageBuilder("KIT_CONTENT_TORNADO_GAME_ITEM_TORNADO_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_GAME_ITEM_TORNADO_DESCRIPTION"))
        .build());
  }

  @EventHandler
  public void onTornadoSpawn(PlugilyPlayerInteractEvent event) {
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if (user.isSpectator() || !(user.getKit() instanceof TornadoKit)) {
      return;
    }
    Player player = event.getPlayer();
    if (!getPlugin().getArenaRegistry().isInArena(player)) {
      return;
    }

    ItemStack stack = VersionUtils.getItemInHand(player);
    if (!ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(new MessageBuilder("KIT_CONTENT_TORNADO_GAME_ITEM_TORNADO_NAME").asKey().build())) {
      getPlugin().getBukkitHelper().takeOneItem(player, stack);
      prepareTornado(player.getLocation());
    } else if (ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(new MessageBuilder("KIT_CONTENT_TORNADO_GAME_ITEM_MONSOON_NAME").asKey().build())) {
      createMonsoon(user);
    }
  }

  private void prepareTornado(final Location loc) {
    new BukkitRunnable() {
      final Vector vector = loc.getDirection();
      Location location = loc;
      int angle;
      int times = 0;
      int pierce = 0;

      @Override
      public void run() {
        int lines = 3;
        for (int l = 0; l < lines; l++) {
          for (double y = 0; y < TORNADO_MAX_HEIGHT; y += 0.5) {
            double radius = y * TORNADO_RADIUS_INCREMENT,
                radians = Math.toRadians(360.0 / lines * l + y * 25 - angle),
                x = Math.cos(radians) * radius,
                z = Math.sin(radians) * radius;
            VersionUtils.sendParticles("CLOUD", null, location.clone().add(x, y, z), 1, 0, 0, 0);
          }
        }
        pierce += pushAndDamageNearbyEnemies(location, vector);
        location = location.add(vector.getX() / (3 + Math.random() / 2), 0, vector.getZ() / (3 + Math.random() / 2));
        angle += 50;
        times++;

        if (pierce >= 10 || times > 55) {
          cancel();
        }
      }
    }.runTaskTimer(getPlugin(), 1, 1);
  }

  private int pushAndDamageNearbyEnemies(Location location, Vector vector) {
    int pierce = 0;
    for (Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
      if (CreatureUtils.isEnemy(entity)) {
        pierce++;

        Vector velocityVec = vector.multiply(2).setY(0).add(new Vector(0, 1, 0));
        if (VersionUtils.isPaper() && (vector.getX() > 4.0 || vector.getZ() > 4.0)) {
          velocityVec = vector.setX(2.0).setZ(1.0); // Paper's sh*t
        }
        ((LivingEntity) entity).damage(5.0);
        entity.setVelocity(velocityVec);
      }
    }
    return pierce;
  }

  private void createMonsoon(User user) {
    Player player = user.getPlayer();
    final int spellTime = getMonsoonSpellTime((Arena) user.getArena());
    int cooldown = getKitsConfig().getInt("Kit-Cooldown.Tornado.Monsoon", 20);
    user.setCooldown("tornado_monsoon", cooldown);
    user.setCooldown("tornado_monsoon_running", spellTime);

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_GAME_ITEM_MONSOON_ACTIVE_ACTION_BAR");
    new BukkitRunnable() {
      int spellTick = 0;
      int messageIndex = 0;

      @Override
      public void run() {
        XParticle.circle(3.5, 28, ParticleDisplay.simple(player.getLocation().add(0, 0.5, 0), XParticle.getParticle("CLOUD")));

        if (spellTick % 20 == 0) {
          for (Entity en : player.getNearbyEntities(3.5, 3.5, 3.5)) {
            if (!CreatureUtils.isEnemy(en) || en.equals(player)) {
              continue;
            }
            LivingEntity entity = (LivingEntity) en;
            //damage for 0 to knock it back, todo vector push here
            entity.damage(0, user.getPlayer());
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 9999, 2, false, true));
          }
        }
        if (spellTick % 10 == 0) {
          VersionUtils.sendActionBar(player, messages.get(messageIndex)
              .replace("%number%", String.valueOf(user.getCooldown("tornado_monsoon_running"))));
          messageIndex++;
          if (messageIndex > messages.size() - 1) {
            messageIndex = 0;
          }
        }
        if (spellTick >= 20 * spellTime || !getPlugin().getArenaRegistry().isInArena(user.getPlayer()) || user.isSpectator()) {
          //reset action bar
          VersionUtils.sendActionBar(player, "");
          cancel();
          return;
        }
        spellTick++;
      }
    }.runTaskTimer(getPlugin(), 0, 1);
  }

  private int getMonsoonSpellTime(Arena arena) {
    switch (KitSpecifications.getTimeState(arena)) {
      case LATE:
        return 8;
      case MID:
        return 6;
      case EARLY:
      default:
        return 4;
    }
  }

}
