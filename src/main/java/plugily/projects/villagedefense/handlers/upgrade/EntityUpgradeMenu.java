/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.handlers.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.event.player.VillagePlayerEntityUpgradeEvent;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.events.EntityUpgradeListener;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.Upgrade;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.UpgradeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public class EntityUpgradeMenu {

  private final String pluginPrefix;
  private final List<Upgrade> upgrades = new ArrayList<>();
  private final Main plugin;

  public EntityUpgradeMenu(Main plugin) {
    this.plugin = plugin;
    this.pluginPrefix = new MessageBuilder("IN_GAME_PLUGIN_PREFIX").build();
    new EntityUpgradeListener(this);
    registerUpgrade(new UpgradeBuilder("Damage")
        .entity(Upgrade.EntityType.BOTH).slot(2, 1).maxTier(4).metadata("VD_Damage")
        //2.0 + (tier * 3)
        .tierValue(0, 2.0).tierValue(1, 5.0).tierValue(2, 8.0).tierValue(3, 11.0).tierValue(4, 14.0)
        .build());
    registerUpgrade(new UpgradeBuilder("Health")
        .entity(Upgrade.EntityType.BOTH).slot(2, 2).maxTier(4).metadata("VD_Health")
        //100.0 + (100.0 * (tier / 2.0))
        .tierValue(0, 100.0).tierValue(1, 150.0).tierValue(2, 200.0).tierValue(3, 250.0).tierValue(4, 300.0)
        .build());
    registerUpgrade(new UpgradeBuilder("Speed")
        .entity(Upgrade.EntityType.BOTH).slot(2, 3).maxTier(4).metadata("VD_Speed")
        //0.25 + (0.25 * ((double) tier / 5.0))
        .tierValue(0, 0.25).tierValue(1, 0.3).tierValue(2, 0.35).tierValue(3, 0.4).tierValue(4, 0.45)
        .build());
    registerUpgrade(new UpgradeBuilder("Swarm-Awareness")
        .entity(Upgrade.EntityType.WOLF).slot(3, 4).maxTier(2).metadata("VD_SwarmAwareness")
        //tier * 0.2
        .tierValue(0, 0).tierValue(1, 0.2).tierValue(2, 0.4)
        .build());
    registerUpgrade(new UpgradeBuilder("Final-Defense")
        .entity(Upgrade.EntityType.IRON_GOLEM).slot(3, 4).maxTier(2).metadata("VD_FinalDefense")
        //tier * 5
        .tierValue(0, 0).tierValue(1, 5).tierValue(2, 10)
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

  @Nullable
  public Upgrade getUpgrade(String id) {
    for(Upgrade upgrade : upgrades) {
      if(upgrade.getId().equals(id)) {
        return upgrade;
      }
    }
    return null;
  }

  /**
   * Opens menu with upgrades for wolf or golem
   *
   * @param livingEntity entity to check upgrades for
   * @param player       player who will see inventory
   */
  public void openUpgradeMenu(LivingEntity livingEntity, Player player) {
    NormalFastInv gui = new NormalFastInv(6 * 9, color("UPGRADE_MENU_TITLE"));
    gui.addClickHandler(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    User user = plugin.getUserManager().getUser(player);

    for(Upgrade upgrade : upgrades) {
      if(upgrade.getApplicableFor() != Upgrade.EntityType.BOTH && !livingEntity.getType().toString().equals(upgrade.getApplicableFor().toString())) {
        continue;
      }
      int x = upgrade.getSlotX();
      int y = upgrade.getSlotY();
      gui.setItem(x + y * 9, upgrade.asItemStack(getTier(livingEntity, upgrade)), event -> {
        int nextTier = getTier(livingEntity, upgrade) + 1;
        int cost = upgrade.getCost(nextTier);
        if(nextTier > upgrade.getMaxTier()) {
          player.sendMessage(pluginPrefix + color("UPGRADE_MENU_MAX_TIER"));
          return;
        }

        int orbs = user.getStatistic("ORBS");
        if(orbs < cost) {
          player.sendMessage(pluginPrefix + color("UPGRADE_MENU_CANNOT_AFFORD"));
          return;
        }

        user.setStatistic("ORBS", orbs - cost);
        player.sendMessage(pluginPrefix + color("UPGRADE_MENU_UPGRADED_ENTITY").replace("%tier%", Integer.toString(nextTier)));
        applyUpgrade(livingEntity, upgrade);

        Bukkit.getPluginManager().callEvent(new VillagePlayerEntityUpgradeEvent(plugin.getArenaRegistry().getArena(player), livingEntity, player, upgrade, nextTier));
        player.closeInventory();
        openUpgradeMenu(livingEntity, player);
      });
      for(int i = 0; i < upgrade.getMaxTier(); i++) {
        if(i < getTier(livingEntity, upgrade)) {
          gui.setItem((x + 1 + i) + y * 9, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem()).name(" ").build());
        } else {
          gui.setItem((x + 1 + i) + y * 9, new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).name(" ").build());
        }
      }
    }
    applyStatisticsBookOfEntityToGui(gui, livingEntity);
    gui.open(player);
  }

  private String color(String key) {
    return new MessageBuilder(key).asKey().build();
  }

  private void applyStatisticsBookOfEntityToGui(NormalFastInv gui, LivingEntity livingEntity) {
    String[] lore = color("UPGRADE_MENU_STATS_ITEM_DESCRIPTION").split(";");

    for(int a = 0; a < lore.length; a++) {
      Upgrade speed = getUpgrade("Speed");
      Upgrade damage = getUpgrade("Damage");
      Upgrade health = getUpgrade("Health");

      lore[a] = lore[a].replace("%speed%", Double.toString(speed.getValueForTier(getTier(livingEntity, speed))))
          .replace("%damage%", Double.toString(damage.getValueForTier(getTier(livingEntity, damage))))
          .replace("%max_hp%", Double.toString(health.getValueForTier(getTier(livingEntity, health))))
          .replace("%current_hp%", Double.toString(livingEntity.getHealth()));
    }

    gui.setItem(4, new ItemBuilder(new ItemStack(Material.BOOK))
        .name(color("UPGRADE_MENU_STATS_ITEM_NAME"))
        .lore(lore)
        .build());
  }

  /**
   * Applies upgrade for target entity
   * automatically increments current tier
   *
   * @param entity  target entity
   * @param upgrade upgrade to apply
   * @return true if applied successfully, false if tier is max and cannot be applied more
   */
  public boolean applyUpgrade(Entity entity, Upgrade upgrade) {
    List<org.bukkit.metadata.MetadataValue> meta = entity.getMetadata(upgrade.getMetadataAccessor());

    if(meta.isEmpty()) {
      entity.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, 1));
      applyUpgradeEffect(entity, upgrade, 1);
      return true;
    }

    if(meta.get(0).asInt() == upgrade.getMaxTier()) {
      return false;
    }

    int tier = getTier(entity, upgrade) + 1;
    entity.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, tier));
    applyUpgradeEffect(entity, upgrade, tier);
    if(upgrade.getMaxTier() == tier) {
      VersionUtils.playSound(entity.getLocation(), "BLOCK_ANVIL_USE");
      VersionUtils.sendParticles("EXPLOSION_LARGE", (Set<Player>) null, entity.getLocation(), 5);
    }
    return true;
  }

  private void applyUpgradeEffect(Entity entity, Upgrade upgrade, int tier) {
    org.bukkit.Location entityLocation = entity.getLocation();

    VersionUtils.sendParticles("FIREWORKS_SPARK", null, entityLocation.add(0, 1, 0), 30, 0.7, 0.7, 0.7);
    VersionUtils.sendParticles("HEART", (Set<Player>) null, entityLocation.add(0, 1.6, 0), 5);
    VersionUtils.playSound(entityLocation, "ENTITY_PLAYER_LEVELUP");

    int[] baseValues = new int[]{getTier(entity, getUpgrade("Health")), getTier(entity, getUpgrade("Speed")), getTier(entity, getUpgrade("Damage"))};

    if(areAllEqualOrHigher(baseValues) && getMinValue(baseValues) == 4) {
      //final mode! rage!!!
      VersionUtils.setGlowing(entity, true);
    }

    switch(upgrade.getId()) {
      case "Damage":
        if(entity.getType() == EntityType.WOLF) {
          CreatureUtils.getCreatureInitializer().applyDamageModifier((LivingEntity) entity, 2.0 + (tier * 3));
        }
        //attribute damage doesn't exist for golems
        break;
      case "Health":
        LivingEntity living = (LivingEntity) entity;

        VersionUtils.setMaxHealth(living, 100.0 + (100.0 * (tier / 2.0)));
        living.setHealth(VersionUtils.getMaxHealth(living));
        break;
      case "Speed":
        CreatureUtils.getCreatureInitializer().applySpeedModifier((LivingEntity) entity, 0.25 + (0.25 * (tier / 5.0)));
        break;
      case "Swarm-Awareness":
      case "Final-Defense":
        //do nothing they are used within events
        break;
      default:
        break;
    }
  }

  public List<Upgrade> getUpgrades() {
    return upgrades;
  }

  private boolean areAllEqualOrHigher(int[] numbers) {
    for(int i = 1; i < numbers.length; i++) {
      if(numbers[0] < numbers[i]) {
        return false;
      }
    }
    return true;
  }

  private int getMinValue(int[] numbers) {
    int minValue = numbers[0];
    for(int i = 1; i < numbers.length; i++) {
      if(numbers[i] < minValue) {
        minValue = numbers[i];
      }
    }
    return minValue;
  }

  /**
   * @param entity  entity to check
   * @param upgrade upgrade type
   * @return current tier of upgrade for target entity
   */
  public int getTier(Entity entity, Upgrade upgrade) {
    List<org.bukkit.metadata.MetadataValue> meta = entity.getMetadata(upgrade.getMetadataAccessor());
    return meta.isEmpty() ? 0 : meta.get(0).asInt();
  }

  public Main getPlugin() {
    return plugin;
  }
}
