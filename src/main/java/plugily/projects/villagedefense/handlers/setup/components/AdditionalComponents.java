/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
 *
 */

package plugily.projects.villagedefense.handlers.setup.components;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.setup.components.PluginAdditionalComponents;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.MaterialUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.FastInv;
import plugily.projects.villagedefense.arena.Arena;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 17.12.2021
 */
public class AdditionalComponents extends PluginAdditionalComponents {

  @Override
  public void injectComponents(FastInv gui) {
    Arena arena = (Arena) getSetupInventory().getArena();
    if(arena == null) {
      return;
    }
    Player player = getSetupInventory().getPlayer();
    FileConfiguration config = getSetupInventory().getConfig();
    PluginMain plugin = getSetupInventory().getPlugin();

    gui.setItem(7, new ItemBuilder(Material.CHEST)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Game Shop"))
        .lore(ChatColor.GRAY + "Look at chest with items")
        .lore(ChatColor.GRAY + "and click it to set it as game shop.")
        .lore(ChatColor.DARK_GRAY + "(it allows to click villagers to buy game items)")
        .lore(ChatColor.RED + "Remember to set item prices for the game")
        .lore(ChatColor.RED + "using /vda setprice command!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      Block targetBlock = player.getTargetBlock(null, 10);
      if(targetBlock == null || targetBlock.getType() != Material.CHEST) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cLook at the chest! You are targeting something else!"));
        return;
      }
      boolean found = false;
      for(ItemStack stack : ((Chest) targetBlock.getState()).getBlockInventory()) {
        if(stack == null) {
          continue;
        }

        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        List<String> lore;

        if(meta != null && meta.hasLore() && (lore = ComplementAccessor.getComplement().getLore(meta)).get(lore.size() - 1)
            .contains(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY"))) {
          found = true;
          break;
        }
      }
      if(!found) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | No items in shop have price set! Set their prices using &6/vda setprice&c! You can ignore this warning"));
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".shop", targetBlock.getLocation());
      player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7You can use special items in shops! Check out https://wiki.plugily.xyz/villagedefense/support/faq#special-shop-items"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    });

    gui.setItem(8, new ItemBuilder(Material.EMERALD_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lAdd Villager Location"))
        .lore(ChatColor.GRAY + "Click add new villager spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore("", getSetupInventory().getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".villagerspawns", 2))
        .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all spawns"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      if(e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".villagerspawns", null);
        arena.getVillagerSpawns().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aVillager spawn points deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }

      org.bukkit.configuration.ConfigurationSection villagerSection = config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns");
      int villagers = (villagerSection != null ? villagerSection.getKeys(false).size() : 0) + 1;

      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".villagerspawns." + villagers, player.getLocation());
      String progress = villagers >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
      player.sendMessage(plugin.getChatManager().colorRawMessage(progress + "&aVillager spawn added! &8(&7" + villagers + "/2&8)"));
      if(villagers == 2) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eInfo | &aYou can add more than 2 villager spawns! Two is just a minimum!"));
      }
      arena.getVillagerSpawns().add(player.getLocation());
      ConfigUtils.saveConfig(plugin, config, "arenas");
    });

    gui.setItem(9, new ItemBuilder(Material.ROTTEN_FLESH)
        .name(plugin.getChatManager().colorRawMessage("&e&lAdd Zombie Location"))
        .lore(ChatColor.GRAY + "Click add new zombie spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore("", getSetupInventory().getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".zombiespawns", 2))
        .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all spawns"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      if(e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".zombiespawns", null);
        arena.getZombieSpawns().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aZombie spawn points deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }

      org.bukkit.configuration.ConfigurationSection zombieSection = config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns");
      int zombies = (zombieSection != null ? zombieSection.getKeys(false).size() : 0) + 1;

      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".zombiespawns." + zombies, player.getLocation());
      String progress = zombies >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
      player.sendMessage(plugin.getChatManager().colorRawMessage(progress + "&aZombie spawn added! &8(&7" + zombies + "/2&8)"));
      if(zombies == 2) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eInfo | &aYou can add more than 2 zombie spawns! Two is just a minimum!"));
      }
      arena.getZombieSpawns().add(player.getLocation());
      ConfigUtils.saveConfig(plugin, config, "arenas");
    });

    gui.setItem(10, new ItemBuilder(XMaterial.OAK_DOOR.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lAdd Game Door"))
        .lore(ChatColor.GRAY + "Target arena door and click this.")
        .lore(ChatColor.DARK_GRAY + "(doors are required and will be")
        .lore(ChatColor.DARK_GRAY + "regenerated each game, villagers will hide")
        .lore(ChatColor.DARK_GRAY + "in houses so you can put doors there)")
        .lore("", getSetupInventory().getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".doors", 1))
        .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all locations"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      if(e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".doors", null);
        arena.getMapRestorerManager().getGameDoorLocations().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aDoor locations deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }
      Block block = player.getTargetBlock(null, 5);
      Material door = block.getType();
      if(!MaterialUtils.isDoor(door)) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cTarget block is not an wood door!"));
        return;
      }

      org.bukkit.configuration.ConfigurationSection doorSection = config.getConfigurationSection("instances." + arena.getId() + ".doors");
      int doors = (doorSection != null ? doorSection.getKeys(false).size() : 0) + 1;

      Block relativeBlock = null;
      Block faceBlock;

      if((faceBlock = block.getRelative(BlockFace.DOWN)).getType() == door) {
        relativeBlock = block;
        block = faceBlock;
      } else if((faceBlock = block.getRelative(BlockFace.UP)).getType() == door) {
        relativeBlock = faceBlock;
      }

      if(relativeBlock == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cThis door doesn't have 2 blocks? Maybe it's bugged? Try placing it again."));
        return;
      }

      String relativeLocation = relativeBlock.getWorld().getName() + "," + relativeBlock.getX() + "," + relativeBlock.getY() + "," + relativeBlock.getZ() + ",0.0" + ",0.0";
      config.set("instances." + arena.getId() + ".doors." + doors + ".location", relativeLocation);
      config.set("instances." + arena.getId() + ".doors." + doors + ".byte", 8);
      doors++;

      String doorLocation = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0" + ",0.0";
      config.set("instances." + arena.getId() + ".doors." + doors + ".location", doorLocation);
      if(!ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1) && !ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_12_R1)
          && block.getState().getData() instanceof Door) {
        config.set("instances." + arena.getId() + ".doors." + doors + ".byte", plugin.getBukkitHelper().getDoorByte(((Door) block.getState().getData()).getFacing()));
      } else {
        try {
          config.set("instances." + arena.getId() + ".doors." + doors + ".byte", block.getClass().getDeclaredMethod("getData").invoke(block));
        } catch(Exception e1) {
          e1.printStackTrace();
        }
      }
      player.sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aDoor successfully added! To apply door changes you must either re-register arena or reload plugin via /vda reload"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    });

  }
}
