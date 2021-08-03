package plugily.projects.villagedefense.handlers.holiday;

import java.time.LocalDateTime;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import plugily.projects.villagedefense.Main;

/**
 * The interface for all holidays
 */
public interface Holiday {
  /**
   * Check if the date is the holiday
   *
   * @param dateTime the date
   * @return true if it is
   */
  boolean isHoliday(LocalDateTime dateTime);

  /**
   * Enable the holiday
   *
   * @param plugin the plugin
   */
  void enable(Main plugin);

  /**
   * Apply creature effects
   *
   * @param creature the creature
   */
  default void applyCreatureEffects(Creature creature) {
    // EMPTY
  }

  /**
   * Apply death effects for the entity
   *
   * @param entity the entity
   */
  default void applyDeathEffects(Entity entity) {
    // EMPTY
  }
}
