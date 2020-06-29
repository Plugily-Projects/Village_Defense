/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.user.User;

/**
 * Created by Tom on 13/08/2014.
 */
public class ChatEvents implements Listener {

  private Main plugin;
  private String[] regexChars = new String[] {"$", "\\"};

  public ChatEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChatIngame(AsyncPlayerChatEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.CHAT_FORMAT_ENABLED)) {
      event.setCancelled(true);
      Iterator<Player> iterator = event.getRecipients().iterator();
      List<Player> remove = new ArrayList<>();
      while (iterator.hasNext()) {
        Player player = iterator.next();
        if (!plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player)) {
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
      message = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format"), plugin.getUserManager().getUser(event.getPlayer()), eventMessage);
      for (Player player : arena.getPlayers()) {
        player.sendMessage(message);
      }
      Bukkit.getConsoleSender().sendMessage(message);
      return;
    }
    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_SEPARATE_CHAT)) {
      if (arena == null) {
        for (Arena loopArena : ArenaRegistry.getArenas()) {
          for (Player player : loopArena.getPlayers()) {
            if (event.getRecipients().contains(player) && !plugin.getArgumentsRegistry().getSpyChat().isSpyChatEnabled(player)) {
              event.getRecipients().remove(player);
            }
          }
        }
        return;
      }
      event.getRecipients().clear();
      event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));
    }
    String message = event.getMessage().replace("%kit%", plugin.getUserManager().getUser(event.getPlayer()).getKit().getName());
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && PlaceholderAPI.containsPlaceholders(message)) {
      message = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
    }
    event.setMessage(message);
  }

  private String formatChatPlaceholders(String message, User user, String saidMessage) {
    String formatted = message;
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    formatted = StringUtils.replace(formatted, "%level%", String.valueOf(user.getStat(StatsStorage.StatisticType.LEVEL)));
    if (user.isSpectator()) {
      formatted = StringUtils.replace(formatted, "%kit%", plugin.getChatManager().colorMessage(Messages.DEAD_TAG_ON_DEATH));
    } else {
      formatted = StringUtils.replace(formatted, "%kit%", user.getKit().getName());
    }
    formatted = StringUtils.replace(formatted, "%player%", user.getPlayer().getName());
    formatted = StringUtils.replace(formatted, "%message%", ChatColor.stripColor(saidMessage));
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && PlaceholderAPI.containsPlaceholders(formatted)) {
      formatted = PlaceholderAPI.setPlaceholders(user.getPlayer(), formatted);
    }
    return formatted;
  }

}
