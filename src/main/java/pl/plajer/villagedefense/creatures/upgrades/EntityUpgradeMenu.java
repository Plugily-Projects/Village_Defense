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

package pl.plajer.villagedefense.creatures.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.handlers.ChatManager;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class EntityUpgradeMenu {

  private static List<Upgrade> upgrades = new ArrayList<>();
  private static Main plugin;

  public static void init(Main plugin) {
    EntityUpgradeMenu.plugin = plugin;
    //todo add config checks + language + locale
    new EntityUpgradeListener(plugin);
    upgrades.add(new Upgrade(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Health.Name"), ChatManager.colorMessage("Upgrade-Menu.Upgrades.Health.Description").split(";"), "VD_Health"));
    upgrades.add(new Upgrade(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Damage.Name"), ChatManager.colorMessage("Upgrade-Menu.Upgrades.Damage.Description").split(";"), "VD_Damage"));
    upgrades.add(new Upgrade(ChatManager.colorMessage("Upgrade-Menu.Upgrades.Speed.Name"), ChatManager.colorMessage("Upgrade-Menu.Upgrades.Speed.Description").split(";"), "VD_Speed"));
  }

  /**
   * Opens menu with upgrades for wolf or golem
   *
   * @param en entity to check upgrades for
   * @param p  player who will see inventory
   */
  public static void openUpgradeMenu(Entity en, Player p) {
    Inventory inv = Bukkit.createInventory(null, /* magic number may be changed */9 * 5, ChatManager.colorMessage("Upgrade-Menu.Title"));

    for (int i = 0; i < 3; i++) {
      inv.setItem(((i + 1) * 9) + 2, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
          .name(upgrades.get(i).getName())
          .lore(upgrades.get(i).getDescription()).build());
      int tier = 0;
      if (en.hasMetadata(upgrades.get(i).getMetadataAccess())) {
        tier = en.getMetadata(upgrades.get(i).getMetadataAccess()).get(0).asInt();
      }
      for (int j = 0; j < tier; j++) {
        inv.setItem(((i + 1) * 9) + 2 + j + j, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem()).build());
      }
      for (int j = 0; j < 4 - tier; j++) {
        inv.setItem(4 + ((i + 1) * 8) + j + tier, new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).build());
      }
    }
    p.openInventory(inv);
  }

  /**
   * Applies upgrade for target entity
   * automatically increments current tier
   *
   * @param en      target entity
   * @param upgrade upgrade to apply
   * @return true if applied successfully, false if tier is max and cannot be applied more
   */
  public static boolean applyUpgrade(Entity en, EntityUpgrade upgrade) {
    if (!en.hasMetadata(upgrade.getMetadataAccess())) {
      en.setMetadata(upgrade.getMetadataAccess(), new FixedMetadataValue(plugin, 1));
      applyUpgradeEffect(en, upgrade, 1);
      return true;
    }
    if (en.getMetadata(upgrade.getMetadataAccess()).get(0).asInt() == upgrade.getMaxTierUpgrade()) {
      return false;
    }
    int tier = getTier(en, upgrade) + 1;
    en.setMetadata(upgrade.getMetadataAccess(), new FixedMetadataValue(plugin, tier));
    applyUpgradeEffect(en, upgrade, tier);
    return true;
  }

  private static void applyUpgradeEffect(Entity en, EntityUpgrade upgrade, int tier) {
    en.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, en.getLocation(), 15, 0.5, 0.5, 0.5);
    if (areAllEqualOrHigher(new int[] {getTier(en, EntityUpgrade.HEALTH), getTier(en, EntityUpgrade.SPEED), getTier(en, EntityUpgrade.DAMAGE)})) {
      //todo
    }
    switch (upgrade) {
      case DAMAGE:
        ((LivingEntity) en).removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        ((LivingEntity) en).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, /* amplifiers counts from 0*/tier - 1));
        break;
      case FINAL_DEFENSE:
        //todo
        break;
      case HEALTH:
        ((LivingEntity) en).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0 + (100.0 * ((double) tier / 2.0)));
        break;
      case SPEED:
        ((LivingEntity) en).removePotionEffect(PotionEffectType.SPEED);
        ((LivingEntity) en).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, /* amplifiers counts from 0*/tier - 1));
        break;
      case SWARM_AWARENESS:
        //todo
        break;
    }
  }

  private static boolean areAllEqualOrHigher(int[] a) {
    for (int i = 1; i < a.length; i++) {
      if (a[0] < a[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param en      entity to check
   * @param upgrade upgrade type
   * @return current tier of upgrade for target entity
   */
  public static int getTier(Entity en, EntityUpgrade upgrade) {
    if (!en.hasMetadata(upgrade.getMetadataAccess())) {
      return 0;
    }
    return en.getMetadata(upgrade.getMetadataAccess()).get(0).asInt();
  }

}
