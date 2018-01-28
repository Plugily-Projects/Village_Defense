package me.tomthedeveloper.kits;

import me.tomthedeveloper.GameAPI;
import me.tomthedeveloper.InvasionInstance;
import me.tomthedeveloper.Main;
import me.tomthedeveloper.game.GameInstance;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.PremiumKit;
import me.tomthedeveloper.permissions.PermissionsManager;
import me.tomthedeveloper.utils.ArmorHelper;
import me.tomthedeveloper.utils.ParticleEffect;
import me.tomthedeveloper.utils.Util;
import me.tomthedeveloper.utils.WeaponHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class TeleporterKit extends PremiumKit implements Listener {

    private Main plugin;
    private GameAPI gameAPI;

    public TeleporterKit(Main plugin) {
        this.plugin = plugin;
        gameAPI = plugin.getGameAPI();
        setName(ChatManager.colorMessage("kits.Teleporter.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Teleporter.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVIP()) || player.hasPermission(PermissionsManager.getMVP()) || player.hasPermission(PermissionsManager.getELITE()) || player.hasPermission("villagedefense.kit.teleporter");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        ItemStack enderpealteleporter = new ItemStack(Material.ENDER_PEARL);
        List<String> teleporationlore = Util.splitString(ChatManager.colorMessage("kits.Teleporter.Game-Item-Lore"), 40);
        this.setItemNameAndLore(enderpealteleporter, ChatManager.colorMessage("kits.Teleporter.Game-Item-Name"), teleporationlore.toArray(new String[teleporationlore.size()]));
        player.getInventory().addItem(enderpealteleporter);
    }

    @Override
    public Material getMaterial() {
        return Material.ENDER_PEARL;
    }

    @Override
    public void reStock(Player player) {

    }

    public void OpenAndCreateTeleportationMenu(World world, Player p) {
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        Inventory inventory = plugin.getServer().createInventory(null, 18, ChatManager.colorMessage("kits.Teleporter.Game-Item-Menu-Name"));
        for (Player player : world.getPlayers()) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) != null && !UserManager.getUser(player.getUniqueId()).isFakeDead()) {
                ItemStack skull = new ItemStack(397, 1, (short) 3);

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(player.getName());
                meta.setDisplayName(player.getName());
                meta.setLore(Arrays.asList(""));
                skull.setItemMeta(meta);
                inventory.addItem(skull);
            }
        }
        for (Villager villager : ((InvasionInstance) gameInstance).getVillagers()) {


            ItemStack villageritem = new ItemStack(Material.EMERALD);
            this.setItemNameAndLore(villageritem, villager.getCustomName(), new String[]{villager.getUniqueId().toString()});

            inventory.addItem(villageritem);

        }
        p.openInventory(inventory);
    }


    @EventHandler
    public void OpenInventoryRightClickEnderPearl(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gameAPI.getGameInstanceManager().getGameInstance(e.getPlayer()) == null)
                return;
            if (!(e.getPlayer().getItemInHand() == null)) {
                if (e.getPlayer().getItemInHand().hasItemMeta()) {
                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null)
                        return;

                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("kits.Teleporter.Game-Item-Name"))) {
                        OpenAndCreateTeleportationMenu(e.getPlayer().getWorld(), e.getPlayer());
                    }
                }
            }
            if (e.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
                e.setCancelled(true);
            }

        }
    }


    @EventHandler
    public void PlayerClickToTeleport(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (gameAPI.getGameInstanceManager().getGameInstance(p) == null)
            return;
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        if (e.getCurrentItem() == null)
            return;
        if (!e.getCurrentItem().hasItemMeta())
            return;
        if (!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if (!e.getCurrentItem().getItemMeta().hasLore())
            return;
        if (e.getCurrentItem().hasItemMeta()) {
            if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("kits.Teleporter.Game-Item-Menu-Name"))) {
                e.setCancelled(true);
                if ((e.isLeftClick() || e.isRightClick())) {
                    if (e.getCurrentItem().getType() == Material.EMERALD) {
                        boolean villagerfound = false;
                        for (Villager villager : ((InvasionInstance) gameInstance).getVillagers()) {
                            if (villager.getCustomName() == null) {
                                villager.remove();
                            }
                            if (villager.getCustomName().equalsIgnoreCase(e.getCurrentItem().getItemMeta().getDisplayName()) && villager.getUniqueId().toString().equalsIgnoreCase(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0)))) {
                                e.getWhoClicked().teleport(villager.getLocation());
                                if(plugin.is1_9_R1() || plugin.is1_12_R1()) {
                                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
                                } else {
                                    p.getWorld().playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
                                }
                                if (!plugin.is1_12_R1())
                                    ParticleEffect.PORTAL.display(1, 1, 1, 10, 30, p.getLocation(), 100);
                                else {
                                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30, 1, 1, 1);
                                }
                                villagerfound = true;
                                p.sendMessage(ChatManager.colorMessage("kits.Teleporter.Teleported-To-Villager"));
                                break;
                            }
                        }
                        if (!villagerfound) {
                            p.sendMessage(ChatManager.colorMessage("kits.Teleporter.Villager-Warning"));
                        }
                        villagerfound = false;
                        e.setCancelled(true);
                    } else { /*if(e.getCurrentItem().getType() == Material.SKULL_ITEM || e.getCurrentItem().getType() == Material.SKULL)*/

                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        for (Player player : gameInstance.getPlayers()) {
                            if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
                                p.sendMessage(ChatManager.formatMessage(ChatManager.colorMessage("kits.Teleporter.Teleported-To-Player"), player));
                                p.teleport(player);
                                if (plugin.is1_9_R1() || plugin.is1_12_R1()) {
                                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
                                } else {
                                    p.getWorld().playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
                                }
                                if (!plugin.is1_12_R1())
                                    ParticleEffect.PORTAL.display(1, 1, 1, 10, 30, p.getLocation(), 100);
                                else {
                                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30, 1, 1, 1);
                                }
                                p.closeInventory();
                                e.setCancelled(true);
                                return;

                            }
                        }
                        p.sendMessage(ChatManager.colorMessage("kits.Teleporter.Player-Not-Found"));
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

}
