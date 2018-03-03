package pl.plajer.villagedefense3.utils;

import org.bukkit.ChatColor;

/**
 * @author Plajer
 * <p>
 * Created at 09.02.2018
 */
public class PercentageUtils {

    //https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/

    public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {

        float percent = (float) current / max;

        int progressBars = (int) (totalBars * percent);

        int leftOver = (totalBars - progressBars);

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.translateAlternateColorCodes('&', completedColor));
        for(int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }
        sb.append(ChatColor.translateAlternateColorCodes('&', notCompletedColor));
        for(int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        return sb.toString();
    }

}
