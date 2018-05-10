/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense3.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.villagedefense3.Main;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionsManager {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private static String joinFullPerm = "villagedefense.fullgames";
    private static String vipPerm = "villagedefense.vip";
    private static String mvpPerm = "villagedefense.mvp";
    private static String elitePerm = "villagedefense.elite";
    private static String editPerm = "villagedefense.edit";
    private static String joinPerm = "villagedefense.join.<arena>";

    public static void init() {
        setupPermissions();
    }

    public static String getJoinFullGames() {
        return joinFullPerm;
    }

    private static void setJoinFullGames(String joinFullGames) {
        PermissionsManager.joinFullPerm = joinFullGames;
    }

    public static String getVip() {
        return vipPerm;
    }

    private static void setVip(String VIP) {
        PermissionsManager.vipPerm = VIP;
    }

    public static String getMvp() {
        return mvpPerm;
    }

    private static void setMvp(String MVP) {
        PermissionsManager.mvpPerm = MVP;
    }

    public static String getElite() {
        return elitePerm;
    }

    private static void setElite(String ELITE) {
        PermissionsManager.elitePerm = ELITE;
    }

    public static String getEditGames() {
        return editPerm;
    }

    private static void setEditGames(String editGames) {
        PermissionsManager.editPerm = editGames;
    }

    public static String getJoinPerm() {
        return joinPerm;
    }

    private static void setJoinPerm(String joinPerm) {
        PermissionsManager.joinPerm = joinPerm;
    }

    private static void setupPermissions() {
        PermissionsManager.setEditGames(plugin.getConfig().getString("Basic-Permissions.Arena-Edit-Permission"));
        PermissionsManager.setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission"));
        PermissionsManager.setVip(plugin.getConfig().getString("Basic-Permissions.Vip-Permission"));
        PermissionsManager.setMvp(plugin.getConfig().getString("Basic-Permissions.Mvp-Permission"));
        PermissionsManager.setElite(plugin.getConfig().getString("Basic-Permissions.Elite-Permission"));
        PermissionsManager.setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission"));
        Main.debug("Basic permissions registered", System.currentTimeMillis());
    }

}
