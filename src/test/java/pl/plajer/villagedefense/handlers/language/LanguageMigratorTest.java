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

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Assert;
import org.junit.Test;

import pl.plajer.villagedefense.MainMock;
import pl.plajer.villagedefense.MockUtils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 02.06.2019
 */
public class LanguageMigratorTest {

  @Test
  public void testConfigMigration() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    FileConfiguration config = plugin.getConfig();
    config.set("Version", 0);
    plugin.saveConfig();

    LanguageManager.init(plugin);
    //get fresh file
    plugin.reloadConfig();
    //from Version 5
    Assert.assertTrue(plugin.getConfig().isSet("Cooldown-Before-Next-Wave"));
    //from Version 6
    Assert.assertTrue(plugin.getConfig().isSet("Wave-Limit"));
    Assert.assertEquals(LanguageMigrator.CONFIG_FILE_VERSION, plugin.getConfig().getInt("Version"));
  }

  @Test
  public void testLanguageFileMigration() {
    MainMock plugin = MockUtils.getPluginMockSafe();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    config.set("File-Version-Do-Not-Edit", 0);
    ConfigUtils.saveConfig(plugin, config, "language");

    LanguageManager.init(plugin);
    //get fresh file
    config = ConfigUtils.getConfig(plugin, "language");
    //from Version 3
    Assert.assertTrue(config.isSet("Commands.Did-You-Mean"));
    //from Version 6
    Assert.assertTrue(config.isSet("In-Game.Messages.Shop-Messages.Mob-Limit-Reached"));
    Assert.assertEquals(LanguageMigrator.LANGUAGE_FILE_VERSION, config.getInt("File-Version-Do-Not-Edit"));
  }

}
