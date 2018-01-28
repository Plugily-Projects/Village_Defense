package me.tomthedeveloper.kits;

import me.tomthedeveloper.InvasionInstance;
import me.tomthedeveloper.Main;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.LevelKit;
import me.tomthedeveloper.utils.Util;
import me.tomthedeveloper.utils.WeaponHelper;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 21/07/2015.
 */
public class ZombieFinderKit extends LevelKit implements Listener {


    private Main plugin;

    public ZombieFinderKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("kits.Zombie-Teleporter.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Zombie-Teleporter.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        this.setLevel(1);

    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return true;
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        ItemStack zombieteleporter = WeaponHelper.getEnchanted(new ItemStack(Material.BOOK), new Enchantment[]{Enchantment.DAMAGE_ALL}, new int[]{1});
        ItemMeta im = zombieteleporter.getItemMeta();
        im.setDisplayName(ChatManager.colorMessage("kits.Zombie-Teleporter.game-Item-Name"));
        im.setLore(Arrays.asList(ChatManager.colorMessage("kits.Zombie-Teleporter.game-Item-Lore")));
        zombieteleporter.setItemMeta(im);
        player.getInventory().addItem(zombieteleporter);
    }

    @Override
    public Material getMaterial() {
        return Material.FISHING_ROD;
    }

    @Override
    public void reStock(Player player) {

    }

    @EventHandler
    public void onClean(PlayerInteractEvent event) {
        if (!event.hasItem())
            return;
        if (event.getItem().getType() != Material.BOOK)
            return;
        if (!(event.getItem().hasItemMeta()))
            return;
        if (!(event.getItem().getItemMeta().hasDisplayName()))
            return;
        if (!(event.getItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("kits.Zombie-Teleporter.game-Item-Name"))))
        	return;
        if (plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
        	/*
        	 * TODO
        	 * Change it from Teleporter to Zombie-Teleporter kit
        	 */
            event.getPlayer().sendMessage(ChatManager.colorMessage("kits.Teleporter.Spectator-Warning"));
            return;
        }
        InvasionInstance invasionInstance = (InvasionInstance) plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer());

        if (UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie") > 0 && !UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
        	String msgstring = ChatManager.colorMessage("kits.Ability-Still-On-Cooldown");
        	msgstring = msgstring.replaceFirst("%COOLDOWN%",Long.toString( UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie")));
        	event.getPlayer().sendMessage(msgstring);
            return;
        }
        if (invasionInstance.getZombies() != null) {
            for (Zombie zombie : invasionInstance.getZombies()) {
                zombie.teleport(event.getPlayer());
                break;
            }
            //invasionInstance.getZombies().clear();
        } else {
            event.getPlayer().sendMessage(ChatManager.colorMessage("kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        if (plugin.is1_9_R1() || plugin.is1_12_R1()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setCooldown("zombie", 30);
    }
}
