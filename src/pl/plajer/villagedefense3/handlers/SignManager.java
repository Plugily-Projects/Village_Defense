package pl.plajer.villagedefense3.handlers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.ArenaState;
import pl.plajer.villagedefense3.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager implements Listener {

    private Main plugin;
    @Getter
    private Map<Sign, Arena> loadedSigns = new HashMap<>();
    private Map<ArenaState, String> gameStateToString = new HashMap<>();

    public SignManager(Main plugin) {
        this.plugin = plugin;
        gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, ChatManager.colorMessage("Signs.Game-States.Inactive"));
        gameStateToString.put(ArenaState.STARTING, ChatManager.colorMessage("Signs.Game-States.Starting"));
        gameStateToString.put(ArenaState.IN_GAME, ChatManager.colorMessage("Signs.Game-States.In-Game"));
        gameStateToString.put(ArenaState.ENDING, ChatManager.colorMessage("Signs.Game-States.Ending"));
        gameStateToString.put(ArenaState.RESTARTING, ChatManager.colorMessage("Signs.Game-States.Restarting"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();
        updateSignScheduler();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if(!e.getPlayer().hasPermission("villagedefense.admin.sign.create")) return;
        if(e.getLine(0).equalsIgnoreCase("[villagedefense]")) {
            if(e.getLine(1).isEmpty()) {
                e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Please-Type-Arena-Name"));
                return;
            }
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(arena.getID().equalsIgnoreCase(e.getLine(1))) {
                    for(int i = 0; i < LanguageManager.getLanguageFile().getStringList("Signs.Lines").size(); i++) {
                        if(i == 1) {
                            //maybe not needed
                            e.setLine(i, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%mapname%", arena.getMapName())));
                        }
                        if(LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i).contains("%state%")) {
                            e.setLine(i, LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Inactive")));
                        }
                        if(LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i).contains("%playersize%")) {
                            e.setLine(i, LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%playersize%", String.valueOf(arena.getPlayers().size()))
                                    .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
                        }
                    }
                    loadedSigns.put((Sign) e.getBlock().getState(), arena);
                    e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
                    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
                    List<String> locs = ConfigurationManager.getConfig("arenas").getStringList("instances." + arena.getID() + ".signs");
                    locs.add(location);
                    FileConfiguration config = ConfigurationManager.getConfig("arenas");
                    config.set("instances." + arena.getID() + ".signs", locs);
                    ConfigurationManager.saveConfig(config, "arenas");
                    return;
                }
            }
            e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Arena-Doesnt-Exists"));
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        if(!e.getPlayer().hasPermission("villagedefense.admin.sign.break")) return;
        if(loadedSigns.get(e.getBlock().getState()) == null) return;
        loadedSigns.remove(e.getBlock().getState());
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + "," + "0.0,0.0";
        for(String arena : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances").getKeys(false)){
            for(String sign : ConfigurationManager.getConfig("arenas").getStringList("instances." + arena + ".signs")){
                if(sign.equals(location)){
                    List<String> signs = ConfigurationManager.getConfig("arenas").getStringList("instances." + arena + ".signs");
                    signs.remove(location);
                    FileConfiguration config = ConfigurationManager.getConfig("arenas");
                    config.set(arena + ".signs", signs);
                    ConfigurationManager.saveConfig(config, "arenas");
                    e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Removed"));
                    return;
                }
            }
        }
        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
    }

    @EventHandler
    public void onJoinAttempt(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                e.getClickedBlock().getState() instanceof Sign && loadedSigns.containsKey(e.getClickedBlock().getState())) {

            Arena arena = loadedSigns.get(e.getClickedBlock().getState());
            if(arena != null) {
                for(Arena loopArena : ArenaRegistry.getArenas()) {
                    if(loopArena.getPlayers().contains(e.getPlayer())) {
                        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
                        return;
                    }
                }
                if(arena.getMaximumPlayers() <= arena.getPlayers().size()) {
                    if((e.getPlayer().hasPermission(PermissionsManager.getVip()) || e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames()))) {
                        boolean b = false;
                        for(Player player : arena.getPlayers()) {
                            if(!player.hasPermission(PermissionsManager.getVip()) || !player.hasPermission(PermissionsManager.getJoinFullGames())) {
                                if((arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS)) {
                                    arena.leaveAttempt(player);
                                    player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
                                    String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), player);
                                    for(Player p : arena.getPlayers()) {
                                        p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                                    }
                                    arena.joinAttempt(e.getPlayer());
                                    return;
                                } else {
                                    arena.joinAttempt(e.getPlayer());
                                    return;
                                }
                            }
                        }
                        if(!b) {
                            e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.No-Slots-For-Premium"));
                        }
                    } else {
                        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Full-Game-No-Permission"));
                    }
                } else {
                    arena.joinAttempt(e.getPlayer());
                }
            }
        }
    }

    public void loadSigns() {
        loadedSigns.clear();
        for(String path : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances").getKeys(false)) {
            for(String sign : ConfigurationManager.getConfig("arenas").getStringList("instances." + path + ".signs")) {
                Location loc = Util.getLocation(false, sign);
                if(loc == null) {
                    if(Main.isDebugged()) {
                        System.out.println("[Village Debugger] Location of sign is null!");
                    }
                }
                if(loc.getBlock().getState() instanceof Sign) {
                    String mapName = ((Sign) loc.getBlock().getState()).getLine(2);
                    for(Arena inst : ArenaRegistry.getArenas()) {
                        if(inst.getMapName().equals(mapName)) {
                            loadedSigns.put((Sign) loc.getBlock().getState(), inst);
                        }
                    }
                } else {
                    if(Main.isDebugged()) {
                        System.out.println("[Village Debugger] Block at location " + loc + " for arena " + path + " isn't a sign!");
                    }
                }
            }
        }
    }

    private void updateSignScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Sign s : loadedSigns.keySet()) {
                Arena arena = loadedSigns.get(s);
                ArenaState arenaState;
                if(arena == null) {
                    arenaState = ArenaState.WAITING_FOR_PLAYERS;
                } else {
                    arenaState = arena.getArenaState();
                }
                s.setLine(0, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(0)));
                if(arena.getPlayers().size() == arena.getMaximumPlayers()) {
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(1).replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Full-Game"))));
                } else {
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(1).replaceAll("%state%", gameStateToString.get(arenaState))));
                }
                s.setLine(2, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(2).replaceAll("%mapname%", arena.getMapName())));
                s.setLine(3, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(3)
                        .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers()))
                        .replaceAll("%playersize%", String.valueOf(arena.getPlayers().size()))));
                s.update();
            }
        }, 10, 10);
    }
}
