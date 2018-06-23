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

package pl.plajer.villagedefense3.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaManager;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.ArenaState;
import pl.plajer.villagedefense3.arena.ArenaUtils;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 25.02.2018
 */
public class AdminCommands extends MainCommand {

    private static List<CommandData> command = new LinkedList<>();

    static {
        ChatColor gray = ChatColor.GRAY;
        ChatColor gold = ChatColor.GOLD;
        command.add(new CommandData("/vd create " + gold + "<arena>", "/vd create <arena>",
                gray + "Create new arena\n" + gold + "Permission: " + gray + "villagedefense.admin.create"));
        command.add(new CommandData("/vd " + gold + "<arena>" + ChatColor.WHITE + " edit", "/vd <arena> edit",
                gray + "Edit existing arena\n" + gold + "Permission: " + gray + "villagedefense.admin.edit"));
        command.add(new CommandData("/vda list", "/vda list",
                gray + "Shows list with all loaded arenas\n" + gold + "Permission: " + gray + "villagedefense.admin.list"));
        command.add(new CommandData("/vda stop", "/vda stop",
                gray + "Stops the arena you're in\n" + gray + "" + ChatColor.BOLD + "You must be in target arena!\n" + gold + "Permission: " + gray + "villagedefense.admin.stop"));
        command.add(new CommandData("/vda forcestart", "/vda forcestart",
                gray + "Force starts arena you're in\n" + gold + "Permission: " + gray + "villagedefense.admin.forcestart"));
        command.add(new CommandData("/vda respawn " + ChatColor.RED + "[player]", "/vda respawn",
                gray + "Respawn yourself or target player in game\n" + gold + "Permission: " + gray + "villagedefense.admin.respawn (for yourself)\n" +
                        gold + "Permission: " + gray + "villagedefense.admin.respawn.others (for others)"));
        command.add(new CommandData("/vda spychat", "/vda spychat",
                gray + "Toggles spy chat for all available arenas\n" + gray + "You will see all messages from these games\n" + gold + "Permission: " +
                        gray + "villagedefense.admin.spychat"));
        command.add(new CommandData("/vda setprice " + gold + "<amount>", "/vda setprice <amount>",
                gray + "Set price of holding item, it's required for game shop\n" + gold + "Permission: " + gray + "villagedefense.admin.setprice"));
        command.add(new CommandData("/vda reload", "/vda reload", gray + "Reload all game arenas\n" + gray + "" + ChatColor.BOLD +
                "They will be stopped!\n" + gold + "Permission: " + gray + "villagedefense.admin.reload"));
        command.add(new CommandData(ChatColor.STRIKETHROUGH + "/vda addsign " + gold + "<arena>", "/vda addsign <arena>",
                gray + "Set sign you look at as a target arena sign\n" + gold + "Permission: " + gray + "villagedefense.admin.addsign\n" +
                        gold + "Permission: " + gray + "villagedefense.admin.sign.create (for creating signs manually)\n" + gold + "Permission: " +
                        gray + "villagedefense.admin.sign.break (for breaking arena signs)\n" + ChatColor.BOLD + "" + ChatColor.RED + "Deprecated since 3.6.4, use Setup menu instead"));
        command.add(new CommandData("/vda delete " + gold + "<arena>", "/vda delete <arena>",
                gray + "Deletes specified arena\n" + gold + "Permission: " + gray + "villagedefense.admin.delete"));
        command.add(new CommandData("/vda tp " + gold + "<arena> <location type>", "/vda tp <arena> <location>",
                gray + "Teleport you to provided arena location\n" + gray + "Valid locations:\n" + gray + "• LOBBY - lobby location\n" + gray +
                        "• START - starting location\n" + gray + "• END - ending location\n" + gold + "Permission: " + gray + "villagedefense.admin.teleport"));
        command.add(new CommandData("/vda clear " + gold + "<zombie/villager/golem>", "/vda clear <mob>",
                gray + "Clear specific mob type from arena you're in\n" + gray + "Valid mob types:\n" + gray + "• ZOMBIE - clear spawned zombies\n" +
                        gray + "• VILLAGER - clear alive villagers\n" + gray + "• GOLEM - clear spawned golems\n" + gold + "Permission: " + gray + "villagedefense.admin.clear"));
        command.add(new CommandData("/vda addorbs " + gold + "<amount>" + ChatColor.RED + " [player]", "/vda addorbs <amount>",
                gray + "Add orbs (game currency) to yourself or target player\n" + gray + "Can be used from console too\n" + gold +
                        "Permission: " + gray + "villagedefense.admin.addorbs (for yourself)\n" + gold + "Permission: " + gray + "villagedefense.admin.addorbs.others (for others)"));
        command.add(new CommandData("/vda setwave " + gold + "<number>", "/vda setwave <num>",
                gray + "Set wave number in arena you're in\n" + gold + "Permission: " + gray + "villagedefense.admin.setwave"));
    }

