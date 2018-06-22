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

package pl.plajer.villagedefense3.handlers.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class LanguageManager {

    private static Main plugin;
    private static VDLocale pluginLocale;
    private static Properties properties = new Properties();

    public static void init(Main pl) {
        plugin = pl;
        if(!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
            plugin.saveResource("language.yml", false);
        }
        setupLocale();
    }

    private static void loadProperties(){
        if(pluginLocale == LanguageManager.VDLocale.DEFAULT) return;
        try {
            properties.load(new InputStreamReader(plugin.getResource("locale_" + pluginLocale.getPrefix() + ".properties"), Charset.forName("UTF-8")));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupLocale() {
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
        loadProperties();
    }

    public static FileConfiguration getLanguageFile() {
        switch(pluginLocale) {
            case DEFAULT:
                return ConfigurationManager.getConfig("language");
            case DEUTSCH:
                return ConfigurationManager.getConfig("language_de");
            case POLSKI:
                return ConfigurationManager.getConfig("language_pl");
            default:
                return ConfigurationManager.getConfig("language");
        }
    }

    public static String getDefaultLanguageMessage(String message) {
        if(ConfigurationManager.getConfig("language").isSet(message)) {
            return ConfigurationManager.getConfig("language").getString(message);
        }
        MessageUtils.errorOccured();
        Bukkit.getConsoleSender().sendMessage("Game message not found!");
        Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
        Bukkit.getConsoleSender().sendMessage("Access string: " + message);
        return "ERR_MESSAGE_NOT_FOUND";
    }

    public static String getLanguageMessage(String message) {
        if(pluginLocale != LanguageManager.VDLocale.DEFAULT) {
            return properties.getProperty(ChatColor.translateAlternateColorCodes('&', message), "ERR_MESSAGE_NOT_FOUND");
        }
        return ConfigurationManager.getConfig("language").getString(message);
    }

    public enum VDLocale {
        DEFAULT("English", "en_GB", "Plajer"),
        POLSKI("Polski", "pl_PL", "Plajer"),
        DEUTSCH("Deutsch", "de_DE", "Tigerkatze");

        String formattedName;
        String prefix;
        String author;

        VDLocale(String formattedName, String prefix, String author) {
            this.prefix = prefix;
            this.formattedName = formattedName;
            this.author = author;
        }

        public String getFormattedName() {
            return formattedName;
        }

        public String getAuthor() {
            return author;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static VDLocale getPluginLocale() {
        return pluginLocale;
    }
}
