package me.tomthedeveloper.kits;

import me.tomthedeveloper.Main;
import me.tomthedeveloper.User;
import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.UserManager;
import me.tomthedeveloper.kitapi.basekits.PremiumKit;
import me.tomthedeveloper.permissions.PermissionsManager;
import me.tomthedeveloper.utils.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.List;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {


    private Main plugin;

    public MedicKit(Main plugin) {
        this.plugin = plugin;
        setName(ChatManager.colorMessage("kits.Medic.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("kits.Medic.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVIP()) || player.hasPermission(PermissionsManager.getMVP()) || player.hasPermission(PermissionsManager.getELITE()) || player.hasPermission("villagedefense.kit.medic");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
        ArmorHelper.setColouredArmor(Color.WHITE, player);
        player.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 8));
        player.getInventory().addItem(Items.getPotion(PotionType.REGEN, 1, true, 1));
    }

    @Override
    public Material getMaterial() {
        return Material.GHAST_TEAR;
    }

    @Override
    public void reStock(Player player) {

    }


    @EventHandler
    public void onZombieHit(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER)
            return;
        if (event.getEntity().getType() != EntityType.ZOMBIE)
            return;
        User user = UserManager.getUser(event.getDamager().getUniqueId());
        if (!(user.getKit() instanceof MedicKit))
            return;
        if (Math.random() <= 0.1) {
            for (Entity entity : user.toPlayer().getNearbyEntities(5, 5, 5)) {
                if (entity.getType() == EntityType.PLAYER) {
                    Player player = (Player) entity;
                    player.setHealth(player.getMaxHealth() != player.getHealth() ? player.getHealth() + 1 : 0);
                    if (!plugin.is1_12_R1()) {
                        ParticleEffect.HEART.display(0, 0, 0, 0, 10, player.getEyeLocation(), 255);
                    } else {
                        player.getEyeLocation().getWorld().spawnParticle(Particle.HEART, player.getEyeLocation(), 20, 1, 1, 1, 1);
                    }
                }

            }
        }
    }
}
