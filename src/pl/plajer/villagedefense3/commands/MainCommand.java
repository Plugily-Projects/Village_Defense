/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_8_R3;
import pl.plajer.villagedefense3.arena.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.utils.SetupInventory;
import pl.plajer.villagedefense3.utils.Util;

import java.util.ArrayList;

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
        if(ArenaRegistry.getArena(player) == null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Not-Playing"));
            return false;
        }
        return true;
    }

    boolean hasPermission(CommandSender sender, String perm) {
        if(sender.hasPermission(perm)) {
            return true;
        }
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
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
            } else if(args[0].equalsIgnoreCase("list")) {
                adminCommands.printList(sender);
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
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("delete")) {
                if(args.length != 1) {
                    adminCommands.deleteArena(sender, args[1]);
                } else {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("setprice")) {
                if(args.length != 1) {
                    adminCommands.setItemPrice(sender, args[1]);
                } else {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type price of item!");
                }
                return true;
            } else if(args[0].equalsIgnoreCase("tp")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    return true;
                }
                if(args.length == 2) {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type location type: END, START, LOBBY");
                    return true;
                }
                adminCommands.teleportToInstance(sender, args[1], args[2]);
                return true;
            } else if(args[0].equalsIgnoreCase("clear")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                    return true;
                }
                if(args[1].equalsIgnoreCase("villager")) {
                    adminCommands.clearVillagers(sender);
                } else if(args[1].equalsIgnoreCase("zombie")) {
                    adminCommands.clearZombies(sender);
                } else if(args[1].equalsIgnoreCase("golem")) {
                    adminCommands.clearGolems(sender);
                } else {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                    return true;
                }
                return true;
            } else if(args[0].equalsIgnoreCase("addorbs")) {
                if(args.length == 1) {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type number of orbs to give!");
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
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type number of wave to set!");
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
                    if(checkSenderIsConsole(sender)) return true;
                    if(!hasPermission(sender, "villagedefense.admin.create")) return true;
                    adminCommands.performSetup(sender, args);
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("join")) {
                if(args.length == 2) {
                    gameCommands.joinGame(sender, args[1]);
                    return true;
                }
                sender.sendMessage(ChatManager.colorMessage("Commands.Type-Arena-Name"));
                return true;
            }
            if(args[0].equalsIgnoreCase("stats")) {
                if(args.length == 2) {
                    gameCommands.sendStatsOther(sender, args[1]);
                }
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
                } else if(args[1].equalsIgnoreCase("list")) {
                    adminCommands.printList(sender);
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
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("delete")) {
                    if(args.length != 2) {
                        adminCommands.deleteArena(sender, args[2]);
                    } else {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("setprice")) {
                    if(args.length != 2) {
                        adminCommands.setItemPrice(sender, args[2]);
                    } else {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type price of item!");
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("tp")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
                        return true;
                    }
                    if(args.length == 3) {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type location type: END, START, LOBBY");
                        return true;
                    }
                    adminCommands.teleportToInstance(sender, args[2], args[3]);
                    return true;
                } else if(args[1].equalsIgnoreCase("clear")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                        return true;
                    }
                    if(args[2].equalsIgnoreCase("villager")) {
                        adminCommands.clearVillagers(sender);
                    } else if(args[2].equalsIgnoreCase("zombie")) {
                        adminCommands.clearZombies(sender);
                    } else if(args[2].equalsIgnoreCase("golem")) {
                        adminCommands.clearGolems(sender);
                    } else {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type valid mob type to clear: VILLAGER, ZOMBIE, GOLEM");
                        return true;
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("addorbs")) {
                    if(args.length == 2) {
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type number of orbs to give!");
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
                        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Please type number of wave to set!");
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

    void onTpCommand(Player player, String ID, LocationType type) {
        if(!ConfigurationManager.getConfig("arenas").contains("instances." + ID)) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        Arena arena = ArenaRegistry.getArena(ID);
        switch(type) {
            case LOBBY:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
                    return;
                }
                arena.teleportToLobby(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena" + ID);
                break;
            case START:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
                    return;
                }
                arena.teleportToStartLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena" + ID);
                break;
            case END:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
                    return;
                }
                arena.teleportToEndLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena" + ID);
                break;
            default:
                break; //o.o
        }
    }

    void performSetup(Player player, String[] args) {
        if(args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
            if(ArenaRegistry.getArena(args[0]) == null) {
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                return;
            }
            new SetupInventory(ArenaRegistry.getArena(args[0])).openInventory(player);
            return;
        }
        if(!(args.length > 2))
            return;
        FileConfiguration config = ConfigurationManager.getConfig("arenas");

        if(!config.contains("instances." + args[0])) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            player.sendMessage(ChatColor.RED + "Usage: /vd < ARENA ID > set <MINPLAYRS | MAXPLAYERS | MAPNAME | SCHEMATIC | LOBBYLOCATION | EndLOCATION | STARTLOCATION  >  < VALUE>");
            return;
        }


        if(args[1].equalsIgnoreCase("addspawn")) {
            if(args[2].equalsIgnoreCase("zombie")) {
                int i;
                if(!config.contains("instances." + args[0] + ".zombiespawns")) {
                    i = 0;
                } else {
                    i = config.getConfigurationSection("instances." + args[0] + ".zombiespawns").getKeys(false).size();
                }
                i++;
                Util.saveLoc("instances." + args[0] + ".zombiespawns." + i, player.getLocation(), false);
                player.sendMessage(ChatColor.GREEN + "Zombie spawn added!");
                return;
            }
            if(args[2].equalsIgnoreCase("villager")) {
                int i;
                if(!config.contains("instances." + args[0] + ".villagerspawns")) {
                    i = 0;
                } else {
                    i = config.getConfigurationSection("instances." + args[0] + ".villagerspawns").getKeys(false).size();
                }

                i++;
                Util.saveLoc("instances." + args[0] + ".villagerspawns." + i, player.getLocation(), false);
                player.sendMessage(ChatColor.GREEN + "Villager spawn added!");
                return;
            }
            if(args[2].equalsIgnoreCase("doors")) {
                String ID = args[0];
                int counter = 0;
                int i;
                if(plugin.getWorldEditPlugin().getSelection(player) == null)
                    return;
                if(!config.contains("instances." + ID + ".doors")) {
                    i = 0;
                } else {
                    i = config.getConfigurationSection("instances." + ID + ".doors").getKeys(false).size();
                }
                i++;
                Selection selection = plugin.getWorldEditPlugin().getSelection(player);
                if(selection instanceof CuboidSelection) {
                    CuboidSelection cuboidSelection = (CuboidSelection) selection;
                    Vector min = cuboidSelection.getNativeMinimumPoint();
                    Vector max = cuboidSelection.getNativeMaximumPoint();
                    for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                        for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                            for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                                Location temporaryBlock = new Location(player.getWorld(), x, y, z);
                                if(temporaryBlock.getBlock().getType() == Material.WOODEN_DOOR) {
                                    String location = temporaryBlock.getWorld().getName() + "," + temporaryBlock.getX() + "," + temporaryBlock.getY() + "," + temporaryBlock.getZ() + "," + temporaryBlock.getYaw() + "," + temporaryBlock.getPitch();
                                    config.set("instances." + ID + ".doors." + i + ".location", location);
                                    config.set("instances." + ID + ".doors." + i + ".byte", temporaryBlock.getBlock().getData());
                                    counter++;
                                    i++;
                                }

                            }
                        }
                    }
                } else {
                    if(selection.getMaximumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                        String location = selection.getMaximumPoint().getWorld().getName() + "," + selection.getMaximumPoint().getX() + "," + selection.getMaximumPoint().getY() + "," + selection.getMaximumPoint().getZ() + "," + selection.getMaximumPoint().getYaw() + "," + selection.getMaximumPoint().getPitch();
                        config.set("instances." + ID + ".doors." + i + ".location", location);
                        config.set("instances." + ID + ".doors." + i + ".byte", selection.getMaximumPoint().getBlock().getData());
                        counter++;
                        i++;
                    }
                    if(selection.getMinimumPoint().getBlock().getType() == Material.WOODEN_DOOR) {
                        String location = selection.getMaximumPoint().getWorld().getName() + "," + selection.getMaximumPoint().getX() + "," + selection.getMaximumPoint().getY() + "," + selection.getMaximumPoint().getZ() + "," + selection.getMaximumPoint().getYaw() + "," + selection.getMaximumPoint().getPitch();
                        config.set("instances." + ID + ".doors." + i + ".location", location);
                        config.set("instances." + ID + ".doors." + i + ".byte", selection.getMinimumPoint().getBlock().getData());
                        counter++;
                        i++;
                    }
                }
                player.sendMessage(ChatColor.GREEN + "" + (int) Math.ceil(counter / 2) + " doors were added!");
            }
            ConfigurationManager.saveConfig(config, "arenas");
            return;
        }
        if(!(args[1].equalsIgnoreCase("set")))
            return;
        if(args.length == 3) {
            if(args[2].equalsIgnoreCase("lobbylocation") || args[2].equalsIgnoreCase("lobbyloc")) {
                String location = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
                config.set("instances." + args[0] + ".lobbylocation", location);
                player.sendMessage("VillageDefense: Lobby location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Startlocation") || args[2].equalsIgnoreCase("Startloc")) {
                String location = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
                config.set("instances." + args[0] + ".Startlocation", location);
                player.sendMessage("VillageDefense: Start location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Endlocation") || args[2].equalsIgnoreCase("Endloc")) {
                String location = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
                config.set("instances." + args[0] + ".Endlocation", location);
                player.sendMessage("VillageDefense: End location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /vd <ARENA > set <StartLOCTION | LOBBYLOCATION | EndLOCATION>");
            }
        } else if(args.length == 4) {
            if(args[2].equalsIgnoreCase("MAXPLAYERS") || args[2].equalsIgnoreCase("maximumplayers")) {
                config.set("instances." + args[0] + ".maximumplayers", Integer.parseInt(args[3]));
                player.sendMessage("VillageDefense: Maximum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));

            } else if(args[2].equalsIgnoreCase("MINPLAYERS") || args[2].equalsIgnoreCase("minimumplayers")) {
                config.set("instances." + args[0] + ".minimumplayers", Integer.parseInt(args[3]));
                player.sendMessage("VillageDefense: Minimum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));
            } else if(args[2].equalsIgnoreCase("MAPNAME") || args[2].equalsIgnoreCase("NAME")) {
                config.set("instances." + args[0] + ".mapname", args[3]);
                player.sendMessage("VillageDefense: Map name for arena/instance " + args[0] + " set to " + args[3]);
            } else if(args[2].equalsIgnoreCase("WORLD") || args[2].equalsIgnoreCase("MAP")) {
                boolean exists = false;
                for(World world : Bukkit.getWorlds()) {
                    if(world.getName().equalsIgnoreCase(args[3]))
                        exists = true;
                }
                if(!exists) {
                    player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "That world doesn't exists!");
                    return;
                }
                config.set("instances." + args[0] + ".world", args[3]);
                player.sendMessage("VillageDefense: World for arena/instance " + args[0] + " set to " + args[3]);
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /vd set <MINPLAYERS | MAXPLAYERS> <value>");
            }
        }
        ConfigurationManager.saveConfig(config, "arenas");
    }

    void createArenaCommand(Player player, String[] args) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getID().equalsIgnoreCase(args[1])) {
                player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
                player.sendMessage(ChatColor.DARK_RED + "Usage: /vd create <ID>");
                return;
            }
        }
        if(ConfigurationManager.getConfig("arenas").contains("instances." + args[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(args[1], player.getWorld().getName());
            player.sendMessage(ChatColor.BOLD + "------------------------------------------");
            player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/vd " + args[1] + " edit" + ChatColor.GREEN + "!");
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
        }
    }

    private void createInstanceInConfig(String ID, String worldName) {
        String path = "instances." + ID + ".";
        Util.saveLoc(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation(), false);
        Util.saveLoc(path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation(), false);
        Util.saveLoc(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation(), false);
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        config.set(path + "minimumplayers", 1);
        config.set(path + "maximumplayers", 10);
        config.set(path + "mapname", ID);
        config.set(path + "signs", new ArrayList<>());
        config.set(path + "isdone", false);
        config.set(path + "world", worldName);
        ConfigurationManager.saveConfig(config, "arenas");

        Arena arena;

        if(plugin.is1_8_R3()) {
            arena = new ArenaInitializer1_8_R3(ID, plugin);
        } else if(plugin.is1_9_R1()) {
            arena = new ArenaInitializer1_9_R1(ID, plugin);
        } else if(plugin.is1_11_R1()) {
            arena = new ArenaInitializer1_11_R1(ID, plugin);
        } else {
            arena = new ArenaInitializer1_12_R1(ID, plugin);
        }

        arena.setMinimumPlayers(ConfigurationManager.getConfig("arenas").getInt(path + "minimumplayers"));
        arena.setMaximumPlayers(ConfigurationManager.getConfig("arenas").getInt(path + "maximumplayers"));
        arena.setMapName(ConfigurationManager.getConfig("arenas").getString(path + "mapname"));
        arena.setLobbyLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "lobbylocation")));
        arena.setStartLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "Startlocation")));
        arena.setEndLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "Endlocation")));
        arena.setReady(false);

        ArenaRegistry.registerArena(arena);
    }

    enum LocationType {
        LOBBY, END, START
    }

}
