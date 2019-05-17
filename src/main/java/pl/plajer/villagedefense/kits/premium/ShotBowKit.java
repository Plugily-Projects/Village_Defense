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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.PremiumKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;

/**
 * Created by Tom on 27/08/2014.
 */
public class ShotBowKit extends PremiumKit implements Listener {

  public ShotBowKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_SHOT_BOW_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_SHOT_BOW_DESCRIPTION), 40);
    this.setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.shotbow");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getEnchantedBow(new Enchantment[] {Enchantment.DURABILITY, Enchantment.ARROW_KNOCKBACK}, new int[] {10, 1}));
    player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    ArmorHelper.setColouredArmor(Color.YELLOW, player);
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
  }

  @Override
  public Material getMaterial() {
    return Material.ARROW;
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
  }

  @EventHandler
  public void onBowInteract(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    User user = getPlugin().getUserManager().getUser(e.getPlayer());
    if (stack == null || stack.getType() != Material.BOW || !e.getPlayer().getInventory().contains(Material.ARROW)
        || !(user.getKit() instanceof ShotBowKit)
        || user.isSpectator()) {
      return;
    }
    if (!user.checkCanCastCooldownAndMessage("shotbow")) {
      return;
    }
    for (int i = 0; i < 4; i++) {
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
        Arrow pr = e.getPlayer().launchProjectile(Arrow.class);
        pr.setVelocity(e.getPlayer().getLocation().getDirection().multiply(3));
        pr.setBounce(false);
        pr.setShooter(e.getPlayer());
        pr.setCritical(true);

        if (e.getPlayer().getInventory().contains(Material.ARROW)) {
          e.getPlayer().getInventory().removeItem(new ItemStack(Material.ARROW, 1));
        }
      }, 2 * (2 * i));
    }
    e.setCancelled(true);
    user.setCooldown("shotbow", 5);
  }

}
