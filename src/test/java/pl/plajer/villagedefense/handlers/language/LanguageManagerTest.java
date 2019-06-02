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

package pl.plajer.villagedefense.handlers.language;

import com.google.common.collect.Iterables;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Assert;
import org.junit.Test;

import pl.plajer.villagedefense.MainMock;
import pl.plajer.villagedefense.MockUtils;
import pl.plajer.villagedefense.utils.services.ServiceRegistry;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class LanguageManagerTest {

  @Test
  public void testMessagesIntegrity() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    //break random section for messages validation purposes
    config.set("Validator", null);
    ConfigUtils.saveConfig(plugin, config, "language");
    LanguageManager.init(plugin);
    //bit tricky way to check if console received message, method provided by MockBukkit
    Assert.assertTrue(Iterables.getLast(((ConsoleCommandSenderMock) MockUtils.getServerMockSafe().getConsoleSender()).messages).contains("Language file integrity check failed!"));
  }

  @Test
  public void testDefaultLocaleUsed() {
    LanguageManager.init(MockUtils.getPluginMockSafe());
    Assert.assertTrue(LanguageManager.isDefaultLanguageUsed());
  }

  @Test
  public void testCustomLocale() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    FileConfiguration config = plugin.getConfig();
    //the only good locale out here, because the official one!
    config.set("locale", "pl");
    plugin.saveConfig();

    //to allow locale service
    ServiceRegistry.registerService(plugin);
    LanguageManager.init(plugin);
    Assert.assertFalse(LanguageManager.isDefaultLanguageUsed());
  }

}