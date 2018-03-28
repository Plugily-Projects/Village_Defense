package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

    private Main plugin;

    public CleanerKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Cleaner.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Cleaner.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite()) || player.hasPermission("villagedefense.kit.cleaner");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setColouredArmor(Color.YELLOW, player);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
        ItemStack cleaneritem = new ItemStack(Material.BLAZE_ROD);
        List<String> cleanerWandLore = Util.splitString(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Lore"), 40);
        String[] cleanerWandLoreArray = cleanerWandLore.toArray(new String[cleanerWandLore.size()]);

        this.setItemNameAndLore(cleaneritem, ChatManager.colorMessage("Kits.Cleaner.Game-Item-Name"), cleanerWandLoreArray);
        player.getInventory().addItem(cleaneritem);
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public void reStock(Player player) {}

    @EventHandler
    public void onClean(PlayerInteractEvent event) {
        if(!event.hasItem())
            return;
        if(event.getItem().getType() != Material.BLAZE_ROD)
            return;
        if(!(event.getItem().hasItemMeta()))
            return;
        if(!(event.getItem().getItemMeta().hasDisplayName()))
            return;
        if(!(event.getItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Name"))))
            return;
        if(ArenaRegistry.getArena(event.getPlayer()) == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Spectator-Warning"));
            return;
        }
        Arena arena = ArenaRegistry.getArena(event.getPlayer());

        if(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("clean") > 0 && !UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("clean")));
            event.getPlayer().sendMessage(msgstring);
            return;
        }
        if(arena.getZombies() != null) {
            for(Zombie zombie : arena.getZombies()) {
                zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                zombie.remove();
            }
            arena.getZombies().clear();
        } else {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("Kits.Cleaner.Cleaned-Map"), event.getPlayer());
        for(Player player1 : ArenaRegistry.getArena(event.getPlayer()).getPlayers()) {
            player1.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setCooldown("clean", 180);
    }
}
