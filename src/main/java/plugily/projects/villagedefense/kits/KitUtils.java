package plugily.projects.villagedefense.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
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
  }

}
