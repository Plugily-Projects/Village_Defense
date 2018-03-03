package pl.plajer.villagedefense3.kits;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 30/12/2015.
 */
public class TornadoKit extends PremiumKit implements Listener {

    private int max_height = 5;
    private double max_radius = 4;
    private int lines = 3;
    private double height_increasement = 0.5;
    private double radius_increasement = max_radius / max_height;
    private Main plugin;
    private List<Tornado> tornados = new ArrayList<>();


    public TornadoKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("Kits.Tornado.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            ArrayList<Tornado> removeAfter = new ArrayList<>();
            for(Tornado tornado : tornados) {
                if(tornado.getTimes() > 75) {
                    removeAfter.add(tornado);
                }
                tornado.update();

            }
            tornados.removeAll(removeAfter);
        }, 1L, 1L);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getKitRegistry().registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("villagedefense.kit.tornado") || player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite());
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        ItemStack enderpealteleporter = new ItemStack(Material.WEB, 5);
        List<String> teleporationlore = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Game-Item-Lore"), 40);
        this.setItemNameAndLore(enderpealteleporter, ChatManager.colorMessage("Kits.Tornado.Game-Item-Name"), teleporationlore.toArray(new String[teleporationlore.size()]));
        player.getInventory().addItem(enderpealteleporter);
    }

    @Override
    public Material getMaterial() {
        return Material.WEB;
    }

    @Override
    public void reStock(Player player) {
        ItemStack enderpealteleporter = new ItemStack(Material.WEB, 5);
        List<String> teleporationlore = Util.splitString(ChatManager.colorMessage("Kits.Tornado.Game-Item-Lore"), 40);
        this.setItemNameAndLore(enderpealteleporter, ChatManager.colorMessage("Kits.Tornado.Game-Item-Name"), teleporationlore.toArray(new String[teleporationlore.size()]));
        player.getInventory().addItem(enderpealteleporter);
    }

    @EventHandler
    public void onTornadoSpawn(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        if(player.getItemInHand() == null)
            return;
        if(plugin.getGameInstanceManager().getGameInstance(player) == null)
            return;
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Kits.Tornado.Game-Item-Name")))
            return;
        if(player.getItemInHand().getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR));

        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
        event.setCancelled(true);
        tornados.add(new Tornado(player.getLocation()));

    }

    private class Tornado {

        private Location location;
        private Vector vector;
        private int angle;
        private int times;


        Tornado(Location location) {
            this.location = location;
            this.vector = location.getDirection();
            times = 0;
        }

        int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        Vector getVector() {
            return vector;
        }

        public void setVector(Vector vector) {
            this.vector = vector;
        }

        Location getLocation() {
            return location;
        }

        void setLocation(Location location) {
            this.location = location;
        }

        void update() {
            times++;
            for(int l = 0; l < lines; l++) {
                for(double y = 0; y < max_height; y += height_increasement) {
                    double radius = y * radius_increasement;
                    double x = Math.cos(Math.toRadians(360 / lines * l + y * 25 - angle)) * radius;
                    double z = Math.sin(Math.toRadians(360 / lines * l + y * 25 - angle)) * radius;
                    getLocation().getWorld().spigot().playEffect(getLocation().clone().add(x, y, z), Effect.CLOUD, 0, 0, 0, 0, 0, 0, 1, 255);

                }
            }

            for(Entity entity : getLocation().getWorld().getNearbyEntities(getLocation(), 2, 2, 2)) {
                if(entity.getType() == EntityType.ZOMBIE) {
                    entity.setVelocity(getVector().multiply(2).setY(0).add(new Vector(0, 1, 0)));
                }
            }
            setLocation(getLocation().add(getVector().getX() / (3 + Math.random() / 2), 0, getVector().getZ() / (3 + Math.random() / 2)));

            angle += 50;

        }
    }
}
