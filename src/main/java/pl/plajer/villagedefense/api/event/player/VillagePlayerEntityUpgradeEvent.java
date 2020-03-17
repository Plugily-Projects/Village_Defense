package pl.plajer.villagedefense.api.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.plajer.villagedefense.api.event.VillageEvent;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.handlers.upgrade.upgrades.Upgrade;

/**
 * @author Plajer
 * @since 1.0.0
 * <p>
 * Called when player upgrades an entity.
 */
public class VillagePlayerEntityUpgradeEvent extends VillageEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private Entity entity;
  private Player player;
  private Upgrade appliedUpgrade;
  private int tier;

  public VillagePlayerEntityUpgradeEvent(Arena eventArena, Entity entity, Player player, Upgrade appliedUpgrade, int tier) {
    super(eventArena);
    this.entity = entity;
    this.player = player;
    this.appliedUpgrade = appliedUpgrade;
    this.tier = tier;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Entity getEntity() {
    return entity;
  }

  /**
   * @return upgrade that was applied to entity
   */
  public Upgrade getAppliedUpgrade() {
    return appliedUpgrade;
  }

  /**
   * @return upgrade tier
   */
  public int getTier() {
    return tier;
  }

  public Player getPlayer() {
    return player;
  }

}
