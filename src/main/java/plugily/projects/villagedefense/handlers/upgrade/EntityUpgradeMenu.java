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
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.FastInv;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.api.event.player.VillagePlayerEntityUpgradeEvent;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.events.EntityUpgradeListener;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.Upgrade;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.UpgradeBuilder;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

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
    this.pluginPrefix = plugin.getChatManager().colorMessage(Messages.PLUGIN_PREFIX);
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
   * @param en     entity to check upgrades for
   * @param player player who will see inventory
   */
  public void openUpgradeMenu(LivingEntity en, Player player) {
    FastInv gui = new FastInv(6 * 9, color(Messages.UPGRADES_MENU_TITLE));
    User user = plugin.getUserManager().getUser(player);

    for(Upgrade upgrade : upgrades) {
      if(upgrade.getApplicableFor() != Upgrade.EntityType.BOTH && !en.getType().toString().equals(upgrade.getApplicableFor().toString())) {
        continue;
      }
      int x = upgrade.getSlotX();
      int y = upgrade.getSlotY();
      gui.setItem(x + y * 9, upgrade.asItemStack(getTier(en, upgrade)), e -> {
        int nextTier = getTier(en, upgrade) + 1;
        int cost = upgrade.getCost(nextTier);
        if(nextTier > upgrade.getMaxTier()) {
          player.sendMessage(pluginPrefix + color(Messages.UPGRADES_MAX_TIER));
          return;
        }

        int orbs = user.getStat(StatsStorage.StatisticType.ORBS);
        if(orbs < cost) {
          player.sendMessage(pluginPrefix + color(Messages.UPGRADES_CANNOT_AFFORD));
          return;
        }

        user.setStat(StatsStorage.StatisticType.ORBS, orbs - cost);
        player.sendMessage(pluginPrefix + color(Messages.UPGRADES_UPGRADED_ENTITY).replace("%tier%", Integer.toString(nextTier)));
        applyUpgrade(en, upgrade);

        Bukkit.getPluginManager().callEvent(new VillagePlayerEntityUpgradeEvent(ArenaRegistry.getArena(player), en, player, upgrade, nextTier));
        player.closeInventory();
      });
      for(int i = 0; i < upgrade.getMaxTier(); i++) {
        if(i < getTier(en, upgrade)) {
          gui.setItem((x + 1 + i) + y * 9, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem()).name(" ").build());
        } else {
          gui.setItem((x + 1 + i) + y * 9, new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).name(" ").build());
        }
      }
    }
    applyStatisticsBookOfEntityToGui(gui, en);
    gui.open(player);
  }

  private String color(Messages value) {
    return plugin.getChatManager().colorRawMessage(plugin.getLanguageConfig().getString(value.getAccessor()));
  }

  private void applyStatisticsBookOfEntityToGui(FastInv gui, LivingEntity en) {
    String[] lore = color(Messages.UPGRADES_STATS_ITEM_DESCRIPTION).split(";");

    for(int a = 0; a < lore.length; a++) {
      Upgrade speed = getUpgrade("Speed");
      Upgrade damage = getUpgrade("Damage");
      Upgrade health = getUpgrade("Health");

      lore[a] = lore[a].replace("%speed%", Double.toString(speed.getValueForTier(getTier(en, speed))))
          .replace("%damage%", Double.toString(damage.getValueForTier(getTier(en, damage))))
          .replace("%max_hp%", Double.toString(health.getValueForTier(getTier(en, health))))
          .replace("%current_hp%", Double.toString(en.getHealth()));
    }

    gui.setItem(4, new ItemBuilder(new ItemStack(Material.BOOK))
        .name(color(Messages.UPGRADES_STATS_ITEM_NAME))
        .lore(lore)
        .build());
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
    List<org.bukkit.metadata.MetadataValue> meta = en.getMetadata(upgrade.getMetadataAccessor());

    if(meta.isEmpty()) {
      en.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, 1));
      applyUpgradeEffect(en, upgrade, 1);
      return true;
    }

    if(meta.get(0).asInt() == upgrade.getMaxTier()) {
      return false;
    }

    int tier = getTier(en, upgrade) + 1;
    en.setMetadata(upgrade.getMetadataAccessor(), new FixedMetadataValue(plugin, tier));
    applyUpgradeEffect(en, upgrade, tier);
    if(upgrade.getMaxTier() == tier) {
      Utils.playSound(en.getLocation(), "BLOCK_ANVIL_USE", "BLOCK_ANVIL_USE");
      VersionUtils.sendParticles("EXPLOSION_LARGE", (Set<Player>) null, en.getLocation(), 5);
    }
    return true;
  }

  private void applyUpgradeEffect(Entity en, Upgrade upgrade, int tier) {
    org.bukkit.Location entityLocation = en.getLocation();

    VersionUtils.sendParticles("FIREWORKS_SPARK", null, entityLocation.add(0, 1, 0), 30, 0.7, 0.7, 0.7);
    VersionUtils.sendParticles("HEART", (Set<Player>) null, entityLocation.add(0, 1.6, 0), 5);
    Utils.playSound(entityLocation, "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP");

    int[] baseValues = new int[]{getTier(en, getUpgrade("Health")), getTier(en, getUpgrade("Speed")), getTier(en, getUpgrade("Damage"))};

    if(areAllEqualOrHigher(baseValues) && getMinValue(baseValues) == 4) {
      //final mode! rage!!!
      VersionUtils.setGlowing(en, true);
    }

    switch(upgrade.getId()) {
      case "Damage":
        if(en.getType() == EntityType.WOLF) {
          CreatureUtils.getCreatureInitializer().applyDamageModifier((LivingEntity) en, 2.0 + (tier * 3));
        }
        //attribute damage doesn't exist for golems
        break;
      case "Health":
        LivingEntity living = (LivingEntity) en;

        VersionUtils.setMaxHealth(living, 100.0 + (100.0 * (tier / 2.0)));
        living.setHealth(VersionUtils.getMaxHealth(living));
        break;
      case "Speed":
        CreatureUtils.getCreatureInitializer().applySpeedModifier((LivingEntity) en, 0.25 + (0.25 * (tier / 5.0)));
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

  private boolean areAllEqualOrHigher(int[] a) {
    for(int i = 1; i < a.length; i++) {
      if(a[0] < a[i]) {
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
   * @param en      entity to check
   * @param upgrade upgrade type
   * @return current tier of upgrade for target entity
   */
  public int getTier(Entity en, Upgrade upgrade) {
    List<org.bukkit.metadata.MetadataValue> meta = en.getMetadata(upgrade.getMetadataAccessor());
    return meta.isEmpty() ? 0 : meta.get(0).asInt();
  }

  public Main getPlugin() {
    return plugin;
  }
}
