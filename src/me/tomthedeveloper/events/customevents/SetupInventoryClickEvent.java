package me.tomthedeveloper.events.customevents;

import me.tomthedeveloper.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryClickEvent extends Event {

    private Player player;
    private GameInstance gameInstance;
    private ItemStack itemStack;
    private ClickType clickType;
    private boolean cancel = false;


    public SetupInventoryClickEvent(GameInstance gameInstance,ItemStack itemStack, Player player, ClickType clickType){
        this.player = player;
        this.gameInstance = gameInstance;
        this.itemStack = itemStack;
        this.clickType = clickType;
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
