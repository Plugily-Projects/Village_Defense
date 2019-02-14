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

package pl.plajer.villagedefense.creatures.upgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private int slotX;
  private int slotY;
  private EntityType entityType;
  private int maxTier;
  private String name;
  private List<String> description;
  private String metadataAccessor;
  private String configAccessor;
  private Map<Integer, Double> tieredValues = new HashMap<>();

  public Upgrade(String id) {
    this.id = id;
    this.configAccessor = "Entity-Upgrades." + id + "-Tiers";
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
    this.slotX = x;
    this.slotY = y;
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
    return plugin.getConfig().getInt(configAccessor + "." + tier);
  }

  public ItemStack asItemStack(int currentTier) {
    String name = this.name;
    List<String> description = new ArrayList<>(this.description);

    double valCurrent = tieredValues.get(currentTier);
    double valNext = tieredValues.getOrDefault(currentTier + 1, tieredValues.get(currentTier));

    description = description.stream().map((lore) -> lore = plugin.getChatManager().colorRawMessage(lore)
        .replace("%cost%", String.valueOf(getCost(currentTier + 1)))
        .replace("%tier%", String.valueOf(currentTier + 1))
        .replace("%from%", String.valueOf(valCurrent))
        .replace("%to%", String.valueOf(valNext))).collect(Collectors.toList());
    return new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
        .name(name)
        .lore(description).build();
  }

  public enum EntityType {
    BOTH, IRON_GOLEM, WOLF
  }

}
