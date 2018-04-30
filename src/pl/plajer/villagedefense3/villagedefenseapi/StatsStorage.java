/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
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

package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.user.UserManager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Plajer, TomTheDeveloper
 * @since 2.0.0
 * <p>
 * Class for accessing users statistics.
 */
public class StatsStorage {

    public static Main plugin;

    private static Map sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
        Map sortedMap = new LinkedHashMap();
        for(Object aList : list) {
            Map.Entry entry = (Map.Entry) aList;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Returns map of all statistics of player
     *
     * @param player event player
     * @return Map with players statistics
     */
    public static Map<UUID, Integer> getStats(Player player) {
        Main.debug("Village API getStats(Player) run", System.currentTimeMillis());
        if(plugin.isDatabaseActivated())
            return plugin.getMySQLDatabase().getColumn(player.getName());
        else {
            FileConfiguration config = ConfigurationManager.getConfig("stats");
            Map<UUID, Integer> stats = new LinkedHashMap<>();
            for(String string : config.getKeys(false)) {
                stats.put(UUID.fromString(string), config.getInt(string + "." + player.getName()));
            }
            return sortByValue(stats);
        }
    }

    /**
     * Returns user statistics.
     *
     * @param player        event player
     * @param statisticType type of stat to return
     * @return Integer of statistic
     */
    public static int getUserStats(Player player, StatisticType statisticType) {
        Main.debug("Village API getUserStats(Player, StatisticType) run", System.currentTimeMillis());
        return UserManager.getUser(player.getUniqueId()).getInt(statisticType.name);
    }

    /**
     * Available statistics to get.
     */
    public enum StatisticType {
        KILLS("kills"), DEATHS("deaths"), GAMES_PLAYED("gamesplayed"), HIGHEST_WAVE("highestwave"), LEVEL("level"), XP("xp");

        String name;

        StatisticType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
