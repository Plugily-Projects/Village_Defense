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

package plugily.projects.villagedefense.kits.premium;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
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
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

  public CleanerKit() {
    setName(new MessageBuilder("KIT_CONTENT_CLEANER_NAME").asKey().build());
    setKey("Cleaner");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_CLEANER_DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.cleaner");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.YELLOW, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD)
        .name(new MessageBuilder("KIT_CONTENT_CLEANER_GAME_ITEM_NAME").asKey().build())
        .lore(getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_CLEANER_GAME_ITEM_DESCRIPTION"))
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
  public void onClean(PlugilyPlayerInteractEvent event) {
    ItemStack itemStack = event.getItem();
    if(itemStack == null || itemStack.getType() != Material.BLAZE_ROD)
      return;

    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(itemStack)
        || !ComplementAccessor.getComplement().getDisplayName(itemStack.getItemMeta())
        .contains(new MessageBuilder("KIT_CONTENT_CLEANER_GAME_ITEM_NAME").asKey().build())) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if(!(user.getKit() instanceof CleanerKit)) {
      return;
    }
    if(user.isSpectator()) {
      new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
      return;
    }
    double cooldown = user.getCooldown("clean");
    if(cooldown > 0 && !user.isSpectator()) {
      new MessageBuilder("KIT_COOLDOWN").asKey().integer((int) cooldown).player(user.getPlayer()).sendPlayer();
      return;
    }
    if(arena.getEnemies().isEmpty()) {
      new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().player(user.getPlayer()).sendPlayer();
      return;
    }
    int amount = getKitsConfig().getInt("Kit-Settings.Cleaner.Base-Amount", 10);
    if(amount < arena.getEnemies().size()) {
      int increaseUnit = arena.getWave() / Math.max(1, getKitsConfig().getInt("Kit-Settings.Cleaner.Increase-After-Wave", 5));
      amount += increaseUnit * Math.max(0, getKitsConfig().getInt("Kit-Settings.Cleaner.Increase-Amount", 5));
      amount = Math.min(amount, getKitsConfig().getInt("Kit-Settings.Cleaner.Max-Amount", 50));
    }
    ArenaUtils.removeSpawnedEnemies(arena, amount, getKitsConfig().getDouble("Kit-Settings.Cleaner.Max-Health", 2048));

    VersionUtils.playSound(event.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH");
    new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_MAP").asKey().arena(arena).player(user.getPlayer()).sendArena();
    user.setCooldown("clean", getKitsConfig().getInt("Kit-Cooldown.Cleaner", 60));
  }
}
