package pl.plajer.villagedefense3.creatures;

import org.bukkit.ChatColor;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.PercentageUtils;

import java.lang.reflect.Field;

/**
 * @author Plajer
 * <p>
 * Created at 17 lis 2017
 */
public class CreatureUtils {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static void applyHealthBar(Zombie zombie){
        if(plugin.getConfig().getBoolean("Simple-Zombie-Health-Bar-Enabled")) {
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(PercentageUtils.getProgressBar((int) zombie.getMaxHealth(), (int) zombie.getMaxHealth(), 50, "|", ChatColor.YELLOW + "", ChatColor.GRAY + ""));
        }
    }

}
