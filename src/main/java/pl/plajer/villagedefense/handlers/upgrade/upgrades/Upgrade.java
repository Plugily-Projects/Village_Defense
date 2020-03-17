package pl.plajer.villagedefense.handlers.upgrade.upgrades;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.Main;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;


/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class Upgrade {

  private static Main plugin;
  private String id;
  private int slotX;
  private int slotY;
  private EntityType entityType;
  private int maxTier;
  private String name;
  private List<String> description = new ArrayList<>();
  private String metadataAccessor;
  private String configAccessor;
  private Map<Integer, Double> tieredValues = new HashMap<>();

  public Upgrade(String id) {
    this.id = id;
    this.configAccessor = "Entity-Upgrades." + id + "-Tiers";
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
    return plugin.getEntityUpgradesConfig().getInt(configAccessor + "." + tier);
  }

  public ItemStack asItemStack(int currentTier) {
    double valCurrent = tieredValues.get(currentTier);
    double valNext = tieredValues.getOrDefault(currentTier + 1, tieredValues.get(currentTier));
    return new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
        .name(getName())
        .lore(getDescription().stream().map(lore -> lore = plugin.getChatManager().colorRawMessage(lore)
            .replace("%cost%", String.valueOf(getCost(currentTier + 1)))
            .replace("%tier%", String.valueOf(currentTier + 1))
            .replace("%from%", String.valueOf(valCurrent))
            .replace("%to%", String.valueOf(valNext))).collect(Collectors.toList())).build();
  }

  public enum EntityType {
    BOTH, IRON_GOLEM, WOLF
  }

}
