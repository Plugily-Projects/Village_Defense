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
package plugily.projects.villagedefense.arena.managers.enemy.spawner;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomCreature;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomCreatureEvents;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomRideableCreature;
import plugily.projects.villagedefense.creatures.v1_9_UP.Equipment;
import plugily.projects.villagedefense.creatures.v1_9_UP.Rate;
import plugily.projects.villagedefense.creatures.v1_9_UP.RideableCreatureEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.05.2022
 */
public class EnemySpawnerRegistry {

  private static final String CREATURES_MISSING_SECTION = "Creatures section {0} is missing! Was it manually removed?";
  private final Set<EnemySpawner> enemySpawnerSet = new TreeSet<>(Collections.reverseOrder());
  private final Set<CustomRideableCreature> rideableCreatures = new HashSet<>();
  private final Main plugin;

  public EnemySpawnerRegistry(Main plugin) {
    this.plugin = plugin;
    registerCreatures();
    registerRideableCreatures();
  }

  public void registerRideableCreatures() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "creatures");
    ConfigurationSection village = config.getConfigurationSection("Creatures.Village");
    if(village == null) {
      plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Village");
      return;
    }
    for(String rideable : village.getKeys(false)) {
      CustomRideableCreature.RideableType rideableType = CustomRideableCreature.RideableType.valueOf(rideable.toUpperCase().replace("RIDEABLE_", ""));
      boolean holidayEffects = village.getBoolean(rideable + ".holiday_effects", false);
      EnumMap<Attribute, Double> attributes = new EnumMap<>(Attribute.class);
      ConfigurationSection attributeSection = village.getConfigurationSection(rideable + ".attributes");
      if(attributeSection == null) {
        plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Village." + rideable + ".attributes");
        continue;
      }
      for(String attribute : attributeSection.getKeys(false)) {
        try {
          attributes.put(Attribute.valueOf(attribute.toUpperCase()), attributeSection.getDouble(attribute));
        } catch(IllegalArgumentException exception) {
          plugin.getDebugger().debug(Level.WARNING, "Creatures attribute {0} not found! Check JavaDocs?", "Creatures.Village." + rideable + ".attributes." + attribute);
        }
      }

      String item = village.getString(rideable + ".drop_item", null);
      ItemStack dropItem = null;
      if(item != null) {
        dropItem = XMaterial.matchXMaterial(item).orElse(XMaterial.BEDROCK).parseItem();
      }
      plugin.getDebugger().debug("Registered CustomRideableCreature of type {0}", rideableType);
      rideableCreatures.add(new CustomRideableCreature(rideableType, holidayEffects, attributes, dropItem));
    }
  }

  public void registerCreatures() {
    new CustomCreatureEvents(plugin);
    new RideableCreatureEvents(plugin);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "creatures");

    ConfigurationSection content = config.getConfigurationSection("Creatures.Content");
    if(content == null) {
      plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Content");
      return;
    }
    for(String creature : content.getKeys(false)) {
      boolean enabled = content.getBoolean(creature + ".enabled", false);
      if(!enabled) {
        continue;
      }

      int waveMin = content.getInt(creature + ".wave.min", 0);
      int waveMax = content.getInt(creature + ".wave.max", 0);

      CustomCreature.PriorityTarget priorityTarget = CustomCreature.PriorityTarget.valueOf(content.getString(creature + ".priority_type", "ANY"));

      boolean explodeTarget = content.getBoolean(creature + ".explosive_hit", false);

      String name = content.getString(creature + ".name", "Zombie");
      String key = creature.toUpperCase();

      EntityType entityType = EntityType.valueOf(content.getString(creature + ".entity_type", "ZOMBIE").toUpperCase());

      boolean doorBulldozing = content.getBoolean(creature + ".door_bulldozing", false);
      boolean baby = content.getBoolean(creature + ".baby", false);
      boolean breed = content.getBoolean(creature + ".breed", false);
      int age = content.getInt(creature + ".age", 0);
      boolean ageLook = content.getBoolean(creature + ".age_lock", false);
      boolean holidayEffects = content.getBoolean(creature + ".holiday_effects", true);
      int expDrop = content.getInt(creature + ".exp", 0);

      List<Rate> rates = new ArrayList<>();
      ConfigurationSection rate = content.getConfigurationSection(creature + ".rates");
      if(rate == null) {
        plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Content." + creature + ".rates");
        continue;
      }
      for(String rateType : rate.getKeys(false)) {
        int phase = rate.getInt(rateType + ".phase", 0);
        int waveHigher = rate.getInt(rateType + ".wave_higher", 0);
        int waveLower = rate.getInt(rateType + ".wave_lower", 0);
        int spawnLower = rate.getInt(rateType + ".spawn_lower", 0);
        int rateInt = rate.getInt(rateType + ".rate", 0);
        int division = rate.getInt(rateType + ".division", 0);
        int reduce = rate.getInt(rateType + ".reduce", 0);
        Rate.RateType rateTypeValue = Rate.RateType.valueOf(rateType.toUpperCase());
        rates.add(new Rate(phase, waveHigher, waveLower, spawnLower, rateInt, division, reduce, rateTypeValue));
      }

      EnumMap<Attribute, Double> attributes = new EnumMap<>(Attribute.class);
      ConfigurationSection attributeSection = content.getConfigurationSection(creature + ".attributes");
      if(attributeSection == null) {
        plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Content." + creature + ".attributes");
        continue;
      }
      for(String attribute : attributeSection.getKeys(false)) {
        try {
          attributes.put(Attribute.valueOf(attribute.toUpperCase()), attributeSection.getDouble(attribute));
        } catch(IllegalArgumentException exception) {
          plugin.getDebugger().debug(Level.WARNING, "Creatures attribute {0} not found! Check JavaDocs?", "Creatures.Content." + creature + ".attributes" + attribute);
        }
      }

      List<Equipment> equipments = new ArrayList<>();
      ConfigurationSection equipmentSection = content.getConfigurationSection(creature + ".equipment");
      if(equipmentSection == null) {
        plugin.getDebugger().debug(Level.WARNING, CREATURES_MISSING_SECTION, "Creatures.Content." + creature + ".equipment");
        continue;
      }
      for(String equipmentType : equipmentSection.getKeys(false)) {
        String item = equipmentSection.getString(equipmentType + ".item", null);
        if(item == null) {
          continue;
        }
        ItemStack itemStack = XMaterial.matchXMaterial(item).orElse(XMaterial.BEDROCK).parseItem();
        int dropChance = equipmentSection.getInt(equipmentType + ".drop_chance", 0);
        Equipment.EquipmentType equipmentTypeValue = Equipment.EquipmentType.valueOf(equipmentType.toUpperCase());
        equipments.add(new Equipment(itemStack, dropChance, equipmentTypeValue));
      }

      String item = content.getString(creature + ".drop_item", null);
      ItemStack dropItem = null;
      if(item != null) {
        dropItem = XMaterial.matchXMaterial(item).orElse(XMaterial.BEDROCK).parseItem();
      }
      plugin.getDebugger().debug("Registered CustomCreature named {0}", key);
      enemySpawnerSet.add(new CustomCreature(plugin, name, waveMin, waveMax, priorityTarget, explodeTarget, key, entityType, doorBulldozing, baby, breed, age, ageLook, expDrop, holidayEffects, rates, attributes, equipments, dropItem));
    }
  }

  /**
   * Spawn the enemies at the arena
   *
   * @param random the random instance
   * @param arena  the arena
   */
  public void spawnEnemies(Random random, Arena arena) {
    int spawn = arena.getWave();
    int zombiesLimit = plugin.getConfig().getInt("Limit.Spawn.Creatures", 75);
    if(zombiesLimit < spawn) {
      spawn = (int) Math.ceil(zombiesLimit / 2.0);
    }
    String zombieSpawnCounterOption = "ZOMBIE_SPAWN_COUNTER";
    arena.changeArenaOptionBy(zombieSpawnCounterOption, 1);
    if(arena.getArenaOption(zombieSpawnCounterOption) == 20) {
      arena.setArenaOption(zombieSpawnCounterOption, 0);
    }

    List<EnemySpawner> enemySpawners = new ArrayList<>(enemySpawnerSet);
    Collections.shuffle(enemySpawners);
    for(EnemySpawner enemySpawner : enemySpawners) {
      plugin.getDebugger().debug("Trying enemy spawn for " + enemySpawner.getName());
      enemySpawner.spawn(random, arena, spawn);
    }
  }

  /**
   * Get the set of enemy spawners
   *
   * @return the set of enemy spawners
   */
  public Set<EnemySpawner> getEnemySpawnerSet() {
    return enemySpawnerSet;
  }

  public Set<CustomRideableCreature> getRideableCreatures() {
    return rideableCreatures;
  }

  /**
   * Get the rideable creature by its type
   *
   * @param type the tyoe
   * @return the rideable creature
   */
  public Optional<CustomRideableCreature> getRideableCreatureByName(CustomRideableCreature.RideableType type) {
    return rideableCreatures.stream()
      .filter(creature -> creature.getRideableType().equals(type))
      .findFirst();
  }

  /**
   * Get the enemy spawner by its name
   *
   * @param name the name
   * @return the enemy spawner
   */
  public Optional<EnemySpawner> getSpawnerByName(String name) {
    return enemySpawnerSet.stream()
      .filter(enemySpawner -> enemySpawner.getName().equals(name))
      .findFirst();
  }


}
