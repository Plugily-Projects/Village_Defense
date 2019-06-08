/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.handlers.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.utils.Constants;
import pl.plajer.villagedefense.utils.MessageUtils;
import pl.plajer.villagedefense.utils.services.ServiceRegistry;
import pl.plajer.villagedefense.utils.services.locale.Locale;
import pl.plajer.villagedefense.utils.services.locale.LocaleRegistry;
import pl.plajer.villagedefense.utils.services.locale.LocaleService;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

public class LanguageManager {

  private static Main plugin;
  private static Locale pluginLocale;
  private static Properties properties = new Properties();
  private static FileConfiguration languageConfig;
  private static boolean messagesIntegrityPassed = true;

  /**
   * Initializes language management system
   * Executes language migration if needed
   *
   * @param plugin plugin instance
   * @see LanguageMigrator
   */
  public static void init(Main plugin) {
    LanguageManager.plugin = plugin;
    if (!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    new LanguageMigrator(plugin);
    languageConfig = ConfigUtils.getConfig(plugin, Constants.Files.LANGUAGE.getName());
    registerLocales();
    setupLocale();
    if (isDefaultLanguageUsed()) {
      validateMessagesIntegrity();
    }
  }

  private static void validateMessagesIntegrity() {
    for (Messages message : Messages.values()) {
      if (languageConfig.isSet(message.getAccessor())) {
        continue;
      }
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Language file integrity check failed! Message "
          + message.getAccessor() + " not found! It will be set to default value of ERR_MSG_" + message.name() + "_NOT_FOUND");
      languageConfig.set(message.getAccessor(), "ERR_MSG_" + message.name() + "_NOT_FOUND");
      messagesIntegrityPassed = false;
    }
    if (!messagesIntegrityPassed) {
      ConfigUtils.saveConfig(plugin, languageConfig, Constants.Files.LANGUAGE.getName());
    }
  }

  private static void registerLocales() {
    LocaleRegistry.registerLocale(new Locale("Chinese (Simplified)", "简体中文", "zh_Hans", "POEditor contributors (Haoting)", Arrays.asList("简体中文", "中文", "chinese", "zh")));
    LocaleRegistry.registerLocale(new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")));
    LocaleRegistry.registerLocale(new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")));
    LocaleRegistry.registerLocale(new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")));
    LocaleRegistry.registerLocale(new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")));
    LocaleRegistry.registerLocale(new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors (montlikadani)", Arrays.asList("hungarian", "magyar", "hu")));
    LocaleRegistry.registerLocale(new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")));
    LocaleRegistry.registerLocale(new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")));
    LocaleRegistry.registerLocale(new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")));
    LocaleRegistry.registerLocale(new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")));
    LocaleRegistry.registerLocale(new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors (Davi)", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")));
    LocaleRegistry.registerLocale(new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors (Andrei)", Arrays.asList("romanian", "romanesc", "românesc", "ro")));
    LocaleRegistry.registerLocale(new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")));
    LocaleRegistry.registerLocale(new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")));
    LocaleRegistry.registerLocale(new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors (HStreamGamer)", Arrays.asList("vietnamese", "viet", "việt", "vn")));
  }

  private static void loadProperties() {
    if (isDefaultLanguageUsed()) {
      return;
    }
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(plugin.getDataFolder() + "/locales/"
        + pluginLocale.getPrefix() + ".properties"), StandardCharsets.UTF_8)) {
      properties.load(reader);
    } catch (IOException e) {
      plugin.getLogger().log(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getLogger().log(Level.WARNING, "Cause: " + e.getMessage());
      pluginLocale = LocaleRegistry.getByName("English");
    }
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for (Locale locale : LocaleRegistry.getRegisteredLocales()) {
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    /* is beta release */
    if ((plugin.getDescription().getVersion().contains("b") || plugin.getDescription().getVersion().contains("pre")) && !plugin.getConfig().getBoolean("Developer-Mode", false)) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Village Defense] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
        + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  /**
   * Low level language.yml file accessor.
   * For normal usages we suggest ChatManager and its methods.
   * <p>
   * <p>
   * Gets list of strings from language.yml flat file or locale if enabled
   *
   * @param path path to list
   * @return raw list of language strings
   * @see pl.plajer.villagedefense.handlers.ChatManager
   */
  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return languageConfig.getStringList(path);
    } else {
      return Arrays.asList(getLanguageMessage(path).split(";"));
    }
  }

  /**
   * Low level language.yml file accessor.
   * For normal usages we suggest ChatManager and its methods.
   * <p>
   * <p>
   * Gets message from language.yml flat file or locale if enabled
   *
   * @param path messages to get
   * @return raw language message
   * @see pl.plajer.villagedefense.handlers.ChatManager
   */
  public static String getLanguageMessage(String path) {
    if (isDefaultLanguageUsed()) {
      if (!languageConfig.isSet(path)) {
        Bukkit.getConsoleSender().sendMessage("Game message not found!");
        Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
        Bukkit.getConsoleSender().sendMessage("Access string: " + path);
        return "ERR_MESSAGE_NOT_FOUND";
      }
      return languageConfig.getString(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null) {
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Game message not found!");
      Bukkit.getConsoleSender().sendMessage("Please contact the developer!");
      Bukkit.getConsoleSender().sendMessage("Access string: " + path);
      return "ERR_MESSAGE_NOT_FOUND";
    }
    return prop;
  }

  public static void reloadConfig() {
    languageConfig = ConfigUtils.getConfig(plugin, Constants.Files.LANGUAGE.getName());
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }
}
