package pl.plajer.villagedefense3.kits;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 21/07/2015.
 */
public class ZombieFinderKit extends LevelKit implements Listener {

    private Main plugin;

    public ZombieFinderKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Zombie-Teleporter.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Zombie-Teleporter.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        this.setLevel(ConfigurationManager.getConfig("kits").getInt("Required-Level.ZombieFinder"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getKitRegistry().registerKit(this);
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
        im.setDisplayName(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Name"));
        im.setLore(Collections.singletonList(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Lore")));
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
        if(!event.hasItem())
            return;
        if(event.getItem().getType() != Material.BOOK)
            return;
        if(!(event.getItem().hasItemMeta()))
            return;
        if(!(event.getItem().getItemMeta().hasDisplayName()))
            return;
        if(!(event.getItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Name"))))
            return;
        if(plugin.getArenaRegistry().getArena(event.getPlayer()) == null)
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            /*
             * TODO
             * Change it from Teleporter to Zombie-Teleporter kit
             */
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Teleporter.Spectator-Warning"));
            return;
        }
        Arena arena = plugin.getArenaRegistry().getArena(event.getPlayer());

        if(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie") > 0 && !UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie")));
            event.getPlayer().sendMessage(msgstring);
            return;
        }
        if(arena.getZombies() != null || !arena.getZombies().isEmpty() || !(arena.getZombies().size() == 0)) {
            Integer rand = new Random().nextInt(arena.getZombies().size());
            arena.getZombies().get(rand).teleport(event.getPlayer());
            arena.getZombies().get(rand).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 1));
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Telelporter.Zombie-Teleported"));
        } else {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Teleporter.No-Available-Zombies"));
            return;
        }
        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setCooldown("zombie", 30);
    }
}
