/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.handlers.language;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import com.google.common.collect.Iterables;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Assert;
import org.junit.Test;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.villagedefense.MainMock;
import plugily.projects.villagedefense.MockUtils;
import plugily.projects.villagedefense.utils.services.ServiceRegistry;

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
    Assert.assertEquals("Polish", LanguageManager.getPluginLocale().getName());
  }

  @Test
  public void testInvalidCustomLocale() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    FileConfiguration config = plugin.getConfig();
    config.set("locale", "somebrokenlocale");
    plugin.saveConfig();

    //to allow locale service
    ServiceRegistry.registerService(plugin);
    LanguageManager.init(plugin);
    Assert.assertTrue(LanguageManager.isDefaultLanguageUsed());
  }

  @Test
  public void testGetLanguageMessage() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    //on default locale
    plugin.getConfig().set("locale", "default");
    plugin.saveConfig();
    LanguageManager.init(plugin);

    Assert.assertNotEquals("ERR_MESSAGE_NOT_FOUND", LanguageManager.getLanguageMessage("Commands.Did-You-Mean"));

    //on custom locale
    plugin.getConfig().set("locale", "pl");
    plugin.saveConfig();
    LanguageManager.init(plugin);

    Assert.assertNotEquals("ERR_MESSAGE_NOT_FOUND", LanguageManager.getLanguageMessage("Commands.Did-You-Mean"));
  }

  @Test
  public void testGetInvalidLanguageMessage() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    //on default locale
    plugin.getConfig().set("locale", "default");
    plugin.saveConfig();
    LanguageManager.init(plugin);

    Assert.assertEquals("ERR_MESSAGE_NOT_FOUND", LanguageManager.getLanguageMessage("Invalid.Path.Name"));

    //on custom locale
    plugin.getConfig().set("locale", "pl");
    plugin.saveConfig();
    LanguageManager.init(plugin);

    Assert.assertEquals("ERR_MESSAGE_NOT_FOUND", LanguageManager.getLanguageMessage("Invalid.Path.Name"));
  }

}