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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.plajerlair.core.utils.ConfigUtils;
import pl.plajer.villagedefense3.plajerlair.core.utils.MigratorUtils;
import pl.plajer.villagedefense3.utils.MessageUtils;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't now how it works! You can break migrator modyfing these values!
 */
public class LanguageMigrator {

  public static final int LANGUAGE_FILE_VERSION = 8;
  public static final int CONFIG_FILE_VERSION = 5;
  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private static List<String> migratable = Arrays.asList("bungee", "config", "kits", "language", "lobbyitems", "mysql");

  public static void configUpdate() {
    if (plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] System notify >> Your config file is outdated! Updating...");
    File file = new File(plugin.getDataFolder() + "/config.yml");

    MigratorUtils.removeLineFromFile(file, "# Don't modify.");
    MigratorUtils.removeLineFromFile(file, "Version: " + plugin.getConfig().getInt("Version"));
    MigratorUtils.removeLineFromFile(file, "# No way! You've reached the end! But... where's the dragon!?");

    switch (plugin.getConfig().getInt("Version")) {
      case 1:
        MigratorUtils.addNewLines(file, "# Power ups section. If you want to have classic Village Defense game mode i recommend to disable this.\r\nPowerups:\r\n" +
                "  # Do you want to enable in-game power ups?\r\n  # This will make zombies to drop some power ups when they're killed\r\n" +
                "  # REQUIRES Holographic Displays otherwise it won't be enabled!\r\n  Enabled: true\r\n  # Modify powerup drop chance here\r\n" +
                "  Drop-Chance: 1.0 # 1% chance by default\r\n  # Enable or disable specific power ups here.\r\n  List:\r\n" +
                "    Map-Clean: true\r\n    Double-Damage-For-Players:\r\n      Enabled: true\r\n      Time: 15 # seconds\r\n" +
                "    Healing-For-Players:\r\n      Enabled: true\r\n      Amplifier: 1\r\n      Time-Of-Healing: 10 # seconds\r\n" +
                "    # Spawns X golems in village\r\n    # Owner of golems is person who picked up power up\r\n    Golem-Raid:\r\n" +
                "      Enabled: true\r\n      Golems-Amount: 3\r\n    # Every zombie can be killed for one hit\r\n    One-Shot-One-Kill:\r\n" +
                "      Enabled: true\r\n      Time: 15 # seconds\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Should blocks behind game signs change their color based on game state?\r\n# They will change color to:\r\n" +
                "# - white (waiting for players) stained glass\r\n# - yellow (starting) stained glass\r\n# - orange (in game) stained glass\r\n# - gray (ending) stained glass\r\n" +
                "# - black (restarting) stained glass\r\nSigns-Block-States-Enabled: true\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Commands which can be used in game, remove all of them to disable\r\nWhitelisted-Commands:\r\n- me\r\n- help\r\n");
        MigratorUtils.addNewLines(file, "# Limit of mobs can be spawned per player in-game\r\n# Will affect only buying them in in-game shop\r\n" +
                "Wolves-Spawn-Limit: 20\r\nGolems-Spawn-Limit: 15\r\n");
        MigratorUtils.addNewLines(file, "# Time before next wave starts\n" +
                "Cooldown-Before-Next-Wave: 25\r\n" +
                "# Don't modify\r\nVersion: 6\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 2:
        MigratorUtils.addNewLines(file, "# Should blocks behind game signs change their color based on game state?\r\n# They will change color to:\r\n" +
                "# - white (waiting for players) stained glass\r\n# - yellow (starting) stained glass\r\n# - orange (in game) stained glass\r\n# - gray (ending) stained glass\r\n" +
                "# - black (restarting) stained glass\r\nSigns-Block-States-Enabled: true\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Commands which can be used in game, remove all of them to disable\r\nWhitelisted-Commands:\r\n- me\r\n- help\r\n");
        MigratorUtils.addNewLines(file, "# Limit of mobs can be spawned per player in-game\r\n# Will affect only buying them in in-game shop\r\n" +
                "Wolves-Spawn-Limit: 20\r\nGolems-Spawn-Limit: 15\r\n");
        MigratorUtils.addNewLines(file, "# Time before next wave starts\n" +
                "Cooldown-Before-Next-Wave: 25\r\n" +
                "# Don't modify\r\nVersion: 6\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 3:
        MigratorUtils.addNewLines(file, "# Commands which can be used in game, remove all of them to disable\r\nWhitelisted-Commands:\r\n- me\r\n- help\r\n");
        MigratorUtils.addNewLines(file, "# Limit of mobs can be spawned per player in-game\r\n# Will affect only buying them in in-game shop\r\n" +
                "Wolves-Spawn-Limit: 20\r\nGolems-Spawn-Limit: 15\r\n");
        MigratorUtils.addNewLines(file, "# Time before next wave starts\n" +
                "Cooldown-Before-Next-Wave: 25\r\n" +
                "# Don't modify\r\nVersion: 6\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 4:
        MigratorUtils.addNewLines(file, "# Limit of mobs can be spawned per player in-game\r\n# Will affect only buying them in in-game shop\r\n" +
                "Wolves-Spawn-Limit: 20\r\nGolems-Spawn-Limit: 15\r\n");
        MigratorUtils.addNewLines(file, "# Time before next wave starts\n" +
                "Cooldown-Before-Next-Wave: 25\r\n" +
                "# Don't modify\r\nVersion: 6\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 5:
        MigratorUtils.addNewLines(file, "# Time before next wave starts\n" +
                "Cooldown-Before-Next-Wave: 25\r\n" +
                "# Don't modify\r\nVersion: 6\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] [System notify] Config updated, no comments were removed :)");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] [System notify] You're using latest config file version! Nice!");
  }

  public static void languageFileUpdate() {
    if (LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(String.valueOf(LANGUAGE_FILE_VERSION))) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Defense] [System notify] Your language file is outdated! Updating...");

    int version = 0;
    if (NumberUtils.isNumber(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit"))) {
      version = Integer.valueOf(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit"));
    }
    LanguageMigrator.updateLanguageVersionControl(version);

    File file = new File(plugin.getDataFolder() + "/language.yml");

    for (int i = version; i < LANGUAGE_FILE_VERSION; i++) {
      switch (version) {
        case 0:
          MigratorUtils.insertAfterLine(file, "Spectator-Menu-Name", "    Target-Player-Health: \"&cHealth: &7%health%\"");
          MigratorUtils.insertAfterLine(file, "Scoreboard:", "  Footer: \"&ewww.spigotmc.org\"");
          break;
        case 1:
          MigratorUtils.addNewLines(file, "\r\nPowerups:\r\n  Map-Clean-Powerup:\r\n    Name: \"&e&lMAP CLEANER\"\r\n    # Used as sub title description\r\n" +
                  "    Description: \"&7Map has been cleaned!\"\r\n  Double-Damage-Powerup:\r\n    Name: \"&c&lDOUBLE DAMAGE\"\r\n    Description: \"&7Double damage for %time% seconds!\"\r\n" +
                  "  Healing-Powerup:\r\n    Name: \"&6&lREJUVENATION\"\r\n    Description: \"&7Healing for %time% seconds!\"\r\n  Golem-Raid-Powerup:\r\n    Name: \"&a&lIRONBOUND RAID\"\r\n" +
                  "    Description: \"&7Golems have invaded this village!\"\r\n  One-Shot-One-Kill-Powerup:\r\n    Name: \"&b&lFRENZY\"\r\n    Description: \"&7Every zombie for 1 hit!\"\r\n" +
                  "  Powerup-Ended-Title-Message: \"&4Powerup %powerup% has ended!\"\r\n");
          break;
        case 2:
          MigratorUtils.insertAfterLine(file, "Commands:", "  Statistics:");
          MigratorUtils.insertAfterLine(file, "Statistics:", "    Format: \"&e#%position% %name% - %value% &7%statistic%\"");
          MigratorUtils.insertAfterLine(file, "Statistics:", "    Header: \"&8&m-------------------[&6 Top 10 &8&m]-------------------\"");
          MigratorUtils.insertAfterLine(file, "Statistics:", "    Invalid-Name: \"&cName of statistic is invalid! Type: kills, deaths, games_played, highest_wave, level\"");
          MigratorUtils.insertAfterLine(file, "Statistics:", "    Type-Name: \"&cPlease type statistic name to view!\"");
          break;
        case 3:
          MigratorUtils.insertAfterLine(file, "Commands:", "  Did-You-Mean: \"&6Did you mean &7/%command%&6?\"");
          break;
        case 4:
          MigratorUtils.insertAfterLine(file, "Scoreboard:", "  Content:\r\n    # Contents of scoreboard while wave is running\r\n    Playing:\r\n" +
                  "      - \"&fVillagers Left: &e%VILLAGERS%\"\r\n      - \"&fPlayers Left: &e%PLAYERS_LEFT%\"\r\n      - \"&fZombies Left: &e%ZOMBIES%\"\r\n" +
                  "      - \"\"\r\n      - \"&fRotten Flesh: &e%ROTTEN_FLESH%\"\r\n      - \"&fOrbs: &e%ORBS%\"\r\n      - \"\"\r\n      - \"&ewww.spigotmc.org\"\r\n" +
                  "    # Contents while players are waiting for the wave\r\n    Playing-Waiting:\r\n      - \"&fVillagers Left: &e%VILLAGERS%\"\r\n" +
                  "      - \"&fPlayers Left: &e%PLAYERS_LEFT%\"\r\n      - \"\"\r\n      - \"&fNext Wave In: &e%TIME%\"\r\n      - \"\"\r\n      - \"&fRotten Flesh: &e%ROTTEN_FLESH%\"\r\n" +
                  "      - \"&fOrbs: &e%ORBS%\"\r\n      - \"\"\r\n      - \"&ewww.spigotmc.org\"\r\n    Waiting:\r\n      - \"&fPlayers: &e%PLAYERS%\"\r\n      - \"\"\r\n" +
                  "      - \"&fMinimum Players: &e%MIN_PLAYERS%\"\r\n      - \"\"\r\n      - \"&ewww.spigotmc.org\"\r\n    Starting:\r\n      - \"&fStarting In: &e%TIME%\"\r\n" +
                  "      - \"\"\r\n      - \"&fPlayers: &e%PLAYERS%\"\r\n      - \"\"\r\n      - \"&fMinimum Players: &e%MIN_PLAYERS%\"\r\n      - \"&fMinimum Players: &e%MIN_PLAYERS%\"\r\n" +
                  "      - \"&ewww.spigotmc.org\"");
          MigratorUtils.insertAfterLine(file, "Scoreboard:", "  Title: \"&a&lVillage Defense\"");
          break;
        case 5:
          MigratorUtils.insertAfterLine(file, "Did-You-Mean", "  Command-Executed: \"&aCommand succesfully executed!\"\r\n" +
                  "  Command-Executed-Item-Updated: \"&aCommand succesfully executed, item updated!\"\r\n" +
                  "  Removed-Game-Instance: \"&cSuccessfully removed game instance!\"\r\n  Look-Sign: \"&cYou have to look at a sign to perform this command!\"\r\n" +
                  "  Hold-Any-Item: \"&cYou must hold any item!\"\r\n" +
                  "  Invalid-Number: \"&cWave needs to be number! Do %correct%\"\r\n  Target-Player-Not-Found: \"&cTarget player doesn't exist!\"\r\n" +
                  "  Location-Teleport-Invalid: \"&cLocation to teleport is invalid!\"\r\n  Wrong-Usage: \"&cWrong usage. Do %correct%\"");
          MigratorUtils.insertAfterLine(file, "Admin-Commands", "    Success-Reload: \"&aArenas reloaded!\"");
          break;
        case 6:
          MigratorUtils.insertAfterLine(file, "Shop-Messages", "      Mob-Limit-Reached: \"&cYou can't buy mobs! You've reached the limit of %amount% mobs!\"");
          break;
        case 7:
          MigratorUtils.insertAfterLine(file, "Commands:", "  No-Free-Arenas: \"&cThere are no free arenas!\"");
          MigratorUtils.insertAfterLine(file, "Game-End-Messages:", "      Summary-Message:\r\n" +
                  "        - \"&a&m--------------------------------------------------\"\r\n" +
                  "        - \"&f&lVillage Defense\"\r\n        - \"\"\r\n        - \"%summary%\"\r\n        - \"\"\r\n        - \"\"\r\n" +
                  "        - \"&e&lReached wave: &7%wave% &8(best %player_best_wave%)\"\r\n        - \"&6&lTotal zombies killed: &7%zombies%\"\r\n" +
                  "        - \"&c&lTotal coins spent: &7%orbs_spent%\"\r\n        - \"&a&m--------------------------------------------------\"\r\n" +
                  "      Summary-Players-Died: \"&7All players died!\"\r\n      Summary-Villagers-Died: \"&7All villagers died!\"");
          break;
      }
      version++;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] [System notify] Language file updated! Nice!");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] [System notify] You're using latest language file version! Nice!");
  }

  public static void migrateToNewFormat() {
    MessageUtils.gonnaMigrate();
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Village Defense 3 is migrating all files to the new file format...");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
    for (String file : migratable) {
      if (ConfigUtils.getFile(plugin, file).exists()) {
        ConfigUtils.getFile(plugin, file).renameTo(new File(plugin.getDataFolder(), "VD2_" + file + ".yml"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
      }
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling Village Defense 3...");
  }

  private static void updateLanguageVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/language.yml");
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "File-Version-Do-Not-Edit: " + oldVersion);
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\n# Really, don't edit ;p\nFile-Version-Do-Not-Edit: " + LANGUAGE_FILE_VERSION);
  }

}
