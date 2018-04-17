package pl.plajer.villagedefense3.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Plajer
 * <p>
 * Created at 08.04.2018
 */
public class PowerupManager {

    private boolean enabled = false;
    private Main plugin;

    public PowerupManager(Main plugin) {
        if(!plugin.getConfig().getBoolean("Powerups.Enabled")) return;
        if(plugin.getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
            Main.debug("Power up module: Holographic Displays dependency not found, disabling", System.currentTimeMillis());
            return;
        }
        enabled = true;
        this.plugin = plugin;
        Main.debug("Registering power ups module!", System.currentTimeMillis());
        PowerupType.CLEANER.setName(ChatManager.colorMessage("Powerups.Map-Clean-Powerup.Name"));
        PowerupType.CLEANER.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Map-Clean"));

        PowerupType.DOUBLE_DAMAGE.setName(ChatManager.colorMessage("Powerups.Double-Damage-Powerup.Name"));
        PowerupType.DOUBLE_DAMAGE.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Double-Damage-For-Players.Enabled"));

        PowerupType.GOLEM_RAID.setName(ChatManager.colorMessage("Powerups.Golem-Raid-Powerup.Name"));
        PowerupType.GOLEM_RAID.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Golem-Raid.Enabled"));

        PowerupType.HEALING.setName(ChatManager.colorMessage("Powerups.Healing-Powerup.Name"));
        PowerupType.HEALING.setEnabled(plugin.getConfig().getBoolean("Powerups.List.Healing-For-Players.Enabled"));

        PowerupType.ONE_SHOT_ONE_KILL.setName(ChatManager.colorMessage("Powerups.One-Shot-One-Kill-Powerup.Name"));
        PowerupType.DOUBLE_DAMAGE.setEnabled(plugin.getConfig().getBoolean("Powerups.List.One-Shot-One-Kill.Enabled"));

        List<PowerupType> powerups = new ArrayList<>();
        for(PowerupType pt : PowerupType.values()) {
            if(!pt.isEnabled()) {
                powerups.add(pt);
            }
        }
        if(powerups.size() == PowerupType.values().length) {
            Main.debug("Disabling power up module, all power ups disabled", System.currentTimeMillis());
            enabled = false;
        }
    }

    public void spawnPowerup(Location loc, Arena arena) {
        if(!enabled) return;
        PowerupType powerupType = PowerupType.random();
        if(!powerupType.isEnabled()) {
            spawnPowerup(loc, arena);
        }
        if(!(ThreadLocalRandom.current().nextDouble(0.0, 100.0) <= plugin.getConfig().getDouble("Powerups.Drop-Chance"))) return;
        final PowerupType finalPowerUp = powerupType;
        String text = powerupType.getName();
        ItemStack icon = new ItemStack(powerupType.getMaterial());

        final Hologram hologram = HologramsAPI.createHologram(plugin, loc.clone().add(0.0, 1.2, 0.0));
        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);
        final String powerUpTitle = powerupType.getName();
        final String powerUpSubtitle = ChatManager.colorMessage(powerupType.getAccessPath() + ".Description");
        itemLine.setPickupHandler(player -> {
            if(ArenaRegistry.getArena(player) != arena) return;
            String subTitle = powerUpSubtitle;
            switch(finalPowerUp) {
                case CLEANER:
                    if(arena.getZombies() != null) {
                        for(Zombie zombie : arena.getZombies()) {
                            zombie.getWorld().playEffect(zombie.getLocation(), Effect.LAVA_POP, 20);
                            zombie.remove();
                        }
                        arena.getZombies().clear();
                    }
                    break;
                case DOUBLE_DAMAGE:
                    for(Player p : arena.getPlayers()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                                plugin.getConfig().getInt("Powerups.List.Double-Damage-For-Players.Time"), 1, false, false));
                    }
                    subTitle = subTitle.replaceAll("%time%", plugin.getConfig().getString("Powerups.List.Double-Damage-For-Players.Time"));
                    break;
                case HEALING:
                    for(Player p : arena.getPlayers()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 *
                                plugin.getConfig().getInt("Powerups.List.Healing-For-Players.Time-Of-Healing"), 1, false, false));
                    }
                    subTitle = subTitle.replaceAll("%time%", plugin.getConfig().getString("Powerups.List.Healing-For-Players.Time-Of-Healing"));
                    break;
                case GOLEM_RAID:
                    for(int i = 0; i < plugin.getConfig().getInt("Powerups.List.Golem-Raid.Golems-Amount"); i++) {
                        arena.spawnGolem(arena.getStartLocation(), player);
                    }
                    break;
                case ONE_SHOT_ONE_KILL:
                    for(Player p : arena.getPlayers()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *
                                plugin.getConfig().getInt("Powerups.List.One-Shot-One-Kill.Time"), 255, false, false));
                    }
                    subTitle = subTitle.replaceAll("%time%", plugin.getConfig().getString("Powerups.List.One-Shot-One-Kill.Time"));
                    break;
            }
            for(Player p : arena.getPlayers()) {
                MessageUtils.sendTitle(p, powerUpTitle, 5, 30, 5, ChatColor.DARK_RED);
                MessageUtils.sendSubTitle(p, subTitle, 5, 30, 5, ChatColor.GRAY);
            }
            hologram.delete();
        });
    }

    @Getter
    @AllArgsConstructor
    private enum PowerupType {
        CLEANER("Cleaner", Material.BLAZE_POWDER, "Powerups.Map-Clean-Powerup", true), DOUBLE_DAMAGE("Doubledamage", Material.REDSTONE, "Powerups.Double-Damage-Powerup", true),
        HEALING("Healing", Material.GOLDEN_APPLE, "Powerups.Healing-Powerup", true), GOLEM_RAID("raid", Material.IRON_INGOT, "Powerups.Golem-Raid-Powerup", true),
        ONE_SHOT_ONE_KILL("oson", Material.DIAMOND_SWORD, "Powerups.One-Shot-One-Kill-Powerup", true);

        @Setter
        String name;
        Material material;
        String accessPath;
        @Setter
        boolean enabled;

        private static PowerupType random() {
            Random r = new Random();
            return values()[r.nextInt(values().length)];
        }


    }

}
