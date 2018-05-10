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

package pl.plajer.villagedefense3.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;

import java.lang.reflect.Constructor;

/**
 * @author Plajer
 * <p>
 * Created at 11.02.2018
 */
public class MessageUtils {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
        if(plugin.is1_8_R3()) {
            try {
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

                Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

                sendPacket(player, packet);
            } catch(Exception ignored) {
            }
        } else {
            if(plugin.is1_9_R1()) {
                player.sendTitle(text, null);
            } else {
                player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
            }
        }
    }

    public static void sendSubTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
        if(plugin.is1_8_R3()) {
            try {
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

                Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

                sendPacket(player, packet);
            } catch(Exception ignored) {
            }
        } else {
            if(plugin.is1_9_R1()) {
                player.sendTitle(null, text);
            } else {
                player.sendTitle(null, text, fadeInTime, showTime, fadeOutTime);
            }
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch(Exception ignored) {
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch(ClassNotFoundException ignored) {
        }
        return null;
    }

    public static void thisVersionIsNotSupported() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  _   _           _                                                    _                _ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | \\ | |   ___   | |_     ___   _   _   _ __    _ __     ___    _ __  | |_    ___    __| |");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |  \\| |  / _ \\  | __|   / __| | | | | | '_ \\  | '_ \\   / _ \\  | '__| | __|  / _ \\  / _` |");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | |\\  | | (_) | | |_    \\__ \\ | |_| | | |_) | | |_) | | (_) | | |    | |_  |  __/ | (_| |");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |_| \\_|  \\___/   \\__|   |___/  \\__,_| | .__/  | .__/   \\___/  |_|     \\__|  \\___|  \\__,_|");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "                                       |_|     |_|                                        ");
    }

    public static void weAreSadSadSad() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " __        __                                                     _          __         __");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " \\ \\      / /   ___      __ _   _ __    ___     ___    __ _    __| |    _   / /    _   / /");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  \\ \\ /\\ / /   / _ \\    / _` | | '__|  / _ \\   / __|  / _` |  / _` |   (_) | |    (_) | | ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "   \\ V  V /   |  __/   | (_| | | |    |  __/   \\__ \\ | (_| | | (_| |    _  | |     _  | | ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "    \\_/\\_/     \\___|    \\__,_| |_|     \\___|   |___/  \\__,_|  \\__,_|   (_) | |    (_) | | ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "                                                                            \\_\\        \\_\\");
    }

    public static void errorOccured() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  _____                                                                                  _   _ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | ____|  _ __   _ __    ___    _ __      ___     ___    ___   _   _   _ __    ___    __| | | |");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |  _|   | '__| | '__|  / _ \\  | '__|    / _ \\   / __|  / __| | | | | | '__|  / _ \\  / _` | | |");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | |___  | |    | |    | (_) | | |      | (_) | | (__  | (__  | |_| | | |    |  __/ | (_| | |_|");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |_____| |_|    |_|     \\___/  |_|       \\___/   \\___|  \\___|  \\__,_| |_|     \\___|  \\__,_| (_)");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "                                                                                               ");
    }

    public static void updateIsHere() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  _   _               _           _          ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | | | |  _ __     __| |   __ _  | |_    ___ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | | | | | '_ \\   / _` |  / _` | | __|  / _ \\");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | |_| | | |_) | | (_| | | (_| | | |_  |  __/");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  \\___/  | .__/   \\__,_|  \\__,_|  \\__|  \\___|");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "         |_|                                 ");
    }

    public static void gonnaMigrate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  __  __   _                          _     _                    ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " |  \\/  | (_)   __ _   _ __    __ _  | |_  (_)  _ __     __ _             ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | |\\/| | | |  / _` | | '__|  / _` | | __| | | | '_ \\   / _` |            ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | |  | | | | | (_| | | |    | (_| | | |_  | | | | | | | (_| |  _   _   _ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " |_|  |_| |_|  \\__, | |_|     \\__,_|  \\__| |_| |_| |_|  \\__, | (_) (_) (_)");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "               |___/                                    |___/             ");
    }

}
