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

package pl.plajer.villagedefense3.villagedefenseapi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import pl.plajer.villagedefense3.arena.Arena;

/**
 * @author Plajer
 * @since 3.8.0
 * <p>
 * Called when player receive new statistic.
 * @see pl.plajer.villagedefense3.villagedefenseapi.StatsStorage.StatisticType
 */
public class VillagePlayerStatisticChangeEvent extends VillageEvent {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private StatsStorage.StatisticType statisticType;

    public VillagePlayerStatisticChangeEvent(Arena eventArena, Player player, StatsStorage.StatisticType statisticType) {
        super(eventArena);
        this.player = player;
        this.statisticType = statisticType;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public StatsStorage.StatisticType getStatisticType() {
        return statisticType;
    }
}
