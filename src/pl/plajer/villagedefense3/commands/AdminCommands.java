package pl.plajer.villagedefense3.commands;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import pl.plajer.villagedefense3.ArenaInstance;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.game.GameState;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.UserManager;
import pl.plajer.villagedefense3.utils.Util;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 25.02.2018
 */
public class AdminCommands extends MainCommand {

    private Main plugin;

    public AdminCommands(Main plugin) {
        super(plugin, false);
        this.plugin = plugin;
    }

    public void sendHelp(CommandSender sender) {
        if(!sender.hasPermission("villagedefense.admin")) return;
        sender.sendMessage(ChatManager.HIGHLIGHTED + "--------{Admin commands}-----------");
        sender.sendMessage(ChatManager.PREFIX + "/vd create <arena>" + ChatColor.GRAY + ": Create new arena");
        sender.sendMessage(ChatManager.PREFIX + "/vd <arena> edit" + ChatColor.GRAY + ": Edits existing arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda stop" + ChatColor.GRAY + ": Stops the arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda forcestart" + ChatColor.GRAY + ": ForceStarts the arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda respawn" + ChatColor.GRAY + ": Respawns you if u are dead");
        sender.sendMessage(ChatManager.PREFIX + "/vda respawn <player>" + ChatColor.GRAY + ": Respawns the named if he is dead");
        sender.sendMessage(ChatManager.PREFIX + "/vda spychat" + ChatColor.GRAY + ": Toggle all games chat visibility (only multi-arena)");
        sender.sendMessage(ChatManager.PREFIX + "/vda setprice <amount>" + ChatColor.GRAY + ": Sets holding item price (for shop)");
        sender.sendMessage(ChatManager.PREFIX + "/vda reload" + ChatColor.GRAY + ": Stops and reloads all game instances");
        sender.sendMessage(ChatManager.PREFIX + "/vda setshopchest" + ChatColor.GRAY + ": Sets game shop");
        sender.sendMessage(ChatManager.PREFIX + "/vda addsign <arena>" + ChatColor.GRAY + ": Adds target sign as arena game sign");
        sender.sendMessage(ChatManager.PREFIX + "/vda delete <arena>" + ChatColor.GRAY + ": Removes arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda tp <arena> <location type>" + ChatColor.GRAY + ": Teleports to provided arena location");
        sender.sendMessage(ChatManager.PREFIX + "/vda clear zombie" + ChatColor.GRAY + ": Clears the zombies in the arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda clear villager" + ChatColor.GRAY + ": Clears the villagers in the arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda clear golem" + ChatColor.GRAY + ": Clears the golems in the arena");
        sender.sendMessage(ChatManager.PREFIX + "/vda addorbs <amount>" + ChatColor.GRAY + ": Gives u the given amount of orbs");
        sender.sendMessage(ChatManager.PREFIX + "/vda addorbs <amount> <player>" + ChatColor.GRAY + ": Gives the named player the given amount of orbs");
        sender.sendMessage(ChatManager.PREFIX + "/vda setwave <number>" + ChatColor.GRAY + ": Sets the number from a wave");
    }

