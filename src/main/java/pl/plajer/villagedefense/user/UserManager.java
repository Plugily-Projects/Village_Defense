/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.Arena;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

  private static HashMap<UUID, User> users = new HashMap<>();

  public static void registerUser(UUID uuid) {
    Main.debug(Main.LogLevel.INFO, "Registering new user with UUID: " + uuid);
    users.put(uuid, new User(uuid));
  }

  public static User getUser(UUID uuid) {
    if (users.containsKey(uuid)) {
      return users.get(uuid);
    } else {
      users.put(uuid, new User(uuid));
      return users.get(uuid);
    }
  }

  public static List<User> getUsers(Arena arena) {
    List<User> users = new ArrayList<>();
    for (Player player : arena.getPlayers()) {
      users.add(getUser(player.getUniqueId()));
    }
    return users;
  }

  public static void removeUser(UUID uuid) {
    users.remove(uuid);
  }

}
