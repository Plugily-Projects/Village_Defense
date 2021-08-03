package plugily.projects.villagedefense.handlers.powerup;

import java.util.function.Consumer;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;

/**
 * The interface for power-ups
 */
public interface BasePowerup {
    /**
     * Get the id of the power-up
     *
     * @return the id
     */
    String getId();

    /**
     * Get the name of the power-up
     *
     * @return the name
     */
    String getName();

    /**
     * Get the description of the power-up
     *
     * @return the description
     */
    String getDescription();

    /**
     * Get the display material of the power-up
     *
     * @return the material
     */
    XMaterial getMaterial();

    /**
     * Get the pickup consumer for the power-up
     *
     * @return the pickup consumer
     */
    Consumer<PowerupPickupHandler> getOnPickup();
}
