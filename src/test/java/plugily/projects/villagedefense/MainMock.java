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

package plugily.projects.villagedefense;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaMock;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.commands.arguments.ArgumentsRegistry;
import plugily.projects.villagedefense.handlers.ChatManager;
import plugily.projects.villagedefense.handlers.HolidayManager;
import plugily.projects.villagedefense.handlers.items.SpecialItemManager;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.powerup.PowerupRegistry;
import plugily.projects.villagedefense.handlers.reward.RewardsFactory;
import plugily.projects.villagedefense.handlers.sign.SignManager;
import plugily.projects.villagedefense.kits.KitMenuHandler;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.user.UserManager;
import plugily.projects.villagedefense.utils.Utils;

import java.io.File;

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
  public SignManager getSignManager() {
    throw new UnimplementedOperationException();
  }

  @Override
  public KitMenuHandler getKitMenuHandler() {
    throw new UnimplementedOperationException();
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
    this.configPreferences = new ConfigPreferences(this);
    this.userManager = new UserManager(this);
    this.rewardsFactory = new RewardsFactory(this);
    this.chatManager = new ChatManager(this);
    this.specialItemManager = new SpecialItemManager(this);

    this.testArena = new ArenaMock();
    //ArenaRegistry.registerArena(testArena);

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
