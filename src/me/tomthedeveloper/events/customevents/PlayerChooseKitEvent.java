package me.tomthedeveloper.events.customevents;

import me.tomthedeveloper.kitapi.basekits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 28/07/2014.
 */
public class PlayerChooseKitEvent extends Event {

    private Player player;
    private Kit kit;
    private boolean cancel = false;

    public PlayerChooseKitEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
