package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_8_R3;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

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
        KitRegistry.registerKit(this);
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
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(plugin.is1_8_R3()) {
            ArenaInitializer1_8_R3 initializer = (ArenaInitializer1_8_R3) arena;
            initializer.spawnGolem(initializer.getStartLocation(), player);
        } else if(plugin.is1_9_R1()) {
            ArenaInitializer1_9_R1 initializer = (ArenaInitializer1_9_R1) arena;
            initializer.spawnGolem(initializer.getStartLocation(), player);
        } else if(plugin.is1_11_R1()) {
            ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
            initializer.spawnGolem(initializer.getStartLocation(), player);
        } else if(plugin.is1_12_R1()) {
            ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
            initializer.spawnGolem(initializer.getStartLocation(), player);
        }
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_INGOT;
    }

    @Override
    public void reStock(Player player) {
        Arena arena = ArenaRegistry.getArena(player);
        if(arena.getWave() % 5 == 0) {
            if(plugin.is1_8_R3()) {
                ArenaInitializer1_8_R3 initializer = (ArenaInitializer1_8_R3) arena;
                initializer.spawnGolem(initializer.getStartLocation(), player);
            } else if(plugin.is1_9_R1()) {
                ArenaInitializer1_9_R1 initializer = (ArenaInitializer1_9_R1) arena;
                initializer.spawnGolem(initializer.getStartLocation(), player);
            } else if(plugin.is1_11_R1()) {
                ArenaInitializer1_11_R1 initializer = (ArenaInitializer1_11_R1) arena;
                initializer.spawnGolem(initializer.getStartLocation(), player);
            } else if(plugin.is1_12_R1()) {
                ArenaInitializer1_12_R1 initializer = (ArenaInitializer1_12_R1) arena;
                initializer.spawnGolem(initializer.getStartLocation(), player);
            }
        }
    }
}
