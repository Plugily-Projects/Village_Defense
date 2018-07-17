/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.kits.premium;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Utils;
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
        List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Cleaner.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[0]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.cleaner");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setColouredArmor(Color.YELLOW, player);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
        ItemStack cleaneritem = new ItemStack(Material.BLAZE_ROD);
        List<String> cleanerWandLore = Utils.splitString(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Lore"), 40);
        String[] cleanerWandLoreArray = cleanerWandLore.toArray(new String[0]);

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
    public void onClean(PlayerInteractEvent e) {
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        if(!e.hasItem() || e.getItem().getType() != Material.BLAZE_ROD || !(e.getItem().hasItemMeta()) || !(e.getItem().getItemMeta().hasDisplayName()) ||
                !(e.getItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("Kits.Cleaner.Game-Item-Name"))) || arena == null)
            return;
        if(UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Spectator-Warning"));
            return;
        }
        if(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("clean") > 0 && !UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("clean")));
            e.getPlayer().sendMessage(msgstring);
            return;
        }
        if(arena.getZombies() != null) {
            for(Zombie zombie : arena.getZombies()) {
                zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
                zombie.remove();
            }
            arena.getZombies().clear();
        } else {
            e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        //todo sound manager!
        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1() || plugin.is1_13_R1()) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("Kits.Cleaner.Cleaned-Map"), e.getPlayer());
        for(Player player1 : ArenaRegistry.getArena(e.getPlayer()).getPlayers()) {
            player1.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
        UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("clean", 180);
    }
}
