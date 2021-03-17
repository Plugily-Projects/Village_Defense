/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.handlers.sign;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaManager;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.constants.Constants;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SignManager implements Listener {

  private final Main plugin;
  private final List<ArenaSign> arenaSigns = new ArrayList<>();
  private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private final List<String> signLines;

  public SignManager(Main plugin) {
    this.plugin = plugin;
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_INACTIVE));
    gameStateToString.put(ArenaState.STARTING, plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_STARTING));
    gameStateToString.put(ArenaState.IN_GAME, plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_IN_GAME));
    gameStateToString.put(ArenaState.ENDING, plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_ENDING));
    gameStateToString.put(ArenaState.RESTARTING, plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_RESTARTING));
    signLines = LanguageManager.getLanguageList("Signs.Lines");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    if(!e.getPlayer().hasPermission("villagedefense.admin.sign.create")
        || !ComplementAccessor.getComplement().getLine(e, 0).equalsIgnoreCase("[villagedefense]")) {
      return;
    }
    if(ComplementAccessor.getComplement().getLine(e, 1).isEmpty()) {
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.COMMANDS_TYPE_ARENA_NAME));
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!arena.getId().equalsIgnoreCase(ComplementAccessor.getComplement().getLine(e, 1))) {
        continue;
      }
      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(e, i, formatSign(signLines.get(i), arena));
      }
      arenaSigns.add(new ArenaSign((Sign) e.getBlock().getState(), arena));
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SIGNS_SIGN_CREATED));
      String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
      FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.ARENAS.getName());
      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(location);
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
      return;
    }
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SIGNS_ARENA_DOESNT_EXISTS));
  }

  private String formatSign(String msg, Arena a) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%mapname%", a.getMapName());
    if(a.getPlayers().size() >= a.getMaximumPlayers()) {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getChatManager().colorMessage(Messages.SIGNS_GAME_STATES_FULL_GAME));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", gameStateToString.get(a.getArenaState()));
    }
    formatted = StringUtils.replace(formatted, "%playersize%", String.valueOf(a.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%maxplayers%", String.valueOf(a.getMaximumPlayers()));
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    return formatted;
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent e) {
    ArenaSign arenaSign = getArenaSignByBlock(e.getBlock());
    if(!e.getPlayer().hasPermission("villagedefense.admin.sign.break") || arenaSign == null) {
      return;
    }
    arenaSigns.remove(arenaSign);
    FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.ARENAS.getName());
    if(!config.isConfigurationSection("instances")) {
      return;
    }

    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + "," + "0.0,0.0";
    for(String arena : config.getConfigurationSection("instances").getKeys(false)) {
      for(String sign : config.getStringList("instances." + arena + ".signs")) {
        if(!sign.equals(location)) {
          continue;
        }
        List<String> signs = config.getStringList("instances." + arena + ".signs");
        signs.remove(location);
        config.set("instances." + arena + ".signs", signs);
        ConfigUtils.saveConfig(plugin, config, Constants.Files.ARENAS.getName());
        e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SIGNS_SIGN_REMOVED));
        return;
      }
    }
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoinAttempt(CBPlayerInteractEvent e) {
    ArenaSign arenaSign = getArenaSignByBlock(e.getClickedBlock());
    if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign && arenaSign != null) {
      Arena arena = arenaSign.getArena();
      if(arena == null) {
        return;
      }
      if(ArenaRegistry.isInArena(e.getPlayer())) {
        e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.ALREADY_PLAYING));
        return;
      }
      ArenaManager.joinAttempt(e.getPlayer(), arena);
    }
  }

  @Nullable
  private ArenaSign getArenaSignByBlock(Block block) {
    if(block == null) {
      return null;
    }

    for(ArenaSign sign : arenaSigns) {
      if(sign.getSign().getLocation().equals(block.getLocation())) {
        return sign;
      }
    }

    return null;
  }

  public void loadSigns() {
    Debugger.debug("Signs load event started");
    long start = System.currentTimeMillis();

    arenaSigns.clear();
    FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.ARENAS.getName());
    if(!config.isConfigurationSection("instances")) {
      Debugger.debug(Level.WARNING, "No arena instances found. Signs won't be loaded");
      return;
    }

    for(String path : config.getConfigurationSection("instances").getKeys(false)) {
      for(String sign : config.getStringList("instances." + path + ".signs")) {
        Location loc = LocationSerializer.getLocation(sign);
        if(loc.getBlock().getState() instanceof Sign) {
          arenaSigns.add(new ArenaSign((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path)));
          continue;
        }
        Debugger.debug(Level.WARNING, "Block at location {0} for arena {1} is not a sign!", LocationSerializer.locationToString(loc), path);
      }
    }
    Debugger.debug("Sign load event finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void updateSigns() {
    Debugger.performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updating signs");
    long start = System.currentTimeMillis();

    for(ArenaSign arenaSign : arenaSigns) {
      Sign sign = arenaSign.getSign();
      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(sign, i, formatSign(signLines.get(i), arenaSign.getArena()));
      }
      if(plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true) && arenaSign.getBehind() != null) {
        Block behind = arenaSign.getBehind();
        try {
          switch(arenaSign.getArena().getArenaState()) {
            case WAITING_FOR_PLAYERS:
              behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 0);
              }
              break;
            case STARTING:
              behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 4);
              }
              break;
            case IN_GAME:
              behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 1);
              }
              break;
            case ENDING:
              behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 7);
              }
              break;
            case RESTARTING:
              behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 15);
              }
              break;
            default:
              break;
          }
        } catch(Exception ignored) {
        }
      }
      sign.update();
    }
    Debugger.performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updated signs took {0}ms", System.currentTimeMillis() - start);
  }

  public List<ArenaSign> getArenaSigns() {
    return arenaSigns;
  }

  public Map<ArenaState, String> getGameStateToString() {
    return gameStateToString;
  }
}
