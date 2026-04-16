/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package plugily.projects.villagedefense.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.migrator.MigratorUtils;
import plugily.projects.villagedefense.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't know how it works! You can break migrator modifying these values!
 */

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.2026
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public enum PluginFileVersion {
    /*ARENA_SELECTOR(0),*/ BUNGEE(1), CONFIG(1), LANGUAGE(2),
    /*LEADERBOARDS(0),*/ MYSQL(1), PERMISSIONS(1), POWERUPS(1),
    /*SIGNS(0),*/ SPECIAL_ITEMS(1), SPECTATOR(1)/*, STATS(0)*/;

    private final int version;

    PluginFileVersion(int version) {
      this.version = version;
    }

    public int getVersion() {
      return version;
    }
  }

  private final Main plugin;

  public LanguageMigrator(Main plugin) {
    this.plugin = plugin;
    updatePluginFiles();
  }

  private void updatePluginFiles() {
    for(PluginFileVersion pluginFileVersion : PluginFileVersion.values()) {
      String fileName = pluginFileVersion.name().toLowerCase();
      int newVersion = pluginFileVersion.getVersion();
      File file = new File(plugin.getDataFolder() + "/" + fileName + ".yml");
      FileConfiguration configuration = ConfigUtils.getConfig(plugin, fileName, false);
      if(configuration == null) {
        continue;
      }
      int oldVersion = configuration.getInt("Do-Not-Edit.File-Version", 0);
      if(oldVersion == newVersion) {
        continue;
      }
      if(fileName.equalsIgnoreCase(PluginFileVersion.LANGUAGE.name()) && oldVersion == 1) {
        try {
          Files.createDirectory(Paths.get(plugin.getDataFolder() + "/" + "OLD_FILES_VD_4_7_1"));
        } catch(IOException e) {
          Bukkit.getLogger().info("[System notify] &cCouldn't create subfolder " + "OLD_FILES_VD_4_7_1" + ". Problems might occur!");
        }
        if(!file.exists()) {
          Bukkit.getLogger().info("[System notify] &cFile " + file + ".yml does not exits!");
          return;
        }
        try {
          Files.move(Paths.get(file.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/" + "OLD_FILES_VD_4_7_1" + "/" + file.getName()));
          File kitsFile = new File(plugin.getDataFolder() + "/" + "kits" + ".yml");
          Files.move(Paths.get(kitsFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/" + "OLD_FILES_VD_4_7_1" + "/" + kitsFile.getName()));
          Bukkit.getLogger().info("[System notify] &aRenamed file " + file + "");
        } catch(IOException e) {
          Bukkit.getLogger().info("[System notify] &cCouldn't rename file " + file + ". Problems might occur!");
        }
        break;
      }
      Bukkit.getLogger().info("[System notify] The " + fileName + "  file is outdated! Updating...");
      for(int i = oldVersion; i < newVersion; i++) {
        executeUpdate(file, pluginFileVersion, i);
      }

      updatePluginFileVersion(file, configuration, oldVersion, newVersion);
      Bukkit.getLogger().info("[System notify] " + fileName + " updated, no comments were removed :)");
      Bukkit.getLogger().info("[System notify] You're using latest " + fileName + " file now! Nice!");
    }
  }

  private void executeUpdate(File file, PluginFileVersion pluginFileVersion, int version) {
    switch(pluginFileVersion) {
      case LANGUAGE:
        switch(version) {
          case 1:
            //gets replaced
            break;
          default:
            break;
        }
      default:
        break;
    }
  }

  public void updatePluginFileVersion(File file, FileConfiguration fileConfiguration, int oldVersion, int newVersion) {
    int coreVersion = fileConfiguration.getInt("Do-Not-Edit.Core-Version", 0);
    updateFileVersion(file, coreVersion, coreVersion, newVersion, oldVersion);
  }

  private void updateFileVersion(File file, int coreVersion, int oldCoreVersion, int fileVersion, int oldFileVersion) {
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "# You edited it, huh? Next time hurt yourself!");
    MigratorUtils.removeLineFromFile(file, "Do-Not-Edit:");
    MigratorUtils.removeLineFromFile(file, "  File-Version: " + oldFileVersion + "");
    MigratorUtils.removeLineFromFile(file, "  Core-Version: " + oldCoreVersion + "");
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\r\n" +
        "# Really, don't edit ;p\r\n" +
        "# You edited it, huh? Next time hurt yourself!\r\n" +
        "Do-Not-Edit:\r\n" +
        "  File-Version: " + fileVersion + "\r\n" +
        "  Core-Version: " + coreVersion + "\r\n");
  }

}
