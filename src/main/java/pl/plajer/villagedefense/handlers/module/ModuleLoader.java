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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.module.ModuleCompatibility;
import pl.plajer.villagedefense.api.module.ModuleHelper;
import pl.plajer.villagedefense.api.module.v1.VillageDefenseModule;

/**
 * @author Plajer
 * <p>
 * Created at 16.06.2019
 */
public class ModuleLoader {

  public static final ModuleCompatibility CURRENT_COMPATIBILITY_VERSION = ModuleCompatibility.API_V1;
  private List<ModuleWrapper> modules = new ArrayList<>();
  private List<String> modulesClassesNames = new ArrayList<>();
  private int notLoadedModules = 0;
  private Main plugin;

  public ModuleLoader(Main plugin) {
    this.plugin = plugin;
    plugin.getLogger().log(Level.INFO, "Attempting to load modules!");
    loadModules();
  }

  private void loadModules() {
    File dir = new File(plugin.getDataFolder() + "/modules");
    if (!dir.exists() || !dir.isDirectory()) {
      if (!dir.mkdir()) {
        plugin.getLogger().log(Level.WARNING, "Couldn't create modules folder! Skipping modules registration!");
        return;
      }
    }

    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      if (!file.getName().endsWith(".jar")) {
        continue;
      }
      loadModule(file);
    }

    ensureModulesCompatibility();
    enableModules();
  }

  private void loadModule(File file) {
    try {
      loadModuleSafely(file);
    } catch (Throwable thr) {
      plugin.getLogger().log(Level.WARNING, "Failed to load module {0}, error {1} {2}", new Object[] {file.getName(), thr.getCause(), thr.getMessage()});
    }
  }

  private void loadModuleSafely(File file) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    try (JarFile jarFile = new JarFile(file)) {
      Enumeration<JarEntry> entries = jarFile.entries();
      URL[] urls = {new URL("jar:file:" + file.getPath() + "!/")};
      try (URLClassLoader classLoader = URLClassLoader.newInstance(urls, plugin.getClass().getClassLoader())) {
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          Bukkit.broadcastMessage(entry.getName() + " ENTRY DETECT");
          if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
            continue;
          }
          // -6 because of .class
          String className = entry.getName().substring(0, entry.getName().length() - 6);
          className = className.replace('/', '.');
          Class clazz = Class.forName(className, true, classLoader);
          if (!VillageDefenseModule.class.isAssignableFrom(clazz)) {
            continue;
          }
          loadModuleInitializer(clazz);
        }
      }
    }
  }

  private void loadModuleInitializer(Class clazz) throws IllegalAccessException, InstantiationException {
    long start = System.currentTimeMillis();
    VillageDefenseModule module = (VillageDefenseModule) clazz.newInstance();
    if (!ensureCompatibility(module)) {
      return;
    }
    if (isModulePresent(module.getModuleName())) {
      plugin.getLogger().log(Level.WARNING, "Module with name {0} is already registered! Skipping registration of another one...", new Object[] {module.getModuleName()});
      ModuleWrapper moduleInfo = new ModuleWrapper(module, clazz.getName(), ModuleWrapper.LoadStatus.AMBIGUOUS);
      moduleInfo.applyInfo(ModuleWrapper.LogInfoKey.AMBIGUOUS_NAME.getKey(), module.getModuleName());
      modules.add(moduleInfo);
      return;
    }
    try {
      for (String filename : module.getProvidedResources()) {
        InputStream stream = module.getClass().getClassLoader().getResourceAsStream(filename);
        ModuleHelper.createFileInPluginDirectory(stream, filename);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ModuleWrapper moduleInfo = new ModuleWrapper(module, clazz.getName(), ModuleWrapper.LoadStatus.LOADED);
    modulesClassesNames.add(clazz.getName());
    moduleInfo.applyInfo(ModuleWrapper.LogInfoKey.LOAD_TIME.getKey(), System.currentTimeMillis() - start + "ms");
    modules.add(moduleInfo);
  }

  private boolean ensureCompatibility(VillageDefenseModule module) {
    boolean supported = false;
    for (ModuleCompatibility compatibility : module.getCompatibleVersions()) {
      if (compatibility.getVersion() == CURRENT_COMPATIBILITY_VERSION.getVersion()) {
        supported = true;
        break;
      }
    }
    if (!supported) {
      plugin.getLogger().log(Level.WARNING, "Outdated module detected! Module {0} is not compatible with current version {1}! Skipping...", new Object[] {module.getModuleName(),
          CURRENT_COMPATIBILITY_VERSION.getVersion()});
      notLoadedModules++;
      return false;
    }
    return true;
  }

  private void ensureModulesCompatibility() {
    int disabled = 0;
    for (ModuleWrapper moduleInfo : modules) {
      for (String moduleName : moduleInfo.getModule().incompatibleModules()) {
        if (isModulePresent(moduleName)) {
          plugin.getLogger().log(Level.WARNING, "Modules incompatibility detected! Module {0} is not compatible with module {1}, disabling module {0}",
              new Object[] {moduleInfo.getModule().getModuleName(), moduleName});
          moduleInfo.setLoadStatus(ModuleWrapper.LoadStatus.INCOMPATIBLE);
          moduleInfo.applyInfo(ModuleWrapper.LogInfoKey.INCOMPATIBILITY.getKey(), Arrays.toString(moduleInfo.getModule().incompatibleModules().toArray()));
          disabled++;
          break;
        }
      }
    }
    notLoadedModules = disabled;
    plugin.getLogger().log(Level.INFO, "Modules compatibility checked! Disabled {0} modules!", disabled);
  }

  private void enableModules() {
    for (ModuleWrapper moduleInfo : modules) {
      if (moduleInfo.getLoadStatus() != ModuleWrapper.LoadStatus.LOADED) {
        continue;
      }
      try {
        enableModuleSafely(moduleInfo);
      } catch (Throwable thr) {
        thr.printStackTrace();
        notLoadedModules++;
        moduleInfo.setLoadStatus(ModuleWrapper.LoadStatus.FAILED_TO_LOAD);
        moduleInfo.applyInfo(ModuleWrapper.LogInfoKey.LOAD_EXCEPTION.getKey(), thr.getMessage() + " (" + thr.getCause() + ")");
        plugin.getLogger().log(Level.WARNING, "Failed to enable module {0}, error {1} {2}", new Object[] {moduleInfo.getModule().getModuleName(), thr.getCause(), thr.getMessage()});
      }
    }
  }

  private void enableModuleSafely(ModuleWrapper moduleInfo) {
    VillageDefenseModule module = moduleInfo.getModule();
    plugin.getLogger().log(Level.INFO, "[{0}] Loading module! Version {1} by {2}", new Object[] {module.getModuleName(), module.getVersion(), module.getAuthor()});
    module.onLoad(plugin);
  }

  public boolean isModulePresent(String moduleName) {
    for (ModuleWrapper moduleInfo : modules) {
      if (moduleInfo.getModule().getModuleName().equalsIgnoreCase(moduleName)) {
        return true;
      }
    }
    return false;
  }

  public void disable() {
    modules.clear();
  }

  public List<ModuleWrapper> getModulesInfo() {
    return Collections.unmodifiableList(modules);
  }

  public List<String> getModulesClassesNames() {
    return Collections.unmodifiableList(modulesClassesNames);
  }

  public int getNotLoadedModulesAmount() {
    return notLoadedModules;
  }
}
