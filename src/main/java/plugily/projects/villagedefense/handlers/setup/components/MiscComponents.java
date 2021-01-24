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

package plugily.projects.villagedefense.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.handlers.sign.ArenaSign;
import plugily.projects.villagedefense.utils.MaterialUtil;
import plugily.projects.villagedefense.utils.Utils;
import plugily.projects.villagedefense.utils.constants.Constants;
import plugily.projects.villagedefense.utils.conversation.SimpleConversationBuilder;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class MiscComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Arena arena = setupInventory.getArena();
    if (arena == null) {
      return;
    }
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    ItemStack bungeeItem;
    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      bungeeItem = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
          .name(plugin.getChatManager().colorRawMessage("&e&lAdd Game Sign"))
          .lore(ChatColor.GRAY + "Target a sign and click this.")
          .lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)")
          .build();
    } else {
      bungeeItem = new ItemBuilder(Material.BARRIER)
          .name(plugin.getChatManager().colorRawMessage("&c&lAdd Game Sign"))
          .lore(ChatColor.GRAY + "Option disabled with Bungee Cord module.")
          .lore(ChatColor.DARK_GRAY + "Bungee mode is meant to be one arena per server")
          .lore(ChatColor.DARK_GRAY + "If you wish to have multi arena, disable bungee in config!")
          .build();
    }
    pane.addItem(new GuiItem(bungeeItem, e -> {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        return;
      }
      e.getWhoClicked().closeInventory();
      Location location = player.getTargetBlock(null, 10).getLocation();
      if (!(location.getBlock().getState() instanceof Sign)) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cPlease look at sign to add as a game sign!"));
        return;
      }
      if (location.distance(e.getWhoClicked().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()
          && e.getClick() != ClickType.SHIFT_LEFT) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
            + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so."));
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&cYou can ignore this warning and add sign with Shift + Left Click, but for now &c&loperation is cancelled"));
        return;
      }
      plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) location.getBlock().getState(), arena));
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SIGNS_SIGN_CREATED));
      String signLoc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(signLoc);
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 5, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Map Name"))
        .lore(ChatColor.GRAY + "Click to set arena map name")
        .lore("", plugin.getChatManager().colorRawMessage("&a&lCurrently: &e" + config.getString("instances." + arena.getId() + ".mapname")))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(plugin).withPrompt(new StringPrompt() {
        @Override
        public String getPromptText(ConversationContext context) {
          return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat arena name! You can use color codes.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          String name = plugin.getChatManager().colorRawMessage(input);
          player.sendRawMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + arena.getId() + " set to " + name));
          arena.setMapName(name);
          config.set("instances." + arena.getId() + ".mapname", arena.getMapName());
          ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());

          new SetupInventory(arena, player).openInventory();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    }), 6, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.CHEST)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Game Shop"))
        .lore(ChatColor.GRAY + "Look at chest with items")
        .lore(ChatColor.GRAY + "and click it to set it as game shop.")
        .lore(ChatColor.DARK_GRAY + "(it allows to click villagers to buy game items)")
        .lore(ChatColor.RED + "Remember to set item prices for the game")
        .lore(ChatColor.RED + "using /vda setprice command!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      Block targetBlock = player.getTargetBlock(null, 10);
      if (targetBlock == null || targetBlock.getType() != Material.CHEST) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cLook at the chest! You are targeting something else!"));
        return;
      }
      boolean found = false;
      for (ItemStack stack : ((Chest) targetBlock.getState()).getBlockInventory()) {
        if (stack == null) {
          continue;
        }
        if (stack.hasItemMeta() && stack.getItemMeta().hasLore()
            && stack.getItemMeta().getLore().get(stack.getItemMeta().getLore().size() - 1)
            .contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_CURRENCY_IN_SHOP))) {
          found = true;
          break;
        }
      }
      if (!found) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | No items in shop have price set! Set their prices using &6/vda setprice&c! You can ignore this warning"));
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".shop", targetBlock.getLocation());
      player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7You can use special items in shops! Check out https://bit.ly/2T2GhA9"));
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 7, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.EMERALD_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lAdd Villager Location"))
        .lore(ChatColor.GRAY + "Click add new villager spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore("", setupInventory.getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".villagerspawns", 2))
        .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all spawns"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".villagerspawns", null);
        arena.getVillagerSpawns().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aVillager spawn points deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
        return;
      }
      int villagers = (config.isSet("instances." + arena.getId() + ".villagerspawns")
          ? config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns").getKeys(false).size() : 0) + 1;
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".villagerspawns." + villagers, player.getLocation());
      String progress = villagers >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
      player.sendMessage(plugin.getChatManager().colorRawMessage(progress + "&aVillager spawn added! &8(&7" + villagers + "/2&8)"));
      if (villagers == 2) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eInfo | &aYou can add more than 2 villager spawns! Two is just a minimum!"));
      }
      arena.getVillagerSpawns().add(player.getLocation());
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 8, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.ROTTEN_FLESH)
        .name(plugin.getChatManager().colorRawMessage("&e&lAdd Zombie Location"))
        .lore(ChatColor.GRAY + "Click add new zombie spawn")
        .lore(ChatColor.GRAY + "on the place you're standing at.")
        .lore("", setupInventory.getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".zombiespawns", 2))
        .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all spawns"))
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".zombiespawns", null);
        arena.getZombieSpawns().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aZombie spawn points deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
        return;
      }
      int zombies = (config.isSet("instances." + arena.getId() + ".zombiespawns")
          ? config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns").getKeys(false).size() : 0) + 1;
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".zombiespawns." + zombies, player.getLocation());
      String progress = zombies >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
      player.sendMessage(plugin.getChatManager().colorRawMessage(progress + "&aZombie spawn added! &8(&7" + zombies + "/2&8)"));
      if (zombies == 2) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eInfo | &aYou can add more than 2 zombie spawns! Two is just a minimum!"));
      }
      arena.getZombieSpawns().add(player.getLocation());
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 0, 1);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.OAK_DOOR.parseItem())
            .name(plugin.getChatManager().colorRawMessage("&e&lAdd Game Door"))
            .lore(ChatColor.GRAY + "Target arena door and click this.")
            .lore(ChatColor.DARK_GRAY + "(doors are required and will be")
            .lore(ChatColor.DARK_GRAY + "regenerated each game, villagers will hide")
            .lore(ChatColor.DARK_GRAY + "in houses so you can put doors there)")
            .lore("", setupInventory.getSetupUtilities().isOptionDoneSection("instances." + arena.getId() + ".doors", 1))
            .lore("", plugin.getChatManager().colorRawMessage("&8Shift + Right Click to remove all locations"))
            .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".doors", null);
        arena.getMapRestorerManager().getGameDoorLocations().clear();
        player.sendMessage(plugin.getChatManager().colorRawMessage("&eDone | &aDoor locations deleted, you can add them again now!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
        return;
      }
      Block block = player.getTargetBlock(null, 5);
      Material door = block.getType();
      if (!MaterialUtil.isDoor(door)) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cTarget block is not an wood door!"));
        return;
      }
      int doors = (config.isSet("instances." + arena.getId() + ".doors")
          ? config.getConfigurationSection("instances." + arena.getId() + ".doors").getKeys(false).size() : 0) + 1;

      Block relativeBlock = null;
      if (block.getRelative(BlockFace.DOWN).getType() == door) {
        relativeBlock = block;
        block = block.getRelative(BlockFace.DOWN);
      } else if (block.getRelative(BlockFace.UP).getType() == door) {
        relativeBlock = block.getRelative(BlockFace.UP);
      }
      if (relativeBlock == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cThis door doesn't have 2 blocks? Maybe it's bugged? Try placing it again."));
        return;
      }
      String relativeLocation = relativeBlock.getWorld().getName() + "," + relativeBlock.getX() + "," + relativeBlock.getY() + "," + relativeBlock.getZ() + ",0.0" + ",0.0";
      config.set("instances." + arena.getId() + ".doors." + doors + ".location", relativeLocation);
      config.set("instances." + arena.getId() + ".doors." + doors + ".byte", 8);
      doors++;

      String doorLocation = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0" + ",0.0";
      config.set("instances." + arena.getId() + ".doors." + doors + ".location", doorLocation);
      if (!ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1) && !ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_12_R1)
          && block.getState().getData() instanceof Door) {
        config.set("instances." + arena.getId() + ".doors." + doors + ".byte", Utils.getDoorByte(((Door) block.getState().getData()).getFacing()));
      } else {
        try {
          config.set("instances." + arena.getId() + ".doors." + doors + ".byte", block.getClass().getDeclaredMethod("getData").invoke(block));
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
      player.sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aDoor successfully added! To apply door changes you must either re-register arena or reload plugin via /vda reload"));
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
    }), 1, 1);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&6&l► Enhancements Addon ◄ &8(AD)"))
        .lore(ChatColor.GRAY + "Enhance Village Defense gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Features of this addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://patreon.plugily.xyz/"));
    }), 7, 1);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lView Setup Video"))
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check out this video: " + SetupInventory.VIDEO_LINK));
    }), 8, 1);
  }

}
