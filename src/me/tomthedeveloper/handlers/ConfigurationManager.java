package me.tomthedeveloper.handlers;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author IvanTheBuilder
 */
public class ConfigurationManager {


    public static JavaPlugin plugin;

    public static File getFile(String filename) {
        return new File(plugin.getDataFolder() + File.separator + filename + ".yml");
    }

    public static void Init(JavaPlugin pl) {
        plugin = pl;
    }

    public static FileConfiguration getConfig(String filename) {
        File ConfigFile = new File(plugin.getDataFolder() + File.separator + filename + ".yml");
        if(!ConfigFile.exists()) {
            boolean error = false;
            try {
                plugin.getLogger().info("Creating " + filename + ".yml because it does not exist!");
                ConfigFile.createNewFile();
            } catch(IOException ex) {
                ChatManager.sendErrorHeader(filename + ".yml file");
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- remove " + filename + ".yml to generate a new one");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- copy contents of " + filename + ".yml and check formatting at this website:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "http://yaml-online-parser.appspot.com/");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
                Bukkit.getServer().shutdown();
                ex.printStackTrace();

            }
            ConfigFile = new File(plugin.getDataFolder(), filename + ".yml");
            YamlConfiguration config = new YamlConfiguration();

            try {
                config.load(ConfigFile);
                //YamlConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
            } catch(InvalidConfigurationException ex) {
                ChatManager.sendErrorHeader(filename + ".yml file");
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- remove " + filename + ".yml to generate a new one");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- copy contents of " + filename + ".yml and check formatting at this website:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "http://yaml-online-parser.appspot.com/");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
                Bukkit.getServer().shutdown();

            } catch(FileNotFoundException e) {
                ChatManager.sendErrorHeader(filename + ".yml file");
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- try to restart the server if the file wasn't generated");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                config.save(ConfigFile);

            } catch(IOException ex) {
                ChatManager.sendErrorHeader(filename + ".yml file");
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- remove " + filename + ".yml to generate a new one");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- copy contents of " + filename + ".yml and check formatting at this website:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "http://yaml-online-parser.appspot.com/");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
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
            ChatManager.sendErrorHeader(filename + ".yml file");
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- remove " + filename + ".yml to generate a new one");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- copy contents of " + filename + ".yml and check formatting at this website:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "http://yaml-online-parser.appspot.com/");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
            Bukkit.shutdown();
            return null;

        } catch(FileNotFoundException e) {
            ChatManager.sendErrorHeader(filename + ".yml file");
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- try to restart the server if the file wasn't generated");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
        } catch(IOException e) {
            e.printStackTrace();
        }
        return config;
    }

}
