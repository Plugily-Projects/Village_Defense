package plugily.projects.villagedefense.handlers.holiday;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.handlers.powerup.Powerup;

public class AprilFoolsHoliday implements Holiday, Listener {
  private Random random;
  private Main plugin;

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return (month == 3 && day >= 28) || (month == 4 && day <= 5);
  }

  @Override
  public void enable(Main plugin) {
    this.random = new Random();
    this.plugin = plugin;
    Powerup powerup = new Powerup("APRIL_FOOL", plugin.getChatManager().colorRawMessage("&a&llololol"),
        plugin.getChatManager().colorRawMessage("&a&lApril Fools!"), XMaterial.DIRT, pickup -> {
      pickup.getPlayer().damage(0);
      VersionUtils.sendTitle(pickup.getPlayer(), pickup.getPowerup().getDescription(), 5, 30, 5);
    });
    plugin.getPowerupRegistry().registerPowerup(powerup);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void applyDeathEffects(Entity entity) {
    if (!random.nextBoolean()) {
      return;
    }
    final List<Item> diamonds = new ArrayList<>();
    for (int i = 0; i < random.nextInt(6); i++) {
      Item item = entity.getWorld().dropItem(entity.getLocation(), XMaterial.DIAMOND.parseItem());
      item.setPickupDelay(1000000);
      item.setVelocity(getRandomVector());
      diamonds.add(item);
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      diamonds.forEach(Item::remove);
      diamonds.clear();
    }, 30);
  }

  @EventHandler
  public void onArrowShoot(EntityShootBowEvent e) {
    if (!(e.getEntity() instanceof Player) || ArenaRegistry.getArena((Player) e.getEntity()) == null) {
      return;
    }
    if (random.nextInt(4) == 0) {
      //chance to make arrow shoot somewhere else
      e.getProjectile().setVelocity(getRandomVector());
    }
  }

  private Vector getRandomVector() {
    Vector direction = new Vector();
    direction.setX(0.0D + Math.random() - Math.random());
    direction.setY(Math.random());
    direction.setZ(0.0D + Math.random() - Math.random());
    return direction;
  }
}
