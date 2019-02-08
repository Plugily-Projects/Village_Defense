/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.StatsStorage;
import pl.plajer.villagedefense.api.event.player.VillagePlayerEntityUpgradeEvent;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.Utils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class EntityUpgradeMenu {

  private List<Upgrade> upgrades = new ArrayList<>();
  private Main plugin;

  public EntityUpgradeMenu(Main plugin) {
    this.plugin = plugin;
    if (!plugin.getConfig().getBoolean("Entity-Upgrades.Enabled")) {
      return;
    }
    new EntityUpgradeListener(this);
    registerUpgrade(new UpgradeBuilder("Damage")
        .entity(Upgrade.EntityType.BOTH).slot(2, 1).maxTier(4).metadata("VD_Damage")
        //2.0 + (tier * 3)
        .tierVal(0, 2.0).tierVal(1, 5.0).tierVal(2, 8.0).tierVal(3, 11.0).tierVal(4, 14.0)
        .build());
    registerUpgrade(new UpgradeBuilder("Health")
        .entity(Upgrade.EntityType.BOTH).slot(2, 2).maxTier(4).metadata("VD_Health")
        //100.0 + (100.0 * (tier / 2.0))
        .tierVal(0, 100.0).tierVal(1, 150.0).tierVal(2, 200.0).tierVal(3, 250.0).tierVal(4, 300.0)
        .build());
    registerUpgrade(new UpgradeBuilder("Speed")
        .entity(Upgrade.EntityType.BOTH).slot(2, 3).maxTier(4).metadata("VD_Speed")
        //0.25 + (0.25 * ((double) tier / 5.0))
        .tierVal(0, 0.25).tierVal(1, 0.3).tierVal(2, 0.35).tierVal(3, 0.4).tierVal(4, 0.45)
        .build());
    registerUpgrade(new UpgradeBuilder("Swarm-Awareness")
        .entity(Upgrade.EntityType.WOLF).slot(3, 4).maxTier(2).metadata("VD_SwarmAwareness")
        //tier * 0.2
        .tierVal(0, 0).tierVal(1, 0.2).tierVal(2, 0.4)
        .build());
    registerUpgrade(new UpgradeBuilder("Final-Defense")
        .entity(Upgrade.EntityType.IRON_GOLEM).slot(3, 4).maxTier(2).metadata("VD_FinalDefense")
        //tier * 5
        .tierVal(0, 0).tierVal(1, 5).tierVal(2, 10)
        .build());
  }

  /**
   * Registers new upgrade
   *
   * @param upgrade upgrade to registry
   */
  public void registerUpgrade(Upgrade upgrade) {
    upgrades.add(upgrade);
  }

  public Upgrade getUpgrade(String id) {
    for (Upgrade upgrade : upgrades) {
      if (upgrade.getId().equals(id)) {
        return upgrade;
      }
    }
    return null;
  }

  /**
   * Opens menu with upgrades for wolf or golem
   *
   * @param en     entity to check upgrades for
   * @param player player who will see inventory
   */
  public void openUpgradeMenu(LivingEntity en, Player player) {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Upgrade-Menu.Title"));
    StaticPane pane = new StaticPane(9, 6);
    User user = plugin.getUserManager().getUser(player);

    for (Upgrade upgrade : upgrades) {
      if (upgrade.getApplicableFor() != Upgrade.EntityType.BOTH && !en.getType().toString().equals(upgrade.getApplicableFor().toString())) {
        continue;
      }
      int tier = en.hasMetadata(upgrade.getMetadataAccessor()) ? en.getMetadata(upgrade.getMetadataAccessor()).get(0).asInt() : 0;
      pane.addItem(new GuiItem(upgrade.asItemStack(tier), e -> {
        e.setCancelled(true);
        int nextTier = getTier(en, upgrade) + 1;
        int cost = upgrade.getCost(nextTier);
        if (nextTier > upgrade.getMaxTier()) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Upgrade-Menu.Max-Tier"));
          return;
        }
        if (user.getStat(StatsStorage.StatisticType.ORBS) < cost) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Upgrade-Menu.Cannot-Afford"));
          return;
        }
        user.setStat(StatsStorage.StatisticType.ORBS, user.getStat(StatsStorage.StatisticType.ORBS) - cost);
        player.sendMessage(plugin.getChatManager().getPrefix() +
            plugin.getChatManager().colorMessage("Upgrade-Menu.Upgraded-Entity").replace("%tier%", String.valueOf(nextTier)));
        applyUpgrade(en, upgrade);

        VillagePlayerEntityUpgradeEvent event = new VillagePlayerEntityUpgradeEvent(ArenaRegistry.getArena(player), en, player, upgrade, nextTier);
        Bukkit.getPluginManager().callEvent(event);
        player.closeInventory();
      }), upgrade.getSlotX(), upgrade.getSlotY());
      for (int i = 0; i < upgrade.getMaxTier(); i++) {
        if (i < tier) {
          pane.addItem(new GuiItem(new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem())
              .name(" ").build(), e -> e.setCancelled(true)), upgrade.getSlotX() + 1 + i, upgrade.getSlotY());
        } else {
          pane.addItem(new GuiItem(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem())
              .name(" ").build(), e -> e.setCancelled(true)), upgrade.getSlotX() + 1 + i, upgrade.getSlotY());
        }
      }
    }
    pane.addItem(new GuiItem(new ItemBuilder(new ItemStack(Material.BOOK))
        .name(plugin.getChatManager().colorMessage("Upgrade-Menu.Stats-Item.Name"))
        .lore(Arrays.stream(plugin.getChatManager().colorMessage("Upgrade-Menu.Stats-Item.Description").split(";"))
            .map((lore) -> lore = plugin.getChatManager().colorRawMessage(lore)
                .replace("%speed%", String.valueOf(getUpgrade("Speed").getValueForTier(getTier(en, getUpgrade("Speed")))))
                .replace("%damage%", String.valueOf(getUpgrade("Damage").getValueForTier(getTier(en, getUpgrade("Damage")))))
                .replace("%max_hp%", String.valueOf(getUpgrade("Health").getValueForTier(getTier(en, getUpgrade("Health")))))
                .replace("%current_hp%", String.valueOf(en.getHealth()))).collect(Collectors.toList()))
        .build(), e -> e.setCancelled(true)), 4, 0);
    gui.addPane(pane);
    gui.show(player);
  }

  /**
   * Applies upgrade for target entity
   * automatically increments current tier
   *
   * @param en      target entity
   * @param upgrade upgrade to apply
   * @return true if applied successfully, false if tier is max and cannot be applied more
   */
  public boolean applyUpgrade(Entity en, Upgrade upgrade) {
    if (!en.hasMetadata(upgrade.getMetadataAccessor())) {
      en.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, 1));
      applyUpgradeEffect(en, upgrade, 1);
      return true;
    }
    if (en.getMetadata(upgrade.getMetadataAccessor()).get(0).asInt() == upgrade.getMaxTier()) {
      return false;
    }
    int tier = getTier(en, upgrade) + 1;
    en.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, tier));
    applyUpgradeEffect(en, upgrade, tier);
    if (upgrade.getMaxTier() == tier) {
      Utils.playSound(en.getLocation(), "BLOCK_ANVIL_USE", "BLOCK_ANVIL_USE");
      en.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, en.getLocation().add(0, 0.5, 0), 5);
    }
    return true;
  }

  private void applyUpgradeEffect(Entity en, Upgrade upgrade, int tier) {
    en.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, en.getLocation().add(0, 1, 0), 30, 0.7, 0.7, 0.7, 0);
    en.getWorld().spawnParticle(Particle.HEART, en.getLocation().add(0, 1.6, 0), 5, 0, 0, 0);
    Utils.playSound(en.getLocation(), "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP");
    int[] baseValues = new int[] {getTier(en, getUpgrade("Health")), getTier(en, getUpgrade("Speed")), getTier(en, getUpgrade("Damage"))};
    if (areAllEqualOrHigher(baseValues) && getMinValue(baseValues) == 4) {
      //final mode! rage!!!
      en.setGlowing(true);
    }
    switch (upgrade.getId()) {
      case "Damage":
        if (en.getType() == EntityType.WOLF) {
          ((LivingEntity) en).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0 + (tier * 3));
        }
        //attribute damage doesn't exist for golems
        break;
      case "Health":
        ((LivingEntity) en).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0 + (100.0 * ((double) tier / 2.0)));
        break;
      case "Speed":
        ((LivingEntity) en).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25 + (0.25 * ((double) tier / 5.0)));
        break;
      case "Swarm-Awareness":
      case "Final-Defense":
        //do nothing they are used within events
        break;
      default:
        break;
    }
  }

  public Main getPlugin() {
    return plugin;
  }

  public List<Upgrade> getUpgrades() {
    return upgrades;
  }

  private boolean areAllEqualOrHigher(int[] a) {
    for (int i = 1; i < a.length; i++) {
      if (a[0] < a[i]) {
        return false;
      }
    }
    return true;
  }

  private int getMinValue(int[] numbers) {
    int minValue = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
      if (numbers[i] < minValue) {
        minValue = numbers[i];
      }
    }
    return minValue;
  }

  /**
   * @param en      entity to check
   * @param upgrade upgrade type
   * @return current tier of upgrade for target entity
   */
  public int getTier(Entity en, Upgrade upgrade) {
    if (!en.hasMetadata(upgrade.getMetadataAccessor())) {
      return 0;
    }
    return en.getMetadata(upgrade.getMetadataAccessor()).get(0).asInt();
  }

}
