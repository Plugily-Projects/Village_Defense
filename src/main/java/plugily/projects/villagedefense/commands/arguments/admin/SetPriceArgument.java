/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SetPriceArgument {

  public SetPriceArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("setprice", "villagedefense.admin.setprice", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda setprice &6<amount>", "/vda setprice <amount>",
            "&7Set price of holding item, it's required for game shop\n&6Permission: &7villagedefense.admin.setprice")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type price of item!");
          return;
        }

        Player player = (Player) sender;
        ItemStack item = VersionUtils.getItemInHand(player);
        if(item == null || item.getType() == Material.AIR) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_HOLD_ANY_ITEM"));
          return;
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null || !meta.hasLore()) {
          VersionUtils.setItemInHand(player, new ItemBuilder(item)
              .lore(ChatColor.GOLD + args[1] + " " + registry.getPlugin().getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY")).build());
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_COMMAND_EXECUTED"));
          return;
        }

        //check any price from lore
        List<String> lore = ComplementAccessor.getComplement().getLore(meta);
        for(String search : lore) {
          if(search.contains(registry.getPlugin().getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY"))) {
            lore.remove(search);
            break;
          }
        }
        lore.add(0, ChatColor.GOLD + args[1] + " " + registry.getPlugin().getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY"));
        ComplementAccessor.getComplement().setLore(meta, lore);
        item.setItemMeta(meta);
        player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("COMMANDS_COMMAND_EXECUTED"));
      }
    });
  }

}
