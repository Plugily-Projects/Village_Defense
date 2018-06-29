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
    private static Locale pluginLocale;
    private static Properties properties = new Properties();

    public static void init(Main pl) {
        plugin = pl;
        if(!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
            plugin.saveResource("language.yml", false);
        }
        setupLocale();
    }

    private static void loadProperties() {
        if(pluginLocale == Locale.ENGLISH) return;
        try {
            properties.load(new InputStreamReader(plugin.getResource("locale_" + pluginLocale.getPrefix() + ".properties"), Charset.forName("UTF-8")));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupLocale() {
        String locale = plugin.getConfig().getString("locale", "default");
        switch(locale.toLowerCase()){
            case "default":
            case "english":
            case "en":
                pluginLocale = Locale.ENGLISH;
                break;
            case "german":
            case "deutsch":
            case "de":
                pluginLocale = Locale.GERMAN;
                break;
            case "polish":
            case "polski":
            case "pl":
                pluginLocale = Locale.POLISH;
                break;
            case "spanish":
            case "espanol":
            case "español":
            case "es":
                pluginLocale = Locale.SPANISH;
                break;
            case "french":
            case "francais":
            case "français":
            case "fr":
                pluginLocale = Locale.FRENCH;
                break;
            case "indonesia":
            case "id":
                pluginLocale = Locale.INDONESIA;
                break;
            case "vietnamese":
            case "việt":
            case "vn":
                pluginLocale = Locale.VIETNAMESE;
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
                pluginLocale = Locale.ENGLISH;
                break;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] Loaded locale " + pluginLocale.getFormattedName() + " (" + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
        loadProperties();
    }

    //todo do something with me
    public static FileConfiguration getLanguageFile() {
        return ConfigurationManager.getConfig("language");
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
        if(pluginLocale != Locale.ENGLISH) {
            try {
                return properties.getProperty(ChatColor.translateAlternateColorCodes('&', message));
            } catch(NullPointerException ex){
                MessageUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Game message not found!");
                Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
                Bukkit.getConsoleSender().sendMessage("Access string: " + message);
                return "ERR_MESSAGE_NOT_FOUND";
            }
        }
        return ConfigurationManager.getConfig("language").getString(message);
    }

    public static Locale getPluginLocale() {
        return pluginLocale;
    }
}
