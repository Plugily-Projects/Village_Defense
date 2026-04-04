/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2026 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.kits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.ability.KitAbility;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaUtils;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitAbilityInitializer {

  private final Main plugin;

  public KitAbilityInitializer(Main plugin) {
    this.plugin = plugin;
    registerAbilities();
  }

  public void registerAbilities() {
    plugin.getKitAbilityManager().registerKitAbility("SPAWN_GOLEM", new KitAbility("SPAWN_GOLEM",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
        },
        player -> {
          Arena arena = plugin.getArenaRegistry().getArena(player);
          if(arena != null) {
            arena.spawnWolf(arena.getStartLocation(), player);
          }
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("SPAWN_DOG", new KitAbility("SPAWN_DOG",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
        },
        player -> {
          Arena arena = plugin.getArenaRegistry().getArena(player);
          if(arena != null && (arena.getWave() % 5 == 0 || arena.getWave() == 1)) {
            arena.spawnGolem(arena.getStartLocation(), player);
          }
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("LOOTER", new KitAbility("LOOTER",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
          org.bukkit.entity.LivingEntity entity = entityDeathEvent.getEntity();
          if(!CreatureUtils.isEnemy(entity)) {
            return;
          }
          entity.getKiller().getInventory().addItem(XMaterial.ROTTEN_FLESH.parseItem());
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("DOOR_PLACE", new KitAbility("DOOR_PLACE",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
        },
        player -> {
        },
        blockPlaceEvent -> {
          Arena arena = plugin.getArenaRegistry().getArena(blockPlaceEvent.getPlayer());
          if(arena == null) {
            return;
          }
          if(plugin.getUserManager().getUser(blockPlaceEvent.getPlayer()).isSpectator() || !arena.getMapRestorerManager().getDoorManager().getDoorLocations().contains(blockPlaceEvent.getBlock().getLocation())) {
            blockPlaceEvent.setCancelled(true);
            return;
          }
          if(!XMaterial.valueOf(Utils.getCachedDoor(blockPlaceEvent.getBlock()).name()).isSimilar(VersionUtils.getItemInHand(blockPlaceEvent.getPlayer()))) {
            blockPlaceEvent.setCancelled(true);
            return;
          }
          //to override world guard protection
          blockPlaceEvent.setCancelled(false);
          new MessageBuilder("KIT_CONTENT_WORKER_GAME_ITEM_CHAT").asKey().player(blockPlaceEvent.getPlayer()).sendPlayer();
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("ZOMBIE_TELEPORT", new KitAbility("ZOMBIE_TELEPORT",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          Arena arena = plugin.getArenaRegistry().getArena(playerInteractHandler.getPlayer());
          if(arena == null) {
            return;
          }
          if(!ItemUtils.isItemStackNamed(playerInteractHandler.getItem())) {
            return;
          }
          if(!XMaterial.BOOK.isSimilar(playerInteractHandler.getItem())) {
            return;
          }
          IUser user = plugin.getUserManager().getUser(playerInteractHandler.getPlayer());
          if(user.isSpectator()) {
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
            return;
          }
          if(!user.checkCanCastCooldownAndMessage("zombie")) {
            return;
          }
          if(arena.getEnemies().isEmpty()) {
            new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_WAVE_NEXT_IN").asKey().integer(arena.getTimer()).player(user.getPlayer()).sendPlayer();
            return;
          }

          Creature creature = arena.getEnemies().get(arena.getEnemies().size() == 1 ? 0 : plugin.getRandom().nextInt(arena.getEnemies().size()));
          VersionUtils.teleport(creature, playerInteractHandler.getPlayer().getLocation());
          creature.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0));
          new MessageBuilder("KIT_CONTENT_ZOMBIE_TELEPORTER_TELEPORT_ZOMBIE").asKey().player(user.getPlayer()).sendPlayer();
          VersionUtils.playSound(playerInteractHandler.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH");
          user.setCooldown("zombie", (int) user.getKit().getOptionalConfiguration("cooldown", 30));
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("BARRIER", new KitAbility("BARRIER",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          if(playerInteractHandler.getAction() != Action.RIGHT_CLICK_AIR && playerInteractHandler.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
          }

          Player player = playerInteractHandler.getPlayer();
          Arena arena = (Arena) plugin.getArenaRegistry().getArena(player);
          ItemStack stack = VersionUtils.getItemInHand(player);
          if(!ItemUtils.isItemStackNamed(stack)) {
            return;
          }
          if(!XMaterial.OAK_FENCE.isSimilar(stack)) {
            return;
          }
          Block block = null;
          for(Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
            if(blocks.getType() == Material.AIR) {
              block = blocks;
            }
          }
          if(block == null) {
            new MessageBuilder("KIT_CONTENT_BLOCKER_PLACE_FAIL").asKey().player(player).sendPlayer();
            return;
          }
          plugin.getBukkitHelper().takeOneItem(player, stack);
          playerInteractHandler.setCancelled(false);

          new MessageBuilder("KIT_CONTENT_BLOCKER_PLACE_SUCCESS").asKey().player(player).sendPlayer();
          ZombieBarrier zombieBarrier = new ZombieBarrier();
          zombieBarrier.setLocation(block.getLocation());

          VersionUtils.sendParticles("FIREWORKS_SPARK", arena.getPlayers(), zombieBarrier.location, 20);
          removeBarrierLater(zombieBarrier, arena);
          block.setType(XMaterial.OAK_FENCE.parseMaterial());
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("HEAL_PLAYERS", new KitAbility("HEAL_PLAYERS",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
          if(!(entityDamageByEntityEvent.getEntity() instanceof Creature) || !(entityDamageByEntityEvent.getDamager() instanceof Player)) {
            return;
          }
          if(Math.random() > 0.9) {
            return;
          }
          healNearbyPlayers(entityDamageByEntityEvent.getDamager());
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("BOW_MASTER", new KitAbility("BOW_MASTER",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          if(!(playerInteractHandler.getAction() == Action.LEFT_CLICK_AIR || playerInteractHandler.getAction() == Action.LEFT_CLICK_BLOCK || playerInteractHandler.getAction() == Action.PHYSICAL)) {
            return;
          }

          ItemStack stack = VersionUtils.getItemInHand(playerInteractHandler.getPlayer());
          if(!XMaterial.BOW.isSimilar(stack)) {
            return;
          }

          if(!playerInteractHandler.getPlayer().getInventory().contains(XMaterial.ARROW.get()))
            return;

          IUser user = plugin.getUserManager().getUser(playerInteractHandler.getPlayer());
          if(user.isSpectator()) {
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
            return;
          }
          if(!user.checkCanCastCooldownAndMessage("shotbow")) {
            return;
          }
          for(int i = 0; i < 4; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
              Arrow pr = playerInteractHandler.getPlayer().launchProjectile(Arrow.class);
              pr.setVelocity(playerInteractHandler.getPlayer().getLocation().getDirection().multiply(3));
              pr.setBounce(false);
              pr.setShooter(playerInteractHandler.getPlayer());
              pr.setCritical(true);

              org.bukkit.inventory.PlayerInventory inv = playerInteractHandler.getPlayer().getInventory();

              if(inv.contains(XMaterial.ARROW.get())) {
                inv.removeItem(new ItemStack(XMaterial.ARROW.get(), 1));
              }
            }, 2L * (2 * i));
          }
          playerInteractHandler.setCancelled(true);
          user.setCooldown("shotbow", (int) user.getKit().getOptionalConfiguration("cooldown", 5));
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("TORNADO", new KitAbility("TORNADO",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          if(playerInteractHandler.getAction() != Action.RIGHT_CLICK_AIR && playerInteractHandler.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
          }

          Player player = playerInteractHandler.getPlayer();
          if(!plugin.getArenaRegistry().isInArena(player))
            return;

          ItemStack stack = VersionUtils.getItemInHand(player);
          if(!ItemUtils.isItemStackNamed(stack)) {
            return;
          }
          if(!XMaterial.COBWEB.isSimilar(stack)) {
            return;
          }
          if(activeTornado >= 2) {
            return;
          }
          plugin.getBukkitHelper().takeOneItem(player, stack);
          playerInteractHandler.setCancelled(true);
          prepareTornado(player.getLocation());
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("TELEPORTER", new KitAbility("TELEPORTER",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          if(!(playerInteractHandler.getAction() == Action.RIGHT_CLICK_AIR || playerInteractHandler.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
          }
          Player player = playerInteractHandler.getPlayer();
          Arena arena = plugin.getArenaRegistry().getArena(player);
          if(arena == null) {
            return;
          }
          ItemStack stack = VersionUtils.getItemInHand(player);
          if(!ItemUtils.isItemStackNamed(stack)) {
            return;
          }
          if(!XMaterial.GHAST_TEAR.isSimilar(stack)) {
            return;
          }
          int slots = arena.getVillagers().size();
          for(Player arenaPlayer : arena.getPlayers()) {
            if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
              continue;
            }
            slots++;
          }
          slots = plugin.getBukkitHelper().serializeInt(slots);
          prepareTeleporterGui(player, arena, slots);
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("WIZARD", new KitAbility("WIZARD",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          if(plugin.getArenaRegistry().getArena(playerInteractHandler.getPlayer()) == null) {
            return;
          }
          IUser user = plugin.getUserManager().getUser(playerInteractHandler.getPlayer());
          if(user.isSpectator()) {
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
            return;
          }
          ItemStack stack = VersionUtils.getItemInHand(playerInteractHandler.getPlayer());
          if(!ItemUtils.isItemStackNamed(stack)) {
            return;
          }
          Player player = playerInteractHandler.getPlayer();
          if(XMaterial.BLACK_DYE.isSimilar(stack)) {
            if(!user.checkCanCastCooldownAndMessage("essence")) {
              return;
            }
            wizardsOnDuty.add(player);
            if(VersionUtils.getMaxHealth(player) > (player.getHealth() + 3)) {
              player.setHealth(player.getHealth() + 3);
            } else {
              player.setHealth(VersionUtils.getMaxHealth(player));
            }
            plugin.getBukkitHelper().takeOneItem(player, stack);
            VersionUtils.setGlowing(player, true);
            applyRageParticles(player);
            for(Entity entity : player.getNearbyEntities(2, 2, 2)) {
              if(CreatureUtils.isEnemy(entity)) {
                ((Creature) entity).damage(9.0, player);
              }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
              VersionUtils.setGlowing(player, false);
              wizardsOnDuty.remove(player);
            }, 20L * 15);
            user.setCooldown("essence", (int) user.getKit().getOptionalConfiguration("cooldown", 1) + 14);
          } else if(XMaterial.BLAZE_ROD.isSimilar(stack)) {
            if(!user.checkCanCastCooldownAndMessage("wizard_staff")) {
              return;
            }
            applyMagicAttack(player);
            user.setCooldown("wizard_staff", (int) user.getKit().getOptionalConfiguration("cooldown", 1));
          }
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
          if(!(entityDamageByEntityEvent.getDamager() instanceof Creature && entityDamageByEntityEvent.getEntity() instanceof Player)) {
            return;
          }
          if(!wizardsOnDuty.contains(entityDamageByEntityEvent.getEntity()) || plugin.getArenaRegistry().getArena((Player) entityDamageByEntityEvent.getEntity()) == null) {
            return;
          }
          ((Creature) entityDamageByEntityEvent.getDamager()).damage(2.0, entityDamageByEntityEvent.getEntity());
        }
    ));
    plugin.getKitAbilityManager().registerKitAbility("CLEANER", new KitAbility("CLEANER",
        inventoryClickEvent -> {
        },
        playerInteractHandler -> {
          ItemStack itemStack = playerInteractHandler.getItem();
          if(itemStack == null) {
            return;
          }
          Arena arena = plugin.getArenaRegistry().getArena(playerInteractHandler.getPlayer());
          if(arena == null) {
            return;
          }
          if(!ItemUtils.isItemStackNamed(itemStack)) {
            return;
          }
          if(!XMaterial.BLAZE_ROD.isSimilar(itemStack)) {
            return;
          }
          IUser user = plugin.getUserManager().getUser(playerInteractHandler.getPlayer());
          if(user.isSpectator()) {
            new MessageBuilder("IN_GAME_SPECTATOR_SPECTATOR_WARNING").asKey().player(user.getPlayer()).sendPlayer();
            return;
          }
          if(!user.checkCanCastCooldownAndMessage("zombie")) {
            return;
          }
          if(arena.getEnemies().isEmpty()) {
            new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_NOTHING").asKey().player(user.getPlayer()).sendPlayer();
            return;
          }
          int amount = (int) (arena.getEnemies().size() * Math.max(Math.random(), 0.5));
          ArenaUtils.removeSpawnedEnemies(arena, amount, arena.getEnemies().get(0).getHealth());

          VersionUtils.playSound(playerInteractHandler.getPlayer().getLocation(), "ENTITY_ZOMBIE_DEATH");
          new MessageBuilder("KIT_CONTENT_CLEANER_CLEANED_MAP").asKey().arena(arena).player(user.getPlayer()).sendArena();
          user.setCooldown("clean", (int) user.getKit().getOptionalConfiguration("cooldown", 60));
        },
        player -> {
        },
        blockPlaceEvent -> {
        },
        entityDeathEvent -> {
        },
        entityDamageByEntityEvent -> {
        }
    ));

  }

  private final List<Player> wizardsOnDuty = new ArrayList<>();

  private void applyRageParticles(Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        Location loc = player.getLocation();
        loc.add(0, 0.8, 0);
        VersionUtils.sendParticles("VILLAGER_ANGRY", null, loc, 5, 0, 0, 0);
        if(!wizardsOnDuty.contains(player) || !plugin.getArenaRegistry().isInArena(player)) {
          cancel();
        }
      }
    }.runTaskTimer(plugin, 0, 2);
  }

  private void applyMagicAttack(Player player) {
    new BukkitRunnable() {
      double positionModifier = 0;
      final Location loc = player.getLocation();
      final Vector direction = loc.getDirection().normalize();

      @Override
      public void run() {
        positionModifier += 0.5;
        double x = direction.getX() * positionModifier,
            y = direction.getY() * positionModifier + 1.5,
            z = direction.getZ() * positionModifier;
        loc.add(x, y, z);
        VersionUtils.sendParticles("TOWN_AURA", null, loc, 5, 0, 0, 0);
        for(Entity en : loc.getChunk().getEntities()) {
          if(!(CreatureUtils.isEnemy(en)) || en.getLocation().distance(loc) >= 1.5 || en.equals(player)) {
            continue;
          }
          ((LivingEntity) en).damage(6.0, player);
          VersionUtils.sendParticles("FIREWORKS_SPARK", null, en.getLocation(), 2, 0.5, 0.5, 0.5);
        }
        loc.subtract(x, y, z);
        if(positionModifier > 40) {
          cancel();
        }
      }
    }.runTaskTimer(plugin, 0, 1);
  }

  private void prepareTeleporterGui(Player player, Arena arena, int slots) {
    NormalFastInv gui = new NormalFastInv(slots, new MessageBuilder("KIT_CONTENT_TELEPORTER_GAME_ITEM_GUI").asKey().build());
    gui.addClickHandler(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
      SkullMeta meta = (SkullMeta) skull.getItemMeta();
      meta = VersionUtils.setPlayerHead(player, meta);
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(""));
      skull.setItemMeta(meta);
      gui.addItem(skull, onClick -> {
        new MessageBuilder("KIT_CONTENT_TELEPORTER_TELEPORT_PLAYER").asKey().arena(arena).player(arenaPlayer).sendPlayer();
        VersionUtils.teleport(player, arenaPlayer.getLocation());
        VersionUtils.playSound(player.getLocation(), "ENTITY_ENDERMAN_TELEPORT");
        VersionUtils.sendParticles("PORTAL", arena.getPlayers(), player.getLocation(), 30);
        player.closeInventory();
      });
    }
    for(Villager villager : arena.getVillagers()) {
      gui.addItem(new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(villager.getCustomName())
          .build(), onClick -> {
        VersionUtils.teleport(player, villager.getLocation());
        VersionUtils.playSound(player.getLocation(), "ENTITY_ENDERMAN_TELEPORT");
        VersionUtils.sendParticles("PORTAL", arena.getPlayers(), player.getLocation(), 30);
        new MessageBuilder("KIT_CONTENT_TELEPORTER_TELEPORT_VILLAGER").asKey().player(player).sendPlayer();
      });
    }
    gui.open(player);
  }

  private void prepareTornado(Location location) {
    Tornado tornado = new Tornado(location);
    activeTornado++;
    new BukkitRunnable() {
      @Override
      public void run() {
        tornado.update();
        if(tornado.entities >= 7 || tornado.times > 55) {
          cancel();
          activeTornado--;
        }
      }
    }.runTaskTimer(plugin, 1, 1);
  }

  private final int maxHeight = 5;
  private final double maxRadius = 4;
  private final double radiusIncrement = maxRadius / maxHeight;
  private int activeTornado = 0;

  private class Tornado {
    private Location location;
    private final Vector vector;
    private int angle;
    private int times = 0;
    private int entities = 0;

    Tornado(Location location) {
      this.location = location;
      vector = location.getDirection();
    }

    void setLocation(Location location) {
      this.location = location;
    }

    void update() {
      times++;
      int lines = 3;
      for(int l = 0; l < lines; l++) {
        for(double y = 0; y < maxHeight; y += 0.5) {
          double radius = y * radiusIncrement,
              radians = Math.toRadians(360.0 / lines * l + y * 25 - angle),
              x = Math.cos(radians) * radius,
              z = Math.sin(radians) * radius;
          VersionUtils.sendParticles("CLOUD", null, location.clone().add(x, y, z), 1, 0, 0, 0);
        }
      }
      pushNearbyEnemies();
      setLocation(location.add(vector.getX() / (3 + Math.random() / 2), 0, vector.getZ() / (3 + Math.random() / 2)));

      angle += 50;
    }

    private void pushNearbyEnemies() {
      for(Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
        if(CreatureUtils.isEnemy(entity)) {
          entities++;

          Vector velocityVec = vector.multiply(2).setY(0).add(new Vector(0, 1, 0));
          if(VersionUtils.isPaper() && (vector.getX() > 4.0 || vector.getZ() > 4.0)) {
            velocityVec = vector.setX(2.0).setZ(1.0); // Paper's sh*t
          }

          entity.setVelocity(velocityVec);
        }
      }
    }
  }

  private void healNearbyPlayers(Entity en) {
    for(Entity entity : en.getNearbyEntities(5, 5, 5)) {
      if(!(entity instanceof Player)) {
        continue;
      }

      Player player = (Player) entity;
      double newHealth = player.getHealth() + 1;
      double maxHealth = VersionUtils.getMaxHealth(player);

      if(maxHealth > newHealth) {
        player.setHealth(newHealth);
      } else {
        player.setHealth(maxHealth);
      }

      VersionUtils.sendParticles("HEART", player, player.getLocation(), 20);
    }
  }

  private static class ZombieBarrier {
    private Location location;
    private int seconds = 10;

    void setLocation(Location location) {
      this.location = location;
    }

    void decrementSeconds() {
      seconds--;
    }
  }

  private void removeBarrierLater(ZombieBarrier zombieBarrier, PluginArena arena) {
    new BukkitRunnable() {
      @Override
      public void run() {
        zombieBarrier.decrementSeconds();

        if(zombieBarrier.seconds <= 0) {
          zombieBarrier.location.getBlock().setType(Material.AIR);
          VersionUtils.sendParticles("FIREWORKS_SPARK", arena.getPlayers(), zombieBarrier.location, 20);
          cancel();
        }
      }
    }.runTaskTimer(plugin, 20, 20);
  }
}
