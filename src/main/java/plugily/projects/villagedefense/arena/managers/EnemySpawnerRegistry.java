package plugily.projects.villagedefense.arena.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.BabyZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.FastZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.GolemBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.HalfInvisibleZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.HardZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.KnockbackResistantZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.PlayerBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.SoftHardZombieSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.VillagerBusterSpawner;
import plugily.projects.villagedefense.creatures.v1_8_R3.spawner.VillagerSlayerSpawner;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomCreature;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomCreatureEvents;
import plugily.projects.villagedefense.creatures.v1_9_UP.CustomRideableCreature;
import plugily.projects.villagedefense.creatures.v1_9_UP.Equipment;
import plugily.projects.villagedefense.creatures.v1_9_UP.Rate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * The registry for all {@link EnemySpawner}
 */
public class EnemySpawnerRegistry {
  private final Set<EnemySpawner> enemySpawnerSet = new TreeSet<>(Collections.reverseOrder());
  private final Set<CustomRideableCreature> rideableCreatures = new HashSet<>();
  private final Main plugin;

  public EnemySpawnerRegistry(Main plugin) {
    this.plugin = plugin;
    registerCreatures();
    registerRideableCreatures();
  }

  public void registerRideableCreatures() {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "creatures");
    ConfigurationSection village = config.getConfigurationSection("Creatures.Village");
    if(village == null) {
      plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Village");
      return;
    }
    for(String rideable : village.getKeys(false)) {
      CustomRideableCreature.RideableType rideableType = CustomRideableCreature.RideableType.valueOf(rideable.toUpperCase().replace("RIDEABLE_", ""));
      boolean holidayEffects = village.getBoolean(rideable + ".holiday_effects", false);
      Map<Attribute, Double> attributes = new HashMap<>();
      ConfigurationSection attributeSection = village.getConfigurationSection(rideable + ".attributes");
      if(attributeSection == null) {
        plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Village." + rideable + ".attributes");
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
        dropItem = XMaterial.matchXMaterial(item).get().parseItem();
      }
      plugin.getDebugger().debug("Registered CustomRideableCreature of type {0}", rideableType);
      rideableCreatures.add(new CustomRideableCreature(rideableType, holidayEffects, attributes, dropItem));
    }
  }

  public void registerCreatures() {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      enemySpawnerSet.add(new BabyZombieSpawner());
      enemySpawnerSet.add(new FastZombieSpawner());
      enemySpawnerSet.add(new GolemBusterSpawner());
      enemySpawnerSet.add(new HalfInvisibleZombieSpawner());
      enemySpawnerSet.add(new HardZombieSpawner());
      enemySpawnerSet.add(new KnockbackResistantZombieSpawner());
      enemySpawnerSet.add(new PlayerBusterSpawner());
      enemySpawnerSet.add(new SoftHardZombieSpawner());
      enemySpawnerSet.add(new VillagerBusterSpawner());
      enemySpawnerSet.add(new VillagerSlayerSpawner());
      return;
    }
    new CustomCreatureEvents(plugin);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "creatures");

    ConfigurationSection content = config.getConfigurationSection("Creatures.Content");
    if(content == null) {
      plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Content");
      return;
    }
    for(String creature : content.getKeys(false)) {
      boolean enabled = content.getBoolean(creature + ".enabled", false);
      if(!enabled) {
        continue;
      }

      int waveMin = content.getInt(creature + ".wave.min", 0);
      int waveMax = content.getInt(creature + ".wave.max", 0);

      CustomCreature.PriorityTarget priorityTarget = CustomCreature.PriorityTarget.valueOf(content.getString(".priority_type", "ANY"));

      boolean explodeTarget = content.getBoolean(".explosive_hit", false);

      String key = creature.toUpperCase();

      EntityType entityType = EntityType.valueOf(content.getString(creature + ".entity_type", "ZOMBIE").toUpperCase());

      boolean baby = content.getBoolean(creature + ".baby", false);
      boolean breed = content.getBoolean(creature + ".breed", false);
      int age = content.getInt(creature + ".age", 0);
      boolean ageLook = content.getBoolean(creature + ".age_lock", false);
      boolean holidayEffects = content.getBoolean(creature + ".holiday_effects", true);

      List<Rate> rates = new ArrayList<>();
      ConfigurationSection rate = content.getConfigurationSection(creature + ".rates");
      if(rate == null) {
        plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Content." + creature + ".rates");
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

      Map<Attribute, Double> attributes = new HashMap<>();
      ConfigurationSection attributeSection = content.getConfigurationSection(creature + ".attributes");
      if(attributeSection == null) {
        plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Content." + creature + ".attributes");
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
        plugin.getDebugger().debug(Level.WARNING, "Creatures section {0} is missing! Was it manually removed?", "Creatures.Content." + creature + ".equipment");
        continue;
      }
      for(String equipmentType : equipmentSection.getKeys(false)) {
        String item = equipmentSection.getString(equipmentType + ".item", null);
        if(item == null) {
          continue;
        }
        ItemStack itemStack = XMaterial.matchXMaterial(item).get().parseItem();
        int dropChance = equipmentSection.getInt(equipmentType + ".drop_chance", 0);
        Equipment.EquipmentType equipmentTypeValue = Equipment.EquipmentType.valueOf(equipmentType.toUpperCase());
        equipments.add(new Equipment(itemStack, dropChance, equipmentTypeValue));
      }

      String item = content.getString(creature + ".drop_item", null);
      ItemStack dropItem = null;
      if(item != null) {
        dropItem = XMaterial.matchXMaterial(item).get().parseItem();
      }
      plugin.getDebugger().debug("Registered CustomCreature named {0}", key);
      enemySpawnerSet.add(new CustomCreature(plugin, waveMin, waveMax, priorityTarget, explodeTarget, key, entityType, baby, breed, age, ageLook, holidayEffects, rates, attributes, equipments, dropItem));
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
    int zombiesLimit = plugin.getConfig().getInt("Zombies-Limit", 75);
    if(zombiesLimit < spawn) {
      spawn = (int) Math.ceil(zombiesLimit / 2.0);
    }

    arena.changeArenaOptionBy("ZOMBIE_SPAWN_COUNTER", 1);
    if(arena.getArenaOption("ZOMBIE_SPAWN_COUNTER") == 20) {
      arena.setArenaOption("ZOMBIE_SPAWN_COUNTER", 0);
    }

    for(EnemySpawner enemySpawner : enemySpawnerSet) {
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
