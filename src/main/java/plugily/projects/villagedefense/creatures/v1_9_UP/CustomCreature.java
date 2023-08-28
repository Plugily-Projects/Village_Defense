
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_9_UP;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.DoorBreakListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.01.2022
 */
public class CustomCreature implements SimpleEnemySpawner {

  public static final String CREATURE_ID_METADATA = "VD_CREATURE_NAME";

  private final Main plugin;
  private final int waveMin;
  private final int waveMax;
  private final PriorityTarget priorityTarget;
  private final boolean explodeTarget;
  private final String key;
  private final EntityType entityType;
  private final boolean doorBulldozing;
  private final boolean baby;
  private final boolean breed;
  private final int age;
  private final boolean ageLook;
  private final int expDrop;
  private final boolean holidayEffects;
  private final List<Rate> rates;
  private final List<Rate> spawn = new ArrayList<>();
  private final List<Rate> amount = new ArrayList<>();
  private final List<Rate> check = new ArrayList<>();
  private final Map<Attribute, Double> attributes;
  private final List<Equipment> equipments;
  private final ItemStack dropItem;


  public CustomCreature(Main plugin, int waveMin, int waveMax, PriorityTarget priorityTarget, boolean explodeTarget, String key, EntityType entityType, boolean doorBulldozing,
                        boolean baby, boolean breed, int age, boolean ageLook, int expDrop, boolean holidayEffects, List<Rate> rates, Map<Attribute, Double> attributes, List<Equipment> equipments, ItemStack dropItem) {
    this.priorityTarget = priorityTarget;
    this.explodeTarget = explodeTarget;
    this.plugin = plugin;
    this.waveMin = waveMin;
    this.waveMax = waveMax;
    this.key = key;
    this.entityType = entityType;
    this.doorBulldozing = doorBulldozing;
    this.baby = baby;
    this.breed = breed;
    this.age = age;
    this.ageLook = ageLook;
    this.expDrop = expDrop;
    this.holidayEffects = holidayEffects;
    this.rates = rates;

    for(Rate rate : rates) {
      switch(rate.getRateType()) {
        case AMOUNT:
          amount.add(rate);
          break;
        case SPAWN:
          spawn.add(rate);
          break;
        case CHECK:
          check.add(rate);
          break;
      }
    }

    this.attributes = attributes;
    this.equipments = equipments;
    this.dropItem = dropItem;
  }

  @Override
  public int getMaxWave() {
    return getWaveMax();
  }

  @Override
  public int getMinWave() {
    return getWaveMin();
  }

  public int getWaveMax() {
    return waveMax;
  }

  public PriorityTarget getPriorityTarget() {
    return priorityTarget;
  }

  public boolean isExplodeTarget() {
    return explodeTarget;
  }

  public int getWaveMin() {
    return waveMin;
  }

  public String getKey() {
    return key;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public boolean isDoorBulldozing() {
    return doorBulldozing;
  }

  public boolean isBaby() {
    return baby;
  }

  public boolean isBreed() {
    return breed;
  }

  public int getAge() {
    return age;
  }

  public boolean isAgeLook() {
    return ageLook;
  }

  public int getExpDrop() {
    return expDrop;
  }

  public boolean isHolidayEffects() {
    return holidayEffects;
  }

  public List<Rate> getRates() {
    return rates;
  }

  public Map<Attribute, Double> getAttributes() {
    return attributes;
  }

  public List<Equipment> getEquipments() {
    return equipments;
  }

  public ItemStack getDropItem() {
    return dropItem;
  }

  @Override
  public boolean canApplyHolidayEffect() {
    return isHolidayEffects();
  }

  /**
   * How often the enemies will be spawned? Amount between 0.0 and 1.0
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return the spawn rate in double
   */
  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    for(Rate rate : spawn) {
      if(!rate.isPhase(phase)) {
        continue;
      }
      if(!rate.isSpawnLower(spawnAmount)) {
        continue;
      }
      if(rate.isWaveHigher(wave) && rate.isWaveLower(wave)) {
        return (rate.getRate() / rate.getDivision()) - rate.getReduce();
      }
    }
    return 0;
  }

