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

package plugily.projects.villagedefense.arena;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.handlers.language.LanguageManager;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
public enum ArenaState {
  WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("In-Game"), ENDING("Ending"), RESTARTING("Restarting");

  private final String formattedName;
  private final String placeholder;

  ArenaState(String formattedName) {
    this.formattedName = formattedName;
    placeholder = JavaPlugin.getPlugin(Main.class).getChatManager()
        .colorRawMessage(LanguageManager.getLanguageMessage("Placeholders.Game-States." + formattedName));
  }

  public String getFormattedName() {
    return formattedName;
  }

  public String getPlaceholder() {
    return placeholder;
  }
}