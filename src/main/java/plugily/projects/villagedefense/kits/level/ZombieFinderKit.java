/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.kits.level;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;

import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 21/07/2015.
 */
public class ZombieFinderKit extends LevelKit implements Listener {

  public ZombieFinderKit() {
    setName(new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_NAME").asKey().build());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_ZOMBIE_TELEPORTER_DESCRIPTION");
    setDescription(description);
    setLevel(getKitsConfig().getInt("Required-Level.ZombieFinder"));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(new ItemBuilder(WeaponHelper.getEnchanted(new ItemStack(Material.BOOK), new Enchantment[]{Enchantment.DAMAGE_ALL}, new int[]{1}))
        .name(new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_DESCRIPTION"))
        .build());
  }

  @Override
  public Material getMaterial() {
    return Material.FISHING_ROD;
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }

  @EventHandler
  public void onTeleport(PlugilyPlayerInteractEvent event) {
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(event.getItem()) || event.getItem().getType() != Material.BOOK
        || !ComplementAccessor.getComplement().getDisplayName(event.getItem().getItemMeta()).equals(new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_GAME_ITEM_GUI").asKey().build())) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if(user.isSpectator()) {
      new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
      return;
    }
    if(!(user.getKit() instanceof ZombieFinderKit)) {
      return;
    }
    double zombieCooldown = user.getCooldown("zombie");
    if(zombieCooldown > 0 && !user.isSpectator()) {
      new MessageBuilder("KIT_COOLDOWN").asKey().integer((int) zombieCooldown).player(user.getPlayer()).sendPlayer();
      return;
    }
    if(arena.getEnemies().isEmpty()) {
      new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_NOT_FOUND").asKey().player(user.getPlayer()).sendPlayer();
      return;
    }

    Creature creature = arena.getEnemies().get(arena.getEnemies().size() == 1 ? 0 : getPlugin().getRandom().nextInt(arena.getEnemies().size()));
    VersionUtils.teleport(creature, event.getPlayer().getLocation());
    creature.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0));
    new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_ZOMBIE").asKey().player(user.getPlayer()).sendPlayer();
    VersionUtils.playSound(event.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH");
    user.setCooldown("zombie", getKitsConfig().getInt("Kit-Cooldown.Zombie-Finder", 30));
  }
}
