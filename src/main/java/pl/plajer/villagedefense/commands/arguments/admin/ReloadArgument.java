/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.commands.arguments.admin;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.commands.arguments.data.LabelData;
import pl.plajer.villagedefense.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class ReloadArgument {

  private Set<CommandSender> confirmations = new HashSet<>();

  public ReloadArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefenseadmin", new LabeledCommandArgument("reload", "villagedefense.admin.reload", CommandArgument.ExecutorType.BOTH,
        new LabelData("/vda reload", "/vda reload", "&7Reload all game arenas and configuration files\n&7&lArenas will be stopped!\n&6Permission: &7villagedefense.admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20L * 10);
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix()
              + registry.getPlugin().getChatManager().colorRawMessage("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!"));
          return;
        }
        confirmations.remove(sender);

        registry.getPlugin().reloadConfig();
        LanguageManager.reloadConfig();

        for (Arena arena : ArenaRegistry.getArenas()) {
          for (Player player : arena.getPlayers()) {
            arena.doBarAction(Arena.BarAction.REMOVE, player);
            arena.teleportToEndLocation(player);
            if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(registry.getPlugin(), player);
            } else {
              player.getInventory().clear();
              player.getInventory().setArmorContents(null);
              for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
              }
            }
          }
          ArenaManager.stopGame(true, arena);
        }
        ArenaRegistry.registerArenas();
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage(Messages.COMMANDS_ADMIN_SUCCESS_RELOAD));
      }
    });
  }

}
