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

package pl.plajer.villagedefense.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.kits.basekits.FreeKit;
import pl.plajer.villagedefense.kits.basekits.Kit;
import pl.plajer.villagedefense.kits.free.KnightKit;
import pl.plajer.villagedefense.kits.free.LightTankKit;
import pl.plajer.villagedefense.kits.free.ZombieFinderKit;
import pl.plajer.villagedefense.kits.level.ArcherKit;
import pl.plajer.villagedefense.kits.level.GolemFriendKit;
import pl.plajer.villagedefense.kits.level.HardcoreKit;
import pl.plajer.villagedefense.kits.level.HealerKit;
import pl.plajer.villagedefense.kits.level.LooterKit;
import pl.plajer.villagedefense.kits.level.MediumTankKit;
import pl.plajer.villagedefense.kits.level.PuncherKit;
import pl.plajer.villagedefense.kits.level.RunnerKit;
import pl.plajer.villagedefense.kits.level.TerminatorKit;
import pl.plajer.villagedefense.kits.level.WorkerKit;
import pl.plajer.villagedefense.kits.premium.BlockerKit;
import pl.plajer.villagedefense.kits.premium.CleanerKit;
import pl.plajer.villagedefense.kits.premium.DogFriendKit;
import pl.plajer.villagedefense.kits.premium.HeavyTankKit;
import pl.plajer.villagedefense.kits.premium.MedicKit;
import pl.plajer.villagedefense.kits.premium.NakedKit;
import pl.plajer.villagedefense.kits.premium.PremiumHardcoreKit;
import pl.plajer.villagedefense.kits.premium.ShotBowKit;
import pl.plajer.villagedefense.kits.premium.TeleporterKit;
import pl.plajer.villagedefense.kits.premium.TornadoKit;
import pl.plajer.villagedefense.kits.premium.WizardKit;
import pl.plajer.villagedefense.utils.constants.Constants;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * Kit registry class for registering new kits.
 *
 * @author TomTheDeveloper
 */
public class KitRegistry {

  private static List<Kit> kits = new ArrayList<>();
  private static Kit defaultKit = null;
  private static Main plugin;
  private static List<Class> classKitNames = Arrays.asList(LightTankKit.class, ZombieFinderKit.class, ArcherKit.class, PuncherKit.class, HealerKit.class, LooterKit.class, RunnerKit.class,
      MediumTankKit.class, WorkerKit.class, GolemFriendKit.class, TerminatorKit.class, HardcoreKit.class, CleanerKit.class, TeleporterKit.class, HeavyTankKit.class, ShotBowKit.class,
      DogFriendKit.class, PremiumHardcoreKit.class, TornadoKit.class, BlockerKit.class, MedicKit.class, NakedKit.class, WizardKit.class);

  public static void init(Main plugin) {
    KitRegistry.plugin = plugin;
    setupGameKits();
    setupKitSelectorItem();
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public static void registerKit(Kit kit) {
    kits.add(kit);
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public static Kit getDefaultKit() {
    return defaultKit;
  }

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  public static void setDefaultKit(FreeKit defaultKit) {
    KitRegistry.defaultKit = defaultKit;
  }

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  public static List<Kit> getKits() {
    return kits;
  }

  /**
   * Get registered kit by it's represented item stack
   *
   * @param itemStack itemstack that kit represents
   * @return Registered kit or default if not found
   */
  public static Kit getKit(ItemStack itemStack) {
    Kit returnKit = getDefaultKit();
    for (Kit kit : kits) {
      if (itemStack.getType() == kit.getMaterial()) {
        returnKit = kit;
        break;
      }
    }
    return returnKit;
  }

  private static void setupGameKits() {
    KnightKit knightkit = new KnightKit();
    FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.KITS.getName());
    for (Class kitClass : classKitNames) {
      if (config.getBoolean("Enabled-Game-Kits." + kitClass.getSimpleName().replace("Kit", ""))) {
        try {
          Class.forName(kitClass.getName()).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
          plugin.getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
          plugin.getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        }
      }
    }

    KitRegistry.setDefaultKit(knightkit);
  }

  private static void setupKitSelectorItem() {
    plugin.getKitManager().setMaterial(Material.NETHER_STAR);
    plugin.getKitManager().setItemName(plugin.getChatManager().colorMessage(Messages.KITS_KIT_MENU_ITEM_NAME));
    plugin.getKitManager().setMenuName(plugin.getChatManager().colorMessage(Messages.KITS_MENU_TITLE));
    plugin.getKitManager().setDescription(new String[] {plugin.getChatManager().colorMessage(Messages.KITS_OPEN_KIT_MENU)});
  }

}
