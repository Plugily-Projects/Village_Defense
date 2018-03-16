package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author IvanTheBuilder
 */
public class ConfigurationManager {

    private static Main plugin;

    public ConfigurationManager(Main plugin){
        ConfigurationManager.plugin = plugin;
    }

    public static File getFile(String filename) {
        return new File(plugin.getDataFolder() + File.separator + filename + ".yml");
    }

    public static FileConfiguration getConfig(String filename) {
        File ConfigFile = new File(plugin.getDataFolder() + File.separator + filename + ".yml");
        if(!ConfigFile.exists()) {
            try {
                plugin.getLogger().info("Creating " + filename + ".yml because it does not exist!");
                ConfigFile.createNewFile();
            } catch(IOException ex) {
                ex.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
                Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
                Bukkit.getServer().shutdown();
            }
            ConfigFile = new File(plugin.getDataFolder(), filename + ".yml");
            YamlConfiguration config = new YamlConfiguration();

            try {
                config.load(ConfigFile);
                //YamlConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
            } catch(InvalidConfigurationException ex) {
                ex.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
                Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
                Bukkit.getServer().shutdown();

            } catch(FileNotFoundException e) {
                e.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
                Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                config.save(ConfigFile);

            } catch(IOException ex) {
                ex.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
                Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
                Bukkit.getServer().shutdown();
                ex.printStackTrace();
            }
        }
        ConfigFile = new File(plugin.getDataFolder(), filename + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(ConfigFile);
            //YamlConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
        } catch(InvalidConfigurationException ex) {
            ex.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
            Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
            Bukkit.shutdown();
            return null;

        } catch(FileNotFoundException e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
            Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
        } catch(IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static void saveConfig(FileConfiguration config, String name){
        try {
            config.save(new File(plugin.getDataFolder(), name + ".yml"));
        } catch(IOException e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save file " + name + ".yml!");
            Bukkit.getConsoleSender().sendMessage("Create blank file " + name  + ".yml or restart the server!");
        }
    }

}
