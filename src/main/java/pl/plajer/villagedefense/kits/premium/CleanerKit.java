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

package pl.plajer.villagedefense.kits.premium;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.PermissionsManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.ArmorHelper;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

  public CleanerKit() {
    setName(ChatManager.colorMessage("Kits.Cleaner.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Cleaner.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.cleaner");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.YELLOW, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemBuilder(new ItemStack(Material.BLAZE_ROD))
        .name(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Name"))
        .lore(Utils.splitString(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Lore"), 40))
        .build());
    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
  }

  @Override
  public Material getMaterial() {
    return Material.BLAZE_POWDER;
  }

  @Override
  public void reStock(Player player) {
  }

  @EventHandler
  public void onClean(PlayerInteractEvent e) {
    try {
      Arena arena = ArenaRegistry.getArena(e.getPlayer());
      if (!Utils.isNamed(e.getItem()) || e.getItem().getType() != Material.BLAZE_ROD || !e.getItem().getItemMeta().getDisplayName()
          .contains(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Name")) || arena == null) {
        return;
      }
      User user = (getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId()));
      if (user.isSpectator()) {
        e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Spectator-Warning"));
        return;
      }
      if (user.getCooldown("clean") > 0 && !user.isSpectator()) {
        String message = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
        message = message.replaceFirst("%COOLDOWN%", Long.toString(user.getCooldown("clean")));
        e.getPlayer().sendMessage(message);
        return;
      }
      if (arena.getZombies() == null || arena.getZombies().isEmpty()) {
        e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
        return;
      }
      for (Zombie zombie : arena.getZombies()) {
        zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
        zombie.remove();
      }
      arena.getZombies().clear();
      Utils.playSound(e.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH");
      ChatManager.broadcast(arena, ChatManager.formatMessage(arena, ChatManager.colorMessage("Kits.Cleaner.Cleaned-Map"), e.getPlayer()));
      user.setCooldown("clean", 180);
    } catch (Exception ex) {
      new ReportedException(getPlugin(), ex);
    }
  }
}
