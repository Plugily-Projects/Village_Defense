package pl.plajer.villagedefense3.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LanguageMigrator {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private static List<String> migratable = Arrays.asList("bungee", "config", "kits", "language", "lobbyitems", "mysql");

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

    public static void migrateToNewFormat() {
        BigTextUtils.gonnaMigrate();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Village Defense 3 is migrating all files to the new file format...");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
        for(String file : migratable) {
            if(ConfigurationManager.getFile(file).exists()) {
                ConfigurationManager.getFile(file).renameTo(new File(plugin.getDataFolder(), "VD2_" + file + ".yml"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling VD3...");
    }

}
