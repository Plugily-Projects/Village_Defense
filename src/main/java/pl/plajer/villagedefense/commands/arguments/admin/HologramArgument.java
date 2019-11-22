package pl.plajer.villagedefense.commands.arguments.admin;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.handlers.hologram.LeaderboardHologram;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import pl.plajerlair.commonsbox.number.NumberUtils;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class HologramArgument {

 private ArgumentsRegistry registry;

  public HologramArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    registry.getPlugin().getArgumentsRegistry().mapArgument("villagedefenseadmin", new LabeledCommandArgument("hologram", "villagedefense.admin.hologram.manage", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/vda hologram &6<action>", "/vda hologram <action>", "&7Command handles 3 arguments:\n&7• /vda hologram add <statistic type> <amount> - creates new hologram"
            + "of target statistic\n&7with top X amount of players (max 20)\n&7• /vda hologram remove <id> - removes hologram of target ID\n"
            + "&7• /vda hologram list - prints list of all leaderboard holograms")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cToo few arguments! Please type /vda hologram <add/remove/list>"));
          return;
        }
        if (args[1].equalsIgnoreCase("add")) {
          handleAddArgument((Player) sender, args);
        } else if (args[1].equalsIgnoreCase("list")) {
          handleListArgument(sender);
        } else if(args[1].equalsIgnoreCase("remove")) {
          handleDeleteArgument(sender, args);
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cBad arguments! Please type /vda hologram <add/remove/list>"));
        }
      }
    });
  }

  private void handleAddArgument(Player player, String[] args) {
    StatsStorage.StatisticType statistic;
    try {
      statistic = StatsStorage.StatisticType.valueOf(args[2].toUpperCase());
      if (!statistic.isPersistent()) {
        sendInvalidStatisticMessage(player);
        return;
      }
    } catch (Exception ex) {
      sendInvalidStatisticMessage(player);
      return;
    }
    if (args.length != 4) {
      player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cToo few arguments! Please type /vda hologram add <statistic type> <amount>"));
      return;
    }
    if (!NumberUtils.isInteger(args[3])) {
      player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cLeaderboard amount entries must be a number!"));
      return;
    }
    int amount = Integer.parseInt(args[3]);
    if (amount <= 0 || amount > 20) {
      player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cLeaderboard amount entries amount are limited to 20 and minimum of 0!"));
      return;
    }

    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/holograms_data");
    int nextValue = config.getConfigurationSection("holograms").getKeys(false).size() + 1;
    config.set("holograms." + nextValue + ".statistic", statistic.name());
    config.set("holograms." + nextValue + ".top-amount", amount);
    config.set("holograms." + nextValue + ".location", LocationSerializer.locationToString(player.getLocation()));
    ConfigUtils.saveConfig(registry.getPlugin(), config, "internal/holograms_data");

    LeaderboardHologram leaderboard = new LeaderboardHologram(nextValue, statistic, amount, player.getLocation());
    leaderboard.initHologram(registry.getPlugin());
    registry.getPlugin().getHologramsRegistry().registerHologram(leaderboard);

    player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&aHologram with ID " + nextValue + " with statistic " + statistic.name() + " added!"));
  }

  private void sendInvalidStatisticMessage(Player player) {
    StringBuilder values = new StringBuilder();
    for (StatsStorage.StatisticType value : StatsStorage.StatisticType.values()) {
      values.append(value).append(" ");
    }
    player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cInvalid statistic type! Valid types: &e" + values));
  }

  private void handleListArgument(CommandSender sender) {
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/holograms_data");
    for (String key : config.getConfigurationSection("holograms").getKeys(false)) {
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&aID " + key));
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage(" &eTop: " + config.getInt("holograms." + key + ".top-amount")
       + " Stat: " + config.getStringList("holograms." + key + ".statistic")));
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage(" &eLocation: " + getFriendlyLocation(LocationSerializer.getLocation(config.getString("holograms." + key + ".location")))));
    }
  }

  private String getFriendlyLocation(Location location) {
    return "World: " + location.getWorld().getName() + ", X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
  }

  private void handleDeleteArgument(CommandSender sender, String[] args) {
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/holograms_data");
    if(args.length != 3) {
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cPlease type leaderboard ID to remove it!"));
      return;
    }
    if(!NumberUtils.isInteger(args[2])) {
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cLeaderboard ID must be a number!"));
      return;
    }
    if(!config.isSet("holograms." + args[2])) {
      sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&cLeaderboard with that ID doesn't exist!"));
      return;
    }
    config.set("holograms." + args[2], null);
    ConfigUtils.saveConfig(registry.getPlugin(), config, "internal/holograms_data");
    registry.getPlugin().getHologramsRegistry().disableHologram(Integer.parseInt(args[2]));
    sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&aLeaderboard with ID " + args[2] + " sucessfully deleted!"));
  }

}