    public void stopGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.stopgame")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ((ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender)).stopGame(false);
    }

    public void forceStartGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.forcestart")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        if(arenaInstance.getGameState() == GameState.WAITING_FOR_PLAYERS || arenaInstance.getGameState() == GameState.STARTING) {
            arenaInstance.setGameState(GameState.STARTING);
            arenaInstance.setTimer(0);
            for(Player p1 : plugin.getGameInstanceManager().getGameInstance((Player) sender).getPlayers()) {
                p1.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
            }
        }
    }

    public void respawn(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!hasPermission(player, "villagedefense.admin.respawn")) return;
        if(!checkIsInGameInstance(player)) return;
        ArenaInstance instance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance(player);
        player.setGameMode(GameMode.SURVIVAL);
        User user = UserManager.getUser(player.getUniqueId());
        user.setFakeDead(false);
        user.setSpectator(false);
        instance.teleportToStartLocation(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        instance.showPlayer(player);
        player.getInventory().clear();
        user.getKit().giveKitItems(player);
        player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
    }

    public void respawnOther(CommandSender sender, String player) {
        if(checkSenderIsConsole(sender)) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.respawn.others")) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        for(Player loopPlayer : arenaInstance.getPlayers()) {
            if(player.equalsIgnoreCase(loopPlayer.getName())) {
                loopPlayer.setGameMode(GameMode.SURVIVAL);
                User user = UserManager.getUser(loopPlayer.getUniqueId());
                user.setFakeDead(false);
                user.setSpectator(false);

                sender.sendMessage(ChatColor.GREEN + "Player respawned!");
                arenaInstance.teleportToStartLocation(loopPlayer);
                loopPlayer.setFlying(false);
                loopPlayer.setAllowFlight(false);
                arenaInstance.showPlayer(loopPlayer);
                loopPlayer.getInventory().clear();
                user.getKit().giveKitItems(loopPlayer);
                loopPlayer.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "Player not found!");
    }

    public void toggleSpyChat(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.spychat")) return;
        boolean bool = !plugin.getSpyChatEnabled().getOrDefault(((Player) sender).getUniqueId(), false);
        plugin.getSpyChatEnabled().put(((Player) sender).getUniqueId(), bool);
        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.GREEN + "Game spy chat toggled to " + bool);
    }

    public void reloadInstances(CommandSender sender) {
        if(!hasPermission((Player) sender, "villagedefense.admin.reload")) return;
        plugin.loadInstances();
        sender.sendMessage(ChatManager.PLUGINPREFIX + "Instances reloaded!");
    }

    public void setShopChest(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!hasPermission(player, "villagedefense.admin.setshopchest")) return;
        Block targetBlock;
        targetBlock = player.getTargetBlock(null, 100);
        if(targetBlock == null || targetBlock.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "Look at the chest! You are targeting something else!");
            return;
        }
        Util.saveLoc("shop.location", targetBlock.getLocation());
        player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
    }

    public void addSign(CommandSender sender, String arena) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!hasPermission((Player) sender, "villagedefense.admin.addsign")) return;
        if(plugin.getGameInstanceManager().getGameInstance(arena) == null) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
        } else {
            Location location = player.getTargetBlock(null, 10).getLocation();
            if(location.getBlock().getState() instanceof Sign) {
                plugin.getSignManager().getLoadedSigns().put((Sign) location.getBlock().getState(), plugin.getGameInstanceManager().getGameInstance(arena));
                player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
                String loc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
                List<String> locs = plugin.getConfig().getStringList("signs");
                locs.add(loc);
                plugin.getConfig().set("signs", locs);
                plugin.saveConfig();
            } else {
                player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
            }
        }
    }

    public void deleteArena(CommandSender sender, String arena) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!checkIsInGameInstance(player)) return;
        if(!checkIsInGameInstance(player)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.delete")) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance(player);
        if(plugin.getGameInstanceManager().getGameInstance(arena) == null) {
            sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        arenaInstance.stopGame(false);
        plugin.getConfig().set("instances." + arena, null);
        plugin.getGameInstanceManager().unregisterGameInstance(arenaInstance);
        sender.sendMessage(ChatManager.PLUGINPREFIX + "§Successfully removed game instance!");
    }

    public void setItemPrice(CommandSender sender, String price) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.setprice")) return;
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand().clone();
        if(item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "You must hold any item!");
            return;
        }
        //check any price from lore
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for(int i = 0; i < item.getItemMeta().getLore().size(); i++) {
                if(item.getItemMeta().getLore().get(i).contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
                    List<String> lore = item.getItemMeta().getLore();
                    lore.remove(i);
                    lore.add(ChatColor.GOLD + price + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
                    item.getItemMeta().setLore(lore);
                    player.setItemInHand(item);
                    player.sendMessage(ChatColor.GREEN + "Command succesfully executed, item updated!");
                    return;
                }
            }
            Util.addLore(item, ChatColor.GOLD + price + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
            player.sendMessage(ChatColor.GREEN + "Command succesfully executed, added lore!");
        } else {
            Util.addLore(item, ChatColor.GOLD + price + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
            player.sendMessage(ChatColor.GREEN + "Command succesfully executed!");
        }
    }

    public void teleportToInstance(CommandSender sender, String arena, String locationType) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.teleport")) return;
        Player player = (Player) sender;
        try {
            LocationType.valueOf(locationType.toUpperCase());
        } catch(Exception e) {
            sender.sendMessage(ChatColor.RED + "Location to teleport is invalid!");
            return;
        }
        for(GameInstance instance : plugin.getGameInstanceManager().getGameInstances()) {
            if(instance.getID().equalsIgnoreCase(arena)) {
                super.onTpCommand(player, arena, LocationType.valueOf(locationType.toUpperCase()));
            }
        }
    }

    public void clearZombies(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        if(arenaInstance.getZombies() != null) {
            for(Zombie zombie : arenaInstance.getZombies()) {
                zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                zombie.remove();
            }
            arenaInstance.getZombies().clear();
        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH");
        for(Player loopPlayer : arenaInstance.getPlayers()) {
            String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
    }

    public void clearVillagers(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        if(arenaInstance.getVillagers() != null) {
            for(Villager villager : arenaInstance.getVillagers()) {
                villager.getWorld().playEffect(villager.getLocation(), Effect.LAVA_POP, 20);
                villager.remove();
            }
            arenaInstance.getVillagers().clear();
        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_VILLAGER_DEATH", "VILLAGER_DEATH");
        for(Player loopPlayer : arenaInstance.getPlayers()) {
            String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Villagers"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
    }

    public void clearGolems(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        if(arenaInstance.getIronGolems() != null) {
            for(IronGolem golem : arenaInstance.getIronGolems()) {
                golem.getWorld().playEffect(golem.getLocation(), Effect.LAVA_POP, 20);
                golem.remove();
            }
            arenaInstance.getIronGolems().clear();

        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_IRONGOLEM_DEATH", "IRONGOLEM_DEATH");
        for(Player loopPlayer : arenaInstance.getPlayers()) {
            String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Golems"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGINPREFIX + message);
        }
    }

    public void addOrbs(CommandSender sender, String number) {
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.addorbs")) return;
        if(NumberUtils.isNumber(number)) {
            User user = UserManager.getUser(((Player) sender).getUniqueId());
            user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(number));
            sender.sendMessage(ChatManager.PLUGINPREFIX + "Orbs added!");
        } else {
            sender.sendMessage(ChatColor.RED + "Wrong usage. Do /villagedefense addorbs <amount>");
        }
    }

    public void addOrbsOther(CommandSender sender, String p, String number) {
        //check only target player, maybe sender would do this from console.
        if(Bukkit.getPlayerExact(p) == null) {
            sender.sendMessage(ChatColor.RED + "Target player doesn't exist!");
        }
        Player player = Bukkit.getPlayer(p);
        if(!checkIsInGameInstance(player)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.addorbs.others")) return;
        if(NumberUtils.isNumber(number)) {
            User user = UserManager.getUser(player.getUniqueId());
            user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(number));
            sender.sendMessage(ChatManager.PLUGINPREFIX + "Orbs added!");
        } else {
            sender.sendMessage(ChatColor.RED + "Wrong usage. Do /villagedefense addorbs <player> <amount>");
        }
    }

    public void createArena(CommandSender sender, String[] args) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.create")) return;
        createArenaCommand((Player) sender, args);
    }

    public void setWave(CommandSender sender, String number) {
        if(checkSenderIsConsole(sender)) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.setwave")) return;
        ArenaInstance arenaInstance = (ArenaInstance) plugin.getGameInstanceManager().getGameInstance((Player) sender);
        if(NumberUtils.isNumber(number)) {
            arenaInstance.setWave(Integer.parseInt(number) - 1);
            arenaInstance.endWave();
            String message = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Changed-Wave"), arenaInstance.getWave());
            for(Player player1 : arenaInstance.getPlayers()) {
                player1.sendMessage(ChatManager.PLUGINPREFIX + message);
            }
            if(arenaInstance.getZombies() != null) {
                for(Zombie zombie : arenaInstance.getZombies()) {
                    zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                    zombie.remove();
                }
                arenaInstance.getZombies().clear();
            } else {
                sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
                return;
            }
            sendSound((Player) sender, "ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH");
            for(Player loopPlayer : arenaInstance.getPlayers()) {
                String message1 = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies"), new Player[]{(loopPlayer)});
                loopPlayer.sendMessage(ChatManager.PLUGINPREFIX + message1);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Wave needs to be number! Do /villagedefense set wave <number>");
        }
    }

    public void performSetup(CommandSender sender, String[] args) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission((Player) sender, "villagedefense.admin.setup")) return;
        super.performSetup((Player) sender, args);
    }

}
