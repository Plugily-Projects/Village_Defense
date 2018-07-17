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

package pl.plajer.villagedefense3.kits.level;

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
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.LevelKit;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Utils;
import pl.plajer.villagedefense3.utils.WeaponHelper;

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
        List<String> description = Utils.splitString(ChatManager.colorMessage("Kits.Zombie-Teleporter.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[0]));
        this.setLevel(ConfigurationManager.getConfig("kits").getInt("Required-Level.ZombieFinder"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
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
        im.setLore(Utils.splitString(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Lore"), 40));
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
        if(!event.hasItem() || event.getItem().getType() != Material.BOOK || !(event.getItem().hasItemMeta()) || !(event.getItem().getItemMeta().hasDisplayName())
                || !(event.getItem().getItemMeta().getDisplayName().contains(ChatManager.colorMessage("Kits.Zombie-Teleporter.Game-Item-Name")) || !ArenaRegistry.isInArena(event.getPlayer())))
            return;
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Teleporter.Spectator-Warning"));
            return;
        }
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie") > 0 && !UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
            msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("zombie")));
            event.getPlayer().sendMessage(msgstring);
            return;
        }
        if(arena.getZombies() == null || arena.getZombies().isEmpty() || arena.getZombies().size() <= 0) {
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Teleporter.No-Available-Zombies"));
            return;
        } else {
            Integer rand = new Random().nextInt(arena.getZombies().size());
            arena.getZombies().get(rand).teleport(event.getPlayer());
            arena.getZombies().get(rand).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 1));
            event.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Zombie-Teleporter.Zombie-Teleported"));
        }
        //todo sound manager!
        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1() || plugin.is1_13_R1()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setCooldown("zombie", 30);
    }
}
