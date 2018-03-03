package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class LanguageMigrator {

    public static void languageFileUpdate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> You're using latest language file version! Nice!");
    }

}
