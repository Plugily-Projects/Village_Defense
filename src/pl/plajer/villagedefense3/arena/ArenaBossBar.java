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

package pl.plajer.villagedefense3.arena;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Boss bar manager class which allows 1.8-1.12 compatibility.
 *
 * Thanks to Fr33stylerRO for 1.8 code snippets
 */
public class ArenaBossBar extends BukkitRunnable {

    private Main plugin;
    private BossBar gameBar;
    private String title;
    private Map<Player, EntityWither> withers = new HashMap<>();

    public ArenaBossBar(Main plugin, String title) {
        this.plugin = plugin;
        if(plugin.is1_8_R3()){
            this.title = title;
            this.runTaskTimer(plugin, 0, 10);
        } else {
            gameBar = Bukkit.createBossBar(ChatManager.colorMessage("Bossbar.Main-Title"), BarColor.BLUE, BarStyle.SOLID);
        }
    }

    public void addPlayer(Player p) {
        if(plugin.is1_8_R3()) {
            EntityWither wither = new EntityWither(((CraftWorld) p.getWorld()).getHandle());
            Location l = getWitherLocation(p.getLocation());
            wither.setCustomName(title);
            wither.setInvisible(true);
            wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            withers.put(p, wither);
        } else {
            gameBar.addPlayer(p);
        }
    }

    public void removePlayer(Player p) {
        if(plugin.is1_8_R3()) {
            EntityWither wither = withers.remove(p);
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{wither.getId()});
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        } else {
            gameBar.removePlayer(p);
        }
    }

    public void setTitle(String title) {
        if(plugin.is1_8_R3()) {
            this.title = title;
            for(Map.Entry<Player, EntityWither> entry : withers.entrySet()) {
                EntityWither wither = entry.getValue();
                wither.setCustomName(title);
                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
                ((CraftPlayer) entry.getKey()).getHandle().playerConnection.sendPacket(packet);
            }
        } else {
            gameBar.setTitle(title);
        }
    }

    public void setProgress(double progress) {
        if(plugin.is1_8_R3()) {
            for(Map.Entry<Player, EntityWither> entry : withers.entrySet()) {
                EntityWither wither = entry.getValue();
                wither.setHealth((float) (progress * wither.getMaxHealth()));
                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
                ((CraftPlayer) entry.getKey()).getHandle().playerConnection.sendPacket(packet);
            }
        } else {
            gameBar.setProgress(progress);
        }
    }

    private Location getWitherLocation(Location l) {
        return l.add(l.getDirection().multiply(60));
    }

    @Override
    public void run() {
        for(Map.Entry<Player, EntityWither> en : withers.entrySet()) {
            EntityWither wither = en.getValue();
            Location l = getWitherLocation(en.getKey().getLocation());
            wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wither);
            ((CraftPlayer) en.getKey()).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
