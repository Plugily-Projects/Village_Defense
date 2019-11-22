package pl.plajer.villagedefense.handlers.language;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.handlers.hologram.messages.LanguageMessage;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class LanguageFileUpdater {

  public LanguageFileUpdater(Main plugin) {
    insertModuleMessages(plugin);
  }

  private void insertModuleMessages(Main plugin) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    for(LanguageMessage message : LanguageMessage.values()) {
      if(!config.isSet(message.getAccessor())) {
        config.set(message.getAccessor(), message.getDefaultMessage());
      }
    }
    ConfigUtils.saveConfig(plugin, config, "language");
  }

}
