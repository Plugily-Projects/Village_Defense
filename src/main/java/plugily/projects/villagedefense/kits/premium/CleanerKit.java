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

import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.helper.ArmorHelper;
import plugily.projects.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.PremiumKit;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

  public CleanerKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
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
    player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD)
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_GAME_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_GAME_ITEM_LORE), 40))
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
  public void onClean(CBPlayerInteractEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if(!ItemUtils.isItemStackNamed(e.getItem()) || e.getItem().getType() != Material.BLAZE_ROD
        || !ComplementAccessor.getComplement().getDisplayName(e.getItem().getItemMeta())
        .contains(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_GAME_ITEM_NAME)) || arena == null) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(e.getPlayer());
    if (!(user.getKit() instanceof CleanerKit)) {
      return;
    }
    if(user.isSpectator()) {
      e.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage(Messages.SPECTATOR_WARNING));
      return;
    }
    long cooldown = user.getCooldown("clean");
    if(cooldown > 0 && !user.isSpectator()) {
      String message = getPlugin().getChatManager().colorMessage(Messages.KITS_ABILITY_STILL_ON_COOLDOWN);
      message = message.replaceFirst("%COOLDOWN%", Long.toString(cooldown));
      e.getPlayer().sendMessage(message);
      return;
    }
    if(arena.getEnemies().isEmpty()) {
      e.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_NOTHING_TO_CLEAN));
      return;
    }
    double maxHealth = getKitsConfig().getDouble("Kit-Settings.Cleaner.Max-Health-To-Clean", 2048);
    int amount = getKitsConfig().getInt("Kit-Settings.Cleaner.Base-Amount", 10);
    if (amount < arena.getEnemies().size()) {
      int increaseUnit = arena.getWave() / Math.max(1, getKitsConfig().getInt("Kit-Settings.Cleaner.Base-Amount", 10));
      amount += increaseUnit * Math.max(0, getKitsConfig().getInt("Kit-Settings.Cleaner.Increase-Amount", 10));
    }
    ArenaUtils.removeSpawnedEnemies(arena, amount, maxHealth);

    Utils.playSound(e.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH");
    getPlugin().getChatManager().broadcastMessage(arena, getPlugin().getChatManager()
        .formatMessage(arena, getPlugin().getChatManager().colorMessage(Messages.KITS_CLEANER_CLEANED_MAP), e.getPlayer()));
    user.setCooldown("clean", getKitsConfig().getInt("Kit-Cooldown.Cleaner", 60));
  }
}
