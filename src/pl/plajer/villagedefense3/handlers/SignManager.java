package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;
import pl.plajer.villagedefense3.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager implements Listener {

    public static String[] signLines = new String[]{"--------", "Waiting", "", "--------"};
    private Main plugin;
    private Map<Sign, GameInstance> loadedSigns = new HashMap<>();
    private Map<GameState, String> gameStateToString = new HashMap<>();

    public SignManager(Main plugin) {
        this.plugin = plugin;
        gameStateToString.put(GameState.WAITING_FOR_PLAYERS, ChatManager.colorMessage("Signs.Game-States.Inactive"));
        gameStateToString.put(GameState.STARTING, ChatManager.colorMessage("Signs.Game-States.Starting"));
        gameStateToString.put(GameState.IN_GAME, ChatManager.colorMessage("Signs.Game-States.In-Game"));
        gameStateToString.put(GameState.ENDING, ChatManager.colorMessage("Signs.Game-States.Ending"));
        gameStateToString.put(GameState.RESTARTING, ChatManager.colorMessage("Signs.Game-States.Restarting"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();
        updateSignScheduler();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if(e.getLine(0).equalsIgnoreCase("[villagedefense]")) {
            if(e.getLine(1).isEmpty()) {
                e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Signs.Please-Type-Arena-Name"));
                return;
            }
            for(GameInstance instance : plugin.getGameInstanceManager().getGameInstances()) {
                if(instance.getID().equalsIgnoreCase(e.getLine(1))) {
                    for(int i = 0; i < LanguageManager.getLanguageFile().getStringList("Signs.Lines").size(); i++) {
                        if(i == 1) {
                            //maybe not needed
                            e.setLine(i, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%mapname%", instance.getMapName())));
                        }
                        if(LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i).contains("%state%")) {
                            e.setLine(i, LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Inactive")));
                        }
                        if(LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i).contains("%playersize%")) {
                            e.setLine(i, LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(i)
                                    .replaceAll("%playersize%", String.valueOf(instance.getPlayers().size()))
                                    .replaceAll("%maxplayers%", String.valueOf(instance.getMAX_PLAYERS())));
                        }
                    }
                    loadedSigns.put((Sign) e.getBlock().getState(), instance);
                    e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
                    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
                    List<String> locs = plugin.getConfig().getStringList("signs");
                    locs.add(location);
                    plugin.getConfig().set("signs", locs);
                    plugin.saveConfig();
                    return;
                }
            }
            e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Signs.Arena-Doesnt-Exists"));
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        if(loadedSigns.get(e.getBlock().getState()) == null) return;
        loadedSigns.remove(e.getBlock().getState());
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + "," + "0.0,0.0";
        if(plugin.getConfig().getStringList("signs").contains(location)) {
            plugin.getConfig().getStringList("signs").remove(location);
            e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Signs.Sign-Removed"));
        }
        e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + "§cCouldn't remove sign from configuration! Please do this manually!");
    }

    @EventHandler
    public void onJoinAttempt(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                e.getClickedBlock().getState() instanceof Sign && loadedSigns.containsKey(e.getClickedBlock().getState())) {

            GameInstance instance = loadedSigns.get(e.getClickedBlock().getState());

            if(instance == null) {
                Location location = e.getClickedBlock().getLocation();
                for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                    if(gameInstance.getSigns().contains(location)) {
                        instance = gameInstance;
                        break;
                    }
                }
            }
            if(instance != null) {
                for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                    if(gameInstance.getPlayers().contains(e.getPlayer())) {
                        e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
                        return;
                    }
                }

                if(instance.getMAX_PLAYERS() <= instance.getPlayers().size()) {

                    if((e.getPlayer().hasPermission(PermissionsManager.getVip()) || e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames()))) {

                        boolean b = false;
                        for(Player player : instance.getPlayers()) {
                            if(!player.hasPermission(PermissionsManager.getVip()) || !player.hasPermission(PermissionsManager.getJoinFullGames())) {
                                if((instance.getGameState() == GameState.STARTING || instance.getGameState() == GameState.WAITING_FOR_PLAYERS)) {
                                    instance.leaveAttempt(player);
                                    player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
                                    String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), player);
                                    for(Player p : instance.getPlayers()) {
                                        p.sendMessage(ChatManager.PLUGINPREFIX + message);
                                    }
                                    instance.joinAttempt(e.getPlayer());
                                    return;
                                } else {
                                    instance.joinAttempt(e.getPlayer());
                                    return;
                                }
                            }

                        }
                        if(!b) {
                            e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.No-Slots-For-Premium"));
                        }
                    } else {
                        e.getPlayer().sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Full-Game-No-Permission"));
                    }
                } else {
                    instance.joinAttempt(e.getPlayer());
                }
            }
        }
    }

    private void loadSigns() {
        for(String path : plugin.getConfig().getStringList("signs")) {
            Location loc = Util.getLocation(false, path);
            if(loc == null) {
                if(Main.isDebugged()) {
                    System.out.println("[Village Debugger] Location of sign is null!");
                }
            }
            if(loc.getBlock().getState() instanceof Sign) {
                String mapName = ((Sign) loc.getBlock().getState()).getLine(2);
                for(GameInstance inst : plugin.getGameInstanceManager().getGameInstances()) {
                    if(inst.getMapName().equals(mapName)) {
                        loadedSigns.put((Sign) loc.getBlock().getState(), inst);
                    }
                }
                if(Main.isDebugged()) {
                    System.out.println("[Village Debugger] Broken game sign at location " + path + "!");
                }
            } else {
                if(Main.isDebugged()) {
                    System.out.println("[Village Debugger] Block at given location " + path + " isn't a sign!");
                }
            }
        }
    }

    private void updateSignScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Sign s : loadedSigns.keySet()) {
                GameInstance inst = loadedSigns.get(s);
                GameState gameState;
                if(inst == null) {
                    gameState = GameState.WAITING_FOR_PLAYERS;
                } else {
                    gameState = inst.getGameState();
                }
                s.setLine(0, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(0)));
                if(inst.getPlayers().size() == inst.getMAX_PLAYERS()) {
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(1).replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Full-Game"))));
                } else {
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(1).replaceAll("%state%", gameStateToString.get(gameState))));
                }
                s.setLine(2, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(2).replaceAll("%mapname%", inst.getMapName())));
                s.setLine(3, ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageFile().getStringList("Signs.Lines").get(3)
                        .replaceAll("%maxplayers%", String.valueOf(inst.getMAX_PLAYERS()))
                        .replaceAll("%playersize%", String.valueOf(inst.getPlayers().size()))));
                s.update();
            }
        }, 10, 10);
    }

    public Map<Sign, GameInstance> getLoadedSigns() {
        return loadedSigns;
    }
}
