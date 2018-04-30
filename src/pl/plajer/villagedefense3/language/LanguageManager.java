/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public class LanguageManager {

    private static Main plugin;
    private static FileConfiguration languageConfig = null;
    private static File languageConfigFile = null;
    private static VDLocale pluginLocale;

    public static void init(Main pl) {
        plugin = pl;
        saveDefaultLanguageFile();
        setupLocale();
    }

    private static void setupLocale() {
        plugin.saveResource("language_de.yml", true);
        plugin.saveResource("language_pl.yml", true);
        String locale = plugin.getConfig().getString("locale");
        if(locale.equalsIgnoreCase("default") || locale.equalsIgnoreCase("english")) {
            pluginLocale = VDLocale.DEFAULT;
        } else if(locale.equalsIgnoreCase("de") || locale.equalsIgnoreCase("deutsch")) {
            pluginLocale = VDLocale.DEUTSCH;
            if(!ConfigurationManager.getConfig("language_de").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_de").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is outdated! Not every message will be in german.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else if(locale.equalsIgnoreCase("pl") || locale.equalsIgnoreCase("polski")) {
            pluginLocale = VDLocale.POLSKI;
            if(!ConfigurationManager.getConfig("language_pl").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_pl").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is outdated! Not every message will be in polish.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
            pluginLocale = VDLocale.DEFAULT;
        }
    }

    public static String getDefaultLanguageMessage(String message) {
        if(ConfigurationManager.getConfig("language").isSet(message)) {
            return ConfigurationManager.getConfig("language").getString(message);
        }
        return null;
    }

    public static String getLanguageMessage(String message) {
        switch(pluginLocale) {
            case DEFAULT:
                if(ConfigurationManager.getConfig("language").isSet(message)) {
                    return ConfigurationManager.getConfig("language").getString(message);
                }
                return null;
            case DEUTSCH:
                if(ConfigurationManager.getConfig("language_de").isSet(message)) {
                    return ConfigurationManager.getConfig("language_de").getString(message);
                }
                return null;
            case POLSKI:
                if(ConfigurationManager.getConfig("language_pl").isSet(message)) {
                    return ConfigurationManager.getConfig("language_pl").getString(message);
                }
                return null;
            default:
                if(ConfigurationManager.getConfig("language").isSet(message)) {
                    return ConfigurationManager.getConfig("language").getString(message);
                }
                return null;
        }
    }

    public static void saveDefaultLanguageFile() {
        if(languageConfigFile == null) {
            languageConfigFile = new File(plugin.getDataFolder(), "language.yml");
        }
        if(!languageConfigFile.exists()) {
            plugin.saveResource("language.yml", false);
        }
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
            e.printStackTrace();
            MessageUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot reload language.yml file!");
            Bukkit.getConsoleSender().sendMessage("Restart the server!");
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
            ex.printStackTrace();
            MessageUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save language.yml file!");
            Bukkit.getConsoleSender().sendMessage("Restart the server!");
        }
    }

    private enum VDLocale {
        DEFAULT, DEUTSCH, POLSKI
    }

}
