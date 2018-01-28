package me.tomthedeveloper.permissions;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionsManager {


    private static String joinfullperm = "villagedefense.fullgames";
    private static String vipperm = "villagedefense.vip";
    private static String mvpperm = "villagedefense.mvp";
    private static String eliteperm = "villagedefense.elite";
    private static String editperm = "villagedefense.edit";
    
    public static String getJoinFullGames() {
        return joinfullperm;
    }

    public static void setJoinFullGames(String joinFullGames) {
        PermissionsManager.joinfullperm = joinFullGames;
    }

    public static String getVIP() {
        return vipperm;
    }

    public static void setVIP(String VIP) {
        PermissionsManager.vipperm = VIP;
    }

    public static String getMVP() {
        return mvpperm;
    }

    public static void setMVP(String MVP) {
        PermissionsManager.mvpperm = MVP;
    }

    public static String getELITE() {
        return eliteperm;
    }

    public static void setELITE(String ELITE) {
        PermissionsManager.eliteperm = ELITE;
    }

    public static String getEditGames() {
        return editperm;
    }

    public static void setEditGames(String editGames) {
        PermissionsManager.editperm = editGames;
    }
}
