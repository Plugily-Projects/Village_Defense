/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.handlers.powerup;

import com.gmail.filoghost.holographicdisplays.api.handler.PickupHandler;

import org.bukkit.entity.Player;

import pl.plajer.villagedefense.arena.Arena;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 15.01.2019
 */
public class Powerup {

  private String id;
  private String name;
  private String description;
  private XMaterial material;

  public Powerup(String id, String name, String description, XMaterial material) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.material = material;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public XMaterial getMaterial() {
    return material;
  }

  /**
   * Called when powerup is picked up by player in any arena.
   *
   * @param arena  the event arena
   * @param player player who picked up power-up
   * @see PickupHandler
   */
  public void onPickup(Arena arena, Player player) {
  }

}
