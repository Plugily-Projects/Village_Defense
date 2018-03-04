package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public class LanguageManager {

    private static Main plugin;
    private static FileConfiguration languageConfig = null;
    private static File languageConfigFile = null;

    public static void init(Main pl) {
        plugin = pl;
    }

    public static String getDefaultLanguageMessage(String message){
        if(ConfigurationManager.getConfig("language").isSet(message)) {
            return ConfigurationManager.getConfig("language").getString(message);
        }
        return null;
    }

    public static String getLanguageMessage(String message) {
        switch(plugin.getPluginLocale()) {
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


    //TODO remove meeee or edit to return locale based file
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
            BigTextUtils.errorOccured();
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
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save language.yml file!");
            Bukkit.getConsoleSender().sendMessage("Restart the server!");
        }
    }

}
