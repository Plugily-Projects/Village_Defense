/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.villagedefense.handlers;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.utils.Debugger;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionsManager {

  private static Main plugin;
  private static String joinFullPerm = "villagedefense.fullgames";
  private static String vipPerm = "villagedefense.vip";
  private static String mvpPerm = "villagedefense.mvp";
  private static String elitePerm = "villagedefense.elite";
  private static String joinPerm = "villagedefense.join.<arena>";

  public static void init(Main plugin) {
    PermissionsManager.plugin = plugin;
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

  private static void setVip(String vip) {
    PermissionsManager.vipPerm = vip;
  }

  public static String getMvp() {
    return mvpPerm;
  }

  private static void setMvp(String mvp) {
    PermissionsManager.mvpPerm = mvp;
  }

  public static String getElite() {
    return elitePerm;
  }

  private static void setElite(String elite) {
    PermissionsManager.elitePerm = elite;
  }

  public static String getJoinPerm() {
    return joinPerm;
  }

  private static void setJoinPerm(String joinPerm) {
    PermissionsManager.joinPerm = joinPerm;
  }

  public static boolean isPremium(Player p) {
    return p.hasPermission(vipPerm) || p.hasPermission(mvpPerm) || p.hasPermission(elitePerm);
  }

  private static void setupPermissions() {
    PermissionsManager.setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", "villagedefense.fullgames"));
    PermissionsManager.setVip(plugin.getConfig().getString("Basic-Permissions.Vip-Permission", "villagedefense.vip"));
    PermissionsManager.setMvp(plugin.getConfig().getString("Basic-Permissions.Mvp-Permission", "villagedefense.mvp"));
    PermissionsManager.setElite(plugin.getConfig().getString("Basic-Permissions.Elite-Permission", "villagedefense.elite"));
    PermissionsManager.setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission", "villagedefense.join.<arena>"));
    Debugger.debug(Level.INFO, "Basic permissions registered");
  }

}
