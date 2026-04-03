/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.api.kit.ability.IKitAbility;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XItemStack;
import plugily.projects.villagedefense.arena.Arena;


public class KitUtils {

  public static ItemStack handleItem(PluginMain plugin, Player player, ItemStack itemOriginal) {
    Arena arena = (Arena) plugin.getArenaRegistry().getArena(player);
    ItemStack itemStack = itemOriginal.clone();

    if(arena == null) {
      plugin.getDebugger().performance("Kit", "Handle item method called for player {1} item stack {2} but the arena was null.", player, itemStack);
      return itemStack;
    }
    plugin.getDebugger().performance("Kit", "Arena {0} Handle item method called for player {1} item stack {2}.", arena.getId(), player, itemStack);


    return itemStack;
  }

  public static void reStock(IUser user) {
    IKit kit = user.getKit();
    Player player = user.getPlayer();

    //restock items
    ConfigurationSection restockItems = (ConfigurationSection) kit.getOptionalConfiguration("restock");
    if(restockItems != null) {
      restockItems.getKeys(false).forEach((k) -> {

        ConfigurationSection itemConfigurationSection = restockItems.getConfigurationSection(k);
        assert itemConfigurationSection != null;

        ConfigurationSection itemStackConfigurationSection = itemConfigurationSection.getConfigurationSection("item");
        assert itemStackConfigurationSection != null;
        ItemStack item = XItemStack.deserialize(itemStackConfigurationSection);
        player.getInventory().addItem(item);
      });
    }
    //

    //run custom Abilities on Restock
    for(IKitAbility kitAbility : user.getKit().getAbilities()) {
      kitAbility.getCustomPlayerPluginConsumer().accept(player);
    }
  }

}
