/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits.overhauled;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.handlers.upgrade.EntityUpgradeMenu;
import plugily.projects.villagedefense.handlers.upgrade.upgrades.Upgrade;
import plugily.projects.villagedefense.kits.AbilitySource;
import plugily.projects.villagedefense.kits.KitHelper;
import plugily.projects.villagedefense.kits.KitSpecifications;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 11.08.2023
 */
public class PetsFriend extends PremiumKit implements AbilitySource, Listener {

  private static final String LANGUAGE_ACCESSOR = "KIT_CONTENT_PETS_FRIEND_";
  private final Random random = new Random();

  public PetsFriend() {
    registerMessages();
    setName(new MessageBuilder(LANGUAGE_ACCESSOR + "NAME").asKey().build());
    setKey("PetsFriend");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
  }

  private void registerMessages() {
    MessageManager manager = getPlugin().getMessageManager();
    manager.registerMessage(LANGUAGE_ACCESSOR + "NAME", new Message("Kit.Content.Pets-Friend.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "DESCRIPTION", new Message("Kit.Content.Pets-Friend.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FIGHT_OR_FLIGHT_NAME", new Message("Kit.Content.Pets-Friend.Game-Item.Fight-Or-Flight.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FIGHT_OR_FLIGHT_DESCRIPTION", new Message("Kit.Content.Pets-Friend.Game-Item.Fight-Or-Flight.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_FIGHT_OR_FLIGHT_ACTIVATE", new Message("Kit.Content.Pets-Friend.Game-Item.Fight-Or-Flight.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_UNSTOPPABLE_BEASTS_NAME", new Message("Kit.Content.Pets-Friend.Game-Item.Unstoppable-Beasts.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_UNSTOPPABLE_BEASTS_DESCRIPTION", new Message("Kit.Content.Pets-Friend.Game-Item.Unstoppable-Beasts.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_UNSTOPPABLE_BEASTS_ACTIVATE", new Message("Kit.Content.Pets-Friend.Game-Item.Unstoppable-Beasts.Activate", ""));
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.petsfriend");
  }

  @Override
  public void giveKitItems(Player player) {
    //todo update
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.LEATHER);

    player.getInventory().setItem(3, new ItemBuilder(new ItemStack(XMaterial.GHAST_TEAR.parseMaterial()))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_AURA_DESCRIPTION"))
      .build());
    player.getInventory().setItem(4, new ItemBuilder(new ItemStack(XMaterial.GOLD_NUGGET.parseMaterial()))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_HOMECOMING_DESCRIPTION"))
      .build());

    player.getInventory().setItem(5, new ItemStack(Material.SADDLE));
    player.getInventory().setItem(8, new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    org.bukkit.Location start = arena.getStartLocation();
    for(int i = 0; i < 2; i++) {
      arena.spawnWolf(start, player);
    }
    arena.spawnGolem(start, player);
  }

  @Override
  public Material getMaterial() {
    return Material.BONE;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    EntityUpgradeMenu upgradeMenu = ((Main) getPlugin()).getEntityUpgradeMenu();
    if(arena.getWave() % (int) Settings.PASSIVE_WOLVES_MODULO.getForArenaState(arena) == 0) {
      Creature wolf = arena.spawnWolf(arena.getStartLocation(), player);
      if(wolf != null) {
        List<Upgrade> wolfUpgrades = upgradeMenu.getUpgrades()
          .stream()
          .filter(u -> u.getApplicableFor() == Upgrade.EntityType.WOLF)
          .collect(Collectors.toList());
        Upgrade randomUpgrade = wolfUpgrades.get(random.nextInt(wolfUpgrades.size()));
        upgradeMenu.applyUpgrade(wolf, player, randomUpgrade);
      }
    }
    if(arena.getWave() % (int) Settings.PASSIVE_GOLEMS_MODULO.getForArenaState(arena) == 0) {
      Creature golem = arena.spawnGolem(arena.getStartLocation(), player);
      if(golem != null) {
        List<Upgrade> golemUpgrades = upgradeMenu.getUpgrades()
          .stream()
          .filter(u -> u.getApplicableFor() == Upgrade.EntityType.IRON_GOLEM)
          .collect(Collectors.toList());
        Upgrade randomUpgrade = golemUpgrades.get(random.nextInt(golemUpgrades.size()));
        upgradeMenu.applyUpgrade(golem, player, randomUpgrade);
      }
    }
  }

  @Override
  @EventHandler
  public void onAbilityCast(PlugilyPlayerInteractEvent event) {
    if(!KitHelper.isInGameWithKitAndItemInHand(event.getPlayer(), PetsFriend.class)) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    String displayName = ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta());
    if(displayName.equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FIGHT_OR_FLIGHT_NAME").asKey().build())) {
      onFightOrFlightCast(stack, user);
    } else if(displayName.equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_UNSTOPPABLE_BEASTS_NAME").asKey().build())) {
      onUnstoppableBeastsCast(stack, user);
    }
  }

  private void onFightOrFlightCast(ItemStack stack, User user) {
    String abilityId = "petsfriend_fightorflight";
    if(!user.checkCanCastCooldownAndMessage(abilityId)) {
      return;
    }
    int castTime = 10;
    int cooldown = 25;
    user.setCooldown(abilityId, cooldown);

    KitHelper.scheduleAbilityCooldown(stack, user.getPlayer(), castTime, cooldown);
    Arena arena = ((Arena) user.getArena());
    List<Creature> pets = arena.getWolves()
      .stream()
      .filter(w -> w.hasMetadata("VD_OWNER_UUID")
        && w.getMetadata("VD_OWNER_UUID").get(0).asString().equals(user.getPlayer().getUniqueId().toString()))
      .collect(Collectors.toList());
    pets.addAll(
      arena.getIronGolems()
        .stream()
        .filter(w -> w.hasMetadata("VD_OWNER_UUID")
          && w.getMetadata("VD_OWNER_UUID").get(0).asString().equals(user.getPlayer().getUniqueId().toString()))
        .collect(Collectors.toList())
    );
    for(Creature pet : arena.getWolves()) {
      pet.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * castTime, 1, false, false));
      pet.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * castTime,
        (int) Settings.ABILITY_FIGHT_OR_FLIGHT_DAMAGE_AMPLIFIER.getForArenaState((Arena) user.getArena()), false, false));
    }
    new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_FIGHT_OR_FLIGHT_ACTIVATE").asKey().send(user.getPlayer());
  }

  private void onUnstoppableBeastsCast(ItemStack stack, User user) {
    String abilityId = "petsfriend_unstoppablebeasts";
    if(!user.checkCanCastCooldownAndMessage(abilityId)) {
      return;
    }
    if(KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
      new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(user.getPlayer());
      return;
    }
    int cooldown = 70;
    user.setCooldown(abilityId, cooldown);
    VersionUtils.setMaterialCooldown(user.getPlayer(), stack.getType(), cooldown * 20);

    EntityUpgradeMenu upgradeMenu = ((Main) getPlugin()).getEntityUpgradeMenu();
    Arena arena = (Arena) user.getArena();
    Creature wolf = arena.spawnWolf(arena.getStartLocation(), user.getPlayer());
    if(wolf != null) {
      upgradeMenu.getUpgrades()
        .stream()
        .filter(u -> u.getApplicableFor() == Upgrade.EntityType.WOLF)
        .forEach(upgrade -> upgradeMenu.applyUpgrade(wolf, user.getPlayer(), upgrade, upgrade.getMaxTier()));
    }
    Creature golem = arena.spawnGolem(arena.getStartLocation(), user.getPlayer());
    if(golem != null) {
      upgradeMenu.getUpgrades()
        .stream()
        .filter(u -> u.getApplicableFor() == Upgrade.EntityType.IRON_GOLEM)
        .forEach(upgrade -> upgradeMenu.applyUpgrade(golem, user.getPlayer(), upgrade, upgrade.getMaxTier()));
    }
    new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_UNSTOPPABLE_BEASTS_ACTIVATE").asKey().send(user.getPlayer());
  }

  private enum Settings {
    PASSIVE_WOLVES_MODULO(3, 2, 1), PASSIVE_GOLEMS_MODULO(4, 3, 2), PASSIVE_UPGRADES_TIER(0, 1, 2),
    ABILITY_FIGHT_OR_FLIGHT_DAMAGE_AMPLIFIER(1, 2, 3);

    private final double earlyValue;
    private final double midValue;
    private final double lateValue;

    Settings(double earlyValue, double midValue, double lateValue) {
      this.earlyValue = earlyValue;
      this.midValue = midValue;
      this.lateValue = lateValue;
    }

    public double getForArenaState(Arena arena) {
      switch(KitSpecifications.getTimeState(arena)) {
        case LATE:
          return lateValue;
        case MID:
          return midValue;
        case EARLY:
        default:
          return earlyValue;
      }
    }
  }

}