package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;
import pl.plajer.villagedefense3.versions.ArenaInstance1_11_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_12_R1;
import pl.plajer.villagedefense3.versions.ArenaInstance1_8_R3;
import pl.plajer.villagedefense3.versions.ArenaInstance1_9_R1;

import java.util.List;

/**
 * Created by Tom on 21/07/2015.
 */
public class GolemFriendKit extends LevelKit {

    private Main plugin;


    public GolemFriendKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Golem-Friend.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Golem-Friend.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        setLevel(ConfigurationManager.getConfig("kits").getInt("Required-Level.GolemFriend"));
        plugin.getKitRegistry().registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.isOp() || player.hasPermission("villagefense.kit.golemfriend");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        ArmorHelper.setColouredArmor(Color.WHITE, player);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance == null)
            return;
        if(!(gameInstance instanceof ArenaInstance)) {
            return;
        }
        if(plugin.is1_8_R3()) {
            ArenaInstance1_8_R3 invasionInstance1_8_r3 = (ArenaInstance1_8_R3) gameInstance;
            invasionInstance1_8_r3.spawnGolem(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_9_R1()) {
            ArenaInstance1_9_R1 invasionInstance1_8_r3 = (ArenaInstance1_9_R1) gameInstance;
            invasionInstance1_8_r3.spawnGolem(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_11_R1()) {
            ArenaInstance1_11_R1 invasionInstance1_8_r3 = (ArenaInstance1_11_R1) gameInstance;
            invasionInstance1_8_r3.spawnGolem(invasionInstance1_8_r3.getStartLocation(), player);
        }
        if(plugin.is1_12_R1()) {
            ArenaInstance1_12_R1 invasionInstance1_8_r3 = (ArenaInstance1_12_R1) gameInstance;
            invasionInstance1_8_r3.spawnGolem(invasionInstance1_8_r3.getStartLocation(), player);

        }

    }

    @Override
    public Material getMaterial() {
        return Material.IRON_INGOT;
    }

    @Override
    public void reStock(Player player) {

    }
}
