/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.kits.premium;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.PremiumKit;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;

/**
 * Created by Tom on 30/12/2015.
 */
public class TornadoKit extends PremiumKit implements Listener {

  private int maxHeight = 5;
  private double maxRadius = 4;
  private double radiusIncrement = maxRadius / maxHeight;
  private int active = 0;

  public TornadoKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_DESCRIPTION), 40);
    this.setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return player.hasPermission("villagedefense.kit.tornado") || PermissionsManager.isPremium(player);
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.COBWEB.parseMaterial(), 5))
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_GAME_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_GAME_ITEM_LORE), 40))
        .build());
  }

  @Override
  public Material getMaterial() {
    return XMaterial.COBWEB.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemBuilder(new ItemStack(XMaterial.COBWEB.parseMaterial(), 5))
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_GAME_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_GAME_ITEM_LORE), 40))
        .build());
  }

  @EventHandler
  public void onTornadoSpawn(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Player player = e.getPlayer();
    ItemStack stack = player.getInventory().getItemInMainHand();
    if (!ArenaRegistry.isInArena(player) || !ItemUtils.isItemStackNamed(stack)
        || !stack.getItemMeta().getDisplayName().equalsIgnoreCase(getPlugin().getChatManager().colorMessage(Messages.KITS_TORNADO_GAME_ITEM_NAME))) {
      return;
    }
    if (active >= 2){
      return;
    }
    Utils.takeOneItem(player, stack);
    e.setCancelled(true);
    prepareTornado(player.getLocation());
  }

  private void prepareTornado(Location location) {
    Tornado tornado = new Tornado(location);
    active++;
    new BukkitRunnable() {
      @Override
      public void run() {
        tornado.update();
        if (tornado.entities >= 7 || tornado.getTimes() > 55) {
          this.cancel();
          active--;
        }
      }
    }.runTaskTimer(getPlugin(), 1, 1);
  }

  private class Tornado {
    private Location location;
    private Vector vector;
    private int angle;
    private int times;
    private int entities;

    Tornado(Location location) {
      this.location = location;
      this.vector = location.getDirection();
      times = 0;
      entities = 0;
    }

    int getTimes() {
      return times;
    }

    Vector getVector() {
      return vector;
    }

    Location getLocation() {
      return location;
    }

    public int getEntities() {
      return entities;
    }

    void setLocation(Location location) {
      this.location = location;
    }

    void update() {
      times++;
      int lines = 3;
      for (int l = 0; l < lines; l++) {
        double heightIncrease = 0.5;
        for (double y = 0; y < maxHeight; y += heightIncrease) {
          double radius = y * radiusIncrement;
          double radians = Math.toRadians(360.0 / lines * l + y * 25 - angle);
          double x = Math.cos(radians) * radius;
          double z = Math.sin(radians) * radius;
          getLocation().getWorld().spawnParticle(Particle.CLOUD, getLocation().clone().add(x, y, z), 1, 0, 0, 0, 0);
        }
      }
      pushNearbyZombies();
      setLocation(getLocation().add(getVector().getX() / (3 + Math.random() / 2), 0, getVector().getZ() / (3 + Math.random() / 2)));

      angle += 50;

    }

    private void pushNearbyZombies() {
      for (Entity entity : getLocation().getWorld().getNearbyLivingEntities(getLocation(), 2, 2, 2)) {
        if (entity.getType() == EntityType.ZOMBIE) {
          entities++;
          entity.setVelocity(getVector().multiply(2).setY(0).add(new Vector(0, 1, 0)));
        }
      }
    }
  }
}
