package me.tomthedeveloper.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public class LanguageManager {

    private static JavaPlugin plugin;
    private static FileConfiguration languageConfig = null;
    private static File languageConfigFile = null;

    public static void init(JavaPlugin pl) {
        plugin = pl;
    }

    public static void saveDefaultLanguageFile() {
        if(languageConfigFile == null) {
            languageConfigFile = new File(plugin.getDataFolder(), "language.yml");
        }
        if(!languageConfigFile.exists()) {
            plugin.saveResource("language.yml", false);
        }
    }

    public static String getLanguageMessage(String message) {
        if(languageConfig == null) {
            reloadLanguageFile();
        }
        if(getLanguageFile().isSet(message)) {
            return getLanguageFile().getString(message);
        }
        return null;
    }

    public static FileConfiguration getLanguageFile() {
        if(languageConfig == null) {
            reloadLanguageFile();
        }
        return languageConfig;
    }

    public static void reloadLanguageFile() {
        if(languageConfigFile == null) {
            languageConfigFile = new File(plugin.getDataFolder(), "language.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

        // Look for defaults in the jar
        try {
            Reader defConfigStream = new InputStreamReader(plugin.getResource("language.yml"));
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                languageConfig.setDefaults(defConfig);
            }
        } catch(Exception e) {
            ChatManager.sendErrorHeader("reloading language configuration");
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- restart the server");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
        }
    }

    public static void saveLanguageFile() {
        if(languageConfig == null || languageConfigFile == null) {
            return;
        }
        try {
            getLanguageFile().save(languageConfigFile);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save file to " + languageConfigFile, ex);
            ChatManager.sendErrorHeader("saving language file");
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- restart the server");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
        }
    }

}
