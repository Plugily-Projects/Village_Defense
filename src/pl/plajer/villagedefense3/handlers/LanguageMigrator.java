package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class LanguageMigrator {

    public static void languageFileUpdate() {
        if(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals("0")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] System notify >> Your language file is outdated! Updating...");
            LanguageManager.getLanguageFile().set("In-Game.Spectator.Target-Player-Health", "&cHealth: &7%health%");
            LanguageManager.getLanguageFile().set("Scoreboard.Footer", "&ewww.spigotmc.org");
            LanguageManager.getLanguageFile().set("File-Version-Do-Not-Edit", 1);
            LanguageManager.saveLanguageFile();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> Language file updated! Nice!");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> You're using latest language file version! Nice!");
    }

}
