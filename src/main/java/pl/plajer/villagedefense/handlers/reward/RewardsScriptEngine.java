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

package pl.plajer.villagedefense.handlers.reward;

import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;

/**
 * @author Plajer
 * <p>
 * Created at 23.11.2018
 */
//todo explain it
public class RewardsScriptEngine {

  private ScriptEngine scriptEngine;

  public RewardsScriptEngine() {
    scriptEngine = new ScriptEngineManager().getEngineByName("js");
  }

  public void setValue(String value, Object valueObject) {
    scriptEngine.put(value, valueObject);
  }

  public void execute(String executable) {
    try {
      scriptEngine.eval(executable);
    } catch (ScriptException e) {
      Bukkit.getLogger().log(Level.SEVERE, "Script failed to parse expression from rewards.yml! Expression was written wrongly!");
      Bukkit.getLogger().log(Level.SEVERE, "Expression value: " + executable);
      Bukkit.getLogger().log(Level.SEVERE, "Error log:");
      e.printStackTrace();
      Bukkit.getLogger().log(Level.SEVERE, "---- THIS IS AN ISSUE BY USER CONFIGURATION NOT AUTHOR BUG ----");
    }
  }

}
