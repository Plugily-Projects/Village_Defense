/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.items.SpecialItemManager;
import pl.plajer.villagedefense3.kits.GolemFriendKit;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameJoinAttemptEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameLeaveAttemptEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageGameStopEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageWaveEndEvent;
import pl.plajer.villagedefense3.villagedefenseapi.VillageWaveStartEvent;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class ArenaManager {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    /**
     * Attempts player to join arena.
     * Calls VillageGameJoinAttemptEvent.
     * Can be cancelled only via above-mentioned event
     *
     * @param p player to join
     * @see VillageGameJoinAttemptEvent
     */
    public static void joinAttempt(Player p, Arena arena) {
        Main.debug("Initial join attempt, " + p.getName(), System.currentTimeMillis());
        VillageGameJoinAttemptEvent villageGameJoinAttemptEvent = new VillageGameJoinAttemptEvent(p, arena);
        Bukkit.getPluginManager().callEvent(villageGameJoinAttemptEvent);
        if(!arena.isReady()) {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Arena-Not-Configured"));
            return;
        }
        if(villageGameJoinAttemptEvent.isCancelled()) {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
            return;
        }
        if(!plugin.isBungeeActivated()) {
            if(!(p.hasPermission(PermissionsManager.getJoinPerm().replaceAll("<arena>", "*")) || p.hasPermission(PermissionsManager.getJoinPerm().replaceAll("<arena>", arena.getID())))) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-No-Permission"));
                return;
            }
        }
        Main.debug("Final join attempt, " + p.getName(), System.currentTimeMillis());
        if((arena.getArenaState() == ArenaState.IN_GAME || (arena.getArenaState() == ArenaState.STARTING && arena.getTimer() <= 3) || arena.getArenaState() == ArenaState.ENDING)) {
            if(plugin.isInventoryManagerEnabled()) {
                p.setLevel(0);
                plugin.getInventoryManager().saveInventoryToFile(p);

            }
            arena.teleportToStartLocation(p);
            p.sendMessage(ChatManager.colorMessage("In-Game.You-Are-Spectator"));
            p.getInventory().clear();

            ItemStack spectatorItem = new ItemStack(Material.COMPASS, 1);
            ItemMeta spectatorMeta = spectatorItem.getItemMeta();
            spectatorMeta.setDisplayName(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"));
            spectatorItem.setItemMeta(spectatorMeta);
            p.getInventory().setItem(0, spectatorItem);

            p.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());

            for(PotionEffect potionEffect : p.getActivePotionEffects()) {
                p.removePotionEffect(potionEffect.getType());
            }

            arena.addPlayer(p);
            p.setMaxHealth(p.getMaxHealth() + arena.getRottenFleshLevel());
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(true);
            p.setFlying(true);
            User user = UserManager.getUser(p.getUniqueId());
            user.setSpectator(true);
            user.setFakeDead(true);
            user.setInt("orbs", 0);
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
            ArenaUtils.hidePlayer(p, arena);

            for(Player spectator : arena.getPlayers()) {
                if(UserManager.getUser(spectator.getUniqueId()).isSpectator()) {
                    p.hidePlayer(spectator);
                } else {
                    p.showPlayer(spectator);
                }
            }
            ArenaUtils.hidePlayersOutsideTheGame(p, arena);
            return;
        }
        if(plugin.isInventoryManagerEnabled()) {
            p.setLevel(0);
            plugin.getInventoryManager().saveInventoryToFile(p);

        }
        arena.teleportToLobby(p);
        arena.addPlayer(p);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.setFlying(false);
        p.setAllowFlight(false);
        p.getInventory().clear();
        arena.showPlayers();
        if(plugin.isBossbarEnabled()) {
            arena.getGameBar().addPlayer(p);
        }
        if(!UserManager.getUser(p.getUniqueId()).isSpectator())
            ChatManager.broadcastAction(arena, p, ChatManager.ActionType.JOIN);
        User user = UserManager.getUser(p.getUniqueId());
        user.setKit(KitRegistry.getDefaultKit());
        plugin.getKitManager().giveKitMenuItem(p);
        if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS)
            p.getInventory().setItem(SpecialItemManager.getSpecialItem("Leave").getSlot(), SpecialItemManager.getSpecialItem("Leave").getItemStack());
        p.updateInventory();
        for(Player player : arena.getPlayers()) {
            ArenaUtils.showPlayer(player, arena);
        }
        arena.showPlayers();
    }

    /**
     * Attempts player to leave arena.
     * Calls VillageGameLeaveAttemptEvent event.
     *
     * @param p player to join
     * @see VillageGameLeaveAttemptEvent
     */
    public static void leaveAttempt(Player p, Arena arena) {
        Main.debug("Initial leave attempt, " + p.getName(), System.currentTimeMillis());
        VillageGameLeaveAttemptEvent villageGameLeaveAttemptEvent = new VillageGameLeaveAttemptEvent(p, arena);
        Bukkit.getPluginManager().callEvent(villageGameLeaveAttemptEvent);
        User user = UserManager.getUser(p.getUniqueId());
        user.setInt("orbs", 0);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        arena.removePlayer(p);
        if(!user.isSpectator()) {
            ChatManager.broadcastAction(arena, p, ChatManager.ActionType.LEAVE);
        }
        user.setFakeDead(false);
        user.setSpectator(false);
        user.removeScoreboard();
        if(user.getKit() instanceof GolemFriendKit) {
            for(IronGolem ironGolem : arena.getIronGolems()) {
                if(ironGolem.getCustomName().contains(user.toPlayer().getName())) {
                    ironGolem.remove();
                }
            }
        }
        if(plugin.isBossbarEnabled()) {
            arena.getGameBar().removePlayer(p);
        }
        p.setMaxHealth(20.0);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        for(PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setFireTicks(0);
        if(arena.getPlayers().size() == 0) {
            arena.setArenaState(ArenaState.ENDING);
            arena.setTimer(0);
        }

        p.setGameMode(GameMode.SURVIVAL);
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(ArenaRegistry.getArena(players) != null)
                players.showPlayer(p);
            p.showPlayer(players);
        }
        arena.teleportToEndLocation(p);
        if(!plugin.isBungeeActivated() && plugin.isInventoryManagerEnabled()) {
            plugin.getInventoryManager().loadInventory(p);
        }
    }

    /**
     * Stops current arena. Calls VillageGameStopEvent event
     *
     * @param quickStop should arena be stopped immediately? (use only in important cases)
     * @see VillageGameStopEvent
     */
    public static void stopGame(boolean quickStop, Arena arena) {
        Main.debug("Game stop event initiate, arena " + arena.getID(), System.currentTimeMillis());
        VillageGameStopEvent villageGameStopEvent = new VillageGameStopEvent(arena);
        Bukkit.getPluginManager().callEvent(villageGameStopEvent);
        for(final Player p : arena.getPlayers()) {
            if(arena.getPlayersLeft().size() > 0) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.All-Villagers-Died"));
            } else {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.All-Players-Died"));
            }
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Reached-Wave-X").replaceAll("%NUMBER%", String.valueOf(arena.getWave())));
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds"));
            User user = UserManager.getUser(p.getUniqueId());
            if(user.getInt("highestwave") <= arena.getWave()) {
                user.setInt("highestwave", arena.getWave());
            }
            arena.addExperience(p, arena.getWave());

            UserManager.getUser(p.getUniqueId()).removeScoreboard();
            if(!quickStop) {
                if(plugin.getConfig().getBoolean("Firework-When-Game-Ends")) {
                    new BukkitRunnable() {
                        int i = 0;
                        public void run() {
                            if(i == 4) this.cancel();
                            if(!arena.getPlayers().contains(p)) this.cancel();
                            Util.spawnRandomFirework(p.getLocation());
                            i++;
                        }
                    }.runTaskTimer(plugin, 30, 30);
                }
            }
        }
        arena.setRottenFleshAmount(0);
        arena.setRottenFleshLevel(0);
        arena.restoreDoors();
        for(Zombie zombie : arena.getZombies()) {
            zombie.remove();
        }
        arena.getZombies().clear();
        for(IronGolem ironGolem : arena.getIronGolems()) {
            ironGolem.remove();
        }
        arena.getIronGolems().clear();
        for(Villager villager : arena.getVillagers()) {
            villager.remove();
        }
        arena.getVillagers().clear();
        for(Wolf wolf : arena.getWolfs()) {
            wolf.remove();
        }
        arena.getWolfs().clear();
        for(Entity entity : arena.getStartLocation().getWorld().getEntities()) {
            if(entity.getWorld().getName().equalsIgnoreCase(arena.getStartLocation().getWorld().getName())
                    && entity.getLocation().distance(arena.getStartLocation()) < 300)
                if(entity.getType() != EntityType.PLAYER)
                    entity.remove();
        }
        Main.debug("Game stop event finish, arena " + arena.getID(), System.currentTimeMillis());
    }

    /**
     * End wave in game.
     * Calls VillageWaveEndEvent event
     *
     * @see VillageWaveEndEvent
     */
    public static void endWave(Arena arena) {
        plugin.getRewardsHandler().performEndWaveRewards(arena, arena.getWave());
        arena.setTimer(25);
        arena.getZombieCheckerLocations().clear();
        arena.setWave(arena.getWave() + 1);
        VillageWaveEndEvent villageWaveEndEvent = new VillageWaveEndEvent(arena, arena.getWave());
        Bukkit.getPluginManager().callEvent(villageWaveEndEvent);
        for(Player player : arena.getPlayers()) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Next-Wave-In"), arena.getTimer()));
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.You-Feel-Refreshed"));
            player.setHealth(player.getMaxHealth());
            User user = UserManager.getUser(player.getUniqueId());
            user.addInt("orbs", arena.getWave() * 10);
        }
        if(plugin.getConfig().getBoolean("Respawn-After-Wave"))
            ArenaUtils.bringDeathPlayersBack(arena);
        for(Player player : arena.getPlayersLeft()) {
            arena.addExperience(player, 5);
        }
    }

    /**
     * Starts wave in game.
     * Calls VillageWaveStartEvent event
     *
     * @see VillageWaveStartEvent
     */
    public static void startWave(Arena arena) {
        VillageWaveStartEvent villageWaveStartEvent = new VillageWaveStartEvent(arena, arena.getWave());
        Bukkit.getPluginManager().callEvent(villageWaveStartEvent);
        arena.setZombieAmount();
        if(plugin.getConfig().getBoolean("Respawn-After-Wave"))
            ArenaUtils.bringDeathPlayersBack(arena);
        for(User user : UserManager.getUsers(arena)) {
            user.getKit().reStock(user.toPlayer());
        }
        String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Wave-Started"), arena.getWave());
        for(Player p : arena.getPlayers()) {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
        }
    }

}
