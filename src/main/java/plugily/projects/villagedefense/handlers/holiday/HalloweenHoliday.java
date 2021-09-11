package plugily.projects.villagedefense.handlers.holiday;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.Utils;

public class HalloweenHoliday implements Holiday, Listener {

  private Random random;
  private Main plugin;

  @Override
  public boolean isHoliday(LocalDateTime dateTime) {
    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    return (month == 10 && day >= 27) || (month == 11 && day <= 4);
  }

  @Override
  public void enable(Main plugin) {
    random = new Random();
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void applyCreatureEffects(Creature creature) {
    org.bukkit.inventory.EntityEquipment equipment = creature.getEquipment();

    if (equipment != null && equipment.getHelmet() == null) {
      //randomizing head type
      if (random.nextBoolean()) {
        equipment.setHelmet(new ItemStack(Material.JACK_O_LANTERN, 1));
      } else {
        equipment.setHelmet(new ItemStack(Material.PUMPKIN, 1));
      }
    }
  }

  @Override
  public void applyDeathEffects(Entity entity) {
    org.bukkit.Location entityLoc = entity.getLocation();

    entity.getWorld().strikeLightningEffect(entityLoc);

    //randomizing sound
    if (random.nextBoolean()) {
      Utils.playSound(entityLoc, "ENTITY_WOLF_HOWL", "ENTITY_WOLF_HOWL");
    } else {
      Utils.playSound(entityLoc, "ENTITY_WITHER_DEATH", "ENTITY_WITHER_DEATH");
    }

    //randomizing bats spawn chance
    if (random.nextBoolean()) {
      final List<Entity> bats = new ArrayList<>();

      for (int i = 0; i < random.nextInt(6); i++) {
        final Entity bat = entityLoc.getWorld().spawnEntity(entityLoc, EntityType.BAT);

        bat.setCustomName(plugin.getChatManager().colorRawMessage("&6Halloween!"));
        bats.add(bat);
      }

      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        for (Entity bat : bats) {
          bat.getWorld().playEffect(bat.getLocation(), Effect.SMOKE, 3);
          bat.remove();
        }

        bats.clear();
      }, 30);
    }
  }

  @EventHandler
  public void onBatDamage(EntityDamageEvent e) {
    if (e.getEntityType() != EntityType.BAT) {
      return;
    }

    String customName = e.getEntity().getCustomName();

    if (customName != null && customName.equals(plugin.getChatManager().colorRawMessage("&6Halloween!"))) {
      e.setCancelled(true);
    }
  }
}
