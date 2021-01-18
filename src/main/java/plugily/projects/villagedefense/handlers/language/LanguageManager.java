/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.handlers.language;

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.constants.Constants;
import plugily.projects.villagedefense.utils.services.ServiceRegistry;
import plugily.projects.villagedefense.utils.services.locale.Locale;
import plugily.projects.villagedefense.utils.services.locale.LocaleRegistry;
import plugily.projects.villagedefense.utils.services.locale.LocaleService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LanguageManager {

  private static Main plugin;
  private static Locale pluginLocale;
  private static final Properties PROPERTIES = new Properties();
  private static FileConfiguration languageConfig;
  private static boolean messagesIntegrityPassed = true;
  private static FileConfiguration defaultLanguageConfig;

  private LanguageManager() {
  }

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
    //auto update
    plugin.saveResource("locales/language_default.yml", true);

    new LanguageMigrator(plugin);
    languageConfig = ConfigUtils.getConfig(plugin, Constants.Files.LANGUAGE.getName());
    defaultLanguageConfig = ConfigUtils.getConfig(plugin, "locales/language_default");
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
      Debugger.sendConsoleMsg("&c[Village Defense] Language file integrity check failed! Message "
          + message.getAccessor() + " not found! It will be set to default value of ERR_MSG_" + message.name() + "_NOT_FOUND");
      languageConfig.set(message.getAccessor(), "ERR_MSG_" + message.name() + "_NOT_FOUND");
      messagesIntegrityPassed = false;
    }
    if (!messagesIntegrityPassed) {
      ConfigUtils.saveConfig(plugin, languageConfig, Constants.Files.LANGUAGE.getName());
    }
  }

  private static void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
    .forEach(LocaleRegistry::registerLocale);
  }

  private static void loadProperties() {
    if (isDefaultLanguageUsed()) {
      return;
    }
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service == null) {
      Debugger.sendConsoleMsg("&c[Village Defense] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Debugger.sendConsoleMsg("&c[Village Defense] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Debugger.sendConsoleMsg("&a[Village Defense] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Debugger.sendConsoleMsg("&a[Village Defense] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      Debugger.sendConsoleMsg("&c[Village Defense] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(plugin.getDataFolder() + "/locales/"
        + pluginLocale.getPrefix() + ".properties"), StandardCharsets.UTF_8)) {
      PROPERTIES.load(reader);
    } catch (IOException e) {
      plugin.getLogger().log(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getLogger().log(Level.WARNING, "Cause: " + e.getMessage());
      pluginLocale = LocaleRegistry.getByName("English");
    }
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for (Locale locale : LocaleRegistry.getRegisteredLocales()) {
      if (locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Debugger.sendConsoleMsg("&c[Village Defense] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    /* is beta release */
    if ((plugin.getDescription().getVersion().contains("locales") || plugin.getDescription().getVersion().contains("pre")) && !plugin.getConfig().getBoolean("Developer-Mode", false)) {
      Debugger.sendConsoleMsg("&c[Village Defense] Locales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    Debugger.sendConsoleMsg("&a[Village Defense] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
            + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return "English".equals(pluginLocale.getName());
  }

  public static String getLanguageMessage(String path) {
    if (isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = PROPERTIES.getProperty(path);
    if (prop == null) {
      return getString(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return prop;
    }
    return getString(path);
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = PROPERTIES.getProperty(path);
    if (prop == null) {
      return getStrings(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return Arrays.asList(plugin.getChatManager().colorRawMessage(prop).split(";"));
    }
    return getStrings(path);
  }


  private static List<String> getStrings(String path) {
    if (!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[VillageDefense] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[VillageDefense] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[VillageDefense] Path: " + path);
      return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    }
    List<String> list = languageConfig.getStringList(path);
    list = list.stream().map(string -> plugin.getChatManager().colorRawMessage(string)).collect(Collectors.toList());
    return list;
  }


  private static String getString(String path) {
    if (!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[VillageDefense] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[VillageDefense] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[VillageDefense] Path: " + path);
      return "ERR_MESSAGE_" + path + "_NOT_FOUND";
    }
    return languageConfig.getString(path, "not found");
  }

  public static void reloadConfig() {
    languageConfig = ConfigUtils.getConfig(plugin, Constants.Files.LANGUAGE.getName());
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }
}
