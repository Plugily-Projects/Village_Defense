package plugily.projects.villagedefense.handlers.holiday;

import java.time.LocalDateTime;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.powerup.Powerup;

public class ValentineHoliday implements Holiday, Listener {
  private Main plugin;

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return month == 2 && day >= 10 && day <= 18;
  }

  @Override
  public void enable(Main plugin) {
    this.plugin = plugin;
    Powerup powerup = new Powerup("VALENTINES_HEALING", plugin.getChatManager().colorRawMessage("&c&l<3"),
        plugin.getChatManager().colorRawMessage("&d&lHappy Valentine's Day!"), XMaterial.POPPY, pickup -> {
      pickup.getPlayer().setHealth(VersionUtils.getMaxHealth(pickup.getPlayer()));
      VersionUtils.sendTitle(pickup.getPlayer(), pickup.getPowerup().getDescription(), 5, 30, 5);
    });
    plugin.getPowerupRegistry().registerPowerup(powerup);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onArrowShoot(EntityShootBowEvent e) {
    if (e.getEntityType() != org.bukkit.entity.EntityType.PLAYER || ArenaRegistry.getArena((Player) e.getEntity()) == null) {
      return;
    }
    Entity en = e.getProjectile();
    new BukkitRunnable() {
      @Override
      public void run() {
        if (en.isOnGround() || en.isDead()) {
          cancel();
          return;
        }
        VersionUtils.sendParticles("HEART", (Set<Player>) null, en.getLocation(), 1);
      }
    }.runTaskTimer(plugin, 1, 1);
  }
}
