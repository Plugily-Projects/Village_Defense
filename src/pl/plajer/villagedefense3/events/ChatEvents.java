/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense3.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.language.LanguageManager;
import pl.plajer.villagedefense3.user.UserManager;

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
    if (ArenaRegistry.getArena(event.getPlayer()) == null) {
      for (Player player : event.getRecipients()) {
        if (ArenaRegistry.getArena(event.getPlayer()) == null) {
          return;
        }
        event.getRecipients().remove(player);

      }
    }
    event.getRecipients().clear();
    event.getRecipients().addAll(ArenaRegistry.getArena(event.getPlayer()).getPlayers());
  }

  @EventHandler
  public void onChatIngame(AsyncPlayerChatEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if (arena == null) {
      for (Arena loopArena : ArenaRegistry.getArenas()) {
        for (Player player : loopArena.getPlayers()) {
          if (event.getRecipients().contains(player)) {
            if (!plugin.isSpyChatEnabled(player)) {
              event.getRecipients().remove(player);
            }
          }
        }
      }
      return;
    }
    if (plugin.isChatFormatEnabled()) {
      event.setCancelled(true);
      Iterator<Player> iterator = event.getRecipients().iterator();
      List<Player> remove = new ArrayList<>();
      while (iterator.hasNext()) {
        Player player = iterator.next();
        if (!plugin.isSpyChatEnabled(player)) {
          remove.add(player);
        }
      }
      for (Player player : remove) {
        event.getRecipients().remove(player);
      }
      remove.clear();
      String message;
      String eventMessage = event.getMessage();
      for (String regexChar : regexChars) {
        if (eventMessage.contains(regexChar)) {
          eventMessage = eventMessage.replaceAll(Pattern.quote(regexChar), "");
        }
      }
      if (!UserManager.getUser(event.getPlayer().getUniqueId()).isFakeDead()) {
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
                        .replaceAll("%kit%", ChatManager.colorMessage("In-Game.Dead-Tag-On-Death"))
                        .replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%message%", eventMessage));
      }
      for (Player player : arena.getPlayers()) {
        player.sendMessage(message);
      }
      Bukkit.getConsoleSender().sendMessage(message);
    } else {
      event.getRecipients().clear();
      event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));
      event.setMessage(event.getMessage().replaceAll("%kit%", UserManager.getUser(event.getPlayer().getUniqueId()).getKit().getName()));
    }
  }

}
