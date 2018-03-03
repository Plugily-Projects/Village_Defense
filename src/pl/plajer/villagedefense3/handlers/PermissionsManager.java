package pl.plajer.villagedefense3.handlers;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionsManager {


    private static String joinFullPerm = "villagedefense.fullgames";
    private static String vipPerm = "villagedefense.vip";
    private static String mvpPerm = "villagedefense.mvp";
    private static String elitePerm = "villagedefense.elite";
    private static String editPerm = "villagedefense.edit";
    private static String joinPerm = "villagedefense.join.<arena>";

    public static String getJoinFullGames() {
        return joinFullPerm;
    }

    public static void setJoinFullGames(String joinFullGames) {
        PermissionsManager.joinFullPerm = joinFullGames;
    }

    public static String getVip() {
        return vipPerm;
    }

    public static void setVip(String VIP) {
        PermissionsManager.vipPerm = VIP;
    }

    public static String getMvp() {
        return mvpPerm;
    }

    public static void setMvp(String MVP) {
        PermissionsManager.mvpPerm = MVP;
    }

    public static String getElite() {
        return elitePerm;
    }

    public static void setElite(String ELITE) {
        PermissionsManager.elitePerm = ELITE;
    }

    public static String getEditGames() {
        return editPerm;
    }

    public static void setEditGames(String editGames) {
        PermissionsManager.editPerm = editGames;
    }

    public static String getJoinPerm() {
        return joinPerm;
    }

    public static void setJoinPerm(String joinPerm) {
        PermissionsManager.joinPerm = joinPerm;
    }
}
