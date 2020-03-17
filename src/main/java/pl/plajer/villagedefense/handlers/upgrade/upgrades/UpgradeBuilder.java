package pl.plajer.villagedefense.handlers.upgrade.upgrades;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import pl.plajer.villagedefense.Main;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class UpgradeBuilder {

  private static Main plugin;
  private final Upgrade upgrade;

  public UpgradeBuilder(String id) {
    this.upgrade = new Upgrade(id);
    upgrade.setName(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageConfig().getString("Upgrade-Menu.Upgrades." + id + ".Name")));
    upgrade.setDescription(Arrays.asList(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageConfig().getString("Upgrade-Menu.Upgrades." + id + ".Description")).split(";")));
  }

  public static void init(Main plugin) {
    UpgradeBuilder.plugin = plugin;
  }

  //for other usages
  public UpgradeBuilder name(String name) {
    upgrade.setName(ChatColor.translateAlternateColorCodes('&', name));
    return this;
  }

  //for other usages
  public UpgradeBuilder lore(List<String> lore) {
    upgrade.setDescription(lore);
    return this;
  }

  public UpgradeBuilder slot(int x, int y) {
    upgrade.setSlot(x, y);
    return this;
  }

  public UpgradeBuilder entity(Upgrade.EntityType type) {
    upgrade.setEntityType(type);
    return this;
  }

  public UpgradeBuilder maxTier(int maxTier) {
    upgrade.setMaxTier(maxTier);
    return this;
  }

  public UpgradeBuilder metadata(String metaAccessor) {
    upgrade.setMetadataAccessor(metaAccessor);
    return this;
  }

  public UpgradeBuilder tierValue(int tier, double val) {
    upgrade.setTierValue(tier, val);
    return this;
  }

  public Upgrade build() {
    return upgrade;
  }

}