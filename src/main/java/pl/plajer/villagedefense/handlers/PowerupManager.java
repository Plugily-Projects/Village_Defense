/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.event.player.VillagePlayerPowerupPickupEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 08.04.2018
 */
public class PowerupManager {

  private List<Powerup> registeredPowerups = new ArrayList<>();
  private boolean enabled = false;
  private Main plugin;

  public PowerupManager(Main plugin) {
    if (!plugin.getConfig().getBoolean("Powerups.Enabled", true)) {
      return;
    }
    if (!plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
      Debugger.debug(LogLevel.WARN, "Power up module: Holographic Displays dependency not found, disabling");
      return;
    }
    enabled = true;
    this.plugin = plugin;
    Debugger.debug(LogLevel.INFO, "Registering power ups module!");
    registerPowerups();
    if (registeredPowerups.isEmpty()) {
      Debugger.debug(LogLevel.WARN, "Disabling power up module, all power ups disabled");
      enabled = false;
    }
  }

  private void registerPowerups() {
    List<String> powerupNames = Arrays.asList("Map-Clean", "Double-Damage", "Golem-Raid", "Healing", "One-Shot-One-Kill");
    List<XMaterial> powerupMaterials = Arrays.asList(XMaterial.BLAZE_POWDER, XMaterial.REDSTONE, XMaterial.GOLDEN_APPLE, XMaterial.IRON_INGOT, XMaterial.DIAMOND_SWORD);
    int i = 0;
    for (String pwr : powerupNames) {
      if (plugin.getConfig().getBoolean("Powerups." + pwr + "-Powerup.Enabled", true)) {
        registerPowerup(new Powerup(pwr, ChatManager.colorMessage("Powerups." + pwr + "-Powerup.Name"), ChatManager.colorMessage("Powerups." + pwr + "-Powerup.Description"),
            powerupMaterials.get(i)));
      }
      i++;
    }
  }

  public Powerup getRandomPowerup() {
    Random r = new Random();
    return registeredPowerups.get(r.nextInt(registeredPowerups.size()));
  }

  public void registerPowerup(Powerup powerup) {
    registeredPowerups.add(powerup);
  }

  public void spawnPowerup(Location loc, Arena arena) {
    try {
      if (!enabled) {
        return;
      }
      final Powerup powerup = getRandomPowerup();
      if (!(ThreadLocalRandom.current().nextDouble(0.0, 100.0)
          <= plugin.getConfig().getDouble("Powerups.Drop-Chance", 1.0))) {
        return;
      }

      final Hologram hologram = HologramsAPI.createHologram(plugin, loc.clone().add(0.0, 1.2, 0.0));
      hologram.appendTextLine(powerup.getName());
      ItemLine itemLine = hologram.appendItemLine(powerup.getMaterial().parseItem());
      itemLine.setPickupHandler(player -> {
        if (ArenaRegistry.getArena(player) != arena) {
          return;
        }
        String title = powerup.getName();
        String subTitle = powerup.getDescription();

        VillagePlayerPowerupPickupEvent villagePowerupPickEvent = new VillagePlayerPowerupPickupEvent(arena, player, powerup);
        Bukkit.getPluginManager().callEvent(villagePowerupPickEvent);

        switch (powerup.getID()) {
          case "Map-Clean":
            if (arena.getZombies() != null) {
              for (Zombie zombie : arena.getZombies()) {
                zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
                zombie.remove();
              }
              arena.getZombies().clear();
            }
            break;
          case "Double-Damage":
            for (Player p : arena.getPlayers()) {
              p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                  plugin.getConfig().getInt("Powerups.List.Double-Damage-For-Players.Time", 15), 0, false, false));
            }
            subTitle = StringUtils.replace(subTitle, "%time%", plugin.getConfig().getString("Powerups.List.Double-Damage-For-Players.Time", "15"));
            break;
          case "Golem-Raid":
            for (int i = 0; i < plugin.getConfig().getInt("Powerups.List.Golem-Raid.Golems-Amount", 3); i++) {
              arena.spawnGolem(arena.getStartLocation(), player);
            }
            break;
          case "Healing":
            for (Player p : arena.getPlayers()) {
              p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 *
                  plugin.getConfig().getInt("Powerups.List.Healing-For-Players.Time-Of-Healing", 10), 0, false, false));
            }
            subTitle = StringUtils.replace(subTitle, "%time%", plugin.getConfig().getString("Powerups.List.Healing-For-Players.Time-Of-Healing", "10"));
            break;
          case "One-Shot-One-Kill":
            for (Player p : arena.getPlayers()) {
              p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                  plugin.getConfig().getInt("Powerups.List.One-Shot-One-Kill.Time", 15), 255, false, false));
            }
            subTitle = StringUtils.replace(subTitle, "%time%", plugin.getConfig().getString("Powerups.List.One-Shot-One-Kill.Time", "15"));
            break;
        }
        for (Player p : arena.getPlayers()) {
          p.sendTitle(title, subTitle, 5, 30, 5);
        }
        hologram.delete();
      });
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (!hologram.isDeleted()) {
          hologram.delete();
        }
      }, /* remove after 40 seconds to prevent staying even if arena is finished */ 20 * 40);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  public class Powerup {

    private String ID;
    private String name;
    private String description;
    private XMaterial material;

    public Powerup(String ID, String name, String description, XMaterial material) {
      this.ID = ID;
      this.name = name;
      this.description = description;
      this.material = material;
    }

    public String getID() {
      return ID;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public XMaterial getMaterial() {
      return material;
    }
  }
}
