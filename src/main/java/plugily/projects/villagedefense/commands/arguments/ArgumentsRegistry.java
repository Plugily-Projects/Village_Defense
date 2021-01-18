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

package plugily.projects.villagedefense.commands.arguments;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.string.StringMatcher;
import plugily.projects.villagedefense.ConfigPreferences;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.commands.arguments.admin.*;
import plugily.projects.villagedefense.commands.arguments.admin.arena.DeleteArgument;
import plugily.projects.villagedefense.commands.arguments.admin.arena.ForceStartArgument;
import plugily.projects.villagedefense.commands.arguments.admin.arena.SetWaveArgument;
import plugily.projects.villagedefense.commands.arguments.admin.arena.StopArgument;
import plugily.projects.villagedefense.commands.arguments.admin.level.AddLevelArgument;
import plugily.projects.villagedefense.commands.arguments.admin.level.SetLevelArgument;
import plugily.projects.villagedefense.commands.arguments.data.CommandArgument;
import plugily.projects.villagedefense.commands.arguments.data.LabelData;
import plugily.projects.villagedefense.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.villagedefense.commands.arguments.game.*;
import plugily.projects.villagedefense.commands.completion.TabCompletion;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.setup.SetupInventory;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ArgumentsRegistry implements CommandExecutor {

  private final SpyChatArgument spyChat;
  private final Main plugin;
  private final TabCompletion tabCompletion;
  private final Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();

  public ArgumentsRegistry(Main plugin) {
    this.plugin = plugin;
    tabCompletion = new TabCompletion(this);
    Optional.ofNullable(plugin.getCommand("villagedefense")).ifPresent(vd -> {
      vd.setExecutor(this);
      vd.setTabCompleter(tabCompletion);
    });
    Optional.ofNullable(plugin.getCommand("villagedefenseadmin")).ifPresent(vda -> {
      vda.setExecutor(this);
      vda.setTabCompleter(tabCompletion);
    });

    //register Village Defense basic arguments
    new CreateArgument(this);
    new JoinArguments(this);
    new RandomJoinArgument(this);
    new ArenaSelectorArgument(this, plugin.getChatManager());
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
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.HOLOGRAMS_ENABLED)) {
      new HologramArgument(this);
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    for (Map.Entry<String, List<CommandArgument>> entry : mappedArguments.entrySet()) {
      if (!cmd.getName().equalsIgnoreCase(entry.getKey())) {
        continue;
      }
      if (cmd.getName().equalsIgnoreCase("villagedefense")) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
          sendHelpCommand(sender);
          return true;
        }
        if (args.length > 1 && args[1].equalsIgnoreCase("edit")) {
          if (!checkSenderIsExecutorType(sender, CommandArgument.ExecutorType.PLAYER)
              || !Utils.hasPermission(sender, "villagedefense.admin.create")) {
            return true;
          }
          Arena arena = ArenaRegistry.getArena(args[0]);
          if (arena == null) {
            sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.COMMANDS_NO_ARENA_LIKE_THAT));
            return true;
          }

          new SetupInventory(arena, (Player) sender).openInventory();
          return true;
        }
      }
      if (cmd.getName().equalsIgnoreCase("villagedefenseadmin") && (args.length == 0 || args[0].equalsIgnoreCase("help"))) {
        if (!sender.hasPermission("villagedefense.admin")) {
          return true;
        }
        sendAdminHelpCommand(sender);
        return true;
      }
      for (CommandArgument argument : entry.getValue()) {
        if (argument.getArgumentName().equalsIgnoreCase(args[0])) {
          //does it make sense that it is a list?
          for (String perm : argument.getPermissions()) {
            if (perm.isEmpty() || Utils.hasPermission(sender, perm)) {
              break;
            }
            //user has no permission to execute command
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
      List<StringMatcher.Match> matches = StringMatcher.match(args[0], entry.getValue().stream().map(CommandArgument::getArgumentName).collect(Collectors.toList()));
      if (!matches.isEmpty()) {
        sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_DID_YOU_MEAN).replace("%command%", label + " " + matches.get(0).getMatch()));
        return true;
      }
    }
    return false;
  }

  private void sendHelpCommand(CommandSender sender) {
    sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_MAIN_HEADER));
    sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_MAIN_DESCRIPTION));
    if (sender.hasPermission("villagedefense.admin")) {
      sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_MAIN_ADMIN_BONUS_DESCRIPTION));
    }
    sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_MAIN_FOOTER));
  }

  private void sendAdminHelpCommand(CommandSender sender) {
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
    if (ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1)) {
      for (LabelData labelData : data) {
        sender.sendMessage(labelData.getText() + " - " + labelData.getDescription().split("\n")[0]);
      }
      return;
    }
    for (LabelData labelData : data) {
      if (sender instanceof Player) {
        TextComponent component = new TextComponent(labelData.getText());
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, labelData.getCommand()));

        // Backwards compatibility
        if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1)) {
          component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(labelData.getDescription())));
        } else {
          component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(labelData.getDescription())));
        }

        ((Player) sender).spigot().sendMessage(component);
      } else {
        //more descriptive for console - split at \n to show only basic description
        Debugger.sendConsoleMsg(labelData.getText() + " - " + labelData.getDescription().split("\n")[0]);
      }
    }
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
        sender.sendMessage(plugin.getChatManager().colorMessage(Messages.COMMANDS_ONLY_BY_PLAYER));
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
