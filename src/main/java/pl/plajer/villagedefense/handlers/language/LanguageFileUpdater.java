package pl.plajer.villagedefense.handlers.language;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.handlers.hologram.messages.LanguageMessage;
import pl.plajer.villagedefense.handlers.upgrade.messages.LanguageValues;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class LanguageFileUpdater {

  public LanguageFileUpdater() {
  }

  public static void insertHologramMessages(Main plugin) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    for (LanguageMessage message : LanguageMessage.values()) {
      if (!config.isSet(message.getAccessor())) {
        config.set(message.getAccessor(), message.getDefaultMessage());
      }
    }
    ConfigUtils.saveConfig(plugin, config, "language");
  }

  public static void insertUpgradeMessages(Main plugin) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    for (LanguageValues message : LanguageValues.values()) {
      if (!config.isSet(message.getAccessor())) {
        config.set(message.getAccessor(), message.getDefaultMessage());
      }
    }
    ConfigUtils.saveConfig(plugin, config, "language");
  }

}
