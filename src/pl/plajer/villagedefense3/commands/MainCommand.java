package pl.plajer.villagedefense3.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.events.customevents.PlayerAddSpawnCommandEvent;
import pl.plajer.villagedefense3.game.GameInstance;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.utils.SetupInventory;
import pl.plajer.villagedefense3.utils.Util;

/**
 * Created by Tom on 7/08/2014.
 */
public class MainCommand implements CommandExecutor {

    private Main plugin;
    private AdminCommands adminCommands;
    private GameCommands gameCommands;

    public MainCommand(Main plugin, boolean register) {
        this.plugin = plugin;
        if(register) {
            adminCommands = new AdminCommands(plugin);
            gameCommands = new GameCommands(plugin);
            plugin.getCommand("villagedefense").setExecutor(this);
            plugin.getCommand("villagedefenseadmin").setExecutor(this);
        }
    }

    boolean checkSenderIsConsole(CommandSender sender) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
            return true;
        }
        return false;
    }

    boolean checkIsInGameInstance(Player player) {
        if(plugin.getGameInstanceManager().getGameInstance(player) == null) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Not-Playing"));
            return false;
        }
        return true;
    }

    boolean hasPermission(Player player, String perm) {
        if(player.hasPermission(perm)) {
            return true;
        }
        player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Permission"));
        return false;
    }

    void sendSound(Player p, String newSound, String oldSound) {
        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
            p.playSound(p.getLocation(), Sound.valueOf(newSound), 1, 1);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf(oldSound), 1, 1);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("villagedefenseadmin")) {
            if(args.length == 0) {
                adminCommands.sendHelp(sender);
                return true;
            }
            if(args[0].equalsIgnoreCase("stop")) {
                adminCommands.stopGame(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("forcestart")) {
                adminCommands.forceStartGame(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("respawn")) {
                if(args.length == 1) {
                    adminCommands.respawn(sender);
                } else {
                    adminCommands.respawnOther(sender, args[1]);
                }
                return true;
            } else if(args[0].equalsIgnoreCase("spychat")) {
                adminCommands.toggleSpyChat(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("reload")) {
                adminCommands.reloadInstances(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("setshopchest")) {
                adminCommands.setShopChest(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("addsign")) {
                if(args.length != 1) {
                    adminCommands.addSign(sender, args[1]);
                } else {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("delete")) {
                if(args.length != 1) {
                    adminCommands.deleteArena(sender, args[1]);
                } else {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("setprice")) {
                if(args.length != 1) {
                    adminCommands.setItemPrice(sender, args[1]);
                } else {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type price of item!");
                }
                return true;
            } else if(args[0].equalsIgnoreCase("tp")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    return true;
                }
                if(args.length == 2) {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type location type: END, START, LOBBY");
                    return true;
                }
                adminCommands.teleportToInstance(sender, args[1], args[2]);
                return true;
            } else if(args[0].equalsIgnoreCase("clear")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                    return true;
                }
                if(args[1].equalsIgnoreCase("villager")) {
                    adminCommands.clearVillagers(sender);
                } else if(args[1].equalsIgnoreCase("zombie")) {
                    adminCommands.clearZombies(sender);
                } else if(args[1].equalsIgnoreCase("golem")) {
                    adminCommands.clearGolems(sender);
                } else {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                    return true;
                }
                return true;
            } else if(args[0].equalsIgnoreCase("addorbs")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type number of orbs to give!");
                    return true;
                }
                if(args.length == 2) {
                    adminCommands.addOrbs(sender, args[1]);
                } else {
                    adminCommands.addOrbsOther(sender, args[2], args[1]);
                }
                return true;
            } else if(args[0].equalsIgnoreCase("setwave")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type number of wave to set!");
                    return true;
                }
                adminCommands.setWave(sender, args[1]);
                return true;
            }
            adminCommands.sendHelp(sender);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("villagedefense")) {
            if(args.length == 0) {
                sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Header"));
                sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Description"));
                if(sender.hasPermission("villagedefense.admin")) {
                    sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
                }
                sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Footer"));
                return true;
            }
            if(args.length > 1) {
                if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("addspawn") || args[1].equalsIgnoreCase("edit")) {
                    adminCommands.performSetup(sender, args);
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("join")) {
                if(args.length != 1) {
                    gameCommands.joinGame(sender, args[1]);
                    return true;
                }
                sender.sendMessage(ChatManager.colorMessage("Commands.Type-Arena-Name"));
                return true;
            }
            if(args[0].equalsIgnoreCase("stats")) {
                gameCommands.sendStats(sender);
                return true;
            }
            if(args[0].equalsIgnoreCase("leave")) {
                gameCommands.leaveGame(sender);
                return true;
            }
            if(args[0].equalsIgnoreCase("create")) {
                adminCommands.createArena(sender, args);
                return true;
            }
            if(args[0].equalsIgnoreCase("admin")) {
                if(args.length == 1) {
                    adminCommands.sendHelp(sender);
                    return true;
                }
                if(args[1].equalsIgnoreCase("stop")) {
                    adminCommands.stopGame(sender);
                    return true;
                } else if(args[1].equalsIgnoreCase("forcestart")) {
                    adminCommands.forceStartGame(sender);
                    return true;
                } else if(args[1].equalsIgnoreCase("respawn")) {
                    if(args.length == 2) {
                        adminCommands.respawn(sender);
                    } else {
                        adminCommands.respawnOther(sender, args[2]);
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("spychat")) {
                    adminCommands.toggleSpyChat(sender);
                    return true;
                } else if(args[1].equalsIgnoreCase("reload")) {
                    adminCommands.reloadInstances(sender);
                    return true;
                } else if(args[1].equalsIgnoreCase("setshopchest")) {
                    adminCommands.setShopChest(sender);
                    return true;
                } else if(args[1].equalsIgnoreCase("addsign")) {
                    if(args.length != 2) {
                        adminCommands.addSign(sender, args[2]);
                    } else {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("delete")) {
                    if(args.length != 2) {
                        adminCommands.deleteArena(sender, args[2]);
                    } else {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("setprice")) {
                    if(args.length != 2) {
                        adminCommands.setItemPrice(sender, args[2]);
                    } else {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type price of item!");
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("tp")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                        return true;
                    }
                    if(args.length == 3) {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type location type: END, START, LOBBY");
                        return true;
                    }
                    adminCommands.teleportToInstance(sender, args[2], args[3]);
                    return true;
                } else if(args[1].equalsIgnoreCase("clear")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                        return true;
                    }
                    if(args[2].equalsIgnoreCase("villager")) {
                        adminCommands.clearVillagers(sender);
                    } else if(args[2].equalsIgnoreCase("zombie")) {
                        adminCommands.clearZombies(sender);
                    } else if(args[2].equalsIgnoreCase("golem")) {
                        adminCommands.clearGolems(sender);
                    } else {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                        return true;
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("addorbs")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type number of orbs to give!");
                        return true;
                    }
                    if(args.length == 3) {
                        adminCommands.addOrbs(sender, args[2]);
                    } else {
                        adminCommands.addOrbsOther(sender, args[3], args[2]);
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("setwave")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "Please type number of wave to set!");
                        return true;
                    }
                    adminCommands.setWave(sender, args[2]);
                    return true;
                }
                adminCommands.sendHelp(sender);
                return true;
            }
        }
        return false;
    }

    //TODO optimize me / change me
    void performSetup(Player player, String[] args) {
        if(args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
            if(plugin.getGameInstanceManager().getGameInstance(args[0]) == null) {
                player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                return;
            }
            new SetupInventory(plugin.getGameInstanceManager().getGameInstance(args[0])).openInventory(player);
            return;
        }
        if(!(args.length > 2))
            return;

        if(!plugin.getConfig().contains("instances." + args[0])) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            player.sendMessage(ChatColor.RED + "Usage: /vd < ARENA ID > set <MINPLAYRS | MAXPLAYERS | MAPNAME | SCHEMATIC | LOBBYLOCATION | EndLOCATION | STARTLOCATION  >  < VALUE>");
            return;
        }


        if(args[1].equalsIgnoreCase("addspawn")) {
            PlayerAddSpawnCommandEvent event = new PlayerAddSpawnCommandEvent(player, args[2], args[0]);
            plugin.getServer().getPluginManager().callEvent(event);
            plugin.saveConfig();
            return;
        }
        if(!(args[1].equalsIgnoreCase("set")))
            return;
        if(args.length == 3) {
            if(args[2].equalsIgnoreCase("lobbylocation") || args[2].equalsIgnoreCase("lobbyloc")) {
                Util.saveLoc("instances." + args[0] + ".lobbylocation", player.getLocation());
                player.sendMessage("VillageDefense: Lobby location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Startlocation") || args[2].equalsIgnoreCase("Startloc")) {
                Util.saveLoc("instances." + args[0] + ".Startlocation", player.getLocation());
                player.sendMessage("VillageDefense: Start location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Endlocation") || args[2].equalsIgnoreCase("Endloc")) {
                Util.saveLoc("instances." + args[0] + ".Endlocation", player.getLocation());
                player.sendMessage("VillageDefense: End location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /vd <ARENA > set <StartLOCTION | LOBBYLOCATION | EndLOCATION>");
            }
        } else if(args.length == 4) {
            if(args[2].equalsIgnoreCase("MAXPLAYERS") || args[2].equalsIgnoreCase("maximumplayers")) {
                plugin.getConfig().set("instances." + args[0] + ".maximumplayers", Integer.parseInt(args[3]));
                player.sendMessage("VillageDefense: Maximum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));

            } else if(args[2].equalsIgnoreCase("MINPLAYERS") || args[2].equalsIgnoreCase("minimumplayers")) {
                plugin.getConfig().set("instances." + args[0] + ".minimumplayers", Integer.parseInt(args[3]));
                player.sendMessage("VillageDefense: Minimum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));
            } else if(args[2].equalsIgnoreCase("MAPNAME") || args[2].equalsIgnoreCase("NAME")) {
                plugin.getConfig().set("instances." + args[0] + ".mapname", args[3]);
                player.sendMessage("VillageDefense: Map name for arena/instance " + args[0] + " set to " + args[3]);
            } else if(args[2].equalsIgnoreCase("WORLD") || args[2].equalsIgnoreCase("MAP")) {
                boolean exists = false;
                for(World world : Bukkit.getWorlds()) {
                    if(world.getName().equalsIgnoreCase(args[3]))
                        exists = true;
                }
                if(!exists) {
                    player.sendMessage(ChatManager.PLUGINPREFIX + ChatColor.RED + "That world doesn't exists!");
                    return;
                }
                plugin.getConfig().set("instances." + args[0] + ".world", args[3]);
                player.sendMessage("VillageDefense: World for arena/instance " + args[0] + " set to " + args[3]);
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /vd set <MINPLAYERS | MAXPLAYERS> <value>");
            }
        }
        plugin.saveConfig();
    }

    void createArenaCommand(Player player, String[] strings) {
        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
            if(gameInstance.getID().equalsIgnoreCase(strings[1])) {
                player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
                player.sendMessage(ChatColor.DARK_RED + "Usage: /vd create <ID>");
                return;
            }
        }
        if(plugin.getConfig().contains("instances." + strings[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(strings[1], player.getWorld().getName());
            player.sendMessage(ChatColor.GREEN + "Instances/Arena successfully created! Restart or reload the server to start the arena!");
            player.sendMessage(ChatColor.BOLD + "--------------- INFORMATION --------------- ");
            player.sendMessage(ChatColor.GREEN + "WORLD: " + ChatColor.RED + strings[1]);
            player.sendMessage(ChatColor.GREEN + "MAX PLAYERS: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.minimumplayers"));
            player.sendMessage(ChatColor.GREEN + "MIN PLAYERS: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.maximumplayers"));
            player.sendMessage(ChatColor.GREEN + "MAP NAME: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.mapname"));
            player.sendMessage(ChatColor.GREEN + "LOBBY LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(true, "instances." + strings[1] + ".lobbylocation")));
            player.sendMessage(ChatColor.GREEN + "Start LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(true, "instances." + strings[1] + ".Startlocation")));
            player.sendMessage(ChatColor.GREEN + "End LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(true, "instances." + strings[1] + ".Endlocation")));
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
            player.sendMessage(ChatColor.RED + "You can edit this game instances in the config!");
        }
    }

    private void createInstanceInConfig(String ID, String worldName) {
        String path = "instances." + ID + ".";
        Util.saveLoc(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLoc(path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLoc(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        plugin.getConfig().set(path + "minimumplayers", plugin.getConfig().getInt("instances.default.minimumplayers"));
        plugin.getConfig().set(path + "maximumplayers", plugin.getConfig().getInt("instances.default.maximumplayers"));
        plugin.getConfig().set(path + "mapname", plugin.getConfig().getInt("instances.default.mapname"));

        plugin.getConfig().set(path + "world", worldName);
        plugin.saveConfig();
        plugin.loadInstances();
    }

    void onTpCommand(Player player, String ID, LocationType type) {
        if(!plugin.getConfig().contains("instances." + ID)) {
            player.sendMessage(ChatManager.PLUGINPREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(ID);
        switch(type) {
            case LOBBY:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
                    return;
                }
                gameInstance.teleportToLobby(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena" + ID);
                break;
            case START:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
                    return;
                }
                gameInstance.teleportToStartLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena" + ID);
                break;
            case END:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
                    return;
                }
                gameInstance.teleportToEndLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena" + ID);
                break;
            default:
                break; //o.o
        }
    }

    enum LocationType {
        LOBBY, END, START
    }

}
