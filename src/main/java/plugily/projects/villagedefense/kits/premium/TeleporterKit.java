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

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.PremiumKit;
import plugily.projects.villagedefense.utils.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class TeleporterKit extends PremiumKit implements Listener {

  public TeleporterKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.teleporter");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
    player.getInventory().addItem(new ItemStack(Material.SADDLE));
    player.getInventory().addItem(new ItemBuilder(Material.GHAST_TEAR)
        .name(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_NAME))
        .lore(Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_LORE), 40))
        .build());
  }

  @Override
  public Material getMaterial() {
    return Material.ENDER_PEARL;
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }

  @EventHandler
  public void onRightClick(PlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if(!getPlugin().getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_NAME))) {
      return;
    }
    int rows = arena.getVillagers().size();
    for(Player player : arena.getPlayers()) {
      if(getPlugin().getUserManager().getUser(player).isSpectator()) {
        continue;
      }
      rows++;
    }
    rows = Utils.serializeInt(rows) / 9;
    prepareTeleporterGui(e.getPlayer(), arena, rows);
  }

  private void prepareTeleporterGui(Player player, Arena arena, int rows) {
    Gui gui = new Gui(getPlugin(), rows, getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_GAME_ITEM_MENU_NAME));
    gui.setOnGlobalClick(onClick -> onClick.setCancelled(true));
    OutlinePane pane = new OutlinePane(9, rows);
    gui.addPane(pane);
    for(Player arenaPlayer : arena.getPlayers()) {
      if(getPlugin().getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
      SkullMeta meta = (SkullMeta) skull.getItemMeta();
      meta.setOwningPlayer(arenaPlayer);
      getPlugin().getComplement().setDisplayName(meta, arenaPlayer.getName());
      getPlugin().getComplement().setLore(meta, Collections.singletonList(""));
      skull.setItemMeta(meta);
      pane.addItem(new GuiItem(skull, onClick -> {
        player.sendMessage(getPlugin().getChatManager().formatMessage(arena, getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_PLAYER), arenaPlayer));
        player.teleport(arenaPlayer);
        Utils.playSound(player.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30);
        player.closeInventory();
      }));
    }
    for(Villager villager : arena.getVillagers()) {
      pane.addItem(new GuiItem(new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(villager.getCustomName())
          .lore(villager.getUniqueId().toString())
          .build(), onClick -> {
        player.teleport(villager.getLocation());
        Utils.playSound(player.getLocation(), "ENTITY_ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT");
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30);
        player.sendMessage(getPlugin().getChatManager().colorMessage(Messages.KITS_TELEPORTER_TELEPORTED_TO_VILLAGER));
      }));
    }
    gui.show(player);
  }

}
