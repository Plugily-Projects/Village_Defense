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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense.Main;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class Upgrade {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private String id;
  private int slot;
  private EntityType entityType;
  private int maxTier;
  private String name;
  private List<String> description;
  private String metadataAccessor;
  private String configAccessor;

  public Upgrade(String id, int slot, EntityType entityType, int maxTier, String name, List<String> description, String metadataAccessor, String configAccessor) {
    this.id = id;
    this.slot = slot;
    this.entityType = entityType;
    this.maxTier = maxTier;
    this.name = name;
    this.description = description;
    this.metadataAccessor = metadataAccessor;
    this.configAccessor = configAccessor;
  }

  public String getId() {
    return id;
  }

  public int getSlot() {
    return slot;
  }

  public EntityType getApplicableFor() {
    return entityType;
  }

  public int getMaxTier() {
    return maxTier;
  }

  public String getName() {
    return name;
  }

  public List<String> getDescription() {
    return description;
  }

  public String getMetadataAccessor() {
    return metadataAccessor;
  }

  public int getCost(int tier) {
    return plugin.getConfig().getInt(configAccessor + "." + tier);
  }

  //todo bit weird, maybe recode
  public ItemStack asItemStack(Entity en, int currentTier, Upgrade upgrade) {
    String name = this.name;
    List<String> description = new ArrayList<>(this.description);

    String from;
    String to;
    int tier = plugin.getEntityUpgradeMenu().getTier(en, upgrade);
    switch (upgrade.getId()) {
      case "DAMAGE":
        from = String.valueOf(2.0 + (tier * 3));
        to = String.valueOf(2.0 + ((tier + 1) * 3));
        break;
      case "HEALTH":
        from = String.valueOf(100.0 + (100.0 * ((double) tier / 2.0)));
        to = String.valueOf(100.0 + (100.0 * ((double) (tier + 1) / 2.0)));
        break;
      case "SPEED":
        from = String.valueOf(0.25 + (0.25 * ((double) tier / 5.0)));
        to = String.valueOf(0.25 + (0.25 * ((double) (tier + 1) / 5.0)));
        break;
      case "SWARM_AWARENESS":
        from = String.valueOf(tier * 0.2);
        to = String.valueOf((tier + 1) * 0.2);
        break;
      case "FINAL_DEFENSE":
        from = String.valueOf(tier * 5);
        to = String.valueOf((tier + 1) * 5);
        break;
      default:
        from = "";
        to = "";
        break;
    }
    if (tier == upgrade.getMaxTier()) {
      to = from;
    }

    final String finalTo = to;

    description = description.stream().map((lore) -> lore = plugin.getChatManager().colorRawMessage(lore)
        .replace("%cost%", String.valueOf(getCost(currentTier + 1)))
        .replace("%tier%", String.valueOf(currentTier + 1))
        .replace("%from%", from)
        .replace("%to%", finalTo)).collect(Collectors.toList());
    return new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
        .name(plugin.getChatManager().colorRawMessage(name))
        .lore(description).build();
  }

  public enum EntityType {
    BOTH, IRON_GOLEM, WOLF
  }

}
