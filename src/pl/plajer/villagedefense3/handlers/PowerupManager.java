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

package pl.plajer.villagedefense3.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.utils.MessageUtils;
import pl.plajer.villagedefense3.villagedefenseapi.VillagePowerupPickEvent;

/**
 * @author Plajer
 * <p>
 * Created at 08.04.2018
 */
public class PowerupManager {

  private boolean enabled = false;
  private Main plugin;

  public PowerupManager(Main plugin) {
    if (!plugin.getConfig().getBoolean("Powerups.Enabled", true)) {
      return;
    }
    if (!plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
      Main.debug("Power up module: Holographic Displays dependency not found, disabling", System.currentTimeMillis());
      return;
    }
    enabled = true;
    this.plugin = plugin;
    Main.debug("Registering power ups module!", System.currentTimeMillis());
    PowerupType.CLEANER.setName(ChatManager.colorMessage("Powerups.Map-Clean-Powerup.Name"));
    PowerupType.CLEANER.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Map-Clean", true));

    PowerupType.DOUBLE_DAMAGE.setName(ChatManager.colorMessage("Powerups.Double-Damage-Powerup.Name"));
    PowerupType.DOUBLE_DAMAGE.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Double-Damage-For-Players.Enabled", true));

    PowerupType.GOLEM_RAID.setName(ChatManager.colorMessage("Powerups.Golem-Raid-Powerup.Name"));
    PowerupType.GOLEM_RAID.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Golem-Raid.Enabled", true));

    PowerupType.HEALING.setName(ChatManager.colorMessage("Powerups.Healing-Powerup.Name"));
    PowerupType.HEALING.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Healing-For-Players.Enabled", true));

    PowerupType.ONE_SHOT_ONE_KILL.setName(ChatManager.colorMessage("Powerups.One-Shot-One-Kill-Powerup.Name"));
    PowerupType.DOUBLE_DAMAGE.setEnabled(plugin.getConfig().getBoolean("Powerups.List.One-Shot-One-Kill.Enabled", true));

    List<PowerupType> powerups = new ArrayList<>();
    for (PowerupType pt : PowerupType.values()) {
      if (!pt.isEnabled()) {
        powerups.add(pt);
      }
    }
    if (powerups.size() == PowerupType.values().length) {
      Main.debug("Disabling power up module, all power ups disabled", System.currentTimeMillis());
      enabled = false;
    }
  }

  public void spawnPowerup(Location loc, Arena arena) {
    if (!enabled) {
      return;
    }
    PowerupType powerupType = PowerupType.random();
    if (!powerupType.isEnabled()) {
      spawnPowerup(loc, arena);
    }
    if (!(ThreadLocalRandom.current().nextDouble(0.0, 100.0)
            <= plugin.getConfig().getDouble("Powerups.Drop-Chance", 1.0))) {
      return;
    }
    final PowerupType finalPowerUp = powerupType;
    String text = powerupType.getName();
    ItemStack icon = new ItemStack(powerupType.getMaterial());

    final Hologram hologram = HologramsAPI.createHologram(plugin, loc.clone().add(0.0, 1.2, 0.0));
    hologram.appendTextLine(text);
    ItemLine itemLine = hologram.appendItemLine(icon);
    final String powerUpTitle = powerupType.getName();
    final String powerUpSubtitle = ChatManager.colorMessage(powerupType.getAccessPath() + ".Description");
    itemLine.setPickupHandler(player -> {
      if (ArenaRegistry.getArena(player) != arena) {
        return;
      }

      VillagePowerupPickEvent villagePowerupPickEvent = new VillagePowerupPickEvent(arena, player, finalPowerUp);
      Bukkit.getPluginManager().callEvent(villagePowerupPickEvent);

      String subTitle = powerUpSubtitle;
      switch (finalPowerUp) {
        case CLEANER:
          if (arena.getZombies() != null) {
            for (Zombie zombie : arena.getZombies()) {
              zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
              zombie.remove();
            }
            arena.getZombies().clear();
          }
          break;
        case DOUBLE_DAMAGE:
          for (Player p : arena.getPlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                    plugin.getConfig().getInt("Powerups.List.Double-Damage-For-Players.Time", 15), 1, false, false));
          }
          subTitle = subTitle.replace("%time%", plugin.getConfig().getString("Powerups.List.Double-Damage-For-Players.Time", "15"));
          break;
        case HEALING:
          for (Player p : arena.getPlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 *
                    plugin.getConfig().getInt("Powerups.List.Healing-For-Players.Time-Of-Healing", 10), 1, false, false));
          }
          subTitle = subTitle.replace("%time%", plugin.getConfig().getString("Powerups.List.Healing-For-Players.Time-Of-Healing", "10"));
          break;
        case GOLEM_RAID:
          for (int i = 0; i < plugin.getConfig().getInt("Powerups.List.Golem-Raid.Golems-Amount", 3); i++) {
            arena.spawnGolem(arena.getStartLocation(), player);
          }
          break;
        case ONE_SHOT_ONE_KILL:
          for (Player p : arena.getPlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                    plugin.getConfig().getInt("Powerups.List.One-Shot-One-Kill.Time", 15), 255, false, false));
          }
          subTitle = subTitle.replace("%time%", plugin.getConfig().getString("Powerups.List.One-Shot-One-Kill.Time", "15"));
          break;
      }
      for (Player p : arena.getPlayers()) {
        MessageUtils.sendTitle(p, powerUpTitle, 5, 30, 5);
        MessageUtils.sendSubTitle(p, subTitle, 5, 30, 5);
      }
      hologram.delete();
    });
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (!hologram.isDeleted()) {
        hologram.delete();
      }
    }, /* remove after 40 seconds to prevent staying even if arena is finished */ 20 * 40);
  }

  @Getter
  @AllArgsConstructor
  public enum PowerupType {
    CLEANER("Cleaner", Material.BLAZE_POWDER, "Powerups.Map-Clean-Powerup", true), DOUBLE_DAMAGE("Doubledamage", Material.REDSTONE, "Powerups.Double-Damage-Powerup", true),
    HEALING("Healing", Material.GOLDEN_APPLE, "Powerups.Healing-Powerup", true), GOLEM_RAID("raid", Material.IRON_INGOT, "Powerups.Golem-Raid-Powerup", true),
    ONE_SHOT_ONE_KILL("oson", Material.DIAMOND_SWORD, "Powerups.One-Shot-One-Kill-Powerup", true);

    @Setter
    String name;
    Material material;
    String accessPath;
    @Setter
    boolean enabled;

    private static PowerupType random() {
      Random r = new Random();
      return values()[r.nextInt(values().length)];
    }
  }
}
