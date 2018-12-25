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

package pl.plajer.villagedefense.kits.free;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.WeaponHelper;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 21/07/2015.
 */
public class ZombieFinderKit extends LevelKit implements Listener {

  public ZombieFinderKit() {
    setName(ChatManager.colorMessage("Kits.Zombie-Teleporter.Kit-Name"));
    List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Zombie-Teleporter.Kit-Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    this.setLevel(getKitsConfig().getInt("Required-Level.ZombieFinder"));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    ItemStack zombieteleporter = WeaponHelper.getEnchanted(new ItemStack(Material.BOOK), new Enchantment[] {Enchantment.DAMAGE_ALL}, new int[] {1});
    ItemMeta im = zombieteleporter.getItemMeta();
    im.setDisplayName(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Name"));
    im.setLore(Utils.splitString(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Lore"), 40));
    zombieteleporter.setItemMeta(im);
    player.getInventory().addItem(zombieteleporter);
  }

  @Override
  public Material getMaterial() {
    return Material.FISHING_ROD;
  }

  @Override
  public void reStock(Player player) {

  }

  @EventHandler
  public void onClean(PlayerInteractEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null || !Utils.isNamed(event.getItem()) || event.getItem().getType() != Material.BOOK
          || !event.getItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Name"))) {
        return;
      }
      User user = getPlugin().getUserManager().getUser(event.getPlayer().getUniqueId());
      if (user.isSpectator()) {
        event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Teleporter.Spectator-Warning"));
        return;
      }
      if (user.getCooldown("zombie") > 0 && !user.isSpectator()) {
        String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
        msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(user.getCooldown("zombie")));
        event.getPlayer().sendMessage(msgstring);
        return;
      }
      if (arena.getZombies() == null || arena.getZombies().isEmpty() || arena.getZombies().size() <= 0) {
        event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Teleporter.No-Available-Zombies"));
        return;
      } else {
        Integer rand = new Random().nextInt(arena.getZombies().size());
        arena.getZombies().get(rand).teleport(event.getPlayer());
        arena.getZombies().get(rand).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0));
        event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Teleporter.Zombie-Teleported"));
      }
      Utils.playSound(event.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH");
      user.setCooldown("zombie", 30);
    } catch (Exception ex) {
      new ReportedException(getPlugin(), ex);
    }
  }
}