    private Main plugin;

    public AdminCommands(Main plugin) {
        super(plugin, false);
        this.plugin = plugin;
    }

    public void sendHelp(CommandSender sender) {
        if(!sender.hasPermission("villagedefense.admin")) return;
        sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "Village Defense " + ChatColor.GRAY + plugin.getDescription().getVersion());
        if(!checkSenderIsConsole(sender)) {
            sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
            sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
            for(CommandData data : command) {
                TextComponent component = new TextComponent(data.getText());
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getCommand()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.getDescription()).create()));
                ((Player) sender).spigot().sendMessage(component);
            }
            return;
        }
        //must be updated manually
        sender.sendMessage(ChatColor.WHITE + "/vd create " + ChatColor.GOLD + "<arena>" + ChatColor.GRAY + ": Create new arena");
        sender.sendMessage(ChatColor.WHITE + "/vd " + ChatColor.GOLD + "<arena>" + ChatColor.WHITE + " edit" + ChatColor.GRAY + ": Edit existing arena");
        sender.sendMessage(ChatColor.WHITE + "/vda list" + ChatColor.GRAY + ": Print all loaded instances");
        sender.sendMessage(ChatColor.WHITE + "/vda stop" + ChatColor.GRAY + ": Stop the arena");
        sender.sendMessage(ChatColor.WHITE + "/vda forcestart" + ChatColor.GRAY + ": Force start the arena");
        sender.sendMessage(ChatColor.WHITE + "/vda respawn " + ChatColor.RED + "[player]" + ChatColor.GRAY + ": Respawn yourself or target player");
        sender.sendMessage(ChatColor.WHITE + "/vda spychat" + ChatColor.GRAY + ": Toggle all games chat visibility (only multi-arena)");
        sender.sendMessage(ChatColor.WHITE + "/vda setprice " + ChatColor.GOLD + "<amount>" + ChatColor.GRAY + ": Sets holding item price (for shop)");
        sender.sendMessage(ChatColor.WHITE + "/vda reload" + ChatColor.GRAY + ": Stops and reloads all game instances");
        sender.sendMessage(ChatColor.WHITE + "/vda delete " + ChatColor.GOLD + "<arena>" + ChatColor.GRAY + ": Remove existing arena");
        sender.sendMessage(ChatColor.WHITE + "/vda tp " + ChatColor.GOLD + "<arena> <location type>" + ChatColor.GRAY + ": Teleport you to provided arena location");
        sender.sendMessage(ChatColor.WHITE + "/vda clear " + ChatColor.GOLD + "<zombie/villager/golem>" + ChatColor.GRAY + ": Remove target mob type from your arena");
        sender.sendMessage(ChatColor.WHITE + "/vda addorbs " + ChatColor.GOLD + "<amount> " + ChatColor.RED + "[player]" + ChatColor.GRAY + ": Give yourself or player the given amount of orbs");
        sender.sendMessage(ChatColor.WHITE + "/vda setwave " + ChatColor.GOLD + "<number>" + ChatColor.GRAY + ": Set the wave number");
    }

    public void printList(CommandSender sender) {
        if(!hasPermission(sender, "villagedefense.admin.list")) return;
        sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for(Arena arena : ArenaRegistry.getArenas()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replaceAll("%arena%", arena.getID())
                    .replaceAll("%status%", arena.getArenaState().getFormattedName()).replaceAll("%players%", String.valueOf(arena.getPlayers().size()))
                    .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
            i++;
        }
        if(i == 0) sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
    }

    public void stopGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.stopgame")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        ArenaManager.stopGame(false, ArenaRegistry.getArena((Player) sender));
    }

    public void forceStartGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.forcestart")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
            arena.setArenaState(ArenaState.STARTING);
            arena.setTimer(0);
            for(Player p : ArenaRegistry.getArena((Player) sender).getPlayers()) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
            }
        }
    }

    public void respawn(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!hasPermission(player, "villagedefense.admin.respawn")) return;
        if(!checkIsInGameInstance(player)) return;
        Arena arena = ArenaRegistry.getArena(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        User user = UserManager.getUser(player.getUniqueId());
        user.setFakeDead(false);
        user.setSpectator(false);
        arena.teleportToStartLocation(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        ArenaUtils.showPlayer(player, arena);
        player.getInventory().clear();
        user.getKit().giveKitItems(player);
        player.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
    }

    public void respawnOther(CommandSender sender, String player) {
        if(checkSenderIsConsole(sender)) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.respawn.others")) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        for(Player loopPlayer : arena.getPlayers()) {
            if(player.equalsIgnoreCase(loopPlayer.getName())) {
                loopPlayer.setGameMode(GameMode.SURVIVAL);
                loopPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
                User user = UserManager.getUser(loopPlayer.getUniqueId());
                user.setFakeDead(false);
                user.setSpectator(false);

                sender.sendMessage(ChatColor.GREEN + "Player respawned!");
                arena.teleportToStartLocation(loopPlayer);
                loopPlayer.setFlying(false);
                loopPlayer.setAllowFlight(false);
                ArenaUtils.showPlayer(loopPlayer, arena);
                loopPlayer.getInventory().clear();
                user.getKit().giveKitItems(loopPlayer);
                loopPlayer.sendMessage(ChatManager.colorMessage("In-Game.Back-In-Game"));
                return;
            }
        }
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
    }

    public void toggleSpyChat(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.spychat")) return;
        boolean bool = !plugin.getSpyChatEnabled().getOrDefault(((Player) sender).getUniqueId(), false);
        plugin.getSpyChatEnabled().put(((Player) sender).getUniqueId(), bool);
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.GREEN + "Game spy chat toggled to " + bool);
    }

    public void reloadInstances(CommandSender sender) {
        if(!hasPermission(sender, "villagedefense.admin.reload")) return;
        ArenaRegistry.registerArenas();
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Success-Reload"));
    }

    public void setShopChest(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Please use Setup menu to set shop chest!");
    }

    public void addSign(CommandSender sender, String arena) {
        if(checkSenderIsConsole(sender)) return;
        Player player = (Player) sender;
        if(!hasPermission(sender, "villagedefense.admin.addsign")) return;
        if(ArenaRegistry.getArena(arena) == null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
        } else {
            Location location = player.getTargetBlock(null, 10).getLocation();
            if(location.getBlock().getState() instanceof Sign) {
                plugin.getSignManager().getLoadedSigns().put((Sign) location.getBlock().getState(), ArenaRegistry.getArena(arena));
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
                String loc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
                FileConfiguration config = ConfigurationManager.getConfig("arenas");
                List<String> locs = config.getStringList("instances." + arena + ".signs");
                locs.add(loc);
                config.set("instances." + arena + ".signs", locs);
                ConfigurationManager.saveConfig(config, "arenas");
            } else {
                player.sendMessage(ChatManager.colorMessage("Commands.Look-Sign"));
            }
        }
    }

    public void deleteArena(CommandSender sender, String arenaString) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.delete")) return;
        Arena arena = ArenaRegistry.getArena(arenaString);
        if(arena == null) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        ArenaManager.stopGame(false, arena);
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        config.set("instances." + arenaString, null);
        ConfigurationManager.saveConfig(config, "arenas");
        ArenaRegistry.unregisterArena(arena);
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Removed-Game-Instance"));
    }

    public void setItemPrice(CommandSender sender, String price) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.setprice")) return;
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        if(item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatManager.colorMessage("Commands.Hold-Any-Item"));
            return;
        }
        //check any price from lore
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().getLore();
            for(String search : lore) {
                if(search.contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
                    lore.remove(search);
                    break;
                }
            }
            lore.add(0, ChatColor.GOLD + price + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Command-Executed-Item-Updated"));
        } else {
            Utils.addLore(item, ChatColor.GOLD + price + " " + ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"));
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Command-Executed"));
        }
    }

    public void teleportToInstance(CommandSender sender, String arenaString, String locationType) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.teleport")) return;
        Player player = (Player) sender;
        try {
            LocationType.valueOf(locationType.toUpperCase());
        } catch(Exception e) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Location-Teleport-Invalid"));
            return;
        }
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getID().equalsIgnoreCase(arenaString)) {
                super.onTpCommand(player, arenaString, LocationType.valueOf(locationType.toUpperCase()));
            }
        }
    }

    public void clearZombies(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena.getZombies() != null) {
            for(Zombie zombie : arena.getZombies()) {
                zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                zombie.remove();
            }
            arena.getZombies().clear();
        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH");
        for(Player loopPlayer : arena.getPlayers()) {
            String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
    }

    public void clearVillagers(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena.getVillagers() != null) {
            for(Villager villager : arena.getVillagers()) {
                villager.getWorld().playEffect(villager.getLocation(), Effect.LAVA_POP, 20);
                villager.remove();
            }
            arena.getVillagers().clear();
        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_VILLAGER_DEATH", "VILLAGER_DEATH");
        for(Player loopPlayer : arena.getPlayers()) {
            String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Villagers"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
    }

    public void clearGolems(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.clear")) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena.getIronGolems() != null) {
            for(IronGolem golem : arena.getIronGolems()) {
                golem.getWorld().playEffect(golem.getLocation(), Effect.LAVA_POP, 20);
                golem.remove();
            }
            arena.getIronGolems().clear();

        } else {
            sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
            return;
        }
        sendSound((Player) sender, "ENTITY_IRONGOLEM_DEATH", "IRONGOLEM_DEATH");
        for(Player loopPlayer : arena.getPlayers()) {
            String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Golems"), new Player[]{(loopPlayer)});
            loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
    }

    public void addOrbs(CommandSender sender, String number) {
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.addorbs")) return;
        if(NumberUtils.isNumber(number)) {
            User user = UserManager.getUser(((Player) sender).getUniqueId());
            user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(number));
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Added-Orbs"));
        } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Wrong-Usage").replace("%correct%", "/vd addorbs <amount>"));
        }
    }

    public void addOrbsOther(CommandSender sender, String p, String number) {
        //check only target player, maybe sender would do this from console.
        if(Bukkit.getPlayerExact(p) == null) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Target-Player-Not-Found"));
        }
        Player player = Bukkit.getPlayer(p);
        if(!checkIsInGameInstance(player)) return;
        if(!hasPermission(sender, "villagedefense.admin.addorbs.others")) return;
        if(NumberUtils.isNumber(number)) {
            User user = UserManager.getUser(player.getUniqueId());
            user.setInt("orbs", user.getInt("orbs") + Integer.parseInt(number));
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Added-Orbs"));
        } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Wrong-Usage").replace("%correct%", "/vd addorbs <amount>"));
        }
    }

    public void createArena(CommandSender sender, String[] args) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.create")) return;
        createArenaCommand((Player) sender, args);
    }

    public void setWave(CommandSender sender, String number) {
        if(checkSenderIsConsole(sender)) return;
        if(!checkIsInGameInstance((Player) sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.setwave")) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(NumberUtils.isNumber(number)) {
            arena.setWave(Integer.parseInt(number) - 1);
            ArenaManager.endWave(arena);
            String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Changed-Wave"), arena.getWave());
            for(Player player1 : arena.getPlayers()) {
                player1.sendMessage(ChatManager.PLUGIN_PREFIX + message);
            }
            if(arena.getZombies() != null) {
                for(Zombie zombie : arena.getZombies()) {
                    zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                    zombie.remove();
                }
                arena.getZombies().clear();
            } else {
                sender.sendMessage(ChatManager.colorMessage("Kits.Cleaner.Nothing-To-Clean"));
                return;
            }
            sendSound((Player) sender, "ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH");
            for(Player loopPlayer : arena.getPlayers()) {
                String message1 = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Removed-Zombies"), new Player[]{(loopPlayer)});
                loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + message1);
            }
        } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Number").replace("%correct%", "/villagedefense set wave <number>"));
        }
    }

    public void performSetup(CommandSender sender, String[] args) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "villagedefense.admin.setup")) return;
        performSetup((Player) sender, args);
    }

}
