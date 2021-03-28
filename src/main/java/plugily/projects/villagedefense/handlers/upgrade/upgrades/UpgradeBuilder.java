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

package plugily.projects.villagedefense.handlers.upgrade.upgrades;

import plugily.projects.villagedefense.Main;

import java.util.Arrays;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class UpgradeBuilder {

  private static Main plugin;
  private final Upgrade upgrade;

  public UpgradeBuilder(String id) {
    upgrade = new Upgrade(id);
    upgrade.setName(plugin.getChatManager().colorRawMessage(plugin.getLanguageConfig().getString("Upgrade-Menu.Upgrades." + id + ".Name")));
    upgrade.setDescription(Arrays.asList(plugin.getChatManager().colorRawMessage(plugin.getLanguageConfig().getString("Upgrade-Menu.Upgrades." + id + ".Description")).split(";")));
  }

  public static void init(Main plugin) {
    UpgradeBuilder.plugin = plugin;
  }

  //for other usages
  public UpgradeBuilder name(String name) {
    upgrade.setName(plugin.getChatManager().colorRawMessage(name));
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