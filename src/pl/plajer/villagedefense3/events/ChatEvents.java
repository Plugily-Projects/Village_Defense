package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.LanguageManager;
import pl.plajer.villagedefense3.handlers.UserManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Tom on 13/08/2014.
 */
public class ChatEvents implements Listener {

    private Main plugin;
    private String[] regexChars = new String[]{"$", "\\"};

    public ChatEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) {
            for(Player player : event.getRecipients()) {
                if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
                    return;
                event.getRecipients().remove(player);

            }
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).getPlayers());
    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) {
            for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                for(Player player : gameInstance.getPlayers()) {
                    if(event.getRecipients().contains(player)) {
                        if(!plugin.isSpyChatEnabled(player))
                            event.getRecipients().remove(player);
                    }
                }
            }
            return;
        }
        if(plugin.isChatFormatEnabled()) {
            Iterator<Player> iterator = event.getRecipients().iterator();
            List<Player> remove = new ArrayList<>();
            while(iterator.hasNext()) {
                Player player = iterator.next();
                if(!plugin.isSpyChatEnabled(player))
                    remove.add(player);
            }
            for(Player player : remove) {
                event.getRecipients().remove(player);
            }
            remove.clear();
            GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
            String message;
            String eventMessage = event.getMessage();
            for(String regexChar : regexChars) {
                if(eventMessage.contains(regexChar)) {
                    eventMessage = eventMessage.replaceAll(Pattern.quote(regexChar), "");
                }
            }
            if(!UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead()) {
                message = ChatColor.translateAlternateColorCodes('&',
                        LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format")
                                .replaceAll("%level%", UserManager.getUser(event.getPlayer().getUniqueId()).getInt("level") + "")
                                .replaceAll("%kit%", UserManager.getUser(event.getPlayer().getUniqueId()).getKit().getName())
                                .replaceAll("%player%", event.getPlayer().getName())
                                .replaceAll("%message%", eventMessage));
            } else {
                message = ChatColor.translateAlternateColorCodes('&',
                        LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format")
                                .replaceAll("%level%", UserManager.getUser(event.getPlayer().getUniqueId()).getInt("level") + "")
                                .replaceAll("%kit%", ChatManager.formatMessage(LanguageManager.getLanguageMessage("In-Game.Dead-Tag-On-Death")))
                                .replaceAll("%player%", event.getPlayer().getName())
                                .replaceAll("%message%", eventMessage));
            }
            for(Player player : gameInstance.getPlayers()) {
                player.sendMessage(message);
            }
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
            event.getRecipients().clear();
            event.getRecipients().addAll(new ArrayList<>(gameInstance.getPlayers()));
            event.setMessage(event.getMessage().replaceAll("%kit%", UserManager.getUser(event.getPlayer().getUniqueId()).getKit().getName()));
        }
    }

}
