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

package pl.plajer.villagedefense.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.commands.arguments.data.CommandArgument;
import pl.plajer.villagedefense.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 24.11.2018
 */
public class SelectKitArgument {

  public SelectKitArgument(ArgumentsRegistry registry) {
    registry.mapArgument("villagedefense", new CommandArgument("selectkit", "villagedefense.command.selectkit", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        registry.getPlugin().getKitMenuHandler().createMenu((Player) sender);
      }
    });
  }

}
