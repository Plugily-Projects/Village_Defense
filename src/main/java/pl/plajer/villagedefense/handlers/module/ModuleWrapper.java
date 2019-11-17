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

package pl.plajer.villagedefense.handlers.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.plajer.villagedefense.api.module.v1.VillageDefenseModule;

/**
 * @author Plajer
 * <p>
 * Created at 17.06.2019
 */
public class ModuleWrapper {

  private VillageDefenseModule module;
  private LoadStatus loadStatus;
  private Map<String, String> info = new HashMap<>();
  private List<StackTraceElement[]> encounteredExceptions = new ArrayList<>();
  private String mainClassName;

  public ModuleWrapper(VillageDefenseModule module, String mainClassName, LoadStatus loadStatus) {
    this.module = module;
    this.mainClassName = mainClassName;
    this.loadStatus = loadStatus;
  }

  public VillageDefenseModule getModule() {
    return module;
  }

  public String getMainClassName() {
    return mainClassName;
  }

  public LoadStatus getLoadStatus() {
    return loadStatus;
  }

  public void setLoadStatus(LoadStatus loadStatus) {
    this.loadStatus = loadStatus;
  }

  public void applyInfo(String key, String value) {
    info.put(key, value);
  }

  public String getInfo(String key) {
    return info.getOrDefault(key, "");
  }

  public void logException(StackTraceElement[] exception) {
    encounteredExceptions.add(exception);
  }

  public boolean hasLoggedExceptions() {
    return !encounteredExceptions.isEmpty();
  }

  public enum LoadStatus {
    LOADED, INCOMPATIBLE, FAILED_TO_LOAD, AMBIGUOUS
  }

  public enum LogInfoKey {
    INCOMPATIBILITY("modules-incompatibility"), AMBIGUOUS_NAME("ambiguous-name"), LOAD_TIME("load-time"),
    LOAD_EXCEPTION("load-exception");

    private String key;

    LogInfoKey(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

}
