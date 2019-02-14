/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.creatures.upgrades;

import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.Main;

/**
 * @author Plajer
 * <p>
 * Created at 13.01.2019
 */
public class UpgradeBuilder {

  private final Upgrade upgrade;
  private Main plugin = JavaPlugin.getPlugin(Main.class);

  public UpgradeBuilder(String id) {
    this.upgrade = new Upgrade(id);
    upgrade.setName(plugin.getChatManager().colorMessage("Upgrade-Menu.Upgrades." + id + ".Name"));
    upgrade.setDescription(Arrays.asList(plugin.getChatManager().colorMessage("Upgrade-Menu.Upgrades." + id + ".Description").split(";")));
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
