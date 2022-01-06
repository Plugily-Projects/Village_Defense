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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.List;

/**
 * Created by Tom on 30/12/2015.
 */
public class TornadoKit extends PremiumKit implements Listener {

  private final int maxHeight = 5;
  private final double maxRadius = 4;
  private final double radiusIncrement = maxRadius / maxHeight;
  private int active = 0;

  public TornadoKit() {
    setName(getPlugin().getChatManager().colorMessage("KIT_CONTENT_TORNADO_NAME"));
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
        .name(getPlugin().getChatManager().colorMessage("KIT_CONTENT_TORNADO_GAME_ITEM_NAME"))
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_GAME_ITEM_DESCRIPTION"))
        .build());
  }

  @Override
  public Material getMaterial() {
    return XMaterial.COBWEB.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemBuilder(new ItemStack(getMaterial(), 5))
        .name(getPlugin().getChatManager().colorMessage("KIT_CONTENT_TORNADO_GAME_ITEM_NAME"))
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_TORNADO_GAME_ITEM_DESCRIPTION"))
        .build());
  }

  @EventHandler
  public void onTornadoSpawn(PlugilyPlayerInteractEvent e) {
    if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = e.getPlayer();
    if(!getPlugin().getArenaRegistry().isInArena(player))
      return;

    ItemStack stack = VersionUtils.getItemInHand(player);
    if(!ItemUtils.isItemStackNamed(stack)
        || !ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(getPlugin().getChatManager().colorMessage("KIT_CONTENT_TORNADO_GAME_ITEM_NAME"))) {
      return;
    }
    if(!(getPlugin().getUserManager().getUser(player).getKit() instanceof TornadoKit)) {
      return;
    }
    if(active >= 2) {
      return;
    }
    getPlugin().getBukkitHelper().takeOneItem(player, stack);
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
        if(tornado.entities >= 7 || tornado.times > 55) {
          cancel();
          active--;
        }
      }
    }.runTaskTimer(getPlugin(), 1, 1);
  }

  private class Tornado {
    private Location location;
    private final Vector vector;
    private int angle;
    private int times = 0;
    private int entities = 0;

    Tornado(Location location) {
      this.location = location;
      vector = location.getDirection();
    }

    void setLocation(Location location) {
      this.location = location;
    }

    void update() {
      times++;
      int lines = 3;
      for(int l = 0; l < lines; l++) {
        for(double y = 0; y < maxHeight; y += 0.5) {
          double radius = y * radiusIncrement,
              radians = Math.toRadians(360.0 / lines * l + y * 25 - angle),
              x = Math.cos(radians) * radius,
              z = Math.sin(radians) * radius;
          VersionUtils.sendParticles("CLOUD", null, location.clone().add(x, y, z), 1, 0, 0, 0);
        }
      }
      pushNearbyEnemies();
      setLocation(location.add(vector.getX() / (3 + Math.random() / 2), 0, vector.getZ() / (3 + Math.random() / 2)));

      angle += 50;
    }

    private void pushNearbyEnemies() {
      for(Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
        if(CreatureUtils.isEnemy(entity)) {
          entities++;

          Vector velocityVec = vector.multiply(2).setY(0).add(new Vector(0, 1, 0));
          if(VersionUtils.isPaper() && (vector.getX() > 4.0 || vector.getZ() > 4.0)) {
            velocityVec = vector.setX(2.0).setZ(1.0); // Paper's sh*t
          }

          entity.setVelocity(velocityVec);
        }
      }
    }
  }
}
