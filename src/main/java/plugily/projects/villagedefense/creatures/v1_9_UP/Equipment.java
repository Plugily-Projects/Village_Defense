package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.inventory.ItemStack;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.01.2022
 */
public class Equipment {

  private final ItemStack itemStack;
  private final int dropChance;
  private final EquipmentType equipmentType;

  public Equipment(ItemStack itemStack, int dropChance, EquipmentType equipmentType) {
    this.itemStack = itemStack;
    this.dropChance = dropChance;
    this.equipmentType = equipmentType;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getDropChance() {
    return dropChance;
  }

  public EquipmentType getEquipmentType() {
    return equipmentType;
  }

  public enum EquipmentType {
    HELMET, CHESTPLATE, LEGGINGS, BOOTS, HAND
  }

}
