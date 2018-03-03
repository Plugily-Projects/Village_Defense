package pl.plajer.villagedefense3.events.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.game.GameInstance;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private GameInstance gameInstance;
    private ItemStack itemStack;
    private ClickType clickType;
    private boolean cancel = false;


    public SetupInventoryClickEvent(GameInstance gameInstance, ItemStack itemStack, Player player, ClickType clickType) {
        this.player = player;
        this.gameInstance = gameInstance;
        this.itemStack = itemStack;
        this.clickType = clickType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
