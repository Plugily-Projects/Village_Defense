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

package pl.plajer.villagedefense.commands.arguments.admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.CommandArgument;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SetPriceArgument {

  public SetPriceArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new CommandArgument("setprice", "villagedefense.admin.setprice", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type price of item!");
          return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().equals(Material.AIR)) {
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Hold-Any-Item"));
          return;
        }
        //check any price from lore
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
          ItemMeta meta = item.getItemMeta();
          List<String> lore = item.getItemMeta().getLore();
          for (String search : lore) {
            if (search.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
              lore.remove(search);
              break;
            }
          }
          lore.add(0, ChatColor.GOLD + args[1] + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
          meta.setLore(lore);
          item.setItemMeta(meta);
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Command-Executed-Item-Updated"));
        } else {
          MinigameUtils.addLore(item, ChatColor.GOLD + args[1] + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Command-Executed"));
        }
      }
    });
  }

}