/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.handlers.upgrade.upgrades;

import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class Upgrade {

  private static Main plugin;
  private final String id;
  private int slotX;
  private int slotY;
  private EntityType entityType;
  private int maxTier;
  private String name;
  private List<String> description = new ArrayList<>();
  private String metadataAccessor;
  private final String configAccessor;
  private final Map<Integer, Double> tieredValues = new HashMap<>();

  public Upgrade(String id) {
    this.id = id;
    configAccessor = "Entity-Upgrades." + id + "-Tiers";
  }

  public static void init(Main plugin) {
    Upgrade.plugin = plugin;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public void setTierValue(int tier, double value) {
    tieredValues.put(tier, value);
  }

  public String getId() {
    return id;
  }

  public int getSlotX() {
    return slotX;
  }

  public int getSlotY() {
    return slotY;
  }

  public void setSlot(int x, int y) {
    slotX = x;
    slotY = y;
  }

  public EntityType getApplicableFor() {
    return entityType;
  }

  public int getMaxTier() {
    return maxTier;
  }

  public void setMaxTier(int maxTier) {
    this.maxTier = maxTier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDescription() {
    return description;
  }

  public void setDescription(List<String> description) {
    this.description = description;
  }

  public String getMetadataAccessor() {
    return metadataAccessor;
  }

  public void setMetadataAccessor(String metadataAccessor) {
    this.metadataAccessor = metadataAccessor;
  }

  public double getValueForTier(int tier) {
    return tieredValues.getOrDefault(tier, tieredValues.get(maxTier));
  }

  public int getCost(int tier) {
    return plugin.getEntityUpgradesConfig().getInt(configAccessor + "." + tier);
  }

  public ItemStack asItemStack(int currentTier) {
    double valCurrent = tieredValues.get(currentTier);
    int nextTier = currentTier + 1;
    double valNext = tieredValues.getOrDefault(nextTier, valCurrent);
    return new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
        .name(getName())
        .lore(getDescription().stream().map(lore -> lore = new MessageBuilder(lore).build()
            .replace("%cost%", Integer.toString(getCost(nextTier)))
            .replace("%tier%", Integer.toString(nextTier))
            .replace("%from%", Double.toString(valCurrent))
            .replace("%to%", Double.toString(valNext))).collect(Collectors.toList())).build();
  }

  public enum EntityType {
    BOTH, IRON_GOLEM, WOLF
  }

}
