package me.tomthedeveloper.events;

import me.tomthedeveloper.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Tom on 11/08/2014.
 */
public class QuitEvent implements Listener {


    public GameAPI plugin;

    public QuitEvent(GameAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if(!plugin.isBungeeActivated())
         plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
    }
}
