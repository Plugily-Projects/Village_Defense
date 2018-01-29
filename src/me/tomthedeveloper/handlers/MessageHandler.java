package me.tomthedeveloper.handlers;

import me.tomthedeveloper.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * @author Plajer
 * <p>
 * Created at 10 lis 2017
 */
public class MessageHandler {

    private static Main plugin;

    public MessageHandler(Main plugin) {
        this.plugin = plugin;
    }

    public static void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
        } catch(Exception ex) {
        }
    }

    public static void sendSubtitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
        } catch(Exception ex) {
        }
    }

    public static void sendActionbar(Player player, String text) {
        if(plugin.is1_9_R1() || plugin.is1_12_R1()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch(Exception ex) {
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch(ClassNotFoundException ex) {
        }
        return null;
    }
}
