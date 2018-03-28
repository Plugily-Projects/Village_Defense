package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 21/07/2015.
 */
public class LooterKit extends LevelKit implements Listener {

    public LooterKit(Main plugin) {
        setName(ChatManager.colorMessage("Kits.Looter.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Looter.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        setLevel(ConfigurationManager.getConfig("kits").getInt("Required-Level.Looter"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt("level") >= this.getLevel() || player.isOp() || player.hasPermission("villagefense.kit.looter");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        ArmorHelper.setColouredArmor(Color.ORANGE, player);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));

    }

    @Override
    public Material getMaterial() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public void reStock(Player player) {}

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getType() != EntityType.ZOMBIE) return;
        if(event.getEntity().getKiller() == null) return;
        Player player = event.getEntity().getKiller();
        if(ArenaRegistry.getArena(player) == null) return;
        User user = UserManager.getUser(player.getUniqueId());
        if(user.getKit() instanceof LooterKit) {
            player.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH, 1));
        }

    }
}
