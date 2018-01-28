package me.tomthedeveloper.events.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 15/08/2014.
 */
public class PlayerAddCommandEvent extends Event {

    private Player player;
    private boolean cancel = false;
    private String[] argument;
    private String ID;

    public PlayerAddCommandEvent(Player player, String[] arguments, String ID) {
        this.player = player;
        this.argument= arguments;
        this.ID = ID;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String[] getArguments(){
        return argument;
    }

    public String getArenaID(){
        return ID;
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
