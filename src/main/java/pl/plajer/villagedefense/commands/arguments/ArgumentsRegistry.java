/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import pl.plajer.villagedefense.arena.ArenaRegistry;
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
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.commands.arguments.game.CreateArgument;
import pl.plajer.villagedefense.commands.arguments.game.JoinArguments;
import pl.plajer.villagedefense.commands.arguments.game.LeaderboardArgument;
import pl.plajer.villagedefense.commands.arguments.game.LeaveArgument;
import pl.plajer.villagedefense.commands.arguments.game.SelectKitArgument;
import pl.plajer.villagedefense.commands.arguments.game.StatsArgument;
import pl.plajer.villagedefense.commands.completion.TabCompletion;
import pl.plajer.villagedefense.handlers.setup.SetupInventory;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.StringMatcher;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ArgumentsRegistry implements CommandExecutor {

  private SpyChatArgument spyChat;
  private Main plugin;
  private TabCompletion tabCompletion;
  private Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();

  public ArgumentsRegistry(Main plugin) {
    this.plugin = plugin;
    tabCompletion = new TabCompletion(this);
    plugin.getCommand("villagedefense").setExecutor(this);
    plugin.getCommand("villagedefense").setTabCompleter(tabCompletion);
    plugin.getCommand("villagedefenseadmin").setExecutor(this);
    plugin.getCommand("villagedefenseadmin").setTabCompleter(tabCompletion);

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
    spyChat = new SpyChatArgument(this);
    new TeleportArgument(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      for (String mainCommand : mappedArguments.keySet()) {
        if (cmd.getName().equalsIgnoreCase(mainCommand)) {
          if (cmd.getName().equalsIgnoreCase("villagedefense")) {
            if (args.length == 0) {
              sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Header"));
              sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Description"));
              if (sender.hasPermission("villagedefense.admin")) {
                sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
              }
              sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Footer"));
              return true;
            }
            if (args.length > 1 && args[1].equalsIgnoreCase("edit")) {
              if (!checkSenderIsExecutorType(sender, CommandArgument.ExecutorType.PLAYER)
                  || !Utils.hasPermission(sender, "villagedefense.admin.create")) {
                return true;
              }
              if (ArenaRegistry.getArena(args[0]) == null) {
                sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Arena-Like-That"));
                return true;
              }

              SetupInventory.sendProTip((Player) sender);
              new SetupInventory(ArenaRegistry.getArena(args[0])).openInventory((Player) sender);
            }
          }
          if (cmd.getName().equalsIgnoreCase("villagedefenseadmin")) {
            if (args.length == 0) {
              if (!sender.hasPermission("villagedefense.admin")) {
                return true;
              }
              sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "Village Defense " + ChatColor.GRAY + plugin.getDescription().getVersion());
              sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
              if (sender instanceof Player) {
                sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
              }
              List<LabelData> data = mappedArguments.get("villagedefenseadmin").stream().filter(arg -> arg instanceof LabeledCommandArgument)
                  .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList());
              data.add(new LabelData("/vd &6<arena>&f edit", "/vd <arena> edit",
                  "&7Edit existing arena\n&6Permission: &7villagedefense.admin.edit"));
              data.addAll(mappedArguments.get("villagedefense").stream().filter(arg -> arg instanceof LabeledCommandArgument)
                  .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList()));
              for (LabelData labelData : data) {
                TextComponent component;
                if (sender instanceof Player) {
                  component = new TextComponent(labelData.getText());
                } else {
                  //more descriptive for console - split at \n to show only basic description
                  component = new TextComponent(labelData.getText() + " - " + labelData.getDescription().split("\n")[0]);
                }
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, labelData.getCommand()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(labelData.getDescription()).create()));
                sender.spigot().sendMessage(component);
              }
              return true;
            }
          }
          for (CommandArgument argument : mappedArguments.get(mainCommand)) {
            if (argument.getArgumentName().equalsIgnoreCase(args[0])) {
              boolean hasPerm = false;
              for (String perm : argument.getPermissions()) {
                if (perm.equals("")) {
                  hasPerm = true;
                  break;
                }
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

          //sending did you mean help
          List<StringMatcher.Match> matches = StringMatcher.match(args[0], mappedArguments.get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName).collect(Collectors.toList()));
          if (!matches.isEmpty()) {
            sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Did-You-Mean").replace("%command%", label + " " + matches.get(0).getMatch()));
            return true;
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
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
        sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Only-By-Player"));
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

  public Map<String, List<CommandArgument>> getMappedArguments() {
    return mappedArguments;
  }

  public Main getPlugin() {
    return plugin;
  }

  public TabCompletion getTabCompletion() {
    return tabCompletion;
  }

  public SpyChatArgument getSpyChat() {
    return spyChat;
  }
}
