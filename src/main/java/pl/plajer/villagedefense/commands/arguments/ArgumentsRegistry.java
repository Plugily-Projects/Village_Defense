/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.commands.CommandData;
import pl.plajer.villagedefense.commands.MainCommand;
import pl.plajer.villagedefense.commands.arguments.admin.AddOrbsArgument;
import pl.plajer.villagedefense.commands.arguments.admin.ClearEntitiesArgument;
import pl.plajer.villagedefense.commands.arguments.admin.ListArenasArgument;
import pl.plajer.villagedefense.commands.arguments.admin.RespawnArgument;
import pl.plajer.villagedefense.commands.arguments.admin.SetPriceArgument;
import pl.plajer.villagedefense.commands.arguments.admin.SpyChatArgument;
import pl.plajer.villagedefense.commands.arguments.admin.TeleportArgument;
import pl.plajer.villagedefense.commands.arguments.admin.arena.DeleteArgument;
import pl.plajer.villagedefense.commands.arguments.admin.arena.ForceStartArgument;
import pl.plajer.villagedefense.commands.arguments.admin.arena.ReloadArgument;
import pl.plajer.villagedefense.commands.arguments.admin.arena.SetWaveArgument;
import pl.plajer.villagedefense.commands.arguments.admin.arena.StopArgument;
import pl.plajer.villagedefense.commands.arguments.admin.level.AddLevelArgument;
import pl.plajer.villagedefense.commands.arguments.admin.level.SetLevelArgument;
import pl.plajer.villagedefense.commands.arguments.game.CreateArgument;
import pl.plajer.villagedefense.commands.arguments.game.JoinArguments;
import pl.plajer.villagedefense.commands.arguments.game.LeaderboardArgument;
import pl.plajer.villagedefense.commands.arguments.game.LeaveArgument;
import pl.plajer.villagedefense.commands.arguments.game.SelectKitArgument;
import pl.plajer.villagedefense.commands.arguments.game.StatsArgument;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajerlair.core.utils.StringMatcher;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ArgumentsRegistry extends MainCommand implements CommandExecutor {

  private static List<CommandData> command = new LinkedList<>();

  //todo change me?
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
        gray + "Respawn yourself or target player in game\n" + gold + "Permission: " + gray + "villagedefense.admin.respawn (for yourself)\n"
            + gold + "Permission: " + gray + "villagedefense.admin.respawn.others (for others)"));
    command.add(new CommandData("/vda spychat", "/vda spychat",
        gray + "Toggles spy chat for all available arenas\n" + gray + "You will see all messages from these games\n" + gold + "Permission: "
            + gray + "villagedefense.admin.spychat"));
    command.add(new CommandData("/vda setprice " + gold + "<amount>", "/vda setprice <amount>",
        gray + "Set price of holding item, it's required for game shop\n" + gold + "Permission: " + gray + "villagedefense.admin.setprice"));
    command.add(new CommandData("/vda reload", "/vda reload", gray + "Reload all game arenas\n" + gray + "" + ChatColor.BOLD
        + "They will be stopped!\n" + ChatColor.BOLD + "" + ChatColor.RED + "Not recommended!" + gold + "Permission: " + gray + "villagedefense.admin.reload"));
    command.add(new CommandData("/vda delete " + gold + "<arena>", "/vda delete <arena>",
        gray + "Deletes specified arena\n" + gold + "Permission: " + gray + "villagedefense.admin.delete"));
    command.add(new CommandData("/vda tp " + gold + "<arena> <location type>", "/vda tp <arena> <location>",
        gray + "Teleport you to provided arena location\n" + gray + "Valid locations:\n" + gray + "• LOBBY - lobby location\n" + gray
            + "• START - starting location\n" + gray + "• END - ending location\n" + gold + "Permission: " + gray + "villagedefense.admin.teleport"));
    command.add(new CommandData("/vda clear " + gold + "<zombie/villager/golem>", "/vda clear <mob>",
        gray + "Clear specific mob type from arena you're in\n" + gray + "Valid mob types:\n" + gray + "• ZOMBIE - clear spawned zombies\n"
            + gray + "• VILLAGER - clear alive villagers\n" + gray + "• GOLEM - clear spawned golems\n" + gold + "Permission: " + gray + "villagedefense.admin.clear"));
    command.add(new CommandData("/vda addorbs " + gold + "<amount>" + ChatColor.RED + " [player]", "/vda addorbs <amount>",
        gray + "Add orbs (game currency) to yourself or target player\n" + gray + "Can be used from console too\n" + gold
            + "Permission: " + gray + "villagedefense.admin.addorbs (for yourself)\n" + gold + "Permission: " + gray + "villagedefense.admin.addorbs.others (for others)"));
    command.add(new CommandData("/vda setlevel " + gold + "<amount>" + ChatColor.RED + " [player]", "/vda setlevel <amount>",
        gray + "Set level to yourself or target player\n" + gray + "Can be used from console too\n" + gold
            + "Permission: " + gray + "villagedefense.admin.setlevel (for yourself)\n" + gold + "Permission: " + gray + "villagedefense.admin.setlevel.others (for others)"));
    command.add(new CommandData("/vda addlevel " + gold + "<amount>" + ChatColor.RED + " [player]", "/vda addlevel <amount>",
        gray + "Add level to yourself or target player\n" + gray + "Can be used from console too\n" + gold
            + "Permission: " + gray + "villagedefense.admin.addlevel (for yourself)\n" + gold + "Permission: " + gray + "villagedefense.admin.addlevel.others (for others)"));
    command.add(new CommandData("/vda setwave " + gold + "<number>", "/vda setwave <num>",
        gray + "Set wave number in arena you're in\n" + gold + "Permission: " + gray + "villagedefense.admin.setwave"));
  }

  private Main plugin;
  private Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();

  public ArgumentsRegistry(Main plugin) {
    super(plugin, false);
    this.plugin = plugin;

    //register Village Defense basic arguments
    new CreateArgument(this);
    new JoinArguments(this);
    new LeaderboardArgument(this);
    new LeaveArgument(this);
    new SelectKitArgument(this);
    new StatsArgument(this);

    //register Village Defense admin arguments
    //arena related arguments
    new DeleteArgument(this);
    new ForceStartArgument(this);
    new ReloadArgument(this);
    new SetWaveArgument(this);
    new StopArgument(this);

    //player level related arguments
    new AddLevelArgument(this);
    new SetLevelArgument(this);

    //other admin related arguments
    new AddOrbsArgument(this);
    new ClearEntitiesArgument(this);
    new ListArenasArgument(this);
    new RespawnArgument(this);
    new SetPriceArgument(this);
    new SpyChatArgument(this);
    new TeleportArgument(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    for (String mainCommand : mappedArguments.keySet()) {
      if (cmd.getName().equalsIgnoreCase(mainCommand)) {
        //todo get rid of this
        if (cmd.getName().equalsIgnoreCase("villagedefense")) {
          if (args.length == 0) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Header"));
            sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Description"));
            if (sender.hasPermission("villagedefense.admin")) {
              sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
            }
            sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Footer"));
            return true;
          }
          if (args.length > 1) {
            if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("addspawn") || args[1].equalsIgnoreCase("edit")) {
              if (!checkSenderPlayer(sender) || !hasPermission(sender, "villagedefense.admin.create")) {
                return true;
              }
              plugin.getMainCommand().performSetup((Player) sender, args);
              return true;
            }
          }
        }
        //todo get rid of this
        if (cmd.getName().equalsIgnoreCase("villagedefenseadmin")) {
          if (args.length == 0) {
            if (!sender.hasPermission("villagedefense.admin")) {
              return true;
            }
            sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "Village Defense " + ChatColor.GRAY + plugin.getDescription().getVersion());
            if (checkSenderPlayer(sender)) {
              sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
              sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
              for (CommandData data : command) {
                TextComponent component = new TextComponent(data.getText());
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getCommand()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.getDescription()).create()));
                ((Player) sender).spigot().sendMessage(component);
              }
              return true;
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
            sender.sendMessage(ChatColor.WHITE + "/vda setlevel " + ChatColor.GOLD + "<amount> " + ChatColor.RED + "[player]" + ChatColor.GRAY + ": Set yourself or player the given amount of level");
            sender.sendMessage(ChatColor.WHITE + "/vda addlevel " + ChatColor.GOLD + "<amount> " + ChatColor.RED + "[player]" + ChatColor.GRAY + ": Give yourself or player the given amount of level");
            sender.sendMessage(ChatColor.WHITE + "/vda setwave " + ChatColor.GOLD + "<number>" + ChatColor.GRAY + ": Set the wave number");
            return true;
          }
        }
        for (CommandArgument argument : mappedArguments.get(mainCommand)) {
          if (argument.getArgumentName().equals(args[0])) {
            boolean hasPerm = false;
            for (String perm : argument.getPermissions()) {
              if (sender.hasPermission(perm)) {
                hasPerm = true;
                break;
              }
            }
            if (!hasPerm) {
              return true;
            }
            if (checkSenderIsExecutorType(sender, argument.getValidExecutors())) {
              argument.execute(sender, args);
            }
            //return true even if sender is not good executor or hasn't got permission
            return true;
          }
        }
        //todo change this
        if (cmd.getName().equalsIgnoreCase("villagedefense")) {
          List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("join", "leave", "stats", "top", "create", "selectkit"));
          if (!matches.isEmpty()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replace("%command%", "vd " + matches.get(0).getMatch()));
          }
          return true;
        }
        if (cmd.getName().equalsIgnoreCase("villagedefenseadmin")) {
          List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("stop", "list", "forcestart", "respawn", "spychat",
              "reload", "setshopchest", "delete", "setprice", "tp", "clear", "addorbs", "setlevel", "addlevel", "setwave"));
          if (!matches.isEmpty()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replace("%command%", "vda " + matches.get(0).getMatch()));
          }
        }
      }
    }
    return false;
  }

  private boolean checkSenderIsExecutorType(CommandSender sender, CommandArgument.ExecutorType type) {
    switch (type) {
      case BOTH:
        return sender instanceof ConsoleCommandSender || sender instanceof Player;
      case CONSOLE:
        return sender instanceof ConsoleCommandSender;
      case PLAYER:
        if (sender instanceof Player) {
          return true;
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
        return false;
      default:
        return false;
    }
  }

  /**
   * Maps new argument to the main command
   *
   * @param mainCommand mother command ex. /mm
   * @param argument    argument to map ex. leave (for /mm leave)
   */
  public void mapArgument(String mainCommand, CommandArgument argument) {
    List<CommandArgument> args = mappedArguments.getOrDefault(mainCommand, new ArrayList<>());
    args.add(argument);
    mappedArguments.put(mainCommand, args);
  }

  public Main getPlugin() {
    return plugin;
  }
}
