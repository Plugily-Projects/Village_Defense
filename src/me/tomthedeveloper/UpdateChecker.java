package me.tomthedeveloper;

import me.tomthedeveloper.handlers.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static String latestVersion;

    private static boolean checkHigher(String currentVersion, String newVersion) {
        String current = toReadable(currentVersion);
        String newVers = toReadable(newVersion);
        return current.compareTo(newVers) < 0;
    }

    public static void checkUpdate(String currentVersion) throws Exception {
        String version = getVersion(41869);
        if(checkHigher(currentVersion, version))
            latestVersion = version;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    private static String getVersion(int resourceId) {
        String version = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("resource=" + resourceId).getBytes("UTF-8"));
            version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        } catch(IOException ex) {
            ChatManager.sendErrorHeader("checking for an update");
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Don't panic! Try to do this steps:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- check if spigotmc site isn't offline (and wait until it's online)");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- contact the developer");
        }
        return version;
    }

    public static String toReadable(String version) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version.replace("v", ""));
        version = "";
        for(String s : split)
            version += String.format("%4s", s);
        return version;
    }
}
