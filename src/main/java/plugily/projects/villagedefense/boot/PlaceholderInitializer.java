/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.boot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.PlaceholderManager;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class PlaceholderInitializer {

  private final Main plugin;

  public PlaceholderInitializer(Main plugin) {
    this.plugin = plugin;
    registerPlaceholders();
  }

  private void registerPlaceholders() {
    getPlaceholderManager().registerPlaceholder(new Placeholder("wave", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getWave());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getWave());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_player", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        int wave = pluginArena.getWave();
        String summaryEnding;
        if(pluginArena.getPlugin().getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= pluginArena.getPlugin().getConfig().getInt("Limit.Wave.Game-End", 25)) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
        } else {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE").asKey().arena(pluginArena).build();
        }
        return summaryEnding;
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        int wave = pluginArena.getWave();
        String summaryEnding;
        if(pluginArena.getPlugin().getConfigPreferences().getOption("LIMIT_WAVE_UNLIMITED") && wave >= pluginArena.getPlugin().getConfig().getInt("Limit.Wave.Game-End", 25)) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_SURVIVED").asKey().arena(pluginArena).build();
        } else if(!arena.getPlayersLeft().isEmpty()) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_VILLAGERS").asKey().arena(pluginArena).build();
        } else {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_DIED_PLAYERS").asKey().arena(pluginArena).build();
        }
        return summaryEnding;
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("villager_size", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getVillagers().size());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getVillagers().size());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("zombie_size_left", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getZombiesLeft());
      }

      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return Integer.toString(pluginArena.getZombiesLeft());
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("rotten_flesh_amount", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(arena.getArenaOption("ROTTEN_FLESH_AMOUNT"));
      }

      @Override
      public String getValue(PluginArena arena) {
        return Integer.toString(arena.getArenaOption("ROTTEN_FLESH_AMOUNT"));
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("orbs_amount", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        User user = plugin.getUserManager().getUser(player);
        if(user.getArena() == null) {
          return "0";
        }
        return Integer.toString(user.getStatistic("ORBS"));
      }

      @Override
      public String getValue(PluginArena arena) {
        return "undefined";
      }
    });
  }

  private PlaceholderManager getPlaceholderManager() {
    return plugin.getPlaceholderManager();
  }

  private ArenaRegistry getArenaRegistry() {
    return plugin.getArenaRegistry();
  }

}
