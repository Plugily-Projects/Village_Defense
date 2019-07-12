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

package pl.plajer.villagedefense;

import java.io.File;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaMock;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.commands.arguments.ArgumentsRegistry;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajer.villagedefense.handlers.HolidayManager;
import pl.plajer.villagedefense.handlers.items.SpecialItemManager;
import pl.plajer.villagedefense.handlers.language.LanguageManager;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.handlers.powerup.PowerupRegistry;
import pl.plajer.villagedefense.handlers.reward.RewardsFactory;
import pl.plajer.villagedefense.handlers.sign.SignManager;
import pl.plajer.villagedefense.kits.KitMenuHandler;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.user.UserManager;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;
import pl.plajerlair.commonsbox.database.MysqlDatabase;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.UnimplementedOperationException;

/**
 * @author Plajer
 * <p>
 * Created at 01.06.2019
 */
public class MainMock extends Main {

  private ConfigPreferences configPreferences;
  private UserManager userManager;
  private RewardsFactory rewardsFactory;
  private ChatManager chatManager;
  private SpecialItemManager specialItemManager;
  private ArenaMock testArena;

  public MainMock() {
    super();
  }

  protected MainMock(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public boolean is1_11_R1() {
    return false;
  }

  @Override
  public boolean is1_12_R1() {
    return true;
  }

  @Override
  public boolean is1_13_R1() {
    return false;
  }

  @Override
  public boolean is1_13_R2() {
    return false;
  }

  @Override
  public boolean is1_14_R1() {
    return false;
  }

  @Override
  public SignManager getSignManager() {
    throw new UnimplementedOperationException();
  }

  @Override
  public KitMenuHandler getKitMenuHandler() {
    throw new UnimplementedOperationException();
  }

  @Override
  public String getVersion() {
    return "v1_12_R1";
  }

  @Override
  public void onEnable() {
    Arena.init(this);
    ArenaRegistry.init(this);
    User.init(this);
    User.cooldownHandlerTask();
    Utils.init(this);
    Messages.init(this);
    LanguageManager.init(this);
    CompatMaterialConstants.init(this);
    this.configPreferences = new ConfigPreferences(this);
    this.userManager = new UserManager(this);
    this.rewardsFactory = new RewardsFactory(this);
    this.chatManager = new ChatManager(this, "[Village Defense] ");
    this.specialItemManager = new SpecialItemManager(this);

    this.testArena = new ArenaMock();
    ArenaRegistry.registerArena(testArena);

    //trick to clean up server directory after tests
    Runtime.getRuntime().addShutdownHook(new Thread(MockBukkit::unload));
  }

  @Override
  public ChatManager getChatManager() {
    return chatManager;
  }

  @Override
  public UserManager getUserManager() {
    return userManager;
  }

  @Override
  public RewardsFactory getRewardsHandler() {
    return rewardsFactory;
  }

  @Override
  public SpecialItemManager getSpecialItemManager() {
    return specialItemManager;
  }

  @Override
  public HolidayManager getHolidayManager() {
    throw new UnimplementedOperationException();
  }

  @Override
  public MysqlDatabase getMysqlDatabase() {
    throw new UnimplementedOperationException();
  }

  @Override
  public PowerupRegistry getPowerupRegistry() {
    throw new UnimplementedOperationException();
  }

  @Override
  public ConfigPreferences getConfigPreferences() {
    return configPreferences;
  }

  @Override
  public ArgumentsRegistry getArgumentsRegistry() {
    throw new UnimplementedOperationException();
  }

  public ArenaMock getTestArena() {
    return testArena;
  }

  @Override
  public void onDisable() {
    //do nothing
  }
}
