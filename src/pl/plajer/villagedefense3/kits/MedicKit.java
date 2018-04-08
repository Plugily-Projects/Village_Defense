package pl.plajer.villagedefense3.kits;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.PermissionsManager;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.kits.kitapi.KitRegistry;
import pl.plajer.villagedefense3.kits.kitapi.basekits.PremiumKit;
import pl.plajer.villagedefense3.utils.ArmorHelper;
import pl.plajer.villagedefense3.utils.Util;
import pl.plajer.villagedefense3.utils.WeaponHelper;

import java.util.List;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {

    public MedicKit(Main plugin) {
        setName(ChatManager.colorMessage("Kits.Medic.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Medic.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        KitRegistry.registerKit(this);
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVip()) || player.hasPermission(PermissionsManager.getMvp()) || player.hasPermission(PermissionsManager.getElite()) || player.hasPermission("villagedefense.kit.medic");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        ArmorHelper.setColouredArmor(Color.WHITE, player);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        player.getInventory().addItem(Util.getPotion(PotionType.REGEN, 1, true, 1));
    }

    @Override
    public Material getMaterial() {
        return Material.GHAST_TEAR;
    }

    @Override
    public void reStock(Player player) {}

    @EventHandler
    public void onZombieHit(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        if(event.getEntity().getType() != EntityType.ZOMBIE)
            return;
        User user = UserManager.getUser(event.getDamager().getUniqueId());
        if(!(user.getKit() instanceof MedicKit))
            return;
        if(Math.random() <= 0.1) {
            for(Entity entity : user.toPlayer().getNearbyEntities(5, 5, 5)) {
                if(entity.getType() == EntityType.PLAYER) {
                    Player player = (Player) entity;
                    if(player.getMaxHealth() > (player.getHealth() + 1)) {
                        player.setHealth(player.getHealth() + 1);
                    } else {
                        player.setHealth(player.getMaxHealth());
                    }
                    player.getEyeLocation().getWorld().playEffect(player.getEyeLocation(), Effect.HEART, 20);
                }
            }
        }
    }
}