  /**
   * Get the final amount of enemies to spawn, after some workaround
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return the final amount
   */
  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    for(Rate rate : amount) {
      if(!rate.isPhase(phase)) {
        continue;
      }
      if(!rate.isSpawnLower(spawnAmount)) {
        continue;
      }
      if(rate.isWaveHigher(wave) && rate.isWaveLower(wave)) {
        return (int) ((spawnAmount / (rate.getRate() / rate.getDivision())) - rate.getReduce());
      }
    }
    return 0;
  }

  /**
   * Check if the enemies can be spawned on this phase
   *
   * @param arena       the arena
   * @param wave        the current wave
   * @param phase       the current phase
   * @param spawnAmount the raw amount that the arena suggests
   * @return true if they can
   */
  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    for(Rate rate : check) {
      if(!rate.isPhase(phase)) {
        continue;
      }
      if(!rate.isSpawnLower(spawnAmount)) {
        continue;
      }
      if(rate.isWaveHigher(wave) && rate.isWaveLower(wave)) {
        return true;
      }
    }
    return false;
  }

  public Creature spawn(Location location) {
    Entity entity = VersionUtils.spawnEntity(location, entityType);
    if(entity instanceof Ageable) {
      Ageable ageable = (Ageable) entity;
      ageable.setBreed(isBreed());
      if(isBaby()) {
        ageable.setBaby();
      } else {
        ageable.setAdult();
      }
      if(getAge() > 0) ageable.setAge(getAge());
      ageable.setAgeLock(isAgeLook());
    }
    if(entity instanceof Creature) {
      Creature creature = (Creature) entity;
      for(Equipment equipment : equipments) {
        switch(equipment.getEquipmentType()) {
          case HELMET:
            creature.getEquipment().setHelmet(equipment.getItemStack());
            creature.getEquipment().setHelmetDropChance(equipment.getDropChance());
            break;
          case CHESTPLATE:
            creature.getEquipment().setChestplate(equipment.getItemStack());
            creature.getEquipment().setChestplateDropChance(equipment.getDropChance());
            break;
          case LEGGINGS:
            creature.getEquipment().setLeggings(equipment.getItemStack());
            creature.getEquipment().setLeggingsDropChance(equipment.getDropChance());
            break;
          case BOOTS:
            creature.getEquipment().setBoots(equipment.getItemStack());
            creature.getEquipment().setBootsDropChance(equipment.getDropChance());
            break;
          case HAND:
            VersionUtils.setItemInHand(creature, equipment.getItemStack());
            creature.getEquipment().setItemInMainHandDropChance(equipment.getDropChance());
            break;
        }
      }
      creature.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200D);
      for(Map.Entry<Attribute, Double> attribute : attributes.entrySet()) {
        creature.getAttribute(attribute.getKey()).setBaseValue(attribute.getValue());
        if(attribute.getKey() == Attribute.GENERIC_MAX_HEALTH) {
          VersionUtils.setMaxHealth(creature, attribute.getValue());
          creature.setHealth(attribute.getValue());
        }
      }
      creature.setRemoveWhenFarAway(false);
      creature.setMetadata(CustomCreature.CREATURE_ID_METADATA, new FixedMetadataValue(plugin, key));
      if(doorBulldozing) {
        creature.setMetadata(DoorBreakListener.CREATURE_DOOR_BULLDOZER_METADATA, new FixedMetadataValue(plugin, true));
      }
      return creature;
    } else {
      entity.remove();
      throw new IllegalStateException("Couldn't spawn Creature " + key + " as its not instance of creature");
    }
  }

  /**
   * Get the name of the spawner
   *
   * @return the name
   */
  @Override
  public String getName() {
    return key;
  }


  public enum PriorityTarget {
    ANY, PLAYER, VILLAGER, IRON_GOLEM, WOLF
  }
}
