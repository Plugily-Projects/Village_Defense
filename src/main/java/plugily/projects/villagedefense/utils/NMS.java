package plugily.projects.villagedefense.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.ServerVersion.Version;

@SuppressWarnings("deprecation")
public abstract class NMS {

	private static final Main PLUGIN = JavaPlugin.getPlugin(Main.class);

	public static void hidePlayer(Player to, Player p) {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			to.hidePlayer(PLUGIN, p);
		} else {
			to.hidePlayer(p);
		}
	}

	public static void showPlayer(Player to, Player p) {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			to.showPlayer(PLUGIN, p);
		} else {
			to.showPlayer(p);
		}
	}

	public static void setPassenger(Entity to, Entity... passengers) {
		// setPassenger is for 1.9 and less versions

		for (Entity ps : passengers) {
			to.addPassenger(ps);
		}
	}
}
