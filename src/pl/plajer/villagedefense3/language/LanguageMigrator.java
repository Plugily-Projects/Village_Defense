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

package pl.plajer.villagedefense3.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.utils.MessageUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LanguageMigrator {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private static List<String> migratable = Arrays.asList("bungee", "config", "kits", "language", "lobbyitems", "mysql");

    public static void configUpdate() {
        if(plugin.getConfig().getString("Version").equals("1")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] System notify >> Your config file is outdated! Updating...");
            plugin.getConfig().set("Powerups.Enabled", true);
            plugin.getConfig().set("Powerups.Drop-Chance", 1.0);
            plugin.getConfig().set("Powerups.List.Map-Clean", true);
            plugin.getConfig().set("Powerups.List.Double-Damage-For-Players.Enabled", true);
            plugin.getConfig().set("Powerups.List.Double-Damage-For-Players.Time", 15);
            plugin.getConfig().set("Powerups.List.Healing-For-Players.Enabled", true);
            plugin.getConfig().set("Powerups.List.Healing-For-Players.Amplifier", 1);
            plugin.getConfig().set("Powerups.List.Healing-For-Players.Time-Of-Healing", 15);
            plugin.getConfig().set("Powerups.List.Golem-Raid.Enabled", true);
            plugin.getConfig().set("Powerups.List.Golem-Raid.Golems-Amount", 3);
            plugin.getConfig().set("Powerups.List.One-Shot-One-Kill.Enabled", true);
            plugin.getConfig().set("Powerups.List.One-Shot-One-Kill.Time", 15);
            plugin.getConfig().set("Version", 2);
            plugin.saveConfig();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> Config updated, however comments of file are lost! Please reset file if you wish to get comments again.");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> You're using latest config file version! Nice!");
    }

    public static void languageFileUpdate() {
        if(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals("0")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] System notify >> Your language file is outdated! Updating...");
            LanguageManager.getLanguageFile().set("In-Game.Spectator.Target-Player-Health", "&cHealth: &7%health%");
            LanguageManager.getLanguageFile().set("Scoreboard.Footer", "&ewww.spigotmc.org");
            LanguageManager.getLanguageFile().set("File-Version-Do-Not-Edit", 1);
            LanguageManager.saveLanguageFile();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> Language file updated! Nice!");
        }
        if(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals("1")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] System notify >> Your language file is outdated! Updating...");
            LanguageManager.getLanguageFile().set("Powerups.Map-Clean-Powerup.Name", "&e&lMAP CLEANER");
            LanguageManager.getLanguageFile().set("Powerups.Map-Clean-Powerup.Description", "&7Map has been cleaned!");
            LanguageManager.getLanguageFile().set("Powerups.Double-Damage-Powerup.Name", "&c&lDOUBLE DAMAGE");
            LanguageManager.getLanguageFile().set("Powerups.Double-Damage-Powerup.Description", "&7Double damage for %time% seconds!");
            LanguageManager.getLanguageFile().set("Powerups.Healing-Powerup.Name", "&6&lREJUVENATION");
            LanguageManager.getLanguageFile().set("Powerups.Healing-Powerup.Description", "&7Healing for %time% seconds!");
            LanguageManager.getLanguageFile().set("Powerups.Golem-Raid-Powerup.Name", "&a&lIRONBOUND RAID");
            LanguageManager.getLanguageFile().set("Powerups.Golem-Raid-Powerup.Description", "&7Golems have invaded this village!");
            LanguageManager.getLanguageFile().set("Powerups.One-Shot-One-Kill-Powerup.Name", "&b&lFRENZY");
            LanguageManager.getLanguageFile().set("Powerups.One-Shot-One-Kill-Powerup.Description", "&7Every zombie for 1 hit!");
            LanguageManager.getLanguageFile().set("Powerups.Powerup-Ended-Title-Message", "&4Powerup %powerup% has ended!");
            LanguageManager.getLanguageFile().set("File-Version-Do-Not-Edit", 2);
            LanguageManager.saveLanguageFile();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> Language file updated! Nice!");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] System notify >> You're using latest language file version! Nice!");
    }

    public static void migrateToNewFormat() {
        MessageUtils.gonnaMigrate();
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
