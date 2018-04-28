package pl.plajer.villagedefense3.kits;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2018
 */
public class WizardKit extends PremiumKit implements Listener {

    private List<Player> wizardsOnDuty = new ArrayList<>();
    private Main plugin;

    public WizardKit(Main plugin) {
        setName(ChatManager.colorMessage("Kits.Wizard.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Wizard.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        KitRegistry.registerKit(this);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite()) || player.hasPermission("villagedefense.kit.wizard");
    }

    @Override
    public void giveKitItems(Player player) {
        ItemStack wizardStaff = new ItemStack(Material.BLAZE_ROD, 1);
        List<String> staffLore = Util.splitString(ChatManager.colorMessage("Kits.Wizard.Staff-Item-Lore"), 40);
        this.setItemNameAndLore(wizardStaff, ChatManager.colorMessage("Kits.Wizard.Staff-Item-Name"), staffLore.toArray(new String[staffLore.size()]));
        player.getInventory().addItem(wizardStaff);

        ItemStack essenceOfDarkness = new ItemStack(Material.INK_SACK, 4);
        List<String> essenceLore = Util.splitString(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Lore"), 40);
        this.setItemNameAndLore(essenceOfDarkness, ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"), essenceLore.toArray(new String[essenceLore.size()]));
        player.getInventory().addItem(essenceOfDarkness);

        ArmorHelper.setColouredArmor(Color.GRAY, player);
        player.getInventory().addItem(new ItemStack(Material.SADDLE));

    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public void reStock(Player player) {
        ItemStack essenceOfDarkness = new ItemStack(Material.INK_SACK, 1);
        List<String> essenceLore = Util.splitString(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Lore"), 40);
        this.setItemNameAndLore(essenceOfDarkness, ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"), essenceLore.toArray(new String[essenceLore.size()]));
        player.getInventory().addItem(essenceOfDarkness);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(wizardsOnDuty.contains(e.getPlayer())) wizardsOnDuty.remove(e.getPlayer());
    }

    @EventHandler
    public void onWizardDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Zombie && e.getEntity() instanceof Player) {
            if(!wizardsOnDuty.contains(e.getEntity())) return;
            if(ArenaRegistry.getArena((Player) e.getEntity()) == null) return;
            ((Zombie) e.getDamager()).damage(2.0);
        }
    }

    @EventHandler
    public void onStaffUse(PlayerInteractEvent e) {
        if(UserManager.getUser(e.getPlayer().getUniqueId()) == null) return;
        if(ArenaRegistry.getArena(e.getPlayer()) == null) return;
        if(!(UserManager.getUser(e.getPlayer().getUniqueId()).getKit() instanceof WizardKit)) return;
        final Player p = e.getPlayer();
        ItemStack is = e.getPlayer().getItemInHand();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            if(is.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"))) {
                if(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("essence") > 0 && !UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
                    String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
                    msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("essence")));
                    e.getPlayer().sendMessage(msgstring);
                    return;
                }
                wizardsOnDuty.add(p);
                if(p.getMaxHealth() > (p.getHealth() + 3)) {
                    p.setHealth(p.getHealth() + 3);
                } else {
                    p.setHealth(p.getMaxHealth());
                }
                if(p.getItemInHand().getAmount() <= 1) {
                    ItemStack essenceOfDarkness = new ItemStack(Material.INK_SACK, 1);
                    List<String> essenceLore = Util.splitString(ChatManager.colorMessage("Kits.Wizard.Essence-Item-Lore"), 40);
                    this.setItemNameAndLore(essenceOfDarkness, ChatManager.colorMessage("Kits.Wizard.Essence-Item-Name"), essenceLore.toArray(new String[essenceLore.size()]));
                    p.setItemInHand(essenceOfDarkness);
                } else {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                    p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation(), 40, 1, 1, 1);
                } else if(plugin.is1_8_R3()){
                    p.getWorld().spigot().playEffect(p.getLocation(), Effect.MAGIC_CRIT, 0, 0, 1, 1, 1, 1, 40, 50);
                }
                for(Entity en : p.getNearbyEntities(2, 2, 2)) {
                    if(en instanceof Zombie) {
                        ((Zombie) en).damage(9.0, p);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> wizardsOnDuty.remove(p), 20 * 15);
                UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("essence", 15);
            }
            if(is.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Kits.Wizard.Staff-Item-Name"))) {
                if(UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
                    e.getPlayer().sendMessage(ChatManager.colorMessage("Kits.Cleaner.Spectator-Warning"));
                    return;
                }
                if(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("wizard_staff") > 0 && !UserManager.getUser(e.getPlayer().getUniqueId()).isSpectator()) {
                    String msgstring = ChatManager.colorMessage("Kits.Ability-Still-On-Cooldown");
                    msgstring = msgstring.replaceFirst("%COOLDOWN%", Long.toString(UserManager.getUser(e.getPlayer().getUniqueId()).getCooldown("wizard_staff")));
                    e.getPlayer().sendMessage(msgstring);
                    return;
                }
                new BukkitRunnable() {
                    double t = 0;
                    Location loc = p.getLocation();
                    Vector direction = loc.getDirection().normalize();

                    @Override
                    public void run() {
                        t += 0.5;
                        double x = direction.getX() * t;
                        double y = direction.getY() * t + 1.5;
                        double z = direction.getZ() * t;
                        loc.add(x, y, z);
                        if(plugin.is1_9_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                            p.getWorld().spawnParticle(Particle.TOWN_AURA, loc, 5);
                        } else if(plugin.is1_8_R3()){
                            p.getWorld().spigot().playEffect(loc, Effect.VOID_FOG, 0, 0, 0, 0, 0, 1, 50, 50);
                        }
                        for(Entity en : loc.getChunk().getEntities()) {
                            if(!(en instanceof LivingEntity && en instanceof Zombie)) continue;
                            if(en.getLocation().distance(loc) < 1.0) {
                                if(!en.equals(p)) {
                                    ((LivingEntity) en).damage(6.0, p);
                                }
                            }
                        }
                        loc.subtract(x, y, z);
                        if(t > 40) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1);
                UserManager.getUser(e.getPlayer().getUniqueId()).setCooldown("wizard_staff", 1);
            }
        }
    }
}
